package com.company;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class ClientWorker {
    AsynchronousServerSocketChannel server;
    AsynchronousSocketChannel client;
    SocketAddress clientAddr;

    ByteBuffer buffer;

    ReadHandler rHandler;
    WriteHandler wHandler;

    boolean isReading = false;

    private static final ArrayList<ClientWorker> Workers = new ArrayList<>();

    public static void WriteBroadcast(byte[] bytes) {
        int size = Workers.size();

        for(int i = 0; i < size; ++i){
            ClientWorker worker = Workers.get(i);
            //FIXME у 1го клиента всегда буфер нулевой
            if(worker.buffer != null){
                try {
                    worker.client.write(ByteBuffer.wrap(bytes, 0, bytes.length), worker, worker.wHandler);
                }catch(WritePendingException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ClientWorker() {
        rHandler = new ReadHandler();
        wHandler = new WriteHandler();
        Workers.add(this);
    }

    class ReadHandler implements CompletionHandler<Integer, ClientWorker> {
        @Override
        public void completed(Integer result, ClientWorker attach) {
            if (result == -1) {
                try {
                    attach.client.close();
                    System.out.format("Stopped   listening to the   client %s%n", attach.clientAddr);
                    Workers.remove(attach);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            attach.isReading = false;

            attach.buffer.flip();
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);

            try {
                Charset cs = Charset.forName("UTF-8");
                String msg = new String(bytes, cs);
                System.out.format("Client at  %s  says: %s%n", attach.clientAddr, msg);

                WriteBroadcast(msg.getBytes("UTF8"));
            }catch (Throwable ex){
                ex.printStackTrace();
            }

            //attach.buffer.rewind();
            //attach.client.write(attach.buffer, attach, this);
        }

        @Override
        public void failed(Throwable e, ClientWorker attach) {
            e.printStackTrace();
            Workers.remove(attach);
        }
    }

    class WriteHandler implements CompletionHandler<Integer, ClientWorker> {
        @Override
        public void completed(Integer result, ClientWorker attach) {
            if (result == -1) {
                try {
                    attach.client.close();
                    System.out.format("Stopped   listening to the   client %s%n", attach.clientAddr);
                    Workers.remove(attach);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            if(attach.isReading != true){
                attach.isReading = true;
                attach.buffer.clear();
                attach.client.read(attach.buffer, attach, attach.rHandler);
            }
        }

        @Override
        public void failed(Throwable e, ClientWorker attach) {
            e.printStackTrace();
        }
    }
}

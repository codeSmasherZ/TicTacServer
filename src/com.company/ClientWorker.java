package com.company;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class ClientWorker {
    AsynchronousServerSocketChannel _server;
    AsynchronousSocketChannel _client;
    SocketAddress _clientAddr;

    ByteBuffer _buffer;

    ReadHandler _rHandler;
    WriteHandler _wHandler;

    private Room _room;

    boolean _reading = false;

    public ClientWorker(Room room) {
        _room = room;
        _rHandler = new ReadHandler();
        _wHandler = new WriteHandler();

        _room.Add(this);
    }

    class ReadHandler implements CompletionHandler<Integer, ClientWorker> {
        @Override
        public void completed(Integer result, ClientWorker attach) {
            if (result == -1) {
                try {
                    attach._client.close();
                    System.out.format("Stopped   listening to the   client %s%n", attach._clientAddr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            attach._reading = false;

            attach._buffer.flip();
            int limits = attach._buffer.limit();
            byte bytes[] = new byte[limits];
            attach._buffer.get(bytes, 0, limits);

            try {
                Charset cs = Charset.forName("UTF-8");
                String msg = new String(bytes, cs);
                System.out.format("Client at  %s  says: %s%n", attach._clientAddr, msg);

                //Do write here
                //WriteBroadcast(msg.getBytes("UTF8"));
            }catch (Throwable ex){
                ex.printStackTrace();
            }

            //attach._buffer.rewind();
            //attach._client.write(attach._buffer, attach, this);
        }

        @Override
        public void failed(Throwable e, ClientWorker attach) {
            e.printStackTrace();
        }
    }

    class WriteHandler implements CompletionHandler<Integer, ClientWorker> {
        @Override
        public void completed(Integer result, ClientWorker attach) {
            if (result == -1) {
                try {
                    attach._client.close();
                    System.out.format("Stopped   listening to the   _client %s%n", attach._clientAddr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            if(attach._reading != true){
                attach._reading = true;
                attach._buffer.clear();
                attach._client.read(attach._buffer, attach, attach._rHandler);
            }
        }

        @Override
        public void failed(Throwable e, ClientWorker attach) {
            e.printStackTrace();
        }
    }
}

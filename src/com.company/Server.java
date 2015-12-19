package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;

public class Server {
    public static final int BUFFER_SIZE = 2048;

    public Server(String host, int port) throws Exception{
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        InetSocketAddress sAddr = new InetSocketAddress(host, port);

        server.bind(sAddr);
        System.out.format("Server is listening at %s%n", sAddr);

        ClientWorker attach = new ClientWorker();
        attach.server = server;

        server.accept(attach, new ConnectionHandler());
    }

    class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ClientWorker> {
        @Override
        public void completed(AsynchronousSocketChannel client, ClientWorker attach) {
            try {
                SocketAddress clientAddr = client.getRemoteAddress();

                System.out.format("Accepted a  connection from  %s%n", clientAddr);
                attach.server.accept(attach, this);

                ClientWorker newAttach = new ClientWorker();
                newAttach.server = attach.server;
                newAttach.client = client;
                newAttach.buffer = ByteBuffer.allocate(BUFFER_SIZE);
                newAttach.clientAddr = clientAddr;
                newAttach.isReading = true;
                client.read(newAttach.buffer, newAttach, newAttach.rHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable e, ClientWorker attach) {
            System.out.println("Failed to accept a  connection.");
            e.printStackTrace();
        }
    }
}

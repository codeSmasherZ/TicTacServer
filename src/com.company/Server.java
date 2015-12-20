package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;

public class Server {
    private static final ArrayList<Room> _Rooms = new ArrayList<>();

    public static final int BUFFER_SIZE = 512;

    //Search for free room or creates new one
    private Room GetRoomForNewClient(){
        Room room;

        if(_Rooms.size() > 0){
            if(!_Rooms.get(_Rooms.size()-1).IsFull()){
                room = _Rooms.get(_Rooms.size()-1);
                return room;
            }
        }

        room = new Room();
        _Rooms.add(room);

        return room;
    }

    public Server(String host, int port) throws Exception{
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        InetSocketAddress sAddr = new InetSocketAddress(host, port);

        server.bind(sAddr);
        System.out.format("Server is listening at %s%n", sAddr);

        ClientWorker attach = new ClientWorker(null);
        attach._server = server;

        server.accept(attach, new ConnectionHandler());
    }

    class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ClientWorker> {
        @Override
        public void completed(AsynchronousSocketChannel client, ClientWorker attach) {
            try {
                SocketAddress clientAddr = client.getRemoteAddress();

                System.out.format("Accepted a  connection from  %s%n", clientAddr);
                attach._server.accept(attach, this);

                ClientWorker newAttach = new ClientWorker(GetRoomForNewClient());
                newAttach._server = attach._server;
                newAttach._client = client;
                newAttach._buffer = ByteBuffer.allocate(BUFFER_SIZE);
                newAttach._clientAddr = clientAddr;
                client.read(newAttach._buffer, newAttach, newAttach._rHandler);
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

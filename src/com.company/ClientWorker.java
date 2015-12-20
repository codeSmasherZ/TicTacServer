package com.company;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;


public class ClientWorker {
    AsynchronousServerSocketChannel _server;
    AsynchronousSocketChannel _client;
    SocketAddress _clientAddr;

    ByteBuffer _buffer;

    ReadHandler _rHandler;
    WriteHandler _wHandler;

    private Room _room;
    private int _player;
    private boolean _reading;

    public ClientWorker(Room room) {
        if(room == null){
            _player = 0;
            return;
        }

        _room = room;

        _rHandler = new ReadHandler();
        _wHandler = new WriteHandler();
        _reading = false;

        System.out.println(_room);

        _room.Add(this);
        _player = _room.IsFull() ? 2 : 1;

        System.out.println(_player);
    }

    public void SendTurn(int x, int y){
        byte[] bytes = new byte[2];
        bytes[0] = (byte)x;
        bytes[1] = (byte)y;

        _buffer.rewind();
        _buffer.clear();

        _buffer.put(bytes, 0, bytes.length);

        _buffer.flip();
        _client.write(_buffer, this, _wHandler);
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
                //Make player turn

                byte[] turn_result = new byte[1];

                if((_room.IsFirstPlayerTurn() && _player == 1) ||
                        (!_room.IsFirstPlayerTurn() && _player == 2) ) {
                    int x = bytes[0];
                    int y = bytes[1];

                    turn_result[0] =  _room.MakeTurn(_player, x, y) ? (byte)0 : -1;
                }else{
                    //WRITE ERROR CODE TO CLIENT
                    turn_result[0] =  -1; // error code
                }


                attach._buffer.rewind();
                attach._buffer.clear();

                attach._buffer.put(turn_result, 0, turn_result.length);

                attach._buffer.flip();
                attach._client.write(attach._buffer, attach, attach._wHandler);

            }catch (Throwable ex){
                ex.printStackTrace();
            }
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

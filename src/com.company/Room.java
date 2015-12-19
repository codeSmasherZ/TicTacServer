package com.company;

public class Room {
    ClientWorker _workerA;
    ClientWorker _workerB;

    boolean _firstWorkerTurn;
    boolean _isFull;

    public Room(){
        _firstWorkerTurn = true;
        _isFull = false;
    }

    public boolean Add(ClientWorker worker){
        if(_workerA  == null){
            _workerA = worker;
            return true;
        }else if(_workerB == null){
            _workerB = worker;
            _isFull = true;
            return true;
        }

        return false;
    }

    public boolean isFull(){
        return _isFull;
    }
}

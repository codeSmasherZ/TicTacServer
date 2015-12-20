package com.company;

public class Room {
    private ClientWorker _workerA;
    private ClientWorker _workerB;

    private boolean _firstWorkerTurn;
    private boolean _full;

    private Map _map;

    static int _num = 0;

    public Room(){
        _firstWorkerTurn = true;
        _full = false;
        _map = new Map(3, 3, true);
        _workerA = null;
        _workerB = null;
    }

    public boolean Add(ClientWorker worker){
        if(_workerA  == null){
            _workerA = worker;
            return true;
        }else if(_workerB == null){
            _workerB = worker;
            _full = true;
            return true;
        }

        return false;
    }

    public boolean MakeTurn(int player, int x, int y){
        if((_firstWorkerTurn && player == 1) || (!_firstWorkerTurn && player == 2)){
            _firstWorkerTurn  = _firstWorkerTurn ? false : true;
            return _map.MakeTurn(player, x, y);
        }

        return  false;
    }

    public boolean IsFirstPlayerTurn() { return _firstWorkerTurn; }
    public boolean IsFull(){
        return _full;
    }
}

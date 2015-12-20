package com.company;

/**
 * Map specs:
 * 0 - free cell
 * 1 - first player
 * 2 - second player
 */

public class Map {
    private int[][] _MapArray;
    private boolean _short_line;

    public Map(int width, int height, boolean short_line){
        if(short_line){
            if(width < 3) width = 3;
            if(height < 3) height = 3;
        }else{
            if(width < 5) width = 5;
            if(height < 5) height = 5;
        }

        _MapArray = new int[height][width];
        _short_line = short_line;

        //zero elements
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                _MapArray[i][j] = 0;
            }
        }
    }

    public boolean MakeTurn(int player, int x, int y){
        if(!IsValidTurn(player, x, y) ||
                x >= _MapArray[0].length || y >= _MapArray.length){
            return false;
        }

        if(_MapArray[y][x] == 0){
            _MapArray[y][x] = player;
            return true;
        }

        return false;
    }

    public boolean CheckWin(int player, int x, int y){
        if(CheckWay(player, x, y, Way.DOWN)) return true;
        if(CheckWay(player, x, y, Way.UP)) return true;
        if(CheckWay(player, x, y, Way.LEFT)) return true;
        if(CheckWay(player, x, y, Way.RIGHT)) return true;
        if(CheckWay(player, x, y, Way.BOTTOM_LEFT)) return true;
        if(CheckWay(player, x, y, Way.BOTTOM_RIGHT)) return true;
        if(CheckWay(player, x, y, Way.TOP_LEFT)) return true;
        if(CheckWay(player, x, y, Way.TOP_RIGHT)) return true;

        return false;
    }

    private boolean CheckWay(int player, int x, int y, Way way){
        int height = _MapArray.length;
        int width = _MapArray[0].length;
        int way_len = _short_line ? 3 : 5;

        if(way == Way.DOWN){
            if(  height - y >= way_len){
                for(int i = y; i <= y + way_len; ++i){
                    if(_MapArray[i][x] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.UP){
            if( y >= way_len){
                for(int i = y; i >= y - way_len; --i){
                    if(_MapArray[i][x] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.RIGHT){
            if( width - x >= way_len){
                for(int j = x; j <= x + way_len; ++j){
                    if(_MapArray[y][j] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.LEFT){
            if( x >= way_len){
                for(int j = x; j >= x - way_len; --j){
                    if(_MapArray[y][j] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.TOP_LEFT){
            if( x >= way_len && y >= way_len){
                for(int k = 0; k < way_len; ++k){
                    if(_MapArray[x - k][y - k] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.TOP_RIGHT){
            if( (width - x) >= way_len && y >= way_len){
                for(int k = 0; k < way_len; ++k){
                    if(_MapArray[x + k][y - k] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.BOTTOM_LEFT){
            if( x >= way_len && (height - y) >= way_len){
                for(int k = 0; k < way_len; ++k){
                    if(_MapArray[x - k][y + k] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        if(way == Way.BOTTOM_RIGHT){
            if( (width - x) >= way_len && (height - y) >= way_len){
                for(int k = 0; k < way_len; ++k){
                    if(_MapArray[x + k][y + k] != player){
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }

    private boolean IsValidTurn(int player, int x, int y){
        return (x < 0 || x >=  _MapArray[0].length ) &&
                (y < 0 || y >=  _MapArray.length) &&
                (player == 1 || player == 2);
    }

    public enum Way {
        DOWN, UP, RIGHT, LEFT,
        TOP_RIGHT, TOP_LEFT,
        BOTTOM_RIGHT, BOTTOM_LEFT
    }
}


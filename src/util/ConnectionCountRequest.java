package util;

import java.util.HashMap;

import server.Player;

public class ConnectionCountRequest {

    public int connected;
    public HashMap<Integer, Player> players = new HashMap<Integer, Player>();

}
package util;

import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;

import server.Main;

public class UpdateConnections extends TimerTask {

    public static int connected = 0;
    public static ConnectionCountRequest ccr = new ConnectionCountRequest();


    public void run(){

        connected = 0;


        for(@SuppressWarnings("unused") Connection c : Main.server.getConnections()){
            connected++;
            ccr.connected = connected;
            Main.server.sendToAllTCP(ccr);
        }



    }

}
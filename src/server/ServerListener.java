package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.PlayerData;
import com.trading.networking.packets.PlayerDataPacket;

import util.UpdateConnections;

public class ServerListener extends Listener {
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerData) {
			PlayerData info = ((PlayerData) object);

			System.out.println(connection.getID());
			System.out.println(info.pos);

			/*if (getPlayer(connection.getID(), UpdateConnections.ccr.players) != null) {
				//UpdateConnections.ccr.players.replace(connection.getID(), info.players);
			}*/

		}
		
		if (object instanceof PlayerDataPacket) {
			PlayerDataPacket info = ((PlayerDataPacket) object);
			Player p = new Player(GameServer.instances.get(info.instance));
			p.id = info.id;
			GameServer.instances.get(info.instance).addPlayer(p);
		}
	}

	public void connected(Connection connection) {
		System.out.println("new client with id " + connection.getID());
		//connection.sendTCP(world);
		/*UpdateConnections.ccr.players.put(connection.getID(),
				new Player(world));*/
	}

	public void disconnected(Connection connection) {
		UpdateConnections.ccr.players.remove(connection.getID());
	}
}

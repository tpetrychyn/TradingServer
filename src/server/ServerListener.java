package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.PlayerData;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.PlayerDataPacket;

public class ServerListener extends Listener {
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerData) {
			PlayerData info = ((PlayerData) object);

			System.out.println(connection.getID());
			System.out.println(info.pos);
		}
		
		if (object instanceof PlayerDataPacket) {
			PlayerDataPacket info = ((PlayerDataPacket) object);
			Player p = new Player(GameServer.instances.get(info.instance));
			p.id = info.id;
			p.setPosition(info.playerData.pos);
			GameServer.instances.get(info.instance).addPlayer(p);
			
			for (int key: GameServer.instances.get(info.instance).players.keySet()) {
            	Player pInstance = GameServer.instances.get(info.instance).players.get(key);
            	GameServer.server.sendToUDP(pInstance.id, info);
            }
		}
		
		if (object instanceof InstancePacket) {
			InstancePacket packet = (InstancePacket)object;
			if (packet.action.equals("leave")) {
				System.out.println("removed " + connection.getID() + " from " + packet.id);
				GameServer.instances.get(packet.id).getPlayers().remove(connection.getID());
			}
		}
	}

	public void connected(Connection connection) {
		System.out.println("new client with id " + connection.getID());
	}

	public void disconnected(Connection connection) {
	}
}

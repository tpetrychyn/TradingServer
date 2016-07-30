package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.PlayerData;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.PlayerDataPacket;

import util.Util;

public class ServerListener extends Listener {
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerData) {
			PlayerData info = ((PlayerData) object);

			System.out.println(connection.getID());
			System.out.println(info.pos);
		}
		
		if (object instanceof PlayerDataPacket) {
			PlayerDataPacket info = ((PlayerDataPacket) object);
			
			if (GameServer.instances.get(info.instance) == null) {
				
				Instance in = new Instance("assets/maps/map.tmx");
				in.id = 2;
				in.name = "Instance 2";
				for (int i=0;i<20;i++) {
					NpcController npc = new NpcController( Util.randomRange(0, 20), Util.randomRange(0, 20), in, i, 0.5f);
					npc.startRandomWalk(5);
					npc.setName(in.id + " " + in.generateName());
					in.actors.put(npc.id, npc);
				}
				GameServer.instances.put(in.id, in);
				System.out.println("created " + info.instance);
			}
			
			Player p = new Player(GameServer.instances.get(info.instance));
			p.id = info.id;
			p.setPosition(info.playerData.pos);
			GameServer.instances.get(info.instance).addPlayer(p);
			
			for (int key: GameServer.instances.get(info.instance).players.keySet()) {
            	Player pInstance = GameServer.instances.get(info.instance).players.get(key);
            	if (pInstance.id != connection.getID())
            		GameServer.server.sendToUDP(pInstance.id, info);
            }
		}
		
		if (object instanceof InstancePacket) {
			InstancePacket packet = (InstancePacket)object;
			if (packet.action.equals("leave")) {
				System.out.println("removed " + connection.getID() + " from " + packet.id);
				GameServer.instances.get(packet.id).getPlayers().remove(connection.getID());
				packet.clientId = connection.getID();
				
				for (int key: GameServer.instances.get(packet.id).players.keySet()) {
	            	Player pInstance = GameServer.instances.get(packet.id).players.get(key);
	            	if (pInstance.id != connection.getID())
	            		GameServer.server.sendToUDP(pInstance.id, packet);
	            }
			}
		}
	}

	public void connected(Connection connection) {
		System.out.println("new client with id " + connection.getID());
	}

	public void disconnected(Connection connection) {
	}
}

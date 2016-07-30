package server;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.PlayerData;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.NpcMovePacket;
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
			
			if (GameServer.instances.get(info.instance) == null) {
				
				Instance in = new Instance("house.tmx");
				in.id = 2;
				in.name = "Instance 2";
				NpcController npc = new NpcController(12, 5, in, 0, 0.5f);
				npc.startRandomWalk(5);
				npc.setName(in.id + " " + in.generateName());
				in.actors.put(npc.id, npc);
				GameServer.instances.put(in.id, in);
				System.out.println("created " + info.instance);
			}
			
			//put player in instance hashmap
			Player p = new Player(GameServer.instances.get(info.instance));
			p.id = info.id;
			p.setPosition(info.playerData.pos);
			GameServer.instances.get(info.instance).addPlayer(p);
			
			//update all other players in the instance about the new position
			for (int key: GameServer.instances.get(info.instance).players.keySet()) {
            	Player pInstance = GameServer.instances.get(info.instance).players.get(key);
            	if (pInstance.id != connection.getID())
            		GameServer.server.sendToUDP(pInstance.id, info);
            }
			
			//send player all npcs in that instance?
			NpcMovePacket[] npcs = new NpcMovePacket[GameServer.instances.get(info.instance).actors.size()];
			int index = 0;
			for (int key: GameServer.instances.get(info.instance).actors.keySet()) {
				NpcController c = (NpcController) GameServer.instances.get(info.instance).actors.get(key);
				NpcMovePacket n = new NpcMovePacket(c.getX(), c.getY(), c.id, c.getName());
				npcs[index] = n;
				index++;
            }
			GameServer.server.sendToUDP(connection.getID(), npcs);
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

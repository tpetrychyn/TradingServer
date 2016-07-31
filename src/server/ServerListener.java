package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.Direction;
import com.trading.entities.PlayerData;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerDataPacket;
import com.trading.networking.packets.WorldObjectPacket;

import entities.WorldActor;


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
			GameServer.players.put(connection.getID(), p);
			
			//update all other players in the instance about the new position
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
				
				if (GameServer.instances.get(packet.id).getPlayers().size() <= 0) {
					GameServer.instances.remove(packet.id);
					System.out.println("should remove instance" + packet.id);
					return;
				}
				for (int key: GameServer.instances.get(packet.id).players.keySet()) {
	            	Player pInstance = GameServer.instances.get(packet.id).players.get(key);
	            	if (key != connection.getID())
	            		GameServer.server.sendToTCP(pInstance.id, packet);
	            }
			}
			
			if (packet.action.equals("join")) {
				if (GameServer.instances.get(packet.id) == null) {
					Instance in = new Instance("house.tmx");
					in.id = packet.id;
					in.name = "Instance 2";
					NpcController npc = new NpcController(12, 5, in, 0, 0.5f);
					npc.startRandomWalk(5);
					npc.setName(in.id + " " + in.generateName());
					in.actors.put(npc.id, npc);
					GameServer.instances.put(in.id, in);
					System.out.println("created " + packet.id);
				}
				
				//send player all npcs in that instance
				NpcMovePacket[] npcs = new NpcMovePacket[GameServer.instances.get(packet.id).actors.size()];
				int index = 0;
				for (int key: GameServer.instances.get(packet.id).actors.keySet()) {
					NpcController c = (NpcController) GameServer.instances.get(packet.id).actors.get(key);
					NpcMovePacket n = new NpcMovePacket(c.getX(), c.getY(), c.id, c.getName());
					npcs[index] = n;
					index++;
	            }
				GameServer.server.sendToTCP(connection.getID(), npcs);
				
				//send player all world objects
				WorldObjectPacket[] worldObjs = new WorldObjectPacket[GameServer.instances.get(packet.id).worldObjects.size()];
				index = 0;
				for (int key: GameServer.instances.get(packet.id).worldObjects.keySet()) {
					WorldActor a = GameServer.instances.get(packet.id).worldObjects.get(key);
					WorldObjectPacket p = new WorldObjectPacket(a.getX(), a.getY(), a.id, a.type, a.typeId);
					worldObjs[index] = p;
					index++;
	            }
				GameServer.server.sendToTCP(connection.getID(), worldObjs);
				
				//send player all players in that instance
				PlayerDataPacket[] players = new PlayerDataPacket[GameServer.instances.get(packet.id).players.size()];
				index = 0;
				for (int key: GameServer.instances.get(packet.id).players.keySet()) {
					Player player = (Player) GameServer.instances.get(packet.id).players.get(key);
					PlayerDataPacket p = new PlayerDataPacket(player.id, packet.id, player.getPosition(), Direction.SOUTH);
					players[index] = p;
					index++;
	            }
				GameServer.server.sendToTCP(connection.getID(), players);
				
				//put player in instance hashmap
				Player p = new Player(GameServer.instances.get(packet.id));
				p.id = connection.getID();
				System.out.println(packet);
				p.setPosition(packet.playerData.pos);
				System.out.println(packet.playerData.pos);
				GameServer.instances.get(packet.id).addPlayer(p);
				GameServer.players.put(connection.getID(), p);
				
				//create a new player data packet for the joined player
				PlayerDataPacket info = new PlayerDataPacket();
				info.id = p.id;
				info.instance = packet.id;
				info.playerData.pos = p.getPosition();
				System.out.println(p.getPosition());
				info.playerData.pId = p.id;
				
				//update all other players in the instance about the new player
				for (int key: GameServer.instances.get(packet.id).players.keySet()) {
	            	Player pInstance = GameServer.instances.get(packet.id).players.get(key);
	            	if (pInstance.id != connection.getID())
	            		GameServer.server.sendToUDP(pInstance.id, info);
	            }
			}
		}
	}

	public void connected(Connection connection) {
		System.out.println("new client with id " + connection.getID());
	}

	public void disconnected(Connection connection) {
		
		Player p = GameServer.players.get(connection.getID());
		Instance i = GameServer.instances.get(p.instance.id);
		InstancePacket iPacket = new InstancePacket(i.id, "leave");
		
		System.out.println("diconnection: removed " + connection.getID() + " from " + i.id);
		GameServer.instances.get(connection.getID()).getPlayers().remove(connection.getID());
		iPacket.clientId = connection.getID();
		
		if (GameServer.instances.get(i.id).getPlayers().size() <= 0) {
			GameServer.instances.remove(i.id);
			System.out.println("should remove instance" + i.id);
			return;
		}
		for (int key: GameServer.instances.get(i.id).players.keySet()) {
        	Player pInstance = GameServer.instances.get(i.id).players.get(key);
        	if (key != connection.getID())
        		GameServer.server.sendToTCP(pInstance.id, iPacket);
        }
	}
}

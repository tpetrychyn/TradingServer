package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.trading.networking.packets.NpcMovePacket;

import models.PlayerData;
import util.UpdateConnections;
import util.Util;

class GameServer implements ApplicationListener{

		public static Server server;
		public static final int PORT = 54555;
		public static List<Integer> connections = new ArrayList<Integer>();
		public HashMap<Integer, Instance> instances = new HashMap<Integer, Instance>();
		public HashMap<Integer, Player> ships = new HashMap<Integer, Player>();

		public static Player getPlayer(int id, HashMap<Integer, Player> s) {
			return s.get(id);
		}
		
		@Override
		public void create() {
			server = new Server();
			//Timer updateConnections = new Timer();
			server.start();
			
			try {
				server.bind(54555, 54777);
				System.out.println("Server started");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Instance in = new Instance("assets/maps/map.tmx");
			in.id = 1;
			in.name = "Instance 1";
			for (int i=0;i<100;i++) {
				NpcController npc = new NpcController( Util.randomRange(0, 50), Util.randomRange(0, 50), in, i, 0.5f);
				npc.startRandomWalk(5);
				npc.setName(in.generateName());
				in.actors.addActor(npc);
			}
			instances.put(in.id, in);
			
			in = new Instance("assets/maps/map.tmx");
			in.id = 2;
			in.name = "Instance 2";
			for (int i=0;i<100;i++) {
				NpcController npc = new NpcController( Util.randomRange(0, 50), Util.randomRange(0, 50), in, i, 0.5f);
				npc.startRandomWalk(5);
				npc.setName(in.generateName());
				in.actors.addActor(npc);
			}
			instances.put(in.id, in);
			
			Kryo kryo = server.getKryo();
			kryo.setRegistrationRequired(false);
			
			server.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					if (object instanceof PlayerData) {
						PlayerData info = ((PlayerData) object);

						System.out.println(connection.getID());
						System.out.println(info.pos);

						if (getPlayer(connection.getID(), UpdateConnections.ccr.players) != null) {
							//UpdateConnections.ccr.players.replace(connection.getID(), info.players);
						}

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
			});
		}

		@Override
		public void resize(int width, int height) {
			// TODO Auto-generated method stub
			
		}

		float tickRate = 0;
		
		//main server loop
		@Override
		public void render() {
			float deltaTime = Gdx.graphics.getDeltaTime();
			tickRate += deltaTime;
			
			//60 ticks per second
			if (tickRate < 1/60f)
				return;
			
			//iterate through each instance and act
			Iterator<Entry<Integer, Instance>> iterator = instances.entrySet().iterator();
	        while(iterator.hasNext()){
	            HashMap.Entry<Integer, Instance> instance = iterator.next();
	            instance.getValue().actors.act(deltaTime);
	            //You can remove elements while iterating.
	            //iterator.remove();
	            tickRate = 0;
	        }
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resume() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
		
		public static void updateActor(Instance instance, int id) {
			Actor a = instance.getActors().items[id];
			if (a==null)
				return;
			NpcMovePacket n = new NpcMovePacket(a.getX(), a.getY(), id, a.getName());
			server.sendToAllTCP(n);
		}
	}
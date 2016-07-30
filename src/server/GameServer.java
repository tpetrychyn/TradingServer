package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.trading.networking.packets.NpcMovePacket;

import util.Util;

public class GameServer implements ApplicationListener{

		public static Server server;
		public static final int PORT = 54555;
		public static List<Integer> connections = new ArrayList<Integer>();
		public static HashMap<Integer, Instance> instances = new HashMap<Integer, Instance>();
		public static HashMap<Integer, Player> players = new HashMap<Integer, Player>();

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
			
			Instance in = new Instance("map.tmx");
			in.id = 1;
			in.name = "Instance 1";
			for (int i=0;i<20;i++) {
				NpcController npc = new NpcController( Util.randomRange(0, 20), Util.randomRange(0, 20), in, i, 0.5f);
				npc.startRandomWalk(5);
				npc.setName(in.id + " " + in.generateName());
				in.actors.put(npc.id, npc);
			}
			instances.put(in.id, in);
			
			Kryo kryo = server.getKryo();
			kryo.setRegistrationRequired(false);
			
			server.addListener(new ServerListener());
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
	            Instance instance = iterator.next().getValue();
	            for (int key: instance.getActors().keySet()) {
	            	instance.actors.get(key).act(deltaTime);
		        }
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
	}
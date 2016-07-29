package server;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.trading.networking.packets.NpcMovePacket;

import models.PlayerData;
import util.UpdateConnections;

public class Main {
 
	public static Server server;
	public static World world;
	public static TiledMap map;
	public static MapLayers collisionLayers;
	public static Group group;
	static HeadlessApplication headless;
	static ServerTick t;

	public static final int PORT = 54555;
	public static List<Integer> connections = new ArrayList<Integer>();
	public HashMap<Integer, Player> ships = new HashMap<Integer, Player>();

	public static Player getShip(int id, HashMap<Integer, Player> s) {
		return s.get(id);
	}
	
	public static void loadHeadless() {
        LwjglNativesLoader.load();
		GdxNativesLoader.load();
        Gdx.files = new LwjglFiles();
    }

	public static void main(String[] args) throws IOException {
		//Create Gdx.gl to use textures
	    Gdx.gl = mock(GL20.class);
	    
	    loadHeadless();
		
		AssetManager manager = new AssetManager();
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		manager.load("maps/map.tmx", TiledMap.class);
		
		manager.finishLoading();
		
		world = new World(new Vector2(0,0), false);
		map = manager.get("maps/map.tmx");
		
		collisionLayers = (MapLayers) map.getLayers();

		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);

		System.out.println("Starting server...");
		
		group = new Group();
		
		GameWorld gameWorld = new GameWorld("maps/map.tmx");
		
		t = new ServerTick();
	    headless = new HeadlessApplication(t);
		
		for(Iterator<Actor> i = gameWorld.getActors().iterator(); i.hasNext(); ) {
			try {
				NpcController n = (NpcController) i.next();
				group.addActor(n);
			} catch(Exception e) {
				
			}
		}

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
		
		Kryo kryo = server.getKryo();
		kryo.setRegistrationRequired(false);
		
		server.addListener(new Listener() {

			public void received(Connection connection, Object object) {
				if (object instanceof PlayerData) {
					PlayerData info = ((PlayerData) object);

					System.out.println(connection.getID());
					System.out.println(info.pos);

					if (getShip(connection.getID(), UpdateConnections.ccr.players) != null) {
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
		
		
		
    	// And From your main() method or any other method
    	//Timer timer = new Timer();
    	//timer.schedule(new ServerTick(), 0, 1);

	}
	
	public static void updateActor(int id) {
		Actor a = group.getChildren().items[id];
		if (a==null)
			return;
		NpcMovePacket n = new NpcMovePacket(a.getX(), a.getY(), id, a.getName());
		server.sendToAllTCP(n);
	}
	
	public boolean isCellBlocked(float x, float y) {
		boolean blocked = false;
		for (int i=0;i<3;i++) {
			TiledMapTileLayer coll = (TiledMapTileLayer) collisionLayers.get(i);
			Cell cell = coll.getCell((int) (x), (int) (y));
			blocked = cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
			if (blocked == true)
				return true;
		}
		return blocked;
	}
}
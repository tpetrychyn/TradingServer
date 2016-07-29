package server;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 
	
	public static World world;
	public static TiledMap map;
	public static MapLayers collisionLayers;
	public static Group group;
	static HeadlessApplication headless;
	static GameServer t;
	
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
		manager.load("assets/maps/map.tmx", TiledMap.class);
		
		manager.finishLoading();
		
		world = new World(new Vector2(0,0), false);
		map = manager.get("assets/maps/map.tmx");
		
		collisionLayers = (MapLayers) map.getLayers();

		
		//Scanner s = new Scanner(System.in);

		System.out.println("Starting server...");
		
		group = new Group();
		
		Instance gameWorld = new Instance("assets/maps/map.tmx");
		
		t = new GameServer();
	    headless = new HeadlessApplication(t);
		
		for(Iterator<Actor> i = gameWorld.getActors().iterator(); i.hasNext(); ) {
			try {
				NpcController n = (NpcController) i.next();
				group.addActor(n);
			} catch(Exception e) {
				
			}
		}
		
    	// And From your main() method or any other method
    	//Timer timer = new Timer();
    	//timer.schedule(new ServerTick(), 0, 1);

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
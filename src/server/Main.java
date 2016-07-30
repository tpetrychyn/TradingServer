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

public class Main {
 
	public static Group group;
	static HeadlessApplication headless;
	static GameServer gameServer;
	
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
		manager.load("Maps/map.tmx", TiledMap.class);
		manager.load("Maps/house.tmx", TiledMap.class);
		
		manager.finishLoading();

		System.out.println("Starting server...");
		
		gameServer = new GameServer();
	    headless = new HeadlessApplication(gameServer);

	}
}
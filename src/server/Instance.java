package server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trading.entities.Tree;
import com.trading.entities.WorldObjects;
import com.trading.game.Game;
import com.trading.networking.packets.NpcMovePacket;

import entities.WorldActor;
import util.Util;

public class Instance {
	
	public int id;
	public String name;
	public World world;
	public TiledMap map;
	public int worldWidth;
	public int worldHeight;
	public MapLayers collisionLayers;
	public HashMap<Integer, Actor> actors = new HashMap<Integer, Actor>();
	public HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	public HashMap<Integer, WorldActor> worldObjects = new HashMap<Integer, WorldActor>();
	private int totalLayers = 0;
	
	private static String[] Beginning = { "Kr", "Ca", "Ra", "Mrok", "Cru",
	         "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
	         "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
	         "Mar", "Luk" };
	   private static String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
	         "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
	         "marac", "zoir", "slamar", "salmar", "urak" };
	   private static String[] End = { "d", "ed", "ark", "arc", "es", "er", "der",
	         "tron", "med", "ure", "zur", "cred", "mur" };
	   
	   private static Random rand = new Random();

	   public String generateName() {
	      return Beginning[rand.nextInt(Beginning.length)] + 
	            Middle[rand.nextInt(Middle.length)]+
	            End[rand.nextInt(End.length)];
	   }
	
	public Instance(String mapFile) {
		map = new TmxMapLoader().load("Maps/" + mapFile);
		MapProperties prop = map.getProperties();
		worldWidth = prop.get("width", Integer.class);
		worldHeight = prop.get("width", Integer.class);
		world = new World(new Vector2(0, 0), true);
		this.collisionLayers = (MapLayers) getTiledMap().getLayers();
		
		//parse json file containing each maps info to get layers and total layers used for rendering and collision
		JSONObject obj;
		try {
			obj = new JSONObject(Gdx.files.internal("Maps/Maps.json").readString());
			JSONArray arr = obj.getJSONArray("maps");
			for (int i = 0; i < arr.length(); i++)
			{
				if (arr.getJSONObject(i).getString("file").equals(mapFile)) {
					JSONArray bg = arr.getJSONObject(i).getJSONArray("backgroundLayers");
					for (int x=0;x<bg.length();x++) {
						totalLayers++;
					}
					
					JSONArray fg = arr.getJSONObject(i).getJSONArray("foregroundLayers");
					for (int x=0;x<fg.length();x++) {
						totalLayers++;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}
	
	public void addPlayer(Player p) {
		players.put(p.id, p);
	}
	
	public HashMap<Integer, Player> getPlayers() {
		return players;
	}
	
	public HashMap<Integer, Actor> getActors() {
		return actors;
	}
	
	public World getWorld() {
		return world;
	}
	
	public TiledMap getTiledMap() {
		return map;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		world.dispose();
		map.dispose();
	}
	
	public void setWorldPosition(Actor p, Vector2 pos) {
		p.setX((pos.x + ((pos.y-pos.x)/2)) * 64);
		p.setY((pos.y - pos.x) * 16);
    }
	
	public boolean isCellBlocked(float x, float y) {
		boolean blocked = false;
		for (int i=0;i<totalLayers;i++) {
			TiledMapTileLayer coll = (TiledMapTileLayer) collisionLayers.get(i);
			Cell cell = coll.getCell((int) (x), (int) (y));
			blocked = cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
			if (blocked == true)
				return true;
		}
		return blocked;
	}
	
	Vector2 getTileCoordinates(Vector2 pt, float tileHeight) {
		  Vector2 tempPt = new Vector2(0, 0);
		  tempPt.x = pt.x / tileHeight / 2;
		  tempPt.y = pt.y / tileHeight;
		  return(tempPt);
	}
	
    public Vector2 getWorldPosition(Vector2 pt) {
    	return Util.twoDToIso(getTileCoordinates(pt, 32));
    }
	
    public boolean actorCollision(Actor self) {
    	for (int key: actors.keySet()) {
    		Actor a = actors.get(key);
 		    if (a.hashCode() == self.hashCode())
 		    	continue;
 			Rectangle p = new Rectangle(self.getX(), self.getY(), self.getWidth(), self.getHeight());
 			Rectangle n = new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
 			if (Intersector.overlaps(p, n)) {
 				return true;
 			}
        }
    	for (int key: players.keySet()) {
    		Player a = players.get(key);
    		
 		    if (a.hashCode() == self.hashCode())
 		    	continue;
 			Rectangle p = new Rectangle(self.getX(), self.getY(), self.getWidth(), self.getHeight());
 			Rectangle n = new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
 			if (Intersector.overlaps(p, n)) {
 				return true;
 			}
        }
    	for (int key: worldObjects.keySet()) {
    		WorldActor a = worldObjects.get(key);
 		    if (a.hashCode() == self.hashCode())
 		    	continue;
 			Rectangle p = new Rectangle(self.getX(), self.getY(), self.getWidth(), self.getHeight());
 			Rectangle n = new Rectangle(a.realX, a.realY, a.realWidth, a.realHeight);
 			if (Intersector.overlaps(p, n)) {
 				return true;
 			}
        }
		return false;
	}
	
	public void updateActor(int id) {
		Actor a = getActors().get(id);
		if (a==null)
			return;
		NpcMovePacket n = new NpcMovePacket(a.getX(), a.getY(), id, a.getName());
		n.direction = ((NpcController) a).direction;
		Iterator<Entry<Integer, Player>> iterator = players.entrySet().iterator();
		while(iterator.hasNext()){
            HashMap.Entry<Integer, Player> player = iterator.next();
			GameServer.server.sendToUDP(player.getValue().id, n);
        }
	}
}

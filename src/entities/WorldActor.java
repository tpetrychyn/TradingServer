package entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import server.Instance;

public class WorldActor extends Actor {

	Instance instance;
	public String type;
	public int typeId;
	public int id;
	
	public float realX;
	public float realY;
	public float realWidth;
	public float realHeight;
	
	public boolean isMoving = false;
	
	public WorldActor() {
	}
	
	public WorldActor(float x, float y, Instance instance, int id, float scale) {
		this.id = id;
		this.instance = instance;
		instance.setWorldPosition(this, new Vector2(x,y));
        
        setScale(scale);
	}
}

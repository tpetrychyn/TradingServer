package server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import util.Direction;
import util.Util;

public class NpcController extends Actor {
	
	Sprite sprite;
	Instance instance;
	Vector2 velocity = new Vector2();
	Animation[] walkAnimations;
	Animation walk;
	Task randomWalk;
	Vector2 minBounds;
	Vector2 maxBounds;
	Direction direction = Direction.SOUTH;
	public int id;
	float stateTime;

	public NpcController(Instance world) {
		//super(world);
		// TODO Auto-generated constructor stub
	}
	
	public NpcController(float x, float y, Instance instance, int id, float scale) {
		//super(image, x, y, world, id, scale);
		this.instance = instance;
		instance.setWorldPosition(this, new Vector2(x, y));
		setWidth(24);
		setHeight(38);
		this.id = id;
	}
	float lastUpdate = 0;
	
	public void act(float alpha) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		lastUpdate += deltaTime;
		
		Vector2 oldPos = new Vector2(getX(), getY());
        setX(getX() + velocity.x * deltaTime);
        setY(getY() + velocity.y * deltaTime);
        
        Vector2 newPos = new Vector2(getX(), getY());
        if (Math.abs(velocity.x) > 0 || Math.abs(velocity.y) > 0) {
	        if (instance.getWorldPosition(newPos).x < 0 || instance.getWorldPosition(newPos).y < 0.2
	        		|| instance.getWorldPosition(newPos).x > 99.8 || instance.getWorldPosition(newPos).y > 100
	        		|| instance.isCellBlocked(instance.getWorldPosition(newPos).x, instance.getWorldPosition(newPos).y)
	        		|| instance.actorCollision(this)){
	        	setY(oldPos.y);
	        	setX(oldPos.x);
	        	velocity.x = 0f;
	    		velocity.y = 0f;
	        } 
        }
        
        if (maxBounds != null && minBounds != null) {
        	if (instance.getWorldPosition(newPos).x < minBounds.x || instance.getWorldPosition(newPos).y < minBounds.y
        			|| instance.getWorldPosition(newPos).x > maxBounds.x || instance.getWorldPosition(newPos).y > maxBounds.y) {
        		setX(oldPos.x);
        		setY(oldPos.y);
        		velocity.x = 0f;
        		velocity.y = 0f;
        	}		
        }
        
        if (getX() != oldPos.x || getY() != oldPos.y)
        	instance.updateActor(id);
	}
	
	public void setBounds(int minX, int minY, int maxX, int maxY) {
		minBounds = new Vector2(minX, minY);
		maxBounds = new Vector2(maxX, maxY);
	}
	
	void randomWalk() {
		int randomWalk = Util.randomRange(0, 3);
		switch (randomWalk) {
		case 0:
			velocity.x = 20f;
			velocity.y = 0f;
			direction = Direction.EAST;
			break;
		case 1:
			velocity.x = -20f;
			velocity.y = 0f;
			direction = Direction.WEST;
			break;
		case 2:
			velocity.y = 20f;
			velocity.x = 0f;
			direction = Direction.NORTH;
			break;
		case 3: 
			velocity.y = -20f;
			velocity.x = 0f;
			direction = Direction.SOUTH;
			break;
		default:
			velocity.x = 0;
			velocity.y = 0;
			break;
		}
	}
	
	public void startRandomWalk(int delay) {
		randomWalk = new Timer().scheduleTask(new Task(){
		    @Override
		    public void run() {
		        randomWalk();
		    }
		}, 0, Util.randomRange(1, delay));
	}
	
	public void stopRandomWalk() {
		randomWalk.cancel();
		velocity.x = 0;
		velocity.y = 0;
	}

}

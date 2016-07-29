package server;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import util.Direction;

public class Player extends Actor  {
	
	public Direction direction = Direction.SOUTH;
    float playerSpeed = 200f;
    public boolean isMoving = false;
    public Sprite sprite;
    public Instance instance;
	public int id = 0;
    
    float stateTime = 0;
    BitmapFont font;
    
    public float getSpeed() {
    	return playerSpeed;
    }
    
    public void setSpeed(float speed) {
    	playerSpeed = speed;
    }
    
    public Vector2 getPosition() {
    	Vector2 pos = new Vector2(getX(), getY());
		return pos;
    }
    
    public Vector2 getWorldPosition() {
    	return instance.getWorldPosition(getPosition());
    }
    
    public void setWorldPosition(Vector2 pos) {
    	setX((pos.x + ((pos.y-pos.x)/2)) * 64);
		setY((pos.y - pos.x) * 16);
    }
    
    public void setDirection(Direction dir) {
    	direction = dir;
    }
    
    public Direction getDirection() {
    	return direction;
    }
    
    public void setPosition(Vector2 transform) {
    	setX(transform.x);
    	setY(transform.y);
    }
	
	public Player(Instance instance) {
		this.instance = instance;
		setWidth(24);
		setHeight(38);
	}
	
}

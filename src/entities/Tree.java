package entities;

import com.badlogic.gdx.math.Vector2;
import entities.WorldObjects.TreePrefab;
import server.Instance;

public class Tree extends WorldActor {
	
	Instance instance;

	public Tree() {
		
	}
	
	public Tree(float x, float y, Instance instance, TreePrefab treePrefab) {
		this.instance = instance;
		instance.setWorldPosition(this, new Vector2(x,y));
		
		setWidth(treePrefab.width * treePrefab.scale);
		setHeight(treePrefab.height * treePrefab.scale);
		
		setScale(treePrefab.scale);
		setOrigin(getWidth()/2, getHeight()/2);
		
		realX = getX() +  (treePrefab.offsetX * treePrefab.scale);
		realY = getY() + (treePrefab.offsetY * treePrefab.scale);
		realWidth = (treePrefab.width * treePrefab.scale);
		realHeight = (treePrefab.height * treePrefab.scale);
		typeId = treePrefab.id;
		type = "tree";
	}
}

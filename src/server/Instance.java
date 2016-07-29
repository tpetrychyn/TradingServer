package server;

public class Instance extends GameWorld {
	
	public int id;
	public int name;
	public int width;
	public int height;
	
	public Instance(String mapFile) {
		super(mapFile);
		this.id = -1;
	}
	
	public Instance(String mapFile, int id) {
		super(mapFile);
		this.id = id;
	}
}

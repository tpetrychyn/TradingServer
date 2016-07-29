package server;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

class ServerTick implements ApplicationListener{

		@Override
		public void create() {
			// TODO Auto-generated method stub
			//System.out.println(Gdx.graphics.getDeltaTime());
			
		}

		@Override
		public void resize(int width, int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void render() {
			// TODO Auto-generated method stub
			Main.group.act(Gdx.graphics.getDeltaTime());
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
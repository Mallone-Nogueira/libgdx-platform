package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameInit extends Game {
	public static final int V_WIDTH = 800;
	public static final int V_HEIGHT = 600;
	public SpriteBatch batch;
	private GameScreen screen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		screen = new GameScreen(this);
		setScreen(screen);
	}
	public void update() {
		screen.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void render () {
		this.update();
		Gdx.gl.glClearColor(1f, 0.5f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}

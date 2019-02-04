package com.mygdx.game.world;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.Entity;

public class GameMap {

	String id;
	String name;
	int[][][] map;
	private TextureRegion[][] tiles;

	protected ArrayList<Entity> entities;

	public GameMap(GameMapData data) {
		entities = new ArrayList<Entity>();
		// CustomGameMapData data = CustomGameMapLoader.loadMap("basic", "My Grass
		// Lands!");
//		GameMapData data = new GameMapData();
		this.id = data.id;
		this.name = data.name;
		this.map = data.map;

		tiles = TextureRegion.split(new Texture("tiles.png"), TileType.TILE_SIZE, TileType.TILE_SIZE);
	}

	public void render(OrthographicCamera camera, SpriteBatch batch) {

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (int layer = 0; layer < getLayers(); layer++) {
			for (int row = 0; row < getHeight(); row++) {
				for (int col = 0; col < getWidth(); col++) {
					TileType type = this.getTileTypeByCoordinate(layer, col, row);
					if (type != null)
						batch.draw(tiles[0][type.getId() - 1], col * TileType.TILE_SIZE, row * TileType.TILE_SIZE);
				}
			}
		}

		for (Entity entity : entities) {
			entity.render(batch);
		}
		batch.end();
	}

	public void update(float delta) {
		for (Entity entity : entities) {
			entity.update(delta, -9.8f);
		}

		// if (Gdx.input.isKeyJustPressed(Keys.S)) {
		// EntityLoader.saveEntities("basic", entities);
		// }
	}

	public boolean doesRectCollideWithMap(float x, float y, int width, int height) {
		if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight())
			return true;

		for (int row = (int) (y / TileType.TILE_SIZE); row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
			for (int col = (int) (x / TileType.TILE_SIZE); col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
				for (int layer = 0; layer < getLayers(); layer++) {
					TileType type = getTileTypeByCoordinate(layer, col, row);
					if (type != null && type.isCollidable())
						return true;
				}
			}
		}

		return false;
	}



	public void dispose() {
	}

	public TileType getTileTypeByLocation(int layer, float x, float y) {
		return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE),
				getHeight() - (int) (y / TileType.TILE_SIZE) - 1);
	}

	public TileType getTileTypeByCoordinate(int layer, int col, int row) {
		if (col < 0 || col >= getWidth() || row < 0 || row >= getHeight())
			return null;

		return TileType.getTileTypeById(map[layer][getHeight() - row - 1][col]);
	}

	public int getWidth() {
		return map[0][0].length;
	}

	public int getHeight() {
		return map[0].length;
	}

	public int getLayers() {
		return map.length;
	}

	public int getPixelWidth() {
		return this.getWidth() * TileType.TILE_SIZE;
	}

	public int getPixelHeight() {
		return this.getHeight() * TileType.TILE_SIZE;
	}

}

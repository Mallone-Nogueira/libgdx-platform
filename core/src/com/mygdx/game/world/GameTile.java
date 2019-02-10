package com.mygdx.game.world;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public enum GameTile {

	AIR(0, false, null),
	GRASS(1, true, "Tiles/2.png"),
	DIRT(2, true, "Tiles/5.png"),
	STONE(3, true, null),
	;

	private int id;
	private boolean collidable;
	private Texture texture;

	private GameTile (int id, boolean collidable, String texture) {
		this.id = id;
		this.collidable = collidable;

		if (texture != null) {
			this.texture = new Texture(Gdx.files.internal(texture));
		}
	}

	public int getId() {
		return id;
	}

	public boolean isCollidable() {
		return collidable;
	}

	public Texture getTexture() {
		return texture;
	}

	public boolean hasTexture() {
		return texture != null;
	}

	public boolean hasTextureOrIsCollidable() {
		return hasTexture() || isCollidable();
	}

	private static HashMap<Integer, GameTile> tileMap;

	static {
		tileMap = new HashMap<Integer, GameTile>();
		for (GameTile tileType : GameTile.values()) {
			tileMap.put(tileType.getId(), tileType);
		}
	}

	public static GameTile getTileTypeById (int id) {
		return tileMap.get(id);
	}

}

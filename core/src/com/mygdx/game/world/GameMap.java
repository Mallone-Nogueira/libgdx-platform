package com.mygdx.game.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.mygdx.game.Constants;

public class GameMap {

	private int[][] mapa;
	private Texture cima;
	private Texture baixo;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private GameMapBodyCreate bodyCreate;

	/**
	 * Objetos para reaproveitar body
	 */
	private ObjectMap<String, Body> collisions1 = new ObjectMap<>();
	private ObjectMap<String, Body> collisions2 = new ObjectMap<>();
	private List<Body> instancias = new ArrayList<>();
	private int collision = 1;

	public GameMap(int[][] mapa, SpriteBatch batch, World world, OrthographicCamera camera) {
		this.mapa = mapa;
		this.batch = batch;
		this.camera = camera;

		this.cima = new Texture(Gdx.files.internal("Tiles/2.png"));
		this.baixo = new Texture(Gdx.files.internal("Tiles/5.png"));
		bodyCreate = new GameMapBodyCreate(world);
	}

	public void update(float delta) {
		collision = (collision + 1) % 2;
	}

	public void render(Vector2... entitesPosition) {
		int xMatriz = getPosicaoInicialMatriz(camera.position.x - camera.viewportWidth / 2) - 1;
		int yMatriz = getPosicaoInicialMatriz(camera.position.y - camera.viewportHeight / 2) - 1;

		if (yMatriz < 0) {
			yMatriz = 0;
		}
		if (xMatriz < 0) {
			xMatriz = 0;
		}

		int xTamanho = dividirSeNaoForZero(camera.viewportWidth, Constants.PPM) + 5;
		int yTamanho = dividirSeNaoForZero(camera.viewportHeight, Constants.PPM) + 5;

		for (int x = 0; x < xTamanho; xMatriz++, x++) {
			int yMatrizTemp = yMatriz;
			for (int y = 0; y < yTamanho; yMatrizTemp++, y++) {
				GameTile gameTile = GameTile.getTileTypeById(mapa[xMatriz][yMatrizTemp]);

				if (gameTile.hasTextureOrIsCollidable()) {
					Body body = addBody(xMatriz, yMatrizTemp, gameTile, entitesPosition);

					if (gameTile.hasTexture()) {
						float yPos = getDrawPosition(body.getPosition().y, camera.viewportHeight, camera.position.y);
						float xPos = getDrawPosition(body.getPosition().x, camera.viewportWidth, camera.position.x);

						batch.draw(gameTile.getTexture(), xPos, yPos, Constants.PPM, Constants.PPM);
					}
				}
			}
		}
	}

	private float getDrawPosition(float bodyPosition, float viewportSize, float cameraPostion) {
		return ((((bodyPosition * Constants.PPM) - cameraPostion) * 2) + viewportSize) / 2
				- Constants.PPM/2f;
	}

	private Body addBody(int x, int y, GameTile gameTile, Vector2... entitiesPosition) {
		String key = x + "/" + y;
		ObjectMap<String, Body> mapAdd = getMapAdd();
		ObjectMap<String, Body> mapRemove = getMapRemove();

		Body body = mapRemove.remove(key);

		if (body == null) {
			if (instancias.isEmpty()) {
				body = bodyCreate.create(x, y);
			} else {
				body = instancias.remove(0);
				body.setTransform(x, y, body.getAngle());
			}

			mapAdd.put(key, body);
		}

		if (!mapRemove.isEmpty()) {
			Values<Body> values = mapRemove.values();
			while (values.hasNext()) {
				instancias.add(values.next());
			}
			mapRemove.clear();
		}

		body.setActive(gameTile.isCollidable() && entityIsClose(body, entitiesPosition));

		return body;
	}

	public int blockOnPosition(Vector2 position) {
		return mapa[(int)position.x][(int)position.y];
	}

	private boolean entityIsClose(Body body, Vector2... entitiesPosition) {
		if(entitiesPosition != null) {
			for (Vector2 position : entitiesPosition) {
				if ((body.getPosition().x < position.x + 3 && body.getPosition().x > position.x - 3)
						&& (body.getPosition().y < position.y + 5 && body.getPosition().y > position.y - 3)) {
					return true;
				}
			}
		}

		return false;
	}

	private ObjectMap<String, Body> getMapAdd() {
		return collision == 1 ? collisions1 : collisions2;
	}

	private ObjectMap<String, Body> getMapRemove() {
		return collision == 1 ? collisions2 : collisions1;
	}

	private int dividirSeNaoForZero(float div, float zoom) {
		return Math.round(zoom == 0 ? div : div / zoom);
	}

	public int getPosicaoInicialMatriz(float camera) {
		return Math.round(camera / Constants.PPM);
	}

	public void getAddTileOnPositionOfCamera(float x, float y, GameTile tile) {
		int xCamera = getPosicaoInicialMatriz(camera.position.x - camera.viewportWidth / 2) - 1;
		int yCamera = getPosicaoInicialMatriz(camera.position.y - camera.viewportHeight / 2) - 1;
		mapa[Math.round((xCamera+x))][Math.round(yCamera+y)] = tile.getId();
	}

	public void dispose() {
		cima.dispose();
		baixo.dispose();
	}
}

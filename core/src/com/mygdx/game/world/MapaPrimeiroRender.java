package com.mygdx.game.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

public class MapaPrimeiroRender {

	public int[][] mapa;
	private static final int BLOCO = TileType.TILE_SIZE;
	private Texture cima;
	private Texture baixo;
	private SpriteBatch batch;
	private World world;

	private ObjectMap<String, Body> collisions1 = new ObjectMap<>();
	private ObjectMap<String, Body> collisions2 = new ObjectMap<>();
	private int collision = 1;
	private List<Body> instancias = new ArrayList<>();
	private OrthographicCamera camera;

	public MapaPrimeiroRender(int[][] mapa, SpriteBatch batch, World world, OrthographicCamera camera) {
		this.mapa = mapa;
		this.batch = batch;
		this.world = world;
		this.camera = camera;

		this.cima = new Texture(Gdx.files.internal("Tiles/2.png"));
		this.baixo = new Texture(Gdx.files.internal("Tiles/5.png"));
	}

	public void render(Vector2 playerPosition) {
		collision = (collision + 1) % 2;
		int xMatriz = getPosicaoInicialMatriz(camera.position.x - camera.viewportWidth / 2) - 1;
		int yMatriz = getPosicaoInicialMatriz(camera.position.y - camera.viewportHeight / 2) - 1;

		if (yMatriz < 0) {
			yMatriz = 0;
		}
		if (xMatriz < 0) {
			xMatriz = 0;
		}

		int xTamanho = dividirSeNaoForZero(camera.viewportWidth, BLOCO) + 5;
		int yTamanho = dividirSeNaoForZero(camera.viewportHeight, BLOCO) + 5;

		for (int x = 0; x < xTamanho; xMatriz++, x++) {
			int yMatrizTemp = yMatriz;
			for (int y = 0; y < yTamanho; yMatrizTemp++, y++) {
				int map = mapa[xMatriz][yMatrizTemp];
				if (map != 0) {
					Body body = addBody(xMatriz, yMatrizTemp, playerPosition);

					float xPos = ((((body.getPosition().x * 8) - camera.position.x) * 2) + camera.viewportWidth) / 2
							- 4;
					float yPos = ((((body.getPosition().y * 8) - camera.position.y) * 2) + camera.viewportHeight) / 2
							- 4;

					if (map == 1) {
						batch.draw(cima, xPos, yPos, BLOCO, BLOCO);
					} else if (map == 2) {
						batch.draw(baixo, xPos, yPos, BLOCO, BLOCO);
					}
				}
			}
		}
	}

	private Body addBody(int x, int y, Vector2 playerPosition) {
		String key = x + "/" + y;
		ObjectMap<String, Body> mapAdd = getMapAdd();
		ObjectMap<String, Body> mapRemove = getMapRemove();

		Body body = mapRemove.remove(key);

		if (body == null) {
			if (instancias.isEmpty()) {
				body = createBox(x, y, BLOCO, BLOCO, true);
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

		body.setActive(bodyIsPerto(playerPosition, body));

		return body;
	}

	private boolean bodyIsPerto(Vector2 playerPosition, Body body) {
		return (body.getPosition().x < playerPosition.x + 2 && body.getPosition().x > playerPosition.x - 2)
				&& (body.getPosition().y < playerPosition.y + 4 && body.getPosition().y > playerPosition.y - 2);
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
		return Math.round(camera / BLOCO);
	}

	public void dispose() {
		cima.dispose();
		baixo.dispose();
	}

	static PolygonShape shape;
	static {
		shape = new PolygonShape();
		shape.setAsBox(BLOCO / 2f / BLOCO, BLOCO / 2f / BLOCO);
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic) {
		BodyDef def = new BodyDef();

		if (isStatic)
			def.type = BodyDef.BodyType.StaticBody;
		else
			def.type = BodyDef.BodyType.DynamicBody;

		def.position.set(x, y);
		def.fixedRotation = true;

		Body pBody = world.createBody(def);
		pBody.createFixture(shape, 0);
		pBody.setAwake(false);
		return pBody;
	}
}

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.entities.Player;
import com.mygdx.game.scene.Hud;
import com.mygdx.game.world.MapaPrimeiroRender;
import com.mygdx.game.world.generate.GameMapGenerator;

public class GameScreen implements Screen {
	private static final int BLOCO = 8;
	private GameInit game;
	private OrthographicCamera camera;
    private Box2DDebugRenderer b2dr;
	private Viewport gamePort;
	private Hud hud;

	private World world;
	private Player player;
	private MapaPrimeiroRender mapaRender;
	private boolean DEBUG = false;

	public GameScreen(GameInit game) {
		this.game = game;
		this.camera = new OrthographicCamera(GameInit.V_WIDTH, GameInit.V_HEIGHT);
		this.gamePort = new FitViewport(GameInit.V_WIDTH, GameInit.V_HEIGHT, camera);
		this.hud = new Hud(game.batch);


		this.world = new World(new Vector2(0, -9.8f), false);
		System.out.println("TAMANHO: "+ world.getContactList().size);
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub

			}
		});
		this.player = new Player(world, game.batch);
		camera.position.set((player.getPosition().x*BLOCO - camera.viewportWidth / 2),(player.getPosition().y*BLOCO - camera.viewportHeight / 2), 0);

		this.b2dr = new Box2DDebugRenderer();
        this.mapaRender = new MapaPrimeiroRender(new GameMapGenerator(6).generate(), game.batch, world, camera);
	}


	@Override
	public void show() {

	}

	public void cameraUpdate(float delta) {
		camera.position.lerp(new Vector3(player.getPosition().x*BLOCO, player.getPosition().y*BLOCO, 0), 0.15f);

		if (camera.position.y < 0) {
			camera.position.y = 0;
		}

		if (camera.position.x < 0) {
			camera.position.x = 0;
		}

		if (camera.position.y > 7500) {
			camera.position.y = 7500;
		}

		if (camera.position.x > 7500) {
			camera.position.x = 7500;
		}

        camera.update();
    }


	public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate(delta);


        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        	DEBUG = !DEBUG;
        }
	}

	@Override
	public void render(float delta) {
		game.batch.begin();

		mapaRender.render(player.getPosition());
		player.render(camera, mapaRender.mapa);

		if (DEBUG) {
			b2dr.render(world, camera.combined.scl(BLOCO));
		}

		game.batch.end();

		hud.render(getPosicaoInicialMatriz(camera.position.x) - 1);
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

		hud.stage.draw();
	}

	public int getPosicaoInicialMatriz(float camera) {
		return Math.round(camera/BLOCO);
	}



	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
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
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		hud.dispose();
        world.dispose();
        mapaRender.dispose();
	}


}

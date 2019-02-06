package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.entities.Monster;
import com.mygdx.game.entities.Player;
import com.mygdx.game.scene.Hud;
import com.mygdx.game.world.MapaRender;
import com.mygdx.game.world.generate.GameMapGenerator;

public class GameScreen implements Screen {
	private OrthographicCamera camera;
    private Box2DDebugRenderer b2dr;
	private Viewport gamePort;
	private World world;
	private Hud hud;

	private GameInit game;
	private Player player;
	private Monster monster;
	private MapaRender mapaRender;
	private int[][] mapa;

	private boolean debug = false;

	public GameScreen(GameInit game) {
		this.game = game;
		this.camera = new OrthographicCamera(GameInit.V_WIDTH, GameInit.V_HEIGHT);
		this.gamePort = new FitViewport(GameInit.V_WIDTH, GameInit.V_HEIGHT, camera);
		this.hud = new Hud(game.batch);
		this.world = new World(new Vector2(0, -9.8f), false);
		this.player = new Player(world, game.batch);
		this.b2dr = new Box2DDebugRenderer();
        this.mapa = new GameMapGenerator(6).generate();
		this.mapaRender = new MapaRender(mapa, game.batch, world, camera);
		this.monster = new Monster(world, game.batch);

		world.setContactFilter(new ContactFilter() {

			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				if (fixtureA.getUserData() instanceof Player && fixtureB.getUserData() instanceof Monster) {
					return false;
				}
				return true;
			}
		});
//		world.setContactListener(new ContactListener() {
//			@Override
//			public void preSolve(Contact contact, Manifold oldManifold) {
//				System.out.println("PRE");
//
//			}
//
//			@Override
//			public void postSolve(Contact contact, ContactImpulse impulse) {
//				System.out.println("POST");
//
//			}
//
//			@Override
//			public void endContact(Contact contact) {
//				System.out.println("END");
//
//			}
//
//			@Override
//			public void beginContact(Contact contact) {
//				System.out.println("BEGIN");
//
//			}
//		});
	}


	@Override
	public void show() {

	}

	public void cameraUpdate(float delta) {
		camera.position.lerp(new Vector3(player.getPosition().x*Constants.PPM, player.getPosition().y*Constants.PPM, 0), 0.15f);

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
        player.update(delta);
        monster.update(delta, player.getPosition());
        mapaRender.update(delta);


        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        	debug = !debug;
        }
	}

	@Override
	public void render(float delta) {
		game.batch.begin();

		mapaRender.render(player.getPosition(), monster.getPosition());
		player.render(camera);
		monster.render(camera);

		if (debug) {
			b2dr.render(world, camera.combined.scl(Constants.PPM));
		}

		game.batch.end();

		hud.render("");
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

		hud.stage.draw();
	}

	public int getPosicaoInicialMatriz(float camera) {
		return Math.round(camera/Constants.PPM);
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
        b2dr.dispose();
        player.dispose();
	}

}

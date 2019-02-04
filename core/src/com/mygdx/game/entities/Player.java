package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
	int BLOCO = 8;

    Body body;
    Texture img;
    Sprite sprite;
    Fixture fixture;

	private Batch batch;
	private World world;

	public Player(World world, Batch batch) {
		super();
		this.world = world;
		this.batch = batch;

		img = new Texture("carinha2.jpg");
        sprite = new Sprite(img);

        body = createBodyPlayer(1600, 6670, BLOCO*1.5f, BLOCO*2.5f);
	}

	 public Body createBodyPlayer(int x, int y, float width, float height) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / BLOCO, y / BLOCO);
        def.fixedRotation = true;
        def.gravityScale = 5.0f;
        pBody = world.createBody(def);

        PolygonShape shape1 = new PolygonShape();
//        shape2.setAsBox(width / 2f / BLOCO, height / 2f / BLOCO);
        shape1.setAsBox(width / 2f / BLOCO, height / 2f / BLOCO, new Vector2(0, 1.5f) , 0);
        System.out.println(height / 2f / BLOCO);
        CircleShape shape2 = new CircleShape();
        shape2.setRadius(0.3f);
        shape2.setPosition(new Vector2(-0.5f, -0.1f));
        CircleShape shape3 = new CircleShape();
        shape3.setRadius(0.3f);
        shape3.setPosition(new Vector2(0.5f, -0.1f));

        pBody.createFixture(shape1, 0.5f);
        pBody.createFixture(shape2, 0.5f);
        pBody.createFixture(shape3, 0.5f);
        shape1.dispose();
        shape2.dispose();
        shape3.dispose();
        return pBody;
    }

	public void render(OrthographicCamera camera, int[][] mapa) {
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			sprite.setFlip(false, false);
			body.applyForceToCenter(1500, 0, true);
			if (body.getLinearVelocity().x > 15) {
				body.setLinearVelocity(15, body.getLinearVelocity().y);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			sprite.setFlip(true, false);
			body.applyForceToCenter(-1500, 0, true);
			if (body.getLinearVelocity().x < -15) {
				body.setLinearVelocity(-15, body.getLinearVelocity().y);
			}
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
//            body.applyForceToCenter(0, 3000, false);
			body.setLinearVelocity(body.getLinearVelocity().x, 25);
        }

		sprite.setPosition((body.getPosition().x * BLOCO) + (camera.viewportWidth / 2) - camera.position.x - 6,
				(body.getPosition().y * BLOCO) + (camera.viewportHeight / 2) - camera.position.y - 3);
		batch.draw(sprite, sprite.getX(), sprite.getY());

	}



	private void chaoColisao(int[][] mapa) {
		int x = getPosicaoInicialMatriz(this.getPosition().x);
		int y = getPosicaoInicialMatriz(this.getPosition().y);

		if (mapa[x][y] == 1 && (y+1)*BLOCO > this.getPosition().y) {
			subirPosicao(mapa, x, y);
		} else {
//			setChao(false);
		}
	}

	public void subirPosicao(int[][] mapa, int x, int y) {
		int alturaDoBloco = (y+1)*BLOCO;
		if(mapa[x][y+1] == 1  && alturaDoBloco > this.getPosition().y) {
			subirPosicao(mapa, x, y+1);
		}else {
			body.setTransform(this.getPosition().x, alturaDoBloco+1, 0);
			body.setLinearVelocity(this.body.getLinearVelocity().x, 0f);
		}
	}

	public int getPosicaoInicialMatriz(float camera) {
		return Math.round(camera/BLOCO);
	}

	public boolean keyDown(int keycode) {


//        if(keycode == Input.Keys.SPACE) {
//            body.setLinearVelocity(0f, 0f);
//            body.setAngularVelocity(0f);
//            torque = 0f;
//            sprite.setPosition(0f,0f);
//            body.setTransform(0f,0f,0f);
//        }


		return true;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

    public void dispose() {
        img.dispose();
    }
}

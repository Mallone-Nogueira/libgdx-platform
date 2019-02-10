package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.entities.Direcao;
import com.mygdx.game.entities.Monster;
import com.mygdx.game.entities.Player;

public class GameContactHandler implements ContactListener {
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

		if(contactCall(contact, this::playerSemColisaoComMonster )) return;
		if(contactCall(contact, this::blocoNaFrente              )) return;
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getUserData() instanceof Player
				&& contact.getFixtureB().getUserData() instanceof Monster) {

			// System.out.println("DANO");
		}
	}

	public boolean contactCall(Contact contact, TriConsumer<Fixture, Fixture, Contact> consumer) {
		if(consumer.execute(contact.getFixtureA(), contact.getFixtureB(), contact)) {
			return true;
		}

		if (consumer.execute(contact.getFixtureB(), contact.getFixtureA(), contact)) {
			return true;
		}

		return false;
	}

	public interface TriConsumer<A, B, C> {
		boolean execute(A a, B b, C c);
	}

	/*
	 *   FUNCOES DO LISTENER
	 */

	private boolean blocoNaFrente(Fixture fixA, Fixture fixB, Contact contact) {
		if (!(fixA.getUserData() instanceof Player)) { // Validar se é bloco
			return false;
		}

		if (contact.getWorldManifold().getNormal().x >= 1) {
//			contact.setEnabled(false);
			((Player) fixA.getUserData()).subir(Direcao.ESQUERDA);
		}

		if (contact.getWorldManifold().getNormal().x <= -1) {
//			contact.setEnabled(false);
			((Player) fixA.getUserData()).subir(Direcao.DIREITA);
		}

		return true;

	}

	private boolean playerSemColisaoComMonster(Fixture fixA, Fixture fixB, Contact contact) {
		if(!(fixA.getUserData() instanceof Player)) {
			return false;
		}

		if(!(fixB.getUserData() instanceof Monster)) {
			return false;
		}

		contact.setEnabled(false);
		return true;
	}

}

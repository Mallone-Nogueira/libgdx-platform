package com.mygdx.game.entities;

public enum Direcao {
	DIREITA(1), ESQUERDA(-1);

	private int inteiro;

	private Direcao(int inteiro) {
		this.inteiro = inteiro;
	}

	public int get() {
		return inteiro;
	}

}

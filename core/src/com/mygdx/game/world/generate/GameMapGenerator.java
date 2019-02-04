package com.mygdx.game.world.generate;

import java.util.Random;

public class GameMapGenerator {
	private int blockWidth= 8;
	private int blockHeigth = 8;
	private int worldWidth = 1000;
	private int worldHeigth = 1000;
	private int seed = 154;
	private PerlinNoiseGenerator perlin;
	private int TIME = 5000;

	private Random rand;

	public GameMapGenerator(int seed) {
		this.seed = seed;
		this.perlin = new PerlinNoiseGenerator(this.seed);
		this.rand = new Random(seed);
	}


	public int[][] generate() {
		int[][] mapa = new int[worldWidth][worldHeigth];



		gerarTerreno(mapa, 750);
		gerarCavernas(mapa, 280);
//		gerarCavernasMaior(mapa, 350);
		gerarCavernasMaiorDobro(mapa, 350);

		return mapa;
	}

	private void gerarTerreno(int[][] mapa, int alturaInicial) {
		float numero = 0;
		Bool primeiro = new Bool();

		for (int x = 0; x < mapa.length; x++) {

			numero = noise1D(x + TIME, 0.0110f, 0.0111f, 0.0220f, 0.0221f);
			numero += noise1D(x + TIME, blockWidth+blockHeigth, 0.0420f, 0.0421f);
			numero /= 2;

			primeiro.set(true);

			for (int y = mapa[0].length-1; y >= 0 ; y--) {
				mapa[x][y] = escolherBlocoTerreno( numero + alturaInicial, primeiro, y);
			}
		}
	}

	/**
	 * Escolhe blocos do primiero desenho
	 *
	 * @param topo
	 * @param primeiro
	 * @param y
	 * @return
	 */
	private int escolherBlocoTerreno(float topo, Bool primeiro, int y) {
		if (topo < y) {
			return 0;
		} else if (primeiro.get()) {
			primeiro.set(false);
			return 1;
		}
		return 2;

	}

	private void gerarCavernasMaior(int[][] mapa, int recuoBordaSuperior) {
		int max = mapa[0].length - recuoBordaSuperior;
		float numero = 0;
		float numero2 = 0;
		for (int y = 0; y < mapa[0].length; y++) {
			for (int x = 0; x < mapa.length ; x++) {

				if (suavizarCorte(mapa, max, y, x)) {
					continue;
				}

				numero = noise2D(x + TIME, y + TIME, 0.0190f, 0.0070f);

				numero2 = noise2D(x + TIME, y + TIME, 0.0110f, 0.0150f, 0.00870f, 0.00819f);
				numero2 += noise2D(x + TIME/2, y + TIME/2, 0.0210f, 0.0250f, 0.00770f, 0.00719f);
				numero2 += noise2D(x + TIME/3, y + TIME/3, 0.0310f, 0.0350f, 0.00970f, 0.00919f);
				numero2 /= 3;

				if (numero > 455555 || numero2 > 5  ) {
					mapa[x][y] = 0;
				}
			}
		}
	}

	private void gerarCavernasMaiorDobro(int[][] mapa, int recuoBordaSuperior) {
		int max = mapa[0].length - recuoBordaSuperior;
		float numero2 = 0;
		for (int y = 0; y < mapa[0].length * 2; y++) {
			for (int x = 0; x < mapa.length * 2; x++) {

				if (suavizarCorte(mapa, max, y/2, x/2)) {
					continue;
				}

				numero2 = noise2D(x + TIME, y + TIME,      0.0150f,  0.00819f);
				numero2 += noise2D(x + TIME/2, y + TIME/2, 0.0250f,  0.00719f);
				numero2 += noise2D(x + TIME/3, y + TIME/3, 0.0350f,  0.00919f);
				numero2 /= 3;

				if ( numero2 > 10  ) {
					mapa[x/2][y/2] = 0;
				}
			}
		}
	}

	private void gerarCavernas(int[][] mapa, int recuoBordaSuperior) {
		int max = mapa[0].length - recuoBordaSuperior;
		float numero = 0;
		for (int y = 0; y < mapa[0].length; y++) {
			for (int x = 0; x < mapa.length ; x++) {

				if (suavizarCorte(mapa, max, y, x)) {
					continue;
				}

				numero = noise2D(x + TIME, y + TIME, 0.0050f, 0.0051f, 0.0052f, 0.029f, 0.026f, 0.023f );

				if (numero < 0) {
					numero = noise2D(x +TIME, y + TIME, 0.080f, 0.060f, 0.050f, 0.040f);

//					if (numero < -100) {
					if (numero < -350) {
						mapa[x][y] = 0;
					}
				}
			}
		}

		// Criar bolas no mapa
//		for (int y =  0; y < mapa[0].length; y++) {
//			for (int x = 0; x < mapa.length ; x++) {
//
//				numero = noise2D(x + time, y + time, 0.0030f, 0.0031f, 0.0035f, 0.029f, 0.026f, 0.023f );
//				int buraco =  Math.round(numero);
//
//				if (buraco > 100 || buraco < -100) {
//					mapa[x][y] = 0;
//				}
//			}
//		}


		// Sortear blocos no mapa
//		for (int y =  0; y < mapa[0].length; y++) {
//			for (int x = 0; x < mapa.length ; x++) {
//
//				numero = noise2D(x + time, y + time, 0.50f, 0.51f, 0.55f, 0.29f, 0.26f, 0.23f );
//
//				if (numero > 0 && numero < 2) {
//					mapa[x][y] = 0;
//				}
//			}
//		}
	}

	/**
	 * Se y for maior que máximo e o bloco de baixo e diagonal pra baixo forem diferentes de 0
	 *
	 * @param mapa
	 * @param max máximo
	 * @param y
	 * @param x
	 * @return
	 */
	private boolean suavizarCorte(int[][] mapa, int max, int y, int x) {
		if(max < y ) {
			if (mapa[x][y-1] != 0) {
				if (x == 0 || mapa[x-1][y-1] != 0) {
					if (x == mapa.length-1 || mapa[x+1][y-1] != 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 1D sem média
	 *
	 * @param x
	 * @param frequency
	 * @return
	 */
	private float noise1D(float x, float... frequency) {
		return noise1D(x, blockHeigth, frequency);
	}

	/**
	 * 1D Sem média
	 *
	 * @param x
	 * @param width
	 * @param frequency
	 * @return
	 */
	private float noise1D(float x, int width, float... frequency) {
		if (frequency == null || frequency.length == 0) {
			return 0;
		}
		float result = 0;

		for (float freq : frequency) {
			result += perlin.tileableNoise1((x) * freq, (float)width);
		}

		return result;
	}

	/**
	 * 2D com média
	 *
	 * @param x
	 * @param y
	 * @param frequency
	 * @return
	 */
	private float noise2D(int x, int y, float... frequency) {
		if (frequency == null || frequency.length == 0) {
			return 0;
		}
		float result = 0;

		for (float freq : frequency) {
			result += perlin.tileableNoise2((x)*freq, (y)*freq, blockWidth, blockHeigth);
		}

		return result / frequency.length;
	}

	/**
	 * Frescura
	 *
	 */
	public class Bool {
		boolean primeiro = true;

		public void set(boolean primeiro) {
			this.primeiro = primeiro;
		}

		public boolean get() {
			return primeiro;
		}
	}
}

package com.mygdx.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameInit;

public class Hud {
	public Stage stage;
	private Viewport viewport;

	private Integer worldTime;
	private float timeCount;
	private Integer score;

	Label countdownLabel;
	Label xValueLabel;
	Label timeLabel;
	Label fpsValueLabel;
	Label fpsLabel;
	Label xLabel;

	public Hud(SpriteBatch bach) {
		worldTime = 300;
		timeCount = 0;
		score = 0;

		OrthographicCamera camera = new OrthographicCamera();
		viewport = new FitViewport(GameInit.V_WIDTH, GameInit.V_HEIGHT, camera);
		stage = new Stage(viewport, bach);

		createLayout();
	}

	private void createLayout() {
		Table table = new Table();
		table.setFillParent(true);

		countdownLabel = new Label(String.format("%03d", worldTime), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		xLabel = new Label("X", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		xValueLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		fpsValueLabel  = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		fpsLabel = new Label("FPS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


		table.top();
		table.add(xLabel).expandX().padTop(10);
		table.add(fpsLabel).expandX().padTop(10);
		table.add(timeLabel).expandX().padTop(10);

		table.row();
		table.add(xValueLabel).expandX();
		table.add(fpsValueLabel).expandX();
		table.add(countdownLabel).expandX();

		stage.addActor(table);
	}

	public void render(int x) {
		fpsValueLabel.setText(Gdx.graphics.getFramesPerSecond());
		xValueLabel.setText(x);
	}

	public void dispose() {
		stage.dispose();
	}
}

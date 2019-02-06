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

	Label countdownLabel;
	Label xValueLabel;
	Label fpsValueLabel;
	Label fpsLabel;
	Label xLabel;

	public Hud(SpriteBatch bach) {
		OrthographicCamera camera = new OrthographicCamera();
		viewport = new FitViewport(GameInit.V_WIDTH, GameInit.V_HEIGHT, camera);
		stage = new Stage(viewport, bach);

		createLayout();
	}

	private void createLayout() {
		Table table = new Table();
		table.setFillParent(true);

		xValueLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		fpsValueLabel  = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		fpsLabel = new Label("FPS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


		table.top();
		table.add(fpsLabel).expandX().padTop(10);

		table.row();
		table.add(fpsValueLabel).expandX();
		table.add(xValueLabel).expandX();

		stage.addActor(table);
	}

	public void render(String x) {
		fpsValueLabel.setText(Gdx.graphics.getFramesPerSecond());
		xValueLabel.setText(x);
	}

	public void dispose() {
		stage.dispose();
	}
}

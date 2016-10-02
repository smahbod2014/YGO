package com.ygo.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class YGO extends ApplicationAdapter {

	public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;

    static OrthographicCamera camera;
	SpriteBatch batch;
    Field field;
	
	@Override
	public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
        camera = new OrthographicCamera(WINDOW_WIDTH, WINDOW_HEIGHT);
        camera.translate(WINDOW_WIDTH/2, WINDOW_HEIGHT/2);
        camera.update();
//        camera = new OrthographicCamera();
//        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setProjectionMatrix(camera.combined);
        field = new Field();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        field.renderGrid();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}

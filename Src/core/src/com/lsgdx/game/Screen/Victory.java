package com.lsgdx.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lsgdx.game.LostInSpace;

import java.util.Random;

public class Victory implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;

    private Texture texture;
    private Sprite sprite;


    public Victory(Game game){
        this.game = game;

        viewport = new FitViewport(LostInSpace.VP_WIDTH, LostInSpace.VP_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((LostInSpace)game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOver = new Label("CONGRATULATIONS", font);
        Label press = new Label("press to title", font);

        table.add(gameOver).expandX(); // 카메라 만큼 X좌표 늘림
        table.row();
        table.add(press).expandX().padTop(10f);

        stage.addActor(table);

        switch (new Random().nextInt(2)){
            case 0:
                texture = new Texture("Sprites/gameover1.jpg");
                break;
            default:
                texture = new Texture("Sprites/gameover4.png");
                break;
        }

        sprite = new Sprite(texture);
        sprite.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void show() {

    }

    float wait = 2.0f;
    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched() && wait < 0){
            game.setScreen(new Level_One((LostInSpace) game));
            dispose();
            return;
        }

        wait -= delta;

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        LostInSpace.batch.begin();
        sprite.draw(LostInSpace.batch);
        LostInSpace.batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        texture.dispose();
    }
}

package com.lsgdx.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

public class Title  implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;

    private Texture texture;
    private Sprite sprite;

    private Music music;


    public Title(Game game){
        this.game = game;

        music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Moment-of-Strange_Looping.mp3"));
        music.setLooping(true);
        music.setVolume(LostInSpace.BGM_VOLUM);
        music.play();

        viewport = new FitViewport(LostInSpace.VP_WIDTH, LostInSpace.VP_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((LostInSpace)game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label title = new Label("LOST IN SPACE", font);
        Label press = new Label("press to start", font);

        table.add(title).expandX().padLeft(100f); // 카메라 만큼 X좌표 늘림
        table.row();
        table.add(press).expandX().padTop(15f).padLeft(100f);

        stage.addActor(table);

        texture = new Texture("Sprites/title.jpg");
        sprite =new Sprite(texture);
        sprite.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            game.setScreen(new Level_One((LostInSpace) game));
            dispose();
            return;
        }

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
        music.dispose();
    }
}
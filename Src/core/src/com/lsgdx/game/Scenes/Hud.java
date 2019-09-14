package com.lsgdx.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lsgdx.game.Character.Enemy.Enemy;
import com.lsgdx.game.Character.Player;
import com.lsgdx.game.LostInSpace;


public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private boolean end = true;

    private Float worldTimer;

    private Label worldLabel;
    private Label playerLabel;
    private Label enemyLabel;

    public Hud(SpriteBatch sb){
        reset();

        viewport = new FitViewport(LostInSpace.VP_WIDTH, LostInSpace.VP_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // 레이아웃의 순서를 정하기 위한 테이블 선언
        Table table = new Table();
        table.top(); // 테이블을 스테이지의 상단에 위치
        table.setFillParent(true); // 테이블의 크기를 스테이지만큼 변경

        playerLabel = new Label("Oxygen : " + 100, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        enemyLabel = new Label("RAIDER : UNKOWN", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("TIME LEFT : (" + (int)(worldTimer / 60) + ":" + (int)(worldTimer % 60) + ")", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerLabel.setFontScale(playerLabel.getFontScaleX()/LostInSpace.VP_DEVIDED);
        enemyLabel.setFontScale(enemyLabel.getFontScaleX()/LostInSpace.VP_DEVIDED);
        worldLabel.setFontScale(worldLabel.getFontScaleX()/LostInSpace.VP_DEVIDED);

        table.add(playerLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(enemyLabel).expandX().padTop(10);

        stage.addActor(table);
    }

    public void update(float dt, Player player, Enemy enemy){
        if(player.getOxygen() < 20){
            playerLabel.setColor(Color.RED);
        } else {
            playerLabel.setColor(Color.WHITE);
        }

        playerLabel.setText("Oxygen : " + (int)player.getOxygen());

        if(player.distanceCalculator(enemy.body) < 1.8f){
            enemyLabel.setText("RAIDER : NEAR");
            enemyLabel.setColor(Color.RED);
            player.playBeep(dt, player.distanceCalculator(enemy.body));
        } else {
            enemyLabel.setText("RAIDER : NO SIGN");
            enemyLabel.setColor(Color.WHITE);
        }

        worldTimer -= dt;
        if(worldTimer < 0) { // 승리
            player.victory();
        } else if(worldTimer < 30 && end){
            enemy.detectPlayer();
            end = false;
        }

        worldLabel.setText("TIME LEFT : " + worldTimer.intValue());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }

    public void reset(){
        worldTimer = 2f;
    }
}

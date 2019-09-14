package com.lsgdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lsgdx.game.Screen.GameOver;
import com.lsgdx.game.Screen.Level_One;
import com.lsgdx.game.Screen.Title;

public class LostInSpace extends Game {
	public static final int VP_DEVIDED = 4; // 4
	public static final int VP_WIDTH = 960/VP_DEVIDED;
	public static final int VP_HEIGHT = 480/VP_DEVIDED;
	public static final float PPM = 100;

	public static final float BGM_VOLUM = 0.05f;
	public static final float EFFECT_VOLUM = 0.2f;

	public static final short PLAYER_BIT = 1;
	public static final short LADDER_BIT = 2;
	public static final short DOOR_BIT = 4;
	public static final short CARD_BIT = 8;
	public static final short ENEMY_BIT = 16;
	public static final short GROUND_BIT = 32;
	public static final short ENEMYRAIDER_BIT = 128;
	public static final short OXYGEN_BIT = 518;

	public static SpriteBatch batch; // 모든 이미지 저장 컨테이너, 싱글톤


	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new Title(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
package com.lsgdx.game.Screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lsgdx.game.Character.Enemy.Enemy;
import com.lsgdx.game.Character.Object.Oxygen;
import com.lsgdx.game.Character.Player;
import com.lsgdx.game.Controller.Controller;
import com.lsgdx.game.LostInSpace;
import com.lsgdx.game.Scenes.Hud;
import com.lsgdx.game.Tool.WorldBoxCreater;
import com.lsgdx.game.Tool.WorldContactListener;

import java.util.Random;


public class Level_One implements Screen {

    private LostInSpace game;

    private OrthographicCamera camera;
    public OrthographicCamera getCamera(){
        if(camera == null){
            camera = new OrthographicCamera();
        }
        return camera;
    }

    private Viewport gamePort;
    private Controller controller;

    private Hud hud;
    public Hud getHud(){return hud;}

    private TiledMap map;
    public TiledMap getMap(){
        if(map == null){
            maploader = new TmxMapLoader();
            map = maploader.load("tiles/level1.tmx");
        }
        return map;
    }
    private TmxMapLoader maploader;
    private OrthogonalTiledMapRenderer renderer;

    private int mapWidth;
    private int mapHeight;
    public int getMapWidth() {
        if(mapWidth == 0){
            mapWidth = getMap().getProperties().get("width", Integer.class);
        }
        return mapWidth;
    }
    public int getMapHeight() {
        if(mapHeight == 0){
            mapHeight = getMap().getProperties().get("height", Integer.class); // 32
        }
        return mapHeight;
    }

    private int mapPixelWith;
    private int mapPixelHeight;
    public int getMapPixelWith() {
        if(mapPixelWith == 0){
            mapPixelWith = getMapWidth() * getTilePixelWidth(); // 16
        }
        return mapPixelWith;
    }
    public int getMapPixelHeight() {
        if(mapPixelHeight == 0){
            mapPixelHeight = getMapHeight() * getTilePixelHeight(); // 16
        }
        return mapPixelHeight;
    }

    private int tilePixelWidth;
    private int tilePixelHeight;
    public int getTilePixelWidth() {
        if(tilePixelWidth == 0){
            tilePixelWidth = getMap().getProperties().get("tilewidth", Integer.class); // 16
        }
        return tilePixelWidth;
    }
    public int getTilePixelHeight() {
        if(tilePixelHeight == 0){
            tilePixelHeight = map.getProperties().get("tileheight", Integer.class); // 16
        }
        return tilePixelHeight;
    }

    private Player player;
    public Player getPlayer() {
        return player;
    }
    private Enemy enemy;
    private World world;
    public World getWorld(){return world;}
    private Box2DDebugRenderer b2dr;

    private TextureAtlas atlas;
    public TextureAtlas getAtlas(){return atlas;}

    private float lightPosX, lightPosY;
    private float playerX, playerY;
    private FrameBuffer lightBuffer;
    private Texture lightBufferRegion;

    private float cameraXmin, cameraXmax, cameraYmin;

    public Random random;

    private Music music;
    private String[] bgmList;

    private Sound sound;
    private Sound sound2;

    private Oxygen oxygen;

    private boolean isEnd, badEnd;
    private Sprite gameover;
    private Sprite victory;
    private float wait = 2;


    public Level_One(LostInSpace game){
        // Activate debug mode
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        random = new Random();

        // Get Sprite Sheet
        atlas = new TextureAtlas(Gdx.files.internal("MG_SpriteSheet.atlas"));

        this.game = game;
        camera = new OrthographicCamera();

        // gamePort = new StretchViewport(800, 480, camera); // 화면에 맞추어 그래픽 확장/축소
        gamePort = new FitViewport(LostInSpace.VP_WIDTH / LostInSpace.PPM, LostInSpace.VP_HEIGHT / LostInSpace.PPM, camera); // 남은 여백 검은 색으로 필터링

        hud = new Hud(game.batch);

        MapSetting();

        // Set boundary of a camera
        camera.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2,0);
        cameraXmin = (LostInSpace.VP_WIDTH/2/LostInSpace.PPM);
        cameraXmax = ((mapPixelWith - (LostInSpace.VP_WIDTH/2)) / LostInSpace.PPM);
        cameraYmin = (LostInSpace.VP_HEIGHT/2/LostInSpace.PPM);

        // Set Controller and define the size of a buttons
        controller = new Controller(50/LostInSpace.VP_DEVIDED, player);

        // Set world gravity scale
        world = new World(new Vector2(0,-9.8f), true);
        b2dr = new Box2DDebugRenderer();

        // Display BoxWorld
        new WorldBoxCreater(this);

        player = new Player(world, this);
        enemy = new Enemy(world, this);

        world.setContactListener(new WorldContactListener(player, enemy));

        lightBufferRegion = new Texture("Sprites/corner.png");
        if (lightBuffer!=null) lightBuffer.dispose();
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, mapWidth, mapHeight, false);

        SoundSetting();

        oxygen = new Oxygen(world, this);

        GameOverSetting();
    }

    public void handleInput(float dt){
        // if there's no input set velocity to 0
        if(!controller.isLeftPressed() && !controller.isRightPressed() && !controller.isUpPressed() && !controller.isDownPressed()){
            player.body.setLinearVelocity(0,0);
            return;
        }

        // Get controller input
        if(controller.isRightPressed() && player.body.getLinearVelocity().x <= 1){
            if(!player.getIsClimbing() || player.getOnGround()){
                player.body.applyLinearImpulse(new Vector2(0.5f, 0), player.body.getWorldCenter(), true);
            }
        } else if(controller.isLeftPressed() && player.body.getLinearVelocity().x >= -1){
            if(!player.getIsClimbing() || player.getOnGround()) {
                player.body.applyLinearImpulse(new Vector2(-0.5f, 0), player.body.getWorldCenter(), true);
            }
        } else if(controller.isUpPressed() && player.getOnLadder()){
            if(!player.getIsClimbing()){
                player.setIsClimbing(true); // prevent movement to the right or left
                player.getCurrentLadder().setSensor(true); // player can go through the ladder
                player.body.setTransform(new Vector2(player.getLadderXPos(), player.body.getPosition().y + 0.01f), 0); // 플레이어 위치 고정
            }
            player.body.setAwake(true);
            player.body.setTransform(new Vector2(player.getLadderXPos(), player.body.getPosition().y + 0.01f), 0);
        } else if(controller.isDownPressed() && player.getOnLadder()){
            if(!player.getIsClimbing()){
                player.setIsClimbing(true); // prevent movement to the right or left
                player.getCurrentLadder().setSensor(true); // player can go through the ladder
                player.body.setLinearVelocity(0, 0);
                player.body.setTransform(new Vector2(player.getLadderXPos(), player.body.getPosition().y - 0.01f), 0); // 플레이어 위치 고정
            }
            player.body.setAwake(true);
            player.body.setTransform(new Vector2(player.getLadderXPos(), player.body.getPosition().y - 0.01f), 0);
        }
    }

    public void update(float dt){
        handleInput(dt);
        if(!music.isPlaying()){
            music = Gdx.audio.newMusic(Gdx.files.internal(bgmList[random.nextInt(bgmList.length)]));
            music.setVolume(LostInSpace.BGM_VOLUM);
            music.play();
        }

        // 60 frame per 1 sec
        world.step(1/60f, 6, 2);

        player.update(dt);
        oxygen.update();

        if(player.body.getPosition().x > cameraXmin && player.body.getPosition().x < cameraXmax)
            camera.position.x = player.body.getPosition().x;
        if(player.body.getPosition().y > LostInSpace.VP_HEIGHT/2/LostInSpace.PPM)
            camera.position.y = player.body.getPosition().y;

        camera.update();

        renderer.render();
    }

    @Override
    public void render(float delta) {
        if(isEnd){
            if(Gdx.input.justTouched() && wait < 0){
                reset();
                return;
            }

            wait -= delta;

            game.batch.begin();
            if(badEnd) {
                gameover.setSize(LostInSpace.VP_WIDTH, LostInSpace.VP_HEIGHT);
                gameover.draw(game.batch);
            } else {
                victory.setSize(LostInSpace.VP_WIDTH, LostInSpace.VP_HEIGHT);
                victory.draw(game.batch);
            }
            game.batch.end();

           return;
        }
        update(delta);

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        renderer.setView(camera);

        b2dr.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        player.draw(game.batch);
        oxygen.getSprite().draw(game.batch);

        enemy.getEnemySpriteInfo().draw(game.batch);

        lightPosX = camera.viewportWidth * 2f;
        lightPosY = camera.viewportHeight * 2;
        playerX = player.body.getPosition().x - (lightPosX/2);
        playerY = player.body.getPosition().y - (lightPosY/2);

        game.batch.draw(lightBufferRegion, playerX, playerY,lightPosX, lightPosY);

        game.batch.end();

        hud.update(delta, player, enemy);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.draw();

        enemy.update(delta);
    }

    public void GameOver(){
        //game.setScreen(new GameOver(game));
        //dispose();
        switch (new Random().nextInt(2)){
            case 0:
                gameover.set(new Sprite(new Texture("Sprites/gameover1.jpg")));
                break;
            default:
                gameover.set(new Sprite(new Texture("Sprites/gameover1.jpg")));
                break;
        }
        music.stop();
        sound.play(LostInSpace.BGM_VOLUM);
        isEnd = true;
        badEnd = true;
    }

    public void Victory(){
        //game.setScreen(new Victory(game));
        //dispose();
        victory.setSize(gamePort.getWorldWidth(), gamePort.getWorldHeight());
        music.stop();
        sound2.play(LostInSpace.BGM_VOLUM);
        isEnd = true;
        badEnd = false;
    }

    @Override
    public void resize(int width, int height) {
        controller.resize(width,height);
        gamePort.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        sound.dispose();
        music.dispose();
        player.Dispose();
        enemy.getEnemyAI().Dispose();
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        lightBuffer.dispose();
        lightBufferRegion.dispose();
        player.getTexture().dispose();
        enemy.getEnemySpriteInfo().getTexture().dispose();
    }

    @Override
    public void show() {

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

    private void GameOverSetting(){
        gameover = new Sprite(new Texture("Sprites/gameover1.jpg"));
        victory = new Sprite(new Texture("Sprites/victory.png"));
    }

    private void SoundSetting(){
        bgmList = new String[5];
        bgmList[0] = ("Sounds/Bells-of-Weirdness_Looping.mp3");
        bgmList[1] =("Sounds/City-of-the-Disturbed_Looping.mp3");
        bgmList[2] =("Sounds/Closing-In-2_Looping.mp3");
        bgmList[3] =("Sounds/Disturbing-Chimes_Looping.mp3");
        bgmList[4] =("Sounds/Moment-of-Strange_Looping.mp3");

        music = Gdx.audio.newMusic(Gdx.files.internal(bgmList[random.nextInt(bgmList.length)]));
        music.setVolume(LostInSpace.BGM_VOLUM);
        music.play();

        sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/gameover.mp3"));
        sound2 = Gdx.audio.newSound(Gdx.files.internal("Sounds/victory.wav"));
    }

    private void MapSetting(){
        // load Tile map
        maploader = new TmxMapLoader();
        map = maploader.load("tiles/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/LostInSpace.PPM);

        mapWidth = map.getProperties().get("width", Integer.class); // 128
        mapHeight = map.getProperties().get("height", Integer.class); // 32
        tilePixelWidth = map.getProperties().get("tilewidth", Integer.class); // 16
        tilePixelHeight = map.getProperties().get("tileheight", Integer.class); // 16
        mapPixelWith = mapWidth * tilePixelWidth; // 2048 = 128 * 16
        mapPixelHeight = mapHeight * tilePixelHeight; // 512 = 32 * 16
    }

    private void reset(){
        isEnd = false;
        wait = 2;
        music.play();
        enemy.getEnemyAI().reset();
        player.reset();
        hud.reset();

        camera.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2,0);
        cameraXmin = (LostInSpace.VP_WIDTH/2/LostInSpace.PPM);
        cameraXmax = ((mapPixelWith - (LostInSpace.VP_WIDTH/2)) / LostInSpace.PPM);
        cameraYmin = (LostInSpace.VP_HEIGHT/2/LostInSpace.PPM);

        lightPosX = camera.viewportWidth * 2f;
        lightPosY = camera.viewportHeight * 2;
        playerX = player.body.getPosition().x - (lightPosX/2);
        playerY = player.body.getPosition().y - (lightPosY/2);
    }
}

package io.github.some_example_name;

import static helper.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import helper.TileMapHelper;
import objects.controller.Controller;
import objects.player.Player;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen extends ScreenAdapter {
    private final OrthographicCamera camera; //Camara encargada de seguir al jugador
    public static SpriteBatch batch;
    private final World world;
    private final Viewport viewport;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final OrthogonalTiledMapRenderer renderer;
    private final TileMapHelper Map;
    private TextureAtlas marioAtlas, enemyAtlas;
    public Controller controller;

    //Objetos
    private Player player;
    public GameScreen(OrthographicCamera camera){
        marioAtlas = new TextureAtlas("textures/marioSprites.atlas");

        this.camera = camera;
        this.viewport = new ScreenViewport(camera);
        batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-9.81f),false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        this.Map = new TileMapHelper(this);
        this.renderer = Map.setupMap("maps/testMap.tmx");
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);



        controller = new Controller();
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
        camera.position.set(position);
        camera.update();
    }

    private void update(float delta) {
        world.step(1 / 60f, 6,2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        player.update(delta);

        inputPlayer();
    }

    @Override
    public void render(float delta) {
        // "delta" es el tiempo desde el ultimo render en segundos.
        viewport.apply();
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin(); //Renderizar objetos
        player.draw(batch);
        batch.end();

        box2DDebugRenderer.render(world,camera.combined.scl(PPM));
        controller.draw();
    }

    public World getWorld() {
        return world;
    }

    public TextureAtlas getMarioAtlas(){
        return marioAtlas;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public void inputPlayer(){
        if(controller.isRightPress())
            player.movement("r");
        else if (controller.isLeftPress())
            player.movement("l");
        else
            player.movement("NONE");
        if (controller.isUpPress())
            player.jump(true);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height);
        viewport.apply();
        controller.resize(width,height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        renderer.dispose();
        world.dispose();
    }
}

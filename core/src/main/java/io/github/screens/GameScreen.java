package io.github.screens;

import static helper.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import helper.TileMapHelper;
import objects.controller.Controller;
import objects.hud.HUD;
import objects.player.Player;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen extends ScreenAdapter {

    //Mapas
    private final OrthographicCamera camera; //Camara encargada de seguir al jugador
    public static SpriteBatch batch, hud_batch;
    private final World world;
    private final Viewport viewport;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private int nivel, mapa; private int mapLeft, mapRight, mapBottom, mapTop;
    private float camHalfWidth, camHalfHeight;
    private boolean secreto, end;
    private final OrthogonalTiledMapRenderer renderer;
    private final TileMapHelper Map;
    private MapProperties mapProp;

    //Objetos
    private Player player;
    private Sound enter_sfx, hurt_sfx, finish_sfx, secret_sfx, gameOver_sfx;
    private TextureAtlas marioAtlas, enemyAtlas, hatsAtlas;
    private HUD hud;
    public Controller controller;

    public GameScreen(OrthographicCamera camera, int nivel,int mapa, boolean secreto, boolean end, int lives, int timer){

        marioAtlas = new TextureAtlas("textures/marioSprites.atlas");
        hatsAtlas = new TextureAtlas("textures/hats.atlas");

        this.mapa = mapa; this.nivel = nivel;
        this.secreto = secreto;
        this.end = end;
        this.camera = camera;
        this.viewport = new StretchViewport(800,400);
        hud_batch = batch = new SpriteBatch();
        hud = new HUD(hud_batch); hud.setLives(lives); hud.setTimer(timer);

        //Sonidos
        hurt_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.wav"));
        enter_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/enter.wav"));
        finish_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/finish.wav"));
        secret_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/secret.wav"));
        gameOver_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.wav"));

        this.world = new World(new Vector2(0,-10.81f),false);
        this.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                if ((a != null && a.equals("PLAYER") && b != null && b.equals("RESTART")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("RESTART"))) {
                    if(lives <= 1) {
                        reiniciarMapa(GameScreen.this.nivel, 1, false, true,5, 300);
                        gameOver_sfx.play();
                    }
                    else{
                        if(!end) {
                            reiniciarMapa(GameScreen.this.nivel, GameScreen.this.mapa, false, false, lives - 1, timer);
                            hurt_sfx.play();
                        }else{
                            reiniciarMapa(GameScreen.this.nivel, GameScreen.this.mapa, false, false, lives, timer);
                            finish_sfx.play();
                        }
                    }
                }
                if ((a != null && a.equals("PLAYER") && b != null && b.equals("SIGUIENTE")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("SIGUIENTE"))) {
                    GameScreen.this.mapa = mapa + 1;
                    GameScreen.this.secreto = false;
                    reiniciarMapa(GameScreen.this.nivel, GameScreen.this.mapa, GameScreen.this.secreto,false,hud.getLives(),hud.getTimer());
                    enter_sfx.play();
                }
                if ((a != null && a.equals("PLAYER") && b != null && b.equals("SECRETO")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("SECRETO"))) {
                    GameScreen.this.secreto = true;
                    reiniciarMapa(GameScreen.this.nivel, GameScreen.this.mapa, GameScreen.this.secreto,false,hud.getLives(),hud.getTimer());
                    secret_sfx.play();
                }
                if ((a != null && a.equals("PLAYER") && b != null && b.equals("FINAL")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("FINAL"))) {
                    reiniciarMapa(0, 0, GameScreen.this.secreto,false,hud.getLives(),300);
                    finish_sfx.play();
                }
                if ((a != null && a.equals("PLAYER") && b != null && b.equals("MUNDOUNO")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("MUNDOUNO"))) {
                    reiniciarMapa(1, 1, GameScreen.this.secreto,false,hud.getLives(),hud.getTimer());
                    enter_sfx.play();
                }
                if ((a != null && a.equals("PLAYER") && b != null && b.equals("MUNDODOS")) ||
                    (b != null && b.equals("PLAYER") && a != null && a.equals("MUNDODOS"))) {
                    reiniciarMapa(2, 1, GameScreen.this.secreto,false,hud.getLives(),hud.getTimer());
                    enter_sfx.play();
                }
            }

            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.Map = new TileMapHelper(this);

        if (secreto) {
            this.renderer = Map.setupMap("maps/Map"+nivel+".S.tmx");
        }else if(end){
            this.renderer = Map.setupMap("maps/Map0.1.tmx");
        }
        else{
            this.renderer = Map.setupMap("maps/Map"+nivel+"."+mapa+".tmx");
        }


        //Variables de Cámara y mundo
        mapProp = renderer.getMap().getProperties();
        //Limites del mundo
        mapLeft = 0;
        mapRight = (mapProp.get("width", Integer.class) * mapProp.get("tilewidth", Integer.class));
        mapBottom = 0;
        mapTop = (mapProp.get("height", Integer.class) * mapProp.get("tileheight", Integer.class));

        //Posición X Y media de la cámara (Usado para evitar que muestre fuera del mapa)
        camHalfWidth = camera.viewportWidth * .5f;
        camHalfHeight = camera.viewportHeight * .5f;
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        controller = new Controller();
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
        camera.position.set(position);

        //Evita que la cámara se salga de los bordes horizontales del mapa.
        if(mapRight < camera.viewportWidth){ //Si el mapa es más pequeño que el viewport, centra la cámara horizontalmente.
            camera.position.x = mapRight / 2;
        }else if( (camera.position.x-camHalfWidth) <= mapLeft){ //Borde izquierdo
            camera.position.x = mapLeft + camHalfWidth;
        }else if((camera.position.x+camHalfWidth) >= mapRight){ //Borde derecho
            camera.position.x = mapRight - camHalfWidth;
        }

        // Ditto. Bordes verticales
        if(mapTop < camera.viewportHeight){ //Si el mapa es más pequeño que el viewport, centra la cámara verticalmente.
            camera.position.y = mapTop / 2;
        } else if ((camera.position.y - camHalfHeight) <= mapBottom) { //Borde inferior
            camera.position.y = mapBottom + camHalfHeight;
        }else if ((camera.position.y + camHalfHeight) >= mapTop){ //Borde superior
            camera.position.y = mapTop - camHalfHeight;
        }

        camera.update();
    }

    private void update(float delta) {
        world.step(1 / 60f, 6,2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);

        player.update(delta);

        if(this.nivel != 0)
            hud.update(delta);

        inputPlayer(delta);
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

        //Renderiza las hitbox
        //box2DDebugRenderer.render(world,camera.combined.scl(PPM));
        hud.stage.draw();
        controller.draw();
    }

    public World getWorld() {
        return world;
    }

    public TextureAtlas getMarioAtlas(){
        return marioAtlas;
    }

    public TextureAtlas getHatsAtlas(){
        return hatsAtlas;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public void inputPlayer(float delta){
        if(controller.isRightPress())
            player.movement("r");
        else if (controller.isLeftPress())
            player.movement("l");
        else
            player.movement("NONE");

        player.updateJump(delta, controller.isUpPress());
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

    private void reiniciarMapa(int nivel,int mapa, boolean secreto, boolean end, int lives, int timer) {
        Gdx.app.postRunnable(() -> {
            ((Main) Gdx.app.getApplicationListener()).setScreen(new GameScreen(camera,nivel,mapa,secreto,end,lives, timer));
        });
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        renderer.dispose();
        world.dispose();
    }
}

package objects.player;

import static helper.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import io.github.screens.GameScreen;

public class Player extends Sprite {


    public enum State { FALLING, JUMPING, STANDING, RUNNING};
    protected float x, y, xVel, yVel, speed;
    protected float width, height;
    protected Body body;
    public int jumpCounter, hatID;
    protected State currentState, prevState;
    private float stateTimer;
    private boolean runningRight;
    private Sound jump_sfx;

    private TextureRegion smallMarioAtlas, smallMarioIdle, smallMarioJump, smallMarioFall, smallMarioDuck;
    private Animation smallMarioRun, smallMarioSpin;
    private TextureRegion hatsAtlas, hatOn;
    private Sprite hatOverlay;
    private boolean isJumping = false;
    private float jumpTime = 0f;
    private final float MaxjumpTime = 0.7f;
    public String playerColor;

    public Player(float width, float height, Body body, GameScreen screen) {
        Preferences prefs = Gdx.app.getPreferences("playerData");

        this.width = width; this.height = height;
        currentState = prevState = State.STANDING;
        stateTimer = 0;
        this.body = body;
        this.x = body.getPosition().x; this.y = body.getPosition().y;
        this.width = width; this.height = height;
        this.xVel = 0; this.yVel = 0;
        this.speed = 4f;
        this.jumpCounter = 0;
        this.body.setUserData("PLAYER");
        this.hatOverlay = new Sprite(); this.hatID = prefs.getInteger("playerHat",0); //Leer hatID desde SharedPreferences
        runningRight = true;
        jump_sfx = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));

        this.playerColor = prefs.getString("playerHexCode","#FF000000");
        Array<TextureRegion> frames = new Array<TextureRegion>();


        //Cambio de color
        Texture ogPlayerAtlas = screen.getMarioAtlas().findRegion("small_mario").getTexture();
        Texture coloredPlayer = PlayerColorChanger.cambiarColorRopaConContraste(ogPlayerAtlas, playerColor);

        Texture ogHatAtlas = screen.getHatsAtlas().findRegion("hats").getTexture();
        Texture coloredHat = PlayerColorChanger.cambiarColorRopaConContraste(ogHatAtlas,playerColor);

        TextureRegion ogPlayerRegion = screen.getMarioAtlas().findRegion("small_mario");
        TextureRegion ogHatRegion = screen.getHatsAtlas().findRegion("hats");

        smallMarioAtlas = new TextureRegion(coloredPlayer,
            ogPlayerRegion.getRegionX(),
            ogPlayerRegion.getRegionY(),
            ogPlayerRegion.getRegionWidth(),
            ogPlayerRegion.getRegionHeight()
        );

        hatsAtlas = new TextureRegion(coloredHat,
            ogHatRegion.getRegionX(),
            ogHatRegion.getRegionY(),
            ogHatRegion.getRegionWidth(),
            ogHatRegion.getRegionHeight()
        );

        //Definir sprites para el Mario pequeño.
        smallMarioIdle = new TextureRegion(smallMarioAtlas, 0, 0, 48, 84);

        smallMarioDuck = new TextureRegion(smallMarioAtlas, 96,0,48,84);
        smallMarioJump = new TextureRegion(smallMarioAtlas, 528, 0, 48, 84);
        smallMarioFall = new TextureRegion(smallMarioAtlas, 576, 0, 48, 84);



        //Definir animaciones para el Mario pequeño.
        frames.add(new TextureRegion(smallMarioAtlas, 144, 0, 48, 84));
        frames.add(new TextureRegion(smallMarioAtlas, 0, 0, 48, 84));
        frames.add(new TextureRegion(smallMarioAtlas, 192, 0, 48, 84));
        frames.add(new TextureRegion(smallMarioAtlas, 0, 0, 48, 84));
        smallMarioRun = new Animation(0.1f,frames); frames.clear();

        //Definir sombrero a usar
        hatOn = new TextureRegion(hatsAtlas, (hatID*48), 0,48,48);

        hatOverlay.setRegion(hatOn);
        hatOverlay.setBounds(x,(y+10),48,48);
        setRegion(smallMarioIdle);
        setBounds(x,y, 48, 84);
        //Gdx.app.log("DEBUG", "pos: (" + hatOverlay.getX() + "," + hatOverlay.getY() + "), bounds: (" + hatOverlay.getWidth() + "," + hatOverlay.getHeight() + ")");

    }

    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        //Actualizar Sprite del jugador
        setPosition((x-getWidth()/2),y-64/2);
        setRegion(setAnimation(delta));

        //Actualizar Sprite del sombrero
        setHatMoves();
        hatOverlay.setRegion(flipHat());
    }

    public TextureRegion setAnimation(float delta){
        currentState = getState();

        TextureRegion region;

        switch (currentState){
            case JUMPING:
                region = smallMarioJump;
                break;
            case FALLING:
                region = smallMarioFall;
                break;
            case RUNNING:
                region = (TextureRegion) smallMarioRun.getKeyFrame(stateTimer,true);
                break;
            case STANDING:
            default:
                region = smallMarioIdle;
                break;
        }

        if((body.getLinearVelocity().x < 0 || !runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        else if((body.getLinearVelocity().x > 0 || runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //Usado para las animaciones. Si prevState == currentState, continuar al siguiente frame de la animación.
        stateTimer = currentState == prevState ? stateTimer + delta : 0;
        prevState = currentState;

        return region;
    }
    public TextureRegion flipHat(){
        TextureRegion region = hatOverlay;

        if((body.getLinearVelocity().x < 0 || !runningRight) && region.isFlipX()){
            region.flip(true, false);
        }

        else if((body.getLinearVelocity().x > 0 || runningRight) && !region.isFlipX()){
            region.flip(true, false);
        }
        return region;
    }

    public void setHatMoves(){
        currentState = getState();

        switch (currentState){
            case JUMPING:
                hatOverlay.setPosition((x-getWidth()/2),y+12);
                break;
            case FALLING:
                hatOverlay.setPosition((x-getWidth()/2),y+9);
                break;
            case STANDING:
            default:
                hatOverlay.setPosition((x-getWidth()/2),y+6);
                break;
        }

    }
    public State getState(){
        if(body.getLinearVelocity().y > 0)
            return State.JUMPING;
        else if(body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }


    public void movement(String input){
        xVel = 0;
        if(input.equals("r"))
            xVel = 1;

        else if (input.equals("l"))
            xVel = -1;
        if (input.equals("NONE"))
            xVel = 0;
        body.setLinearVelocity(xVel * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
    }

    public void updateJump(float delta, boolean upHeld) {

        // Salto normalito
        if (upHeld && !isJumping && jumpCounter < 1 && currentState != State.FALLING) {
            isJumping = true;
            jumpTime = 0f;
            jumpCounter++;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, body.getMass() * 6), body.getWorldCenter(), true);
            jump_sfx.play();
        }

        // Salto mientras mantego precionado
        if (isJumping && upHeld) {
            if (jumpTime < MaxjumpTime) {
                body.applyForceToCenter(0, body.getMass() * 10, true);
                jumpTime += delta;
            }
        }

        if (!upHeld && isJumping) {
            isJumping = false;
        }

        if (body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
            isJumping = false;
        }
    }

    public void draw(Batch batch){
        super.draw(batch);
        hatOverlay.draw(batch);
    }
    public Body getBody(){
        return body;
    }

}

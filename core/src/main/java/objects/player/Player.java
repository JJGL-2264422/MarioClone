package objects.player;

import static helper.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.GameScreen;

public class Player extends Sprite {


    public enum State { FALLING, JUMPING, STANDING, RUNNING};
    protected float x, y, xVel, yVel, speed;
    protected float width, height;
    protected Body body;
    public int jumpCounter;
    protected State currentState, prevState;
    private float stateTimer;
    private boolean runningRight;

    private TextureRegion smallMarioAtlas, smallMarioIdle, smallMarioJump, smallMarioFall, smallMarioDuck;
    private Animation smallMarioRun, smallMarioSpin;
    private TextureRegion bigMarioAtlas;

    public Player(float width, float height, Body body, GameScreen screen) {
        this.width = width; this.height = height;
        currentState = prevState = State.STANDING;
        stateTimer = 0;
        this.body = body;
        this.x = body.getPosition().x; this.y = body.getPosition().y;
        this.width = width; this.height = height;
        this.xVel = 0; this.yVel = 0;
        this.speed = 4f;
        this.jumpCounter = 0;
        runningRight = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        smallMarioAtlas = screen.getMarioAtlas().findRegion("small_mario");

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

        setRegion(smallMarioIdle);
        setBounds(x,y, 48, 84);
        //Gdx.app.log("DEBUG", "Region found! Width: " + smallMarioAtlas.getRegionWidth() + ", Height: " + smallMarioAtlas.getRegionHeight());
        //Gdx.app.log("Player", "pos: (" + getX() + "," + getY() + "), bounds: (" + getWidth() + "," + getHeight() + ")");

    }

    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        setPosition((x-getWidth()/2),y-64/2);
        setRegion(setAnimation(delta));
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

    public void jump(boolean jumping){
        if(jumping && jumpCounter < 1){
            float force = body.getMass() * 12;
            body.setLinearVelocity(body.getLinearVelocity().x,0);
            body.applyLinearImpulse(new Vector2(0,force),body.getWorldCenter(),true);
            jumpCounter ++;
        }
        if(body.getLinearVelocity().y == 0){
            jumpCounter = 0;
        }
    }

    public void draw(Batch batch){
       super.draw(batch);
    }
    public Body getBody(){
        return body;
    }

}

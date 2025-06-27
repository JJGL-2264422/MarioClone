package objects.collectables;

import static helper.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.screens.GameScreen;

public class BigCoin extends Sprite {
    public Body body;
    private float x, y;
    protected World world;
    private TextureRegion idle, invisible;
    private Animation animation;
    boolean coinGet,collected;
    private float animTimer;
    public BigCoin(Body body, GameScreen screen){
        this.world = screen.getWorld();
        this.body = body;
        this.x = body.getPosition().x * PPM; this.y = body.getPosition().y * PPM;
        this.animTimer = 0;
        Array<TextureRegion> frames = new Array<TextureRegion>();

        TextureRegion coinAtlas = screen.getCollectAtlas().findRegion("bigCoin");

        invisible = new TextureRegion(coinAtlas,0,0,1,1);
        idle = new TextureRegion(coinAtlas,0,0,64,100);

        frames.add(new TextureRegion(coinAtlas,0,0,64,100));
        frames.add(new TextureRegion(coinAtlas,64,0,64,100));
        frames.add(new TextureRegion(coinAtlas,128,0,64,100));
        frames.add(new TextureRegion(coinAtlas,192,0,64,100));
        frames.add(new TextureRegion(coinAtlas,256,0,64,100));
        frames.add(new TextureRegion(coinAtlas,320,0,64,100));
        animation = new Animation<>(0.15f,frames); frames.clear();

        setRegion(idle);
        setBounds((x-(64/2)),(y-(100/2)),64,100);
    }

    public void update(float delta){
        animTimer += delta;
        if(coinGet && !collected){
            world.destroyBody(body);
            collected = true;
            setRegion(invisible);
            animTimer = 0;
        }
        if(!collected){
            setRegion((TextureRegion) animation.getKeyFrame(animTimer,true));
        }
    }

    public void draw(Batch batch){
        super.draw(batch);
    }

    public void collected(){
        coinGet = true;
    }
}

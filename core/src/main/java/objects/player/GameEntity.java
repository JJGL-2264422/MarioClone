package objects.player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class GameEntity extends Sprite {

    //Propiedades del jugador.
    public GameEntity(float width, float height, Body body){
    }

    public abstract void update();
    public abstract void render(SpriteBatch batch);


}

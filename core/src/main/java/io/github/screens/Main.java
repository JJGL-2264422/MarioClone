package io.github.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static Main INSTANCE;
    public int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;


    public Main(){
        INSTANCE = this;
    }

    @Override
    public void create() {
        Preferences prefs = Gdx.app.getPreferences("playerData");
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.orthographicCamera = new OrthographicCamera();
        this.orthographicCamera.setToOrtho(false,widthScreen,heightScreen);

        if(prefs.getBoolean("tutorial",false))
            setScreen(new GameScreen(orthographicCamera,0,0,false,false,5,300,0,0));
        else
            setScreen(new GameScreen(orthographicCamera,0,3,false,false,5,300,0,0));

    }
}

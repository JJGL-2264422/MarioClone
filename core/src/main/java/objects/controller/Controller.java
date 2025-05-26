package objects.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.GameScreen;

public class Controller {
    Viewport viewport;
    Stage stage;
    boolean upPress, downPress, leftPress, rightPress;
    OrthographicCamera camera;
    public Controller(){
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),camera);
        stage = new Stage(viewport, GameScreen.batch);
        Gdx.input.setInputProcessor(stage);

        Table directionLnR = new Table();
        directionLnR.setPosition(150, 60);

        Table directionUnD = new Table();
        directionUnD.setPosition(Gdx.graphics.getWidth()-100, 90);

        Image upImg = new Image(new Texture("textures/ArrowU.png"));
        upImg.setSize(64,64);
        upImg.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPress = false;
            }
        });

        Image downImg = new Image(new Texture("textures/ArrowD.png"));
        downImg.setSize(64,64);
        downImg.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPress = false;
            }
        });

        Image rightImg = new Image(new Texture("textures/ArrowR.png"));
        rightImg.setSize(64,64);
        rightImg.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPress = false;
            }
        });

        Image leftImg = new Image(new Texture("textures/ArrowL.png"));
        leftImg.setSize(64,64);
        leftImg.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPress = false;
            }
        });

        directionLnR.add();
        directionLnR.row().pad(5,5,5,5);
        directionLnR.add(leftImg).size(leftImg.getWidth(),leftImg.getHeight());
        directionLnR.add().size(64,64);
        directionLnR.add(rightImg).size(rightImg.getWidth(),rightImg.getHeight());
        directionLnR.row().padBottom(5);
        directionLnR.add().size(0,20);

        directionUnD.add(upImg).size(upImg.getWidth(),upImg.getHeight());
        directionUnD.row().pad(5,5,5,5);
        directionUnD.add().size(64,32);
        directionUnD.row().padBottom(5);
        directionUnD.add(downImg).size(downImg.getWidth(),downImg.getHeight());

        stage.addActor(directionLnR);
        stage.addActor(directionUnD);
    }

    public void draw(){
        stage.draw();
    }

    public void resize(int width, int height){
        viewport.update(width,height);
    }

    public boolean isUpPress() {
        return upPress;
    }

    public boolean isDownPress() {
        return downPress;
    }

    public boolean isLeftPress() {
        return leftPress;
    }

    public boolean isRightPress() {
        return rightPress;
    }
}

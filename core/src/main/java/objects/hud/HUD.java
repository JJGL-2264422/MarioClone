package objects.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.screens.Main;

public class HUD {
    public Stage stage;
    private Viewport vp;

    private Integer timer, lives, coins, score;
    private String name;
    private float counter;

    Label nameLabel, coinLabel, timerLabel, timeLabel, livesLabel, scoreLabel;


    @SuppressWarnings("DefaultLocale")
    public HUD(SpriteBatch sb){
        Preferences prefs = Gdx.app.getPreferences("playerData");
        timer = 300; counter = 0;
        name =  prefs.getString("playerNickname","Fabio"); // Placeholders. Tomar los datos desde un SharedPreferences a futuro.
        lives = 5;
        score = 0;
        coins = 0;

        vp = new FitViewport(Main.INSTANCE.widthScreen, Main.INSTANCE.heightScreen, new OrthographicCamera());
        stage = new Stage(vp,sb);

        Table hud = new Table();
        hud.top();
        hud.setFillParent(true);

        Label.LabelStyle HUD_font = new Label.LabelStyle();
        HUD_font.font = new BitmapFont(Gdx.files.internal("UI/SMW.fnt"));
        HUD_font.fontColor = Color.WHITE;

        nameLabel = new Label(name,new Label.LabelStyle(new BitmapFont(Gdx.files.internal("UI/SMW.fnt")), Color.ORANGE));
        livesLabel = new Label("x " + String.format("%02d",lives), HUD_font);
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("UI/SMW.fnt")), Color.GOLDENROD));
        timerLabel = new Label(String.format("%03d",timer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("UI/SMW.fnt")), Color.GOLDENROD));
        coinLabel = new Label("x" + String.format("%02d",coins), HUD_font);
        scoreLabel = new Label(String.format("%06d",score), HUD_font);

        hud.add(nameLabel).expandX().padTop(10);
        hud.add(timeLabel).expandX().padTop(10).padRight(20);
        hud.add(coinLabel).expandX().padTop(10);
        hud.row();
        hud.add(livesLabel).expandX().padTop(2);
        hud.add(timerLabel).expandX().padTop(2);
        hud.add(scoreLabel).expandX().padTop(2).padRight(70);

        stage.addActor(hud);
    }

    public void update(float delta){
        counter += delta;
        if(counter >= 1){
            timer --;
            timerLabel.setText(String.format("%03d",timer));
            counter = 0;
        }
    }

    public void addLife(int value){
        lives += value;
        livesLabel.setText("x " + String.format("%02d",lives));
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
        timerLabel.setText(String.format("%03d",timer));
    }

    public void setLives(Integer lives){
        this.lives = lives;
        livesLabel.setText("x " + String.format("%02d",lives));
    }

    public Integer getTimer(){
        return timer;
    }
    public Integer getLives() {
        return lives;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer newCoin) {
        coins = newCoin;
        coinLabel.setText("x " + String.format("%02d",coins));
    }

    public void addCoins(Integer coinValue) {
        coins += coinValue;
        coinLabel.setText("x " + String.format("%02d",coins));
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer addScore) {
        score += addScore;
        scoreLabel.setText(String.format("%06d",score));
    }
}

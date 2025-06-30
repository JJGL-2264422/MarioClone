package helper;

import static helper.Constants.PPM;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;

import io.github.screens.GameScreen;
import objects.collectables.BigCoin;
import objects.collectables.Coin;
import objects.player.Player;

public class TileMapHelper {

    private TiledMap tileMap;
    private GameScreen gameScreen;
    private Array<Coin> coins; private Array<BigCoin> bigCoins;

    public TileMapHelper(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    //Lee y envía el TileMap con sus objetos
    public OrthogonalTiledMapRenderer setupMap(String mapName){
        tileMap = new TmxMapLoader().load(mapName);
        parseMapObjects(tileMap.getLayers().get("objects").getObjects());
        return new OrthogonalTiledMapRenderer(tileMap);
    }

    //Lee los objetos del TileMap
    public void parseMapObjects(MapObjects mapObjects) {
        coins = new Array<Coin>();
        bigCoins = new Array<BigCoin>();
        for (MapObject mapObject : mapObjects) {

            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }

            //Busca el rectangulo encargado de spawnear al jugador y lo crea en la pantalla.
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectName = mapObject.getName();

                if (rectName.equals("player")) {
                    Body body = BodyHelperServ.createBody(
                        rectangle.getX() + rectangle.getWidth() / 2,
                        rectangle.getY() + rectangle.getHeight() / 2,
                        42,
                        63,
                        false,
                        gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen));
                }

            }

            if (mapObject instanceof EllipseMapObject) {
                EllipseMapObject ellipseObject = (EllipseMapObject) mapObject;
                Ellipse ellipse = ellipseObject.getEllipse();
                String name = mapObject.getName();

                float centerX = (ellipse.x + ellipse.width / 2) / PPM;
                float centerY = (ellipse.y + ellipse.height / 2) / PPM;
                float avgRadius = (ellipse.width + ellipse.height) / 4 / PPM;

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(centerX, centerY);

                Body body = gameScreen.getWorld().createBody(bodyDef);

                CircleShape shape = new CircleShape();
                shape.setRadius(avgRadius);

                body.setUserData(name.toUpperCase());

                body.createFixture(shape, 1f).setSensor(true);
                if(name.equals("coin")) {
                    Coin coin = new Coin(body, gameScreen);
                    body.setUserData(coin);
                    coins.add(coin);
                }

                if(name.equals("bigcoin")) {
                    BigCoin bigCoin = new BigCoin(body, gameScreen);
                    body.setUserData(bigCoin);
                    bigCoins.add(bigCoin);
                }
                shape.dispose();


            }
        }
    }

    //Ubica los objetos del TileMap en la pantalla.
    private void createStaticBody(PolygonMapObject polyObj){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polyObj);
        body.createFixture(shape,1000);
        shape.dispose();
    }

    //Crea los objetos del TileMap como polígonos.
    private Shape createPolygonShape(PolygonMapObject polyObj) {
        float[] vertices = polyObj.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length/2];

        for(int i = 0; i < vertices.length/2;i++){
            Vector2 current = new Vector2(vertices[i*2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }

    public Array<Coin> getCoins(){
        return coins;
    }
    public Array<BigCoin> getBigCoins(){
        return bigCoins;
    }
}

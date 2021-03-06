package com.mygdx.game.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GlobalClasses.Assets;
import com.mygdx.game.Menu.MenuScreen;
import com.mygdx.game.Menu.MenuStage;
import com.mygdx.game.Menu.StarStream;
import com.mygdx.game.MyBaseClasses.MyLabel;
import com.mygdx.game.MyBaseClasses.MyStage;
import com.mygdx.game.MyBaseClasses.OneSpriteStaticActor;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Physics.Asteroid;
import com.mygdx.game.Physics.BadShip;
import com.mygdx.game.Physics.Dot;
import com.mygdx.game.Physics.Pont;
import com.mygdx.game.Physics.SpaceShip;
import com.mygdx.game.Score.ScoreScreen;

import java.util.Vector;

/**
 * Created by tuskeb on 2016. 10. 31..
 */

public class GameStage extends MyStage {
    private float elapsedTime = 0;
    public static final String PREF_COUNT = "PREF_COUNT";
    public static SpaceShip ship;
    public static Vector<BadShip> badships;
    public static Vector<Asteroid> asteroids;
    public static Vector<Dot> reddot;
    public static Vector<Dot> greendot;
    private OneSpriteStaticActor sound, back;
    private float width, height;
    private float speed = 0.5f;
    private float timer;
    public static int time[], score;
    private int badshipcounter = 0;
    public static boolean shoot;
    private MyLabel hpLabel;
    private int hp;
    private  ExplosionActor explosionActor = null;
    private float[] ran;


    public GameStage(Viewport viewport, Batch batch, MyGdxGame game) {
        super(viewport, batch, game);
    }

    @Override
    public void init() {



        shoot = false;

        time = new int[2];
        timer=0;
        score = 0;

        resized();
        soundgenerate();
        hp = 5;
        hpLabel = new MyLabel("Hp "+hp+"/5", game.getLabelStyle());
        hpLabel.setPosition(0, height-hpLabel.getHeight());
        addActor(hpLabel);

        back = new OneSpriteStaticActor(Assets.manager.get(Assets.BACK));
        back.setSize(100,100);
        back.setPosition(width - back.getWidth() - sound.getWidth()-10, height - back.getHeight());
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen(new ScoreScreen(game));
            }
        });
        addActor(back);

        ship = new SpaceShip();
        addActor(ship.actor);
        float arany = (width/5.0f)/ship.actor.getHeight()/4;
        Dot.arany = arany/1.5f;
        BadShip.arany = arany/1.5f;

        ship.actor.setSize(ship.getWidth()*arany,ship.getHeight()*arany);
        ship.actor.setPosition(width/2-(ship.actor.getWidth()*arany)/2,(height/10));

        badships = new Vector<BadShip>();
        greendot = new Vector<Dot>();
        reddot = new Vector<Dot>();
        asteroids = new Vector<Asteroid>();

        generateBadShips();
        generateAsteroids();

        ran = new float[badships.size()];
        ran[0] = 0.5f; //ehez viszonyítja az új hullámot
        for (int i = 1; i < ran.length; i++) {
            ran[i] = (float)(Math.random()*(speed*5-speed*2+2f)+speed*2);
        }
    }

    private void generateBadShips() {
        BadShip b = new BadShip();
        float plusz = (width-width/b.getWidth())/((width/b.getWidth())+2);
        float x = plusz+b.actor.getWidth()/1.5f;
        for (int i = 0; i < width/b.getWidth()-1; i++){
            b = new BadShip();
            addActor(b.actor);
            badships.add(b);
            b.actor.setSize(b.getWidth(),b.getHeight());
            b.actor.setPosition(x,height);
            x += plusz+b.actor.getWidth()/1.5f;
        }
    }

    private void generateAsteroids() {

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer+=delta;
        int t = (int)(timer/60*100);
        time[0] = t/60;
        time[1] = t%60;
        if(hp>=1) {
            shipPhysics();
            enemyPhysics();
            greenPhysics();
            redPhysics();
        }
        sound.setZIndex(Integer.MAX_VALUE);
        back.setZIndex(Integer.MAX_VALUE);
        hpLabel.setZIndex(Integer.MAX_VALUE);
        if(hp >= 0)hpLabel.setText("Hp "+hp+"/5");
        if(hp == 0 && explosionActor==null){
            explosionActor = new ExplosionActor();
            explosionActor.setPosition(ship.actor.getX(),ship.actor.getY());
            ship.actor.remove();
            addActor(explosionActor);
        }
        else if(hp <= 0 && elapsedTime < 2){
            elapsedTime+=delta;
        }
        else if (hp <= 0 && elapsedTime >= 2){
            dispose();
            game.setScreen(new ScoreScreen(game));
        }
        if(explosionActor != null) explosionActor.setZIndex(Integer.MAX_VALUE);
    }

    private void shipPhysics() {
        if (ship.actor.getX() <= width && ship.actor.getX() >= 0) {
            ship.actor.setX(ship.actor.getX() + Gdx.input.getAccelerometerY());
            if(ship.actor.getX() < 0) ship.actor.setX(0);
            if(ship.actor.getX() > width-ship.actor.getWidth()) ship.actor.setX(width-ship.actor.getWidth());
            for(int j = 0; j < reddot.size(); j++){
                if(Pont.utkozik(reddot.get(j).actor,ship.actor)){
                    reddot.get(j).actor.remove();
                    reddot.remove(j);
                    hp--;
                }
            }
        }
    }

    private void enemyPhysics() {
            if (badships.size() <= 0 || badships.get(0).actor.getY() < 0) {
                if (badships.size() != 0) hp--;
                for (int i = 0; i < badships.size(); i++) {
                    badships.get(i).actor.remove();
                }
                badships = new Vector<BadShip>();
                generateBadShips();
            }
        for (int i = 0; i < badships.size() ; i++){
            badships.get(i).actor.setY(badships.get(i).actor.getY()-speed);
            for(int j = 0; j < greendot.size(); j++){
                if(Pont.utkozik(greendot.get(j).actor,badships.get(i).actor)){
                    badships.get(i).actor.remove();
                    badships.remove(i);
                    score++;
                }
            }
            if(i == badshipcounter && Math.random() > 0.97){
                Dot d = new Dot(false);
                reddot.add(d);
                addActor(d.actor);
                d.actor.setPosition(badships.get(i).actor.getWidth()/2+badships.get(i).actor.getX(),badships.get(i).actor.getY());
                badships.get(i);
            }
        }
        badshipcounter++;
        if(badshipcounter> badships.size()) badshipcounter = 0;
    }

    private void greenPhysics() {
        if (shoot) {
            shoot = false;
            Dot dot = new Dot(true);
            dot.actor.setPosition(ship.actor.getX()+ship.actor.getWidth()/2-dot.actor.getWidth()/2, ship.actor.getY() + ship.actor.getHeight());
            addActor(dot.actor);
            greendot.add(dot);
        }
        for (int i = 0; i < greendot.size(); i++) {
            greendot.get(i).actor.setZIndex(0);
            greendot.get(i).actor.setY(greendot.get(i).actor.getY()+speed*5);
        }
    }

    private void redPhysics() {
        for (int i = 0; i < reddot.size() ; i++){
            reddot.get(i).actor.setY(reddot.get(i).actor.getY()-speed*5);
        }
    }

    void soundgenerate(){
        //SOUND
        sound = new OneSpriteStaticActor(MenuStage.musicPlay?Assets.manager.get(Assets.SOUND):Assets.manager.get(Assets.NOSOUND));
        if(MenuStage.musicPlay) MenuScreen.gMusic.setVolume(1f);
        else MenuScreen.gMusic.setVolume(0f);
        sound.setSize(100f,100f);
        sound.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                MenuStage.musicPlay=!MenuStage.musicPlay;
                soundgenerate();
            }
        });
        sound.setPosition(width-sound.getWidth(),height-sound.getHeight());
        addActor(sound);
    }

    @Override
    protected void resized() {
        super.resized();
        setCameraResetToLeftBottomOfScreen();
        width = (((ExtendViewport)getViewport()).getWorldWidth());
        height = ((getViewport()).getWorldHeight());
    }

}

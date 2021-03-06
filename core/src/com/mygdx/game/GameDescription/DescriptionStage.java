package com.mygdx.game.GameDescription;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Game.GameScreen;
import com.mygdx.game.GlobalClasses.Assets;
import com.mygdx.game.Menu.MenuScreen;
import com.mygdx.game.Menu.MenuStage;
import com.mygdx.game.Menu.StarStream;
import com.mygdx.game.MyBaseClasses.MyButton;
import com.mygdx.game.MyBaseClasses.MyLabel;
import com.mygdx.game.MyBaseClasses.MyStage;
import com.mygdx.game.MyBaseClasses.OneSpriteStaticActor;
import com.mygdx.game.MyGdxGame;

/**
 * Created by tuskeb on 2016. 09. 30...
 */
public class DescriptionStage extends MyStage {

    private TextButton textButton;
    private MyLabel myLabel;
    private String s;
    private float width, height;
    private OneSpriteStaticActor sound, back;
    private StarStream starStream;



    public DescriptionStage(Viewport viewport, Batch batch, MyGdxGame game) {
        super(viewport, batch, game);
    }



    public void init() {
        addBackEventStackListener(); //Az eszköz jobbra-balra döntésével lehet az űrhajót irányítani. Az ellenfeleket lőni a képernyőre kattintással lehet(az űhajó csak előre tud lőni).

        s = "Game instruction:\n\r-----------------\n\rYou can control the spaceship\n\rby turning the device left or right.\n\rYou can shoot the enemies\n\rby clicking on the screen\n\r(the spaceship can only shoot forward).";
        resized();

        addActor(starStream= new StarStream());
        starStream.setX(getViewport().getWorldWidth()-starStream.getWidth());
        starStream.setSize(width,height);
        starStream.setZIndex(0);

        StarStream.size[0] = width;
        StarStream.size[1] = height;

        soundgenerate();



        back = new OneSpriteStaticActor(Assets.manager.get(Assets.BACK));
        back.setSize(100,100);
        back.setPosition(width - back.getWidth() - sound.getWidth()-10, height - back.getHeight());
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreenBackByStackPop();
            }
        });
        back.setZIndex(1);
        addActor(back);

        myLabel = new MyLabel(s, game.getLabelStyle());
        addActor(myLabel);
        myLabel.setZIndex(1);
        myLabel.setPosition(width/2-myLabel.getWidth()/2, height/2-myLabel.getHeight()/2);
    }

    @Override
    public void dispose() {
        super.dispose();
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
        sound.setZIndex(1);
        addActor(sound);
    }

    @Override
    protected void resized() {
        super.resized();
        setCameraResetToLeftBottomOfScreen();
        width = ((getViewport()).getWorldWidth());
        height = ((getViewport()).getWorldHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}

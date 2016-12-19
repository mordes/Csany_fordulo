package com.mygdx.game.Physics;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GlobalClasses.Assets;
import com.mygdx.game.MyBaseClasses.OneSpriteActor;
import com.mygdx.game.MyBaseClasses.OneSpriteStaticActor;

/**
 * Created by tanulo on 2016. 12. 19..
 */
public class SpaceShip extends Block {

    public SpaceShip() {
        actor = new OneSpriteStaticActor(Assets.manager.get(Assets.MYSHIP));
    }
}

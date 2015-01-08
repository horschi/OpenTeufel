package org.openteufel.game.entities.player;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.entities.AnimatedEntity;
import org.openteufel.game.entities.WalkingEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class PlayerEntity extends WalkingEntity
{
    public static final int CLASS_WARRIOR = 0;
    public static final int CLASS_SORCEROR = 1;
    public static final int CLASS_ROGUE = 2;

    private final int       playerclass;
    private final boolean   isTown;
    private final String    playerclassinitial;
    private final String    celBasePath;

    public PlayerEntity(final int posX, final int posY, final int playerclass, final boolean isTown)
    {
        super(posX, posY, 10);
        this.playerclass = playerclass;
        this.isTown = isTown;

        String playerclassname;
        switch (playerclass)
        {
            case CLASS_WARRIOR:
                playerclassname = "warrior";
                this.playerclassinitial = "w";
                break;
            case CLASS_SORCEROR:
                playerclassname = "sorceror";
                this.playerclassinitial = "s";
                break;
            case CLASS_ROGUE:
                playerclassname = "rogue";
                this.playerclassinitial = "r";
                break;

            default:
                throw new IllegalArgumentException();
        }
        this.celBasePath = "plrgfx\\" + playerclassname + "\\" + this.playerclassinitial;
    }

    @Override
    protected int getBottomOffset()
    {
        return 0;
    }

    @Override
    protected String getCelPath(final int dir)
    {
        return this.celBasePath + "hb\\" + this.playerclassinitial + "hbwl.cl2";
    }

    @Override
    protected int getNumFrames()
    {
        return 10;
    }

    // e.g.: plrgfx\\rogue\\rhb\\rhbwl.cl2

    //    Character 1
    //    r rogue
    //    s sorceror
    //    w warrior

    //    Character 2
    //    h heavy armor
    //    m
    //    l

    //    Character 3
    //    a axe
    //    b bow
    //    d swort + shield
    //    h mace + shield
    //    m mace
    //    n nothing
    //    s sword
    //    t staff
    //    u shield

    //    Character 4+5
    //    as standing (ready for attack)
    //    at attack
    //    aw walking (ready for attack)
    //
    //    wl town walking
    //    st town standing
    //
    //    ht getting hit
    //
    //    fm fire magic
    //    qm holy magic
    //    lm lightning magic


}

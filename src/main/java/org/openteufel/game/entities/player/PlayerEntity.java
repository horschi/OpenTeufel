package org.openteufel.game.entities.player;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.entities.AnimatedEntity;
import org.openteufel.game.entities.WalkingEntity;
import org.openteufel.game.utils.Position2d;
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

    public PlayerEntity(final Position2d pos, final int playerclass, final boolean isTown)
    {
        super(pos, 5);
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
        this.celBasePath = "plrgfx\\" + playerclassname + "\\" + this.playerclassinitial + "ha" + "\\" + this.playerclassinitial + "ha";
        this.updateAnimation(ANIM_STANDING);
    }


    @Override
    protected int getBottomOffset()
    {
        return 16;
    }

    @Override
    protected String getCelPath(final int animType)
    {
        final StringBuilder ret = new StringBuilder(this.celBasePath);
        switch (animType)
        {
            case ANIM_STANDING:
                if (this.isTown)
                    ret.append("st");
                else
                    ret.append("as");
                break;

            case ANIM_WALKING:
                if (this.isTown)
                    ret.append("wl");
                else
                    ret.append("aw");
                break;
            default:
                throw new IllegalArgumentException();
        }
        ret.append(".cl2");
        return ret.toString();
    }

    @Override
    protected int getNumFrames(final int animType)
    {
        switch (animType)
        {
            case ANIM_STANDING:
                if (this.isTown)
                    return 160 / 8; // st
                else
                    return 64 / 8; // as

            case ANIM_WALKING:
                if (this.isTown)
                    return 64 / 8; // wl
                else
                    return 64 / 8; // aw

            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected int[] getAnimTypes()
    {
        if (this.isTown)
        {
            return new int[] { ANIM_STANDING, ANIM_WALKING, };
        }
        else
        {
            return new int[] { ANIM_STANDING, ANIM_WALKING, };
        }
    }


    @Override
    protected void preWalk(int gametime, int currentFrameId)
    {
    }


    @Override
    protected void finishWalk(int gametime, int currentFrameId)
    {
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

package org.openteufel.game;

import java.io.IOException;

import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class Entity
{
    public static final int TEAM_NEUTRAL = 0;
    public static final int TEAM_GOOD    = 1;
    public static final int TEAM_BAD     = 2;

    protected Position2d    pos;
    protected int           team;
    protected boolean solid;
    private boolean         alive;

    public Entity(final Position2d pos, boolean solid, int team)
    {
        this.pos = pos;
        this.solid = solid;
        this.team = team;
        this.alive = true;
    }

    public boolean isSolid()
    {
        return solid;
    }

    public int getTeam()
    {
        return team;
    }

    public int getEnemyTeam()
    {
        switch (team)
        {
            case TEAM_BAD:
                return TEAM_GOOD;
            case TEAM_GOOD:
                return TEAM_BAD;
            default:
                throw new RuntimeException();
        }
    }

    public Position2d getPos()
    {
        return this.pos;
    }

    protected void killEntity()
    {
        alive = false;
    }

    public boolean isEntityAlive()
    {
        return alive;
    }

    public abstract void preload(ImageLoader imageLoader) throws IOException;

    public abstract void process(int gametime, WorldCallback world);

    public abstract void draw(final ImageLoader imageLoader, Renderer renderer, int screenX, int screenY, int screenZ, double brightness);
}

package org.openteufel.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openteufel.file.dun.DUNFile;
import org.openteufel.ui.ImageLoader;

public class EntityManager implements WorldCallback
{
    private Entity[] entities = new Entity[100];

    public EntityManager(DUNFile dun)
    {
    }

    public void preload(final ImageLoader imageLoader) throws IOException
    {
        for (final Entity ent : this.entities)
        {
            if (ent != null)
            {
                ent.preload(imageLoader);
            }
        }
    }

    public void process(final int gametime)
    {
        for (int i = entities.length - 1; i >= 0; i--)
        {
            final Entity ent = this.entities[i];
            if (ent != null)
            {
                ent.process(gametime, this);
                if (!ent.isEntityAlive())
                    entities[i] = null;
            }
        }
    }

    public void addEntity(final Entity ent)
    {
        for (int i = this.entities.length - 1; i >= 0; i--)
        {
            Entity t = this.entities[i];
            if (t == null || !t.isEntityAlive())
            {
                this.entities[i] = ent;
                return;
            }
        }
        final Entity[] newents = new Entity[this.entities.length + 100];
        System.arraycopy(this.entities, 0, newents, 0, this.entities.length);
        this.entities = newents;
        this.addEntity(ent);
    }

    public Entity getEntityClosest(final int x, final int y, final int maxradius, int team)
    {
        Entity ret = null;
        double retdist = maxradius;
        for (final Entity ent : this.entities)
        {
            if (ent != null && (team == Entity.TEAM_NEUTRAL || ent.getTeam() == team))
            {
                final long diffX = Math.abs(ent.getPos().getPosX() - x);
                final long diffY = Math.abs(ent.getPos().getPosY() - y);
                if (diffX > retdist || diffY > retdist)
                    continue; // avoid sqrt if possible
                final double dist = Math.sqrt(diffX * diffX + diffY * diffY);
                if (dist < retdist)
                {
                    ret = ent;
                    retdist = dist;
                }
            }
        }
        return ret;
    }

    public List<Entity> getEntities(final int x, final int y, final int radius, int team)
    {
        final List<Entity> ret = new ArrayList<Entity>();
        for (final Entity ent : this.entities)
        {
            if (ent != null && (team == Entity.TEAM_NEUTRAL || ent.getTeam() == team))
            {
                final long diffX = Math.abs(ent.getPos().getPosX() - x);
                final long diffY = Math.abs(ent.getPos().getPosY() - y);
                if (diffX > radius || diffY > radius)
                    continue; // avoid sqrt if possible
                final double dist = Math.sqrt(diffX * diffX + diffY * diffY);
                if (dist <= radius)
                    ret.add(ent);
            }
        }
        return ret;
    }

    public List<Entity> getEntities(final int x1, final int y1, final int x2, final int y2, int team)
    {
        final List<Entity> ret = new ArrayList<Entity>();
        for (final Entity ent : this.entities)
        {
            if (ent != null && (team == Entity.TEAM_NEUTRAL || ent.getTeam() == team))
            {
                final int entX = ent.getPos().getPosX();
                if (entX <= x2 && entX >= x1)
                {
                    final int entY = ent.getPos().getPosY();
                    if (entY <= y2 && entY >= y1)
                    {
                        ret.add(ent);
                    }
                }
            }
        }
        return ret;
    }
}

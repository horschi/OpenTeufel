package org.openteufel.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openteufel.ui.ImageLoader;

public class EntityManager
{
    private Entity[] entities = new Entity[100];

    public EntityManager()
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
        for (final Entity ent : this.entities)
        {
            if (ent != null)
            {
                ent.process(gametime);
            }
        }
    }

    public void addEntity(final Entity ent)
    {
        for (int i = this.entities.length - 1; i >= 0; i--)
        {
            if (this.entities[i] == null)
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

    public Entity getEntityClosest(final int x, final int y, final int maxradius)
    {
        Entity ret = null;
        double retdist = maxradius;
        for (final Entity ent : this.entities)
        {
            if (ent != null)
            {
                final long diffX = Math.abs(ent.getPosX() - x);
                final long diffY = Math.abs(ent.getPosY() - y);
                if (diffX > retdist || diffY > retdist)
                    continue;
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

    public List<Entity> getEntities(final int x, final int y, final int radius)
    {
        final List<Entity> ret = new ArrayList<Entity>();
        for (final Entity ent : this.entities)
        {
            if (ent != null)
            {
                final int diffX = Math.abs(ent.getPosX() - x);
                final int diffY = Math.abs(ent.getPosY() - y);
                if (diffX > radius || diffY > radius)
                    continue;
                final double dist = Math.sqrt(diffX * diffX + diffY * diffY);
                if (dist <= radius)
                    ret.add(ent);
            }
        }
        return ret;
    }

    public List<Entity> getEntities(final int x1, final int y1, final int x2, final int y2)
    {
        final List<Entity> ret = new ArrayList<Entity>();
        for (final Entity ent : this.entities)
        {
            if (ent != null)
            {
                final int entX = ent.getPosX();
                if (entX <= x2 && entX >= x1)
                {
                    final int entY = ent.getPosY();
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

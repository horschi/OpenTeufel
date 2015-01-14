package org.openteufel.game;

public interface WorldCallback
{
    public void addEntity(final Entity ent);
    public Entity getEntityClosest(final int x, final int y, final int maxradius, int team);
}

package org.openteufel.game;

public interface WorldCallback
{
    public void addEntity(final Entity ent);

    public Entity getEntityClosest(final int x, final int y, final int maxradius, int team);

    public boolean isSolid(int tileX, int tileY, boolean isRanged);
    
    public Entity hasEntity(int tileX, int tileY, boolean onlySolid, int selectTeam);

    public boolean isWalkable(int tileX, int tileY);
}

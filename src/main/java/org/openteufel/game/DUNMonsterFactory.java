package org.openteufel.game;

import org.openteufel.game.entities.DummyEntity;
import org.openteufel.game.utils.Position2d;

public class DUNMonsterFactory
{
    public static Entity createEntityFromMonsterId(final int id, final int x, final int y)
    {
        switch (id)
        {
            case 0:
                break;

            default:
                break;
        }
        return new DummyEntity(Position2d.byTile(x, y), "m" + id);
    }

}

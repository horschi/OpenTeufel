package org.openteufel.game;

import org.openteufel.game.entities.DummyEntity;

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
        return new DummyEntity(x * 32, y * 32, "m" + id);
    }

}

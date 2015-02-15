package org.openteufel.game;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.lwjgl.input.Mouse;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.MINPillar;
import org.openteufel.file.dun.SOLFile;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.entities.player.PlayerEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.MouseEvent;
import org.openteufel.ui.MouseHandler;
import org.openteufel.ui.Renderer;

public class LevelRenderer implements MouseHandler
{
    private final ImageLoader<?> imageLoader;
    private final LevelState     levelstate;
    private final Renderer       renderer;
    private int                  screenWidth, screenHeight;
    private int                  cameraX, cameraY;
    private Point                lastClickPos;
    private boolean              lmbIsPressed;

    public LevelRenderer(final GamedataLoader dataLoader, final LevelState levelstate, final Renderer renderer) throws IOException
    {
        super();
        this.levelstate = levelstate;
        this.renderer = renderer;
        renderer.registerMouseHandler(this);

        this.imageLoader = new ImageLoader(dataLoader, renderer, levelstate.getPALPath());
        this.imageLoader.preloadTileCel(levelstate.getCELPath(), levelstate.getAllFrameIdsPlus1Pillars());
        levelstate.getEntityManager().preload(this.imageLoader);
    }

    public void renderFrame()
    {
        final EntityManager entityManager = this.levelstate.getEntityManager();

        this.cameraX = this.levelstate.getCameraPos().getPosX();
        this.cameraY = this.levelstate.getCameraPos().getPosY();

        this.screenWidth = this.renderer.getScreenWidth();
        this.screenHeight = this.renderer.getScreenHeight();

        final int screenHalfWidthIso = this.screenWidth / 2 + 32;
        final int screenHalfHeightIso = this.screenHeight / 2 + 32;

        // sort entities
        final List<Entity> entities;
        {
            final int screenHalfWidthCart = isometricToCartesianX(screenHalfWidthIso, screenHalfHeightIso);
            final int screenHalfHeightCart = isometricToCartesianY(screenHalfWidthIso, screenHalfHeightIso);
    
            entities = entityManager.getEntities(this.cameraX - screenHalfWidthCart - screenHalfHeightCart, this.cameraY - screenHalfWidthCart - screenHalfHeightCart, this.cameraX + screenHalfWidthCart + screenHalfHeightCart, this.cameraY + screenHalfWidthCart + screenHalfHeightCart, Entity.TEAM_NEUTRAL);
            Collections.sort(entities, new Comparator<Entity>()
            {
                @Override
                public int compare(final Entity a, final Entity b)
                {
                    final int ay = cartesianToIsometricY(a.getPos().getPosX(), a.getPos().getPosY());
                    final int by = cartesianToIsometricY(b.getPos().getPosX(), b.getPos().getPosY());
                    return by - ay;
                }
            });
        }

        final int screenTilesWidthIso = (screenHalfWidthIso * 2) / 32;
        final int screenTilesHeightIso = (screenHalfHeightIso * 4) / 32 + 12;
        
        int startWorldTileX = (cameraX - isometricToCartesianX(screenHalfWidthIso+32, screenHalfHeightIso))/32;
        int startWorldTileY = (cameraY - isometricToCartesianY(screenHalfWidthIso+32, screenHalfHeightIso))/32;
        for (int y = 0; y <= screenTilesHeightIso; y++)
        {
            drawTileLine(startWorldTileX , startWorldTileY, screenTilesWidthIso, true);
            drawEntities(entities, cartesianToIsometricY((startWorldTileX) * 32, startWorldTileY * 32)+8);
            drawTileLine(startWorldTileX , startWorldTileY, screenTilesWidthIso, false);

            if((y & 1) == 0)
                startWorldTileX++;
            else
                startWorldTileY++;
        }
    }
    
    private void drawTileLine(int rowStartWorldX, int rowStartWorldY, int screenTilesWidthIso, boolean floor)
    {
        for (int x = 0; x <= screenTilesWidthIso; x++)
        {
            if (rowStartWorldX >= 0 && rowStartWorldY >= 0)
                this.drawSingleTile(rowStartWorldX, rowStartWorldY, floor);

            rowStartWorldX++;
            rowStartWorldY--;
        }
    }

    private void drawEntities(List<Entity> entities, int maxIsoY)
    {
        while (entities.size() > 0)
        {
            final Entity ent = entities.get(entities.size() - 1);
            final int entY = cartesianToIsometricY(ent.getPos().getPosX(), ent.getPos().getPosY());
            if (entY >= maxIsoY)
                break;
            this.drawEntity(ent, 0);
            entities.remove(entities.size() - 1);
        }
    }

    private void drawEntity(final Entity ent, int screenZ)
    {
        final int tileDifX = this.cameraX - ent.getPos().getPosX();
        final int tileDifY = this.cameraY - ent.getPos().getPosY();

        final int ixBase = (this.screenWidth >> 1) - cartesianToIsometricX(tileDifX, tileDifY);
        final int iyBase = (this.screenHeight >> 1) - cartesianToIsometricY(tileDifX, tileDifY);
        ent.draw(this.imageLoader, this.renderer, ixBase, iyBase, screenZ, this.levelstate.calculateBrightness(ent.getPos().getPosX(), ent.getPos().getPosY()));
    }

    private void drawSingleTile(final int worldTileX, final int worldTileY, boolean floor)
    {
        final short[] tilsquare = this.levelstate.getSquare(worldTileX / 2, worldTileY / 2);
        if (tilsquare != null)
        {
            int tileOffX = worldTileX & 1;
            int tileOffY = worldTileY & 1;
            short pillarId = tilsquare[tileOffX + (tileOffY * 2)];
            final MINPillar pillar = this.levelstate.getPillar(pillarId);

            int tilePosX = worldTileX << 5;
            int tilePosY = worldTileY << 5;
            double brightness = this.levelstate.calculateBrightness(tilePosX, tilePosY);
            // if (levelstate.isSolid(worldTileX, worldTileY, false))
            //    brightness = 0.4;

            final int difX = this.cameraX - tilePosX;
            final int difY = this.cameraY - tilePosY;
            final int ixBase = (this.screenWidth >> 1) - cartesianToIsometricX(difX, difY) - 16;
            final int iyBase = (this.screenHeight >> 1) - cartesianToIsometricY(difX, difY) - 16;

            drawPillar(pillar, ixBase, iyBase, brightness, floor);
        }
    }

    public void drawPillar(MINPillar pillar, int ixBase, int iyBase, double brightness, boolean floor)
    {
        final int num = pillar.getNumBlocks();
        final int start, end;
        if (floor)
        {
            start = 0;
            end = 2;
        }
        else
        {
            start = 2;
            end = num;
        }
        for (int pillarZ = start; pillarZ < end; pillarZ++)
        {
            final int frameIdPlus1 = pillar.getFrameNumPlus1(pillarZ);
            if (frameIdPlus1 > 0)
            {
                final int xoff = num - ((pillarZ & 1) * 32);
                final int yoff = ((pillarZ) >> 1) * 32;

                final int ix = ixBase + xoff;
                final int iy = iyBase - yoff;

                this.renderer.drawImage(this.imageLoader.loadTileImage(this.levelstate.getCELPath(), frameIdPlus1), ix, iy, 0, brightness);
            }
        }
    }

    public void applyUserInput()
    {
        if (this.lastClickPos != null)
        {
            final int isoX = this.lastClickPos.x;
            final int isoY = this.lastClickPos.y;
            if (!lmbIsPressed)
            {
                this.lastClickPos = null;
            }

            this.levelstate.updateCamPos(isometricToCartesianX(isoX, isoY), isometricToCartesianY(isoX, isoY));
        }
    }

    //

    private static int cartesianToIsometricX(final int cartX, final int cartY)
    {
        return cartX - cartY;
    }

    private static int cartesianToIsometricY(final int cartX, final int cartY)
    {
        return (cartX + cartY) / 2;
    }

    private static int isometricToCartesianX(final int isoX, final int isoY)
    {
        return (2 * isoY + isoX) / 2;
    }

    private static int isometricToCartesianY(final int isoX, final int isoY)
    {
        return (2 * isoY - isoX) / 2;
    }

    @Override
    public void handleMouseEvent(MouseEvent e)
    {
        if (e.type == MouseEvent.eventType.COMBINED || e.type == MouseEvent.eventType.RELEASE || e.type == MouseEvent.eventType.PRESS)
        {
            if (e.button == 0)
            {
                lmbIsPressed = e.eventState;
            }
        }

        if (lmbIsPressed)
        {
            this.lastClickPos = new Point(e.x, e.y);
        }
    }
}

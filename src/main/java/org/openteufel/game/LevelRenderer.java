package org.openteufel.game;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.MINPillar;
import org.openteufel.file.dun.TILSquare;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class LevelRenderer
{
    private final ImageLoader<?> imageLoader;
    private final LevelState     levelstate;
    private final Renderer       renderer;
    private int                  screenWidth, screenHeight;
    private int                  cameraX, cameraY;

    public LevelRenderer(final GamedataLoader dataLoader, final LevelState levelstate, final Renderer renderer) throws IOException
    {
        super();
        this.levelstate = levelstate;
        this.renderer = renderer;

        this.imageLoader = new ImageLoader(dataLoader, renderer, levelstate.getPALPath());
        this.imageLoader.preloadTileCel(levelstate.getCELPath(), levelstate.getAllFrameIdsPlus1Pillars());
        levelstate.getEntityManager().preload(this.imageLoader);
    }

    public void renderFrame()
    {
        final EntityManager entityManager = this.levelstate.getEntityManager();

        this.cameraX = this.levelstate.getCameraX();
        this.cameraY = this.levelstate.getCameraY();

        this.renderer.startFrame();
        this.screenWidth = this.renderer.getScreenWidth();
        this.screenHeight = this.renderer.getScreenHeight();

        final int screenHalfWidthIso = this.screenWidth / 2 + 32;
        final int screenHalfHeightIso = this.screenHeight / 2 + 32;

        final int screenHalfWidthCart = isometricToCartesianX(screenHalfWidthIso, screenHalfHeightIso);
        final int screenHalfHeightCart = isometricToCartesianY(screenHalfWidthIso, screenHalfHeightIso);

        int startWorldX = (this.cameraX - screenHalfWidthCart) & 0xffffffc0;
        int startWorldY = (this.cameraY - screenHalfHeightCart) & 0xffffffc0;

        final List<Entity> entities = entityManager.getEntities(this.cameraX - screenHalfWidthCart - screenHalfHeightCart, this.cameraY - screenHalfWidthCart - screenHalfHeightCart, this.cameraX + screenHalfWidthCart + screenHalfHeightCart, this.cameraY + screenHalfWidthCart + screenHalfHeightCart);
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

        final int screenTilesWidthIso = (screenHalfWidthIso * 2) / 64;
        final int screenTilesHeightIso = (screenHalfHeightIso * 2) / 64 + 4;
        for (int y = 0; y <= screenTilesHeightIso; y++)
        {
            int rowStartWorldX = startWorldX - isometricToCartesianX(64, 0);
            int rowStartWorldY = startWorldY - isometricToCartesianY(64, 0);
            for (int x = 0; x <= screenTilesWidthIso; x++)
            {
                if (rowStartWorldX >= 0 && rowStartWorldY >= 0)
                    this.drawSingleTile((rowStartWorldX) / 64, (rowStartWorldY) / 64);

                rowStartWorldX += isometricToCartesianX(128, 0);
                rowStartWorldY += isometricToCartesianY(128, 0);
            }

            rowStartWorldX = startWorldX;
            rowStartWorldY = startWorldY;
            for (int x = 0; x <= screenTilesWidthIso; x++)
            {
                if (rowStartWorldX >= 0 && rowStartWorldY >= 0)
                    this.drawSingleTile((rowStartWorldX) / 64, (rowStartWorldY) / 64);

                rowStartWorldX += isometricToCartesianX(128, 0);
                rowStartWorldY += isometricToCartesianY(128, 0);
            }

            while (entities.size() > 0)
            {
                final Entity ent = entities.get(entities.size() - 1);
                final int entY = cartesianToIsometricY(ent.getPos().getPosX(), ent.getPos().getPosY());
                if (entY >= cartesianToIsometricY(startWorldX, startWorldY))
                    break;
                this.drawEntity(ent);
                entities.remove(entities.size() - 1);
            }
            startWorldX += isometricToCartesianX(0, 64);
            startWorldY += isometricToCartesianY(0, 64);
        }

        this.renderer.finishFrame();
    }

    private void drawEntity(final Entity ent)
    {
        final int tileDifX = this.cameraX - ent.getPos().getPosX();
        final int tileDifY = this.cameraY - ent.getPos().getPosY();

        final int ixBase = (this.screenWidth >> 1) - this.cartesianToIsometricX(tileDifX, tileDifY);
        final int iyBase = (this.screenHeight >> 1) - this.cartesianToIsometricY(tileDifX, tileDifY);
        ent.draw(this.imageLoader, this.renderer, ixBase, iyBase, this.calculateBrightness(ent.getPos().getPosX(), ent.getPos().getPosY()));
    }

    private void drawSingleTile(final int worldTileX, final int worldTileY)
    {
        final TILSquare tilsquare = this.levelstate.getSquare(worldTileX, worldTileY);
        if (tilsquare != null)
        {
            final MINPillar pillarTop = this.levelstate.getPillar(tilsquare.getPillarTop());
            final MINPillar pillarBottom = this.levelstate.getPillar(tilsquare.getPillarBottom());
            final MINPillar pillarLeft = this.levelstate.getPillar(tilsquare.getPillarLeft());
            final MINPillar pillarRight = this.levelstate.getPillar(tilsquare.getPillarRight());

            this.drawPillar(pillarTop, worldTileX, worldTileY, -16, -16, this.calculateBrightness(worldTileX*64, worldTileY*64)); // 0 -16
            this.drawPillar(pillarLeft, worldTileX, worldTileY, -48, 0, this.calculateBrightness(worldTileX*64, worldTileY*64)); // -32 0
            this.drawPillar(pillarRight, worldTileX, worldTileY, 16, 0, this.calculateBrightness(worldTileX*64, worldTileY*64)); // 32 0
            this.drawPillar(pillarBottom, worldTileX, worldTileY, -16, 16, this.calculateBrightness(worldTileX*64, worldTileY*64)); // 0 16
        }
    }

    private double calculateBrightness(final int x, final int y)
    {
        final int diffX = this.cameraX-x;
        final int diffY = this.cameraY-y;

        double brightness = 1.3 - (Math.sqrt( (diffX*diffX) + (diffY*diffY) ) / 700);
        if(brightness > 1.0)
            brightness = 1.0;
        if(brightness < 0.0)
            brightness = 0.0;
        return brightness;
    }

    private void drawPillar(final MINPillar pillar, final int worldTileX, final int worldTileY, final int offsetX, final int offsetY, final double brightness)
    {
        final int tileDifX = this.cameraX - worldTileX * 64;
        final int tileDifY = this.cameraY - worldTileY * 64;

        final int ixBase = (this.screenWidth >> 1) - this.cartesianToIsometricX(tileDifX, tileDifY) + offsetX;
        final int iyBase = (this.screenHeight >> 1) - this.cartesianToIsometricY(tileDifX, tileDifY) + offsetY;

        final int num = pillar.getNumBlocks();
        for (int pillarZ = 0; pillarZ < num; pillarZ++)
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
        final Point screenPos = this.renderer.getLastRelativeClickPos();
        if (screenPos != null)
        {
            final int isoX = screenPos.x;
            final int isoY = screenPos.y;
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
}

package org.openteufel.game;

import java.awt.Point;
import java.io.IOException;

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

        final int screenTileHalfWidth = this.screenWidth / 2;
        final int screenTileHalfHeight = this.screenHeight / 2;

        //        final int startWorldTileX = this.cameraX - screenTileHalfWidth - screenTileHalfHeight;
        //        final int startWorldTileY = this.cameraY + screenTileHalfWidth - screenTileHalfHeight;
        //        final int endWorldTileX = this.cameraX + screenTileHalfWidth + screenTileHalfHeight;
        //        final int endWorldTileY = this.cameraY - screenTileHalfWidth + screenTileHalfHeight;

        for (int worldTileY = (this.cameraY - screenTileHalfWidth - screenTileHalfHeight) / 64; worldTileY < (this.cameraY + screenTileHalfWidth + screenTileHalfHeight) / 64; worldTileY++)
        {
            for (int worldTileX = (this.cameraX - screenTileHalfWidth - screenTileHalfHeight) / 64; worldTileX < (this.cameraX + screenTileHalfWidth + screenTileHalfHeight) / 64; worldTileX++)
            {
                this.drawSingleTile(worldTileX, worldTileY);
            }
        }

        for (final Entity ent : entityManager.getEntities(this.cameraX - screenTileHalfWidth - screenTileHalfHeight, this.cameraY - screenTileHalfWidth - screenTileHalfHeight, this.cameraX + screenTileHalfWidth + screenTileHalfHeight, this.cameraY + screenTileHalfWidth + screenTileHalfHeight))
        {
            final int tileDifX = this.cameraX - ent.getPosX();
            final int tileDifY = this.cameraY - ent.getPosY();

            final int ixBase = (this.screenWidth >> 1) - this.cartesianToIsometricX(tileDifX, tileDifY) ;
            final int iyBase = (this.screenHeight >> 1) - this.cartesianToIsometricY(tileDifX, tileDifY) ;
            ent.draw(this.imageLoader, this.renderer, ixBase, iyBase, 1.0);
        }

        this.renderer.finishFrame();
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

            this.drawPillar(pillarTop, worldTileX, worldTileY, -16, -16); // 0 -16
            this.drawPillar(pillarLeft, worldTileX, worldTileY, -48, 0); // -32 0
            this.drawPillar(pillarRight, worldTileX, worldTileY, 16, 0); // 32 0
            this.drawPillar(pillarBottom, worldTileX, worldTileY, -16, 16); // 0 16
        }
    }

    private void drawPillar(final MINPillar pillar, final int worldTileX, final int worldTileY, final int offsetX, final int offsetY)
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

                this.renderer.drawImage(this.imageLoader.loadTileImage(this.levelstate.getCELPath(), frameIdPlus1), ix, iy, 1.0);
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

package org.openteufel.ui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.cel.CELFile;
import org.openteufel.file.cel.PALFile;

public class ImageLoader<ImageType>
{
    private final GamedataLoader         dataloader;
    private final Renderer<ImageType>    renderer;
    private final Map<String, ImageType> imageCache = new HashMap<String, ImageType>();
    private PALFile                      palette    = null;

    public ImageLoader(final GamedataLoader dataloader, final Renderer<ImageType> renderer, final String palName) throws IOException
    {
        this.dataloader = dataloader;
        this.renderer = renderer;

        final ByteBuffer data = this.dataloader.getFileByteBuffer(palName);
        if (data == null)
            throw new IOException("Missing PAL: " + palName);
        this.palette = new PALFile(data);

    }

    public void close(final String palName)
    {
        for (final ImageType obj : this.imageCache.values())
        {
            this.renderer.unloadImage(obj);
        }
        this.imageCache.clear();
    }

    public void preloadCel(final String celName, final int frameIdPlus1) throws IOException
    {
        if (this.palette == null)
            throw new IllegalStateException("Missing palette");
        final ByteBuffer data = this.dataloader.getFileByteBuffer(celName);
        if (data == null)
            throw new IOException("Missing CEL: " + celName);
        final CELFile cel = new CELFile(data, celName.toLowerCase().endsWith(".cl2"));

        final String hashMapId = this.genHashMapId(celName, frameIdPlus1);
        if (!this.imageCache.containsKey(hashMapId))
        {
            final int frameId = (frameIdPlus1 & 0x0FFF) - 1;
            final int type = (frameIdPlus1 & 0x7000) >> 12;
            final int[] pixels = cel.getFramePixelsTypeManual(frameId, this.palette, type);
            final int w = 32;
            final int h = pixels.length / w;
            if (pixels.length % w != 0)
                throw new IOException("Cannot find right size for CEL: " + celName + " / " + frameId + " / " + pixels.length);
            final ImageType obj = this.renderer.loadImage(pixels, w, h);
            if (obj == null)
                throw new IOException("Renderer failed caching image: " + celName + " / " + frameId + " / " + pixels.length);
            this.imageCache.put(hashMapId, obj);
        }
    }

    public void preloadCel(final String celName, final List<Integer> frameIdsPlus1) throws IOException
    {
        if (this.palette == null)
            throw new IllegalStateException("Missing palette");

        final ByteBuffer data = this.dataloader.getFileByteBuffer(celName);
        if (data == null)
            throw new IOException("Missing CEL: " + celName);
        final CELFile cel = new CELFile(data, celName.toLowerCase().endsWith(".cl2"));
        for (int i = 0; i < frameIdsPlus1.size(); i++)
        {
            final int frameIdPlus1 = frameIdsPlus1.get(i);
            final int frameId = (frameIdPlus1 & 0x0FFF) - 1;
            final int type = (frameIdPlus1 & 0x7000) >> 12;
            final String hashMapId = this.genHashMapId(celName, frameIdPlus1);
            if (!this.imageCache.containsKey(hashMapId))
            {
                final int[] pixels = cel.getFramePixelsTypeManual(frameId, this.palette, type);
                final int w = 32;
                final int h = pixels.length / w;
                if (pixels.length % w != 0)
                    throw new IOException("Cannot find right size for CEL: " + celName + " / " + frameId + " / " + pixels.length);
                final ImageType obj = this.renderer.loadImage(pixels, w, h);
                if (obj == null)
                    throw new IOException("Renderer failed caching image: " + celName + " / " + frameId + " / " + pixels.length);
                this.imageCache.put(hashMapId, obj);
            }
        }
    }

    public Object loadImage(final String celName, final int frameIdPlus1)
    {
        final String hashMapId = this.genHashMapId(celName, frameIdPlus1);
        final Object ret = this.imageCache.get(hashMapId);
        if (ret == null)
            throw new RuntimeException("Image not loaded: " + celName + " / " + frameIdPlus1);
        return ret;
    }

    private String genHashMapId(final String celName, final int frame)
    {
        return celName.toLowerCase().toLowerCase() + "_" + frame;
    }
}

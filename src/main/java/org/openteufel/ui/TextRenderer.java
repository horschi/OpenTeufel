package org.openteufel.ui;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.nio.charset.Charset;

import org.apache.commons.imaging.formats.pcx.PcxImageParser;
import org.openteufel.file.GamedataLoader;

public class TextRenderer
{
    private static final String  FILENAME_FONT_SMALL  = "\\ctrlpan\\smaltext.cel";       // 16x16

    private static final String  FILENAME_FONT_42GOLD = "ui_art\\font42g.pcx";           // gold
    private static final String  FILENAME_FONT_42GREY = "ui_art\\font42y.pcx";           // dark  
    private static final String  FILENAME_WIDTH_42    = "ui_art\\font42.bin";

    private static final String  FILENAME_FONT_30GOLD = "ui_art\\font30g.pcx";           // gold
    private static final String  FILENAME_FONT_30GREY = "ui_art\\font30s.pcx";           // grey
    private static final String  FILENAME_WIDTH_30    = "ui_art\\font30.bin";

    private static final String  FILENAME_FONT_24GOLD = "ui_art\\font24g.pcx";           // gold
    private static final String  FILENAME_FONT_24GREY = "ui_art\\font24s.pcx";           // grey
    private static final String  FILENAME_WIDTH_24    = "ui_art\\font24.bin";

    private static final String  FILENAME_FONT_16GOLD = "ui_art\\font16g.pcx";           // gold
    private static final String  FILENAME_FONT_16GREY = "ui_art\\font16s.pcx";           // grey
    private static final String  FILENAME_WIDTH_16    = "ui_art\\font16.bin";

    private static final Charset charset              = Charset.forName("ISO-8859-1");
    private static final byte[]  CHARS                = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u00a1\u00a2\u00a3\u00a4\u00a5\u00a6\u00a7\u00a8\u00a9\u00aa\u00ab\u00acSHY\u00ae\u00af\u00b0\u00b1\u00b2\u00b3\u00b4\u00b5\u00b6\u00b7\u00b8\u00b9\u00ba\u00bb\u00bc\u00bd\u00be\u00bf\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d0\u00d1\u00d2\u00d3\u00d4\u00d5\u00d6\u00d7\u00d8\u00d9\u00da\u00db\u00dc\u00dd\u00de\u00df\u00e0\u00e1\u00e2\u00e3\u00e4\u00e5\u00e6\u00e7\u00e8\u00e9\u00ea\u00eb\u00ec\u00ed\u00ee\u00ef\u00f0\u00f1\u00f2\u00f3\u00f4\u00f5\u00f6\u00f7\u00f8\u00f9\u00fa\u00fb\u00fc\u00fd\u00fe\u00ff"
                                                                      .getBytes(charset);

    private final Renderer       renderer;
    private final GamedataLoader dataloader;

    private final Object[]       imagesSmall;
    private final Object[]       images42Gold;
    private final Object[]       images42Grey;
    private final Object[]       images30Gold;
    private final Object[]       images30Grey;
    private final Object[]       images24Gold;
    private final Object[]       images24Grey;
    private final Object[]       images16Gold;
    private final Object[]       images16Grey;

    private final int[]          widthsSmall;
    private final int[]          widths42;
    private final int[]          widths30;
    private final int[]          widths24;
    private final int[]          widths16;

    public TextRenderer(final Renderer renderer, GamedataLoader dataloader)
    {
        super();
        this.renderer = renderer;
        this.dataloader = dataloader;

        try
        {
            widthsSmall = null;
            widths42 = loadWidths(FILENAME_WIDTH_42);
            widths30 = loadWidths(FILENAME_WIDTH_30);
            widths24 = loadWidths(FILENAME_WIDTH_24);
            widths16 = loadWidths(FILENAME_WIDTH_16);

            imagesSmall = null;
            images42Gold = loadImages(FILENAME_FONT_42GOLD, 42);
            images42Grey = loadImages(FILENAME_FONT_42GREY, 42);
            images30Gold = loadImages(FILENAME_FONT_30GOLD, 31);
            images30Grey = loadImages(FILENAME_FONT_30GREY, 31);
            images24Gold = loadImages(FILENAME_FONT_24GOLD, 26);
            images24Grey = loadImages(FILENAME_FONT_24GREY, 26);
            images16Gold = loadImages(FILENAME_FONT_16GOLD, 16);
            images16Grey = loadImages(FILENAME_FONT_16GREY, 16);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private int[] loadWidths(String filename)
    {
        try
        {
            byte[] data = dataloader.getFileByteBuffer(filename).array();
            int[] ret = new int[256];
            for (int i = 0; i < 256; i++)
            {
                int w = (data[i + 2]) & 0xff;
                if (w == 0)
                    w = (data[0]) & 0xff;
                ret[i] = w;
            }
            return ret;
        }
        catch (Exception e)
        {
            throw new RuntimeException("loadWidths(" + filename + ")", e);
        }
    }

    private Object[] loadImages(String filename, int h)
    {
        try
        {
            byte[] data = dataloader.getFileByteBuffer(filename).array();
            PcxImageParser pcxParser = new PcxImageParser();
            BufferedImage img = pcxParser.getBufferedImage(data, null);
            int w = img.getWidth();
            Object[] ret = new Object[256];
            for (byte b : CHARS)
            {
                int character = b & 0xff;
                try
                {
                    int[] pixels = img.getRGB(0, character * h, w, h,  (int[]) null, 0, w);
                    int transparentColor = pixels[pixels.length-1];
                    for(int i=0;i<pixels.length;i++)
                    {
                        int col = pixels[i];
                        if(col == transparentColor)
                            pixels[i] = 0;
                    }
                    ret[character] = renderer.loadImage(pixels, w, h);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error loading char: "+character, e);
                }
            }
            return ret;
        }
        catch (Exception e)
        {
            throw new RuntimeException("loadImages(" + filename + "," + h + ")", e);
        }
    }

    public int writeText(int x, int y, String text, int size)
    {
        Object[] images;
        int[] widths;
        switch (size)
        {
            case 8:
            case -8:
                images = imagesSmall;
                widths = widthsSmall;
                break;

            case 16:
                images = images16Gold;
                widths = widths16;
                break;
            case -16:
                images = images16Grey;
                widths = widths16;
                break;

            case 24:
                images = images24Gold;
                widths = widths24;
                break;
            case -24:
                images = images24Grey;
                widths = widths24;
                break;

            case 30:
                images = images30Gold;
                widths = widths30;
                break;
            case -30:
                images = images30Grey;
                widths = widths30;
                break;

            case 42:
                images = images42Gold;
                widths = widths42;
                break;
            case -42:
                images = images42Grey;
                widths = widths42;
                break;

            default:
                throw new RuntimeException("Illegal size: " + size);
        }

        for (byte b : text.getBytes(charset))
        {
            int ch = b & 0xff;

            Object image = images[ch];
            if (image != null)
                renderer.drawImage(image, x, y, 99, 1.0);

            x += widths[ch];
        }
        return x;
    }
}

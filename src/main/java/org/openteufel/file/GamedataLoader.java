package org.openteufel.file;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.openteufel.file.mpq.MPQArchive;

public class GamedataLoader
{
    private static final String[] MPQLIST = new String[] { "DIABDAT.MPQ", "HELLFIRE.MPQ", "THDATA.MOR" };
    // hfmonk.mpq
    // hfmusic.mpq
    // hfvoice.mpq

    private final MPQArchive[]    mpqFiles;

    public GamedataLoader(final File datadir) throws IOException
    {
        final List<MPQArchive> tmpmpq = new LinkedList<MPQArchive>();
        for (final String fn : MPQLIST)
        {
            for (final File f : datadir.listFiles())
            {
                if (f.isFile())
                {
                    if (f.getName().equalsIgnoreCase(fn))
                    {
                        try
                        {
                            tmpmpq.add(new MPQArchive(f));
                        }
                        catch (final Exception e)
                        {
                            throw new IOException("Error opening: " + f, e);
                        }
                    }
                }
            }
        }
        this.mpqFiles = tmpmpq.toArray(new MPQArchive[0]);
    }

    public ByteBuffer getFileByteBuffer(final String filepath) throws IOException
    {
        for (int i = this.mpqFiles.length - 1; i >= 0; i--)
        {
            final MPQArchive f = this.mpqFiles[i];
            final ByteBuffer ret = f.getFileByteBuffer(filepath);
            if (ret != null)
                return ret;
        }
        return null;
    }
}

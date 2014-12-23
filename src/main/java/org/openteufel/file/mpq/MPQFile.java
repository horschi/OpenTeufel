/**
 * OpenTeufel: A role playing game
 * (MPQParser: MPQ parsing library for Java(R))
 *
 * (C) Copyright 2014-2014 Christian <horschi[at]gmail.com>
 * (C) Copyright 2010-2010 Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file was part of MPQParser and has been adjusted to fit into OpenTeufel.
 *
 * OpenTeufel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTeufel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTeufel.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openteufel.file.mpq;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class MPQFile
{
    private final MPQBlock            block;
    private final int                 fileSizeUncompressed;
    private final List<MPQFileSector> sectors;
    private final int                 sectorSize;

    public MPQFile(MPQBlock block, ByteBuffer fileBuffer, int sectorSize, Long encryptionSeed) throws IOException, DataFormatException
    {
        this.block = block;
        this.fileSizeUncompressed = block.getFileSizeUncompressed();
        this.sectorSize = sectorSize;

        boolean compressed = block.isCompressed() | block.isImploded();
        int[] sectorOffsets;
        if (block.isStoredUnsplit())
        {
            sectorOffsets = new int[] { 0, block.getSize() };
        }
        else
        {
            int sectorCount = (fileSizeUncompressed + sectorSize - 1) / sectorSize; // Calculate the number of sector in the file
            sectorOffsets = new int[sectorCount + 1];
            if (compressed)
            {
                for (int i = 0; i < sectorOffsets.length; i++)
                {
                    sectorOffsets[i] = fileBuffer.getInt();
                }
            }
            else
            {
                for (int i = 0; i < sectorOffsets.length - 1; i++)
                {
                    sectorOffsets[i] = i * sectorSize;
                }
                sectorOffsets[sectorOffsets.length - 1] = block.getSize();
            }
        }

        boolean containsChecksums = block.isChecksumsInlcuded();
        int sectorCount = containsChecksums ? sectorOffsets.length - 2 : sectorOffsets.length - 1;
        sectors = new ArrayList<MPQFileSector>(sectorCount);
        for (int i = 0; i < sectorCount; i++)
        {
            // FIXME: Use equivalent to unsigned int
            fileBuffer.position(sectorOffsets[i]);
            fileBuffer.limit(sectorOffsets[i + 1]);
            MPQFileSector sector = new MPQFileSector(compressed, sectorSize, fileBuffer, encryptionSeed != null ? (encryptionSeed + (long) i) : null);
            sectors.add(sector);
        }
    }

    public InputStream getByteStream() throws IOException
    {
        return new InputStream()
        {
            private int        pos         = 0;
            private int        loadedStart = 0;
            private int        loadedUntil = 0;
            private ByteBuffer data        = null;

            @Override
            public int read() throws IOException
            {
                if (pos >= fileSizeUncompressed)
                    return -1;
                initSector();
                int ret = data.get(pos - loadedStart) & 0xFF;
                pos++;
                return ret;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException
            {
                try
                {
                    ByteBuffer bufOut = ByteBuffer.wrap(b, off, len);
                    int numRead = 0;
                    while (numRead < len && pos < fileSizeUncompressed)
                    {
                        initSector();
                        int tmpLength = Math.min(len - numRead, loadedUntil - pos);
                        bufOut.put(data.array(), pos-loadedStart, tmpLength);
                        numRead += tmpLength;
                        pos += tmpLength;
                    }
                    return numRead;
                }
                catch(Exception e)
                {
                    pos = -1;
                    data = null;
                    throw new IOException(e);
                }
            }

            @Override
            public int read(byte[] b) throws IOException
            {
                return read(b, 0, b.length);
            }

            @Override
            public int available() throws IOException
            {
                return fileSizeUncompressed - pos;
            }

            @Override
            public long skip(long o) throws IOException
            {
                if (o > (long) (Integer.MAX_VALUE) || ((long) pos) + o > fileSizeUncompressed)
                    o = fileSizeUncompressed - pos;
                if (o <= 0)
                    return 0L;
                pos += o;
                return o;
            }

            private void initSector() throws IOException
            {
                if (data == null || pos >= loadedUntil)
                {
                    if(pos < 0)
                        throw new IOException("InputStream broken");
                    int sectorId = pos / sectorSize;
                    MPQFileSector sector = sectors.get(sectorId);
                    try
                    {
                        data = sector.getDecompressed();
                    }
                    catch (DataFormatException e)
                    {
                        throw new IllegalStateException(e);
                    }
                    loadedStart = sectorId * sectorSize;
                    loadedUntil = loadedStart + data.remaining();
                }
            }
        };
    }

    public byte[] getBytes() throws IOException
    {
        byte[] ret = new byte[block.getFileSizeUncompressed()];
        for (int i = 0; i < sectors.size(); i++)
        {
            MPQFileSector sector = sectors.get(i);
            try
            {
                ByteBuffer sectorUncompressed = sector.getDecompressed();
                if (i < sectors.size() - 1) // all sectors are 4k, except last
                {
                    if (sectorUncompressed.remaining() != sectorSize)
                        throw new IOException("Invalid sector size " + sector.getDecompressed().limit() + " for sector " + i);
                }
                sectorUncompressed.get(ret, i * sectorSize, sectorUncompressed.remaining());
            }
            catch (DataFormatException e)
            {
                throw new IllegalStateException(e);
            }
        }
        return ret;
    }

    @Override
    public String toString()
    {
        return "MPQFile [block=" + block + ", sectors=" + sectors + ", sectorSize=" + sectorSize + "]";
    }
}

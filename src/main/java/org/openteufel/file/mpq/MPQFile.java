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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

public class MPQFile
{
    private final MPQBlock            block;
    private final int                 fileSizeUncompressed;
    private final List<MPQFileSector> sectors;
    private final int                 sectorSize;

    public MPQFile(final MPQBlock block, final ByteBuffer fileBuffer, final int sectorSize, final Long encryptionSeed) throws IOException, DataFormatException
    {
        this.block = block;
        this.fileSizeUncompressed = block.getFileSizeUncompressed();
        this.sectorSize = sectorSize;

        int[] sectorOffsets;
        if (block.isStoredUnsplit())
        { // only a single sector
            sectorOffsets = new int[] { 0, block.getSize() };
        }
        else if (block.hasSectorOffsetTable())
        { // load sector offset table
            final int sectorCount = (this.fileSizeUncompressed + sectorSize - 1) / sectorSize; // Calculate the number of sector in the file
            final int offsetsSize = (sectorCount + 1)*4;
            final ByteBuffer encryptedSectorOffsets = fileBuffer.slice();
            encryptedSectorOffsets.order(fileBuffer.order());
            encryptedSectorOffsets.limit(offsetsSize);
            final ByteBuffer decryptedSectorOffsets;
            if(encryptionSeed == null)
            {
                decryptedSectorOffsets = encryptedSectorOffsets;
            }
            else
            {
                decryptedSectorOffsets = MPQEncryptionUtils.decrypt(encryptedSectorOffsets, encryptionSeed-1L);
            }

            sectorOffsets = new int[sectorCount + 1];
            for (int i = 0; i < sectorOffsets.length; i++)
            {
                sectorOffsets[i] = decryptedSectorOffsets.getInt();
            }
        }
        else
        { // calculate evenly
            final int sectorCount = (this.fileSizeUncompressed + sectorSize - 1) / sectorSize; // Calculate the number of sector in the file
            sectorOffsets = new int[sectorCount + 1];
            int lastOffset = 0;
            for (int i = 0; i < sectorOffsets.length - 1; i++)
            {
                sectorOffsets[i] = lastOffset;
                lastOffset += sectorSize;
            }
            sectorOffsets[sectorOffsets.length - 1] = block.getSize();
        }

        // This assertion does not with town.min from hellfire
        //        if(sectorOffsets[sectorOffsets.length - 1] != this.block.getSize())
        //            throw new IllegalStateException("Compressed size does not match last offset: "+sectorOffsets[sectorOffsets.length - 1] +" != "+ this.block.getSize());


        final boolean containsChecksums = block.isChecksumsInlcuded();
        final int sectorCount = containsChecksums ? sectorOffsets.length - 2 : sectorOffsets.length - 1;
        this.sectors = new ArrayList<MPQFileSector>(sectorCount);
        for (int i = 0; i < sectorCount; i++)
        {
            if(sectorOffsets[i] < 0 || sectorOffsets[i + 1] < 0)
                throw new IllegalStateException("Invalid sector offset: "+Arrays.toString(sectorOffsets));

            final int currentSectorSize = sectorOffsets[i + 1] - sectorOffsets[i];
            if(currentSectorSize < 0)
                throw new IllegalStateException("Current sector size is negative: "+currentSectorSize +" / "+Arrays.toString(sectorOffsets));
            fileBuffer.position(sectorOffsets[i]);
            fileBuffer.limit(sectorOffsets[i + 1]);

            final int currentSectorUncompressedSize = (i >= sectorCount-1) ?  (this.fileSizeUncompressed -(sectorSize*(sectorCount-1))) : sectorSize;
            final MPQFileSector sector = new MPQFileSector(block.isCompressed() , block.isImploded(), currentSectorUncompressedSize, fileBuffer, encryptionSeed != null ? (encryptionSeed + (long) i) : null);
            this.sectors.add(sector);
        }
    }

//    public InputStream getByteStream() throws IOException
//    {
//        return new InputStream()
//        {
//            private int        pos         = 0;
//            private int        loadedStart = 0;
//            private int        loadedUntil = 0;
//            private ByteBuffer data        = null;
//
//            @Override
//            public int read() throws IOException
//            {
//                if (pos >= fileSizeUncompressed)
//                    return -1;
//                initSector();
//                int ret = data.get(pos - loadedStart) & 0xFF;
//                pos++;
//                return ret;
//            }
//
//            @Override
//            public int read(byte[] b, int off, int len) throws IOException
//            {
//                try
//                {
//                    ByteBuffer bufOut = ByteBuffer.wrap(b, off, len);
//                    int numRead = 0;
//                    while (numRead < len && pos < fileSizeUncompressed)
//                    {
//                        initSector();
//                        int tmpLength = Math.min(len - numRead, loadedUntil - pos);
//                        bufOut.put(data.array(), pos - loadedStart, tmpLength);
//                        numRead += tmpLength;
//                        pos += tmpLength;
//                    }
//                    return numRead;
//                }
//                catch (Exception e)
//                {
//                    pos = -1;
//                    data = null;
//                    throw new IOException(e);
//                }
//            }
//
//            @Override
//            public int read(byte[] b) throws IOException
//            {
//                return read(b, 0, b.length);
//            }
//
//            @Override
//            public int available() throws IOException
//            {
//                return fileSizeUncompressed - pos;
//            }
//
//            @Override
//            public long skip(long o) throws IOException
//            {
//                if (o > (long) (Integer.MAX_VALUE) || ((long) pos) + o > fileSizeUncompressed)
//                    o = fileSizeUncompressed - pos;
//                if (o <= 0)
//                    return 0L;
//                pos += o;
//                return o;
//            }
//
//            private void initSector() throws IOException
//            {
//                if (data == null || pos >= loadedUntil)
//                {
//                    if (pos < 0)
//                        throw new IOException("InputStream broken");
//                    int sectorId = pos / sectorSize;
//                    MPQFileSector sector = sectors.get(sectorId);
//                    try
//                    {
//                        data = sector.getDecompressed();
//                    }
//                    catch (DataFormatException e)
//                    {
//                        throw new IllegalStateException(e);
//                    }
//                    loadedStart = sectorId * sectorSize;
//                    loadedUntil = loadedStart + data.remaining();
//                }
//            }
//        };
//    }

    public byte[] getBytes() throws IOException
    {
        return this.getByteBuffer().array();
    }

    public ByteBuffer getByteBuffer() throws IOException
    {
        int decompressedSize = 0;
        final ByteBuffer ret = ByteBuffer.allocate(this.block.getFileSizeUncompressed());
        ret.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < this.sectors.size(); i++)
        {
            final MPQFileSector sector = this.sectors.get(i);
            try
            {
                decompressedSize += sector.getDecompressed(ret);
            }
            catch (final DataFormatException e)
            {
                throw new IllegalStateException(e);
            }
        }
        if(decompressedSize != this.block.getFileSizeUncompressed())
            throw new IllegalStateException("Invalid decompressed size: "+decompressedSize +" != "+ this.block.getFileSizeUncompressed());
        ret.rewind();
        return ret;
    }

    @Override
    public String toString()
    {
        return "MPQFile [block=" + this.block + ", sectors=" + this.sectors + ", sectorSize=" + this.sectorSize + "]";
    }
}

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.openteufel.file.mpq.explode.Exploder;

public class MPQFileSector
{
    private static final byte COMPRESSION_ZLIB  = 0x02;
    private static final byte COMPRESSION_BZIP2 = 0x10;

    public enum Compression
    {
        Uncompressed, ZLib, BZip2, Imploded
    }

    // Values contained in the archive
    private final Compression compression;
    private final ByteBuffer  dataRaw;
    private final Long        encryptionSeed;
    private final int         sizeUncompressed;

    protected MPQFileSector(boolean compressed, boolean imploded, int sizeUncompressed, ByteBuffer src, Long encryptionSeed) throws DataFormatException, IOException
    {
        this.encryptionSeed = encryptionSeed;
        this.sizeUncompressed = sizeUncompressed;

        if(sizeUncompressed == src.remaining())
        {
            compression = Compression.Uncompressed;
        }
        else if (imploded)
        {
            compression = Compression.Imploded;
        }
        else if (compressed)
        {
            byte compressionByte = src.get();
            if (compressionByte == COMPRESSION_ZLIB)
            {
                compression = Compression.ZLib;
            }
            else if (compressionByte == COMPRESSION_BZIP2)
            {
                compression = Compression.BZip2;
            }
            else
            {
                // Unkown compression
                compression = Compression.Uncompressed;
                /*
                 * Count the compression byte to the data. Sectors are not
                 * necessarily compressed, if the according flag is set. If the
                 * compression byte is unknown, compressed sector are treated as
                 * uncompressed.
                 */
                src.position(src.position() - 1);
            }
        }
        else
        {
            compression = Compression.Uncompressed;
        }
        dataRaw = src.slice();
        dataRaw.order(src.order());
    }

    public int getSizeUncompressed()
    {
        return sizeUncompressed;
    }

    public ByteBuffer getDecompressed() throws DataFormatException, IOException
    {
        ByteBuffer ret = ByteBuffer.allocate(sizeUncompressed);
        getDecompressed(ret);
        ret.rewind();
        return ret;
    }

    public int getDecompressed(ByteBuffer out) throws DataFormatException, IOException
    {
        // If the file is encrypted, each sector (after compression/implosion, if applicable) is encrypted with the file's key.
        // Each sector is encrypted using the key + the 0-based index of the sector in the file.
        // NOTE compression type byte (if existing) is encrypted as well!
        ByteBuffer dataDecrypted;
        if (this.encryptionSeed != null)
            dataDecrypted = MPQEncryptionUtils.decrypt(dataRaw, encryptionSeed);
        else
            dataDecrypted = dataRaw;
        dataDecrypted.rewind();

        switch (compression)
        {
            case Uncompressed:
            {
                out.put(dataDecrypted);
                return dataDecrypted.capacity();
            }
            case Imploded:
            {
                byte[] buf = new byte[sizeUncompressed];
                int numDecompressed = Exploder.pkexplode(dataDecrypted.array(), buf);
                if (numDecompressed != this.sizeUncompressed)
                    throw new IllegalStateException();
                out.put(buf, 0, sizeUncompressed);
                return sizeUncompressed;
            }
            case ZLib:
            {
                int numDecompressed = 0;
                byte[] buf = new byte[1024];
                Inflater inflater = new Inflater();
                inflater.setInput(dataDecrypted.array());
                while (!inflater.finished())
                {
                    int decompressedBytes = inflater.inflate(buf);
                    numDecompressed += decompressedBytes;
                    out.put(buf, 0, decompressedBytes);
                }
                inflater.end();
                if (numDecompressed != this.sizeUncompressed)
                    throw new IllegalStateException();
                return numDecompressed;
            }
            case BZip2:
            {
                int numDecompressed = 0;
                byte[] buf = new byte[1024];
                InputStream inputStream = new ByteArrayInputStream(dataDecrypted.array());
                BZip2CompressorInputStream uncompressStream = new BZip2CompressorInputStream(inputStream);
                while (true)
                {
                    int decompressedBytes = uncompressStream.read(buf);
                    if (decompressedBytes < 0)
                        break;
                    numDecompressed += decompressedBytes;
                    out.put(buf, 0, decompressedBytes);
                }
                uncompressStream.close();
                inputStream.close();
                if (numDecompressed != sizeUncompressed)
                    throw new IllegalStateException();
                return numDecompressed;
            }
            default:
                throw new IllegalStateException("Unknown Compression");
        }
    }

    @Override
    public String toString()
    {
        return "MPQFileSector [compression=" + compression + ", dataRaw=" + dataRaw.capacity() + "]";
    }
}

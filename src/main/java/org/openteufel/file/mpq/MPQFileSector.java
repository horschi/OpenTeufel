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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class MPQFileSector
{
    private static final byte COMPRESSION_ZLIB  = 0x02;
    private static final byte COMPRESSION_BZIP2 = 0x10;

    // TODO Add support for other compression algorithms
    public enum Compression
    {
        ZLib, BZip2
    }

    // Values contained in the archive
    private final Compression compression;
    private final ByteBuffer  dataRaw;
    private final Long encryptionSeed;

    protected MPQFileSector(boolean compressed, int sizeUncompressed, ByteBuffer src, Long encryptionSeed) throws DataFormatException, IOException
    {
        this.encryptionSeed = encryptionSeed;
        if (compressed)
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
                compression = null;
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
            compression = null;
        }
        dataRaw = src.slice();
        dataRaw.order(src.order());
    }

    public ByteBuffer getDecompressed() throws DataFormatException, IOException
    {
        ByteBuffer dataDecrypted;
        // If the file is encrypted, each sector (after compression/implosion, if applicable) is encrypted with the file's key.
        // Each sector is encrypted using the key + the 0-based index of the sector in the file.
        // NOTE compression type byte (if existing) is encrypted as well!
        if (this.encryptionSeed != null)
            dataDecrypted = MPQEncryptionUtils.decrypt(dataRaw, encryptionSeed);
        else
            dataDecrypted = dataRaw;

        // Initialize variables for decompression
        byte[] buf = new byte[512];
        ByteBuffer uncompressedBuffer;
        ByteArrayOutputStream uncompressed = new ByteArrayOutputStream();
        // Uncompress bytes
        if (Compression.ZLib == compression)
        {
            Inflater inflater = new Inflater();
            inflater.setInput(dataDecrypted.array());
            while (!inflater.finished())
            {
                int decompressedBytes = inflater.inflate(buf);
                uncompressed.write(buf, 0, decompressedBytes);
            }
            inflater.end();
            uncompressedBuffer = ByteBuffer.wrap(uncompressed.toByteArray());
            uncompressedBuffer.order(dataDecrypted.order());
            return uncompressedBuffer;
        }
        else if (Compression.BZip2 == compression)
        {
            throw new IllegalArgumentException("BZIP not supported");
            //            InputStream inputStream = null;
            //            BZip2CompressorInputStream uncompressStream = null;
            //            try
            //            {
            //                inputStream = new ByteArrayInputStream(dataDecrypted.array());
            //                uncompressStream = new BZip2CompressorInputStream(inputStream);
            //                while ((readBytes = uncompressStream.read(buf)) > -1)
            //                {
            //                    uncompressed.write(buf, 0, readBytes);
            //                }
            //                uncompressedBuffer = ByteBuffer.wrap(uncompressed.toByteArray());
            //            }
            //            finally
            //            {
            //                uncompressStream.close();
            //                inputStream.close();
            //            }
            //            return uncompressedBuffer;
        }

        return dataDecrypted;
    }

    @Override
    public String toString()
    {
        return "MPQFileSector [compression=" + compression + ", dataRaw=" + dataRaw.capacity() + "]";
    }
}

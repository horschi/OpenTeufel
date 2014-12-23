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

import org.openteufel.file.mpq.MPQArchive.MPQFormatVersion;

/**
 * Class representing the header of an mpq archive. It contains information
 * about important areas in the file.
 */
public class MPQHeader
{
    private static final byte[]    MAGIC_NUMBER        = new byte[] { 0x4D, 0x50, 0x51, 0x1A };
    private static final int       HEADER_SIZE_PRE_BC  = 32; // Expected header size in the PRE_BC archive format.
    private static final int       HEADER_SIZE_POST_BC = 44; // Expected header size in the POST_BC archive format.

    private final int              sizeHeader;
    private final int              sizeArchive;
    
    // Exponent specifying the size of uncompressed file sectors (sectorSize = 512 * 2^sectorSizeShift)
    // short sectorSize shift
    // int16 representing the format of this archive
    // short formatBytes;
    
    private final int              offsetHashTable; // Offset of the hash table relative to the beginning of the archive.
    private final int              offsetBlockTable; // Offset of the block table relative to the beginning of the archive.
    private final int              entriesHashTable; // Number of entries contained in the hash table.
    private final int              entriesBlockTable; // Number of entries contained in the block table.
    private final long             offsetExtendedBlockTable; // Offset of the extended block table relative to the beginning of the archive. Only present in the POST_BC archive format.
    /**
     * Highest 16 bits of the offset of the hash table relative to the beginning
     * of the archive. If this field is present, hashTableOffset =
     * (offsetHashTableHigh << 32) + offsetHashTable. Only present in the
     * POST_BC archive format.
     */
    private final short            offsetHashTableHigh;
    /**
     * Highest 16 bits of the offset of the block table relative to the
     * beginning of the archive. If this field is present, blockTableOffset =
     * (offsetBlockTableHigh << 32) + offsetBlockTable. Only present in the
     * POST_BC archive format.
     */
    private final short            offsetBlockTableHigh;

    // Values derived from the data
    private final MPQFormatVersion format; // Indicates the format of the archive.
    private final int              sectorSize; // Size of an uncompressed file sector.

    /**
     * Creates a new <code>MPQHeader</code> object with the specified input
     * data.
     * 
     * @param src
     *            Input to parse information from.
     * @throws IOException
     *             If something goes wrong during parsing the header.
     */
    protected MPQHeader(ByteBuffer src) throws IOException
    {
        src.mark();
        // Read and check magic number
        for (int i = 0; i < 4; i++)
        {
            if (MAGIC_NUMBER[i] != src.get())
            {
                throw new IllegalArgumentException("Check of the magic number failed. The parsed file is not a valid mpq archive.");
            }
        }

        sizeHeader = src.getInt();
        sizeArchive = src.getInt();
        /*
         * There are two Possible formats: - The older format, where the header
         * size should be 32 bytes - The newer format, where the header size
         * should be 44 bytes (supports large archives)
         */
        short formatValue = src.getShort();
        if (formatValue == 0x0)
        {
            format = MPQFormatVersion.PRE_BC;
            // Check header size
            if (sizeHeader != HEADER_SIZE_PRE_BC)
            {
                throw new IOException("Security check failed. The actual header size does " + "not match the expected size. expected=" + HEADER_SIZE_PRE_BC + ", actual=" + sizeHeader);
            }
        }
        else if (formatValue == 0x1)
        {
            format = MPQFormatVersion.POST_BC;
            // Check header size
            if (sizeHeader != HEADER_SIZE_POST_BC)
            {
                throw new IOException("Security check failed. The actual header size does " + "not match the expected size. expected=" + HEADER_SIZE_POST_BC + ", actual=" + sizeHeader);
            }
        }
        else
        {
            throw new IllegalArgumentException("The format of the mpq archive is unknown.");
        }

        short sectorSizeShift = src.getShort();
        sectorSize = 512 << sectorSizeShift;
        offsetHashTable = src.getInt();
        offsetBlockTable = src.getInt();
        /*
         * Number of hash table entries must be a power of two. Must be smaller
         * than 2^16 for the older format. Must be smaller than 2^20 for the
         * newer format.
         */
        entriesHashTable = src.getInt();
        entriesBlockTable = src.getInt();

        if (MPQFormatVersion.POST_BC == format)
        {
            offsetExtendedBlockTable = src.getLong();
            offsetHashTableHigh = src.getShort();
            offsetBlockTableHigh = src.getShort();
        }
        else
        {
            offsetExtendedBlockTable = 0;
            offsetHashTableHigh = 0;
            offsetBlockTableHigh = 0;
        }
        src.reset();
    }

    /**
     * Returns the overall size of this header.
     * 
     * @return Size in bytes.
     */
    public int getSizeHeader()
    {
        return sizeHeader;
    }

    /**
     * Returns the overall size of the archive.
     * 
     * @return Size in bytes.
     */
    public int getSizeArchive()
    {
        return sizeArchive;
    }

    /**
     * Returns the size of an uncompressed file sector.
     * 
     * @return Size in bytes.
     */
    public int getSectorSize()
    {
        return sectorSize;
    }

    /**
     * Returns the offset of the hash table relative to the beginning of the
     * archive.
     * 
     * @return Offset in bytes.
     */
    public int getOffsetHashTable()
    {
        return offsetHashTable;
    }

    /**
     * Returns the offset of the block table relative to the beginning of the
     * archive.
     * 
     * @return Offset in bytes.
     */
    public int getOffsetBlockTable()
    {
        return offsetBlockTable;
    }

    /**
     * Returns the number of entries stored in the hash table.
     * 
     * @return Number of hash table entries.
     */
    public int getEntriesHashTable()
    {
        return entriesHashTable;
    }

    /**
     * Returns the number of entries stored in the block table.
     * 
     * @return Number of block table entries.
     */
    public int getEntriesBlockTable()
    {
        return entriesBlockTable;
    }

    /**
     * Returns the offset of the extended block table relative to the beginning
     * of the archive.
     * 
     * @return Offset in bytes.
     */
    public long getOffsetExtendedBlockTable()
    {
        return offsetExtendedBlockTable;
    }

    /**
     * Returns highest 16 bits of the offset of the hash table relative to the
     * beginning of the archive.
     * 
     * @return Offset >> 32 in bytes.
     */
    public short getOffsetHashTableHigh()
    {
        return offsetHashTableHigh;
    }

    /**
     * Returns highest 16 bits of the offset of the block table relative to the
     * beginning of the archive.
     * 
     * @return Offset >> 32 in bytes.
     */
    public short getOffsetBlockTableHigh()
    {
        return offsetBlockTableHigh;
    }

    /**
     * Returns the format of this archive.
     * 
     * @return Format.
     */
    public MPQFormatVersion getFormat()
    {
        return format;
    }

    @Override
    public String toString()
    {
        return "MPQHeader [sizeHeader=" + sizeHeader + ", sizeArchive=" + sizeArchive + ", offsetHashTable=" + offsetHashTable + ", offsetBlockTable=" + offsetBlockTable + ", entriesHashTable=" + entriesHashTable + ", entriesBlockTable=" + entriesBlockTable + ", offsetExtendedBlockTable=" + offsetExtendedBlockTable + ", offsetHashTableHigh=" + offsetHashTableHigh + ", offsetBlockTableHigh="
                        + offsetBlockTableHigh + ", format=" + format + ", sectorSize=" + sectorSize + "]";
    }
}

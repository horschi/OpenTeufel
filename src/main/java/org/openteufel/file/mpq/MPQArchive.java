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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.zip.DataFormatException;

public class MPQArchive
{
    /**
     * Enum specifying the type of the archive. Archives created before
     * "World of Warcraft: The Burning Crusade" are slightly different to
     * archives created from this game's release onwards.
     */
    public enum MPQFormatVersion
    {
        PRE_BC, POST_BC
    };

    private final File             srcFile;
    private final RandomAccessFile raf;
    private final ByteBuffer       src;

    private final MPQHeader        header;
    private final MPQBlockTable    blockTable;
    private final MPQHashTable     hashTable;

    public MPQArchive(File file) throws IOException
    {
        this(file, 0);
    }

    /**
     * Creates a new <code>MPQArchive</code> object from the specified file with
     * specified offset.
     * 
     * @param file
     *            File containing the archive.
     * @param offsetArchive
     *            Offset of the archive within the file.
     * @throws IOException
     *             If something went wrong during parsing the archive.
     */
    public MPQArchive(File file, int offsetArchive) throws IOException
    {
        this.srcFile = file;
        this.raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        // FIXME: This delimits the maximum file size to 4GiB as the size of the buffer must not exceed Integer.MAX_VALUE
        this.src = channel.map(MapMode.READ_ONLY, offsetArchive, file.length() - offsetArchive);
        src.order(ByteOrder.LITTLE_ENDIAN);

        header = new MPQHeader(src);
        blockTable = new MPQBlockTable(header.getOffsetBlockTable(), header.getEntriesBlockTable(), src);
        hashTable = new MPQHashTable(header.getOffsetHashTable(), header.getEntriesHashTable(), src);
    }

    public ByteBuffer getFileByteBuffer(String filepath) throws IOException
    {
        try
        {
            MPQFile mpqFile = getFile(filepath);
            if (mpqFile == null)
                return null;
            try
            {
                return mpqFile.getByteBuffer();
            }
            catch (Exception e)
            {
                throw new IOException("Cannot parse MPQFile: " + mpqFile, e);
            }
        }
        catch (Exception e)
        {
            throw new IOException("Cannot load " + filepath, e);
        }
    }

    public byte[] getFileBytes(String filepath) throws IOException
    {
        try
        {
            MPQFile mpqFile = getFile(filepath);
            if (mpqFile == null)
                return null;
            try
            {
                return mpqFile.getBytes();
            }
            catch (Exception e)
            {
                throw new IOException("Cannot parse MPQFile: " + mpqFile, e);
            }
        }
        catch (Exception e)
        {
            throw new IOException("Cannot load " + filepath, e);
        }
    }

    //    public InputStream getFileStream(String filepath) throws IOException
    //    {
    //        MPQFile mpqFile = getFile(filepath);
    //        if (mpqFile == null)
    //            return null;
    //        return mpqFile.getByteStream();
    //    }

    public MPQFile getFile(String filepath) throws IOException
    {
        MPQHashEntry hashEntry = getFileHashtableEntry(filepath);
        if (hashEntry == null)
            return null;
        return getFile(filepath, hashEntry);
    }

    public MPQHashEntry getFileHashtableEntry(String filepath)
    {
        return getFileHashtableEntry(filepath, null, null);
    }

    public MPQHashEntry getFileHashtableEntry(String filepath, Short lang, Short platform)
    {
        long currentEntryIndex = MPQEncryptionUtils.hash(filepath, MPQEncryptionUtils.MPQ_HASH_TABLE_OFFSET) & (long) (this.hashTable.count() - 1);
        int hashNameA = (int) MPQEncryptionUtils.hash(filepath, MPQEncryptionUtils.MPQ_HASH_NAME_A) & 0xFFffFFff;
        int hashNameB = (int) MPQEncryptionUtils.hash(filepath, MPQEncryptionUtils.MPQ_HASH_NAME_B) & 0xFFffFFff;

        for (int i = 0; i < 100; i++)
        {
            MPQHashEntry hashEntry = hashTable.getEntry(currentEntryIndex);
            if (hashEntry.isNullBlock())
                return null;

            if (!hashEntry.isDeleted())
            {
                if (hashEntry.getFilePathHashA() == hashNameA && hashEntry.getFilePathHashB() == hashNameB)
                {
                    if ((lang == null || hashEntry.getLanguage() == lang) && (platform == null || hashEntry.getPlatform() == platform))
                    {
                        return hashEntry;
                    }
                }
            }
            currentEntryIndex = currentEntryIndex + 1L;
        }
        return null;
    }

    private MPQFile getFile(String filepath, MPQHashEntry hashEntry) throws IOException
    {
        if (hashEntry.isDeleted() || hashEntry.isNullBlock())
            throw new IllegalStateException("Empty or null hash entry: " + hashEntry);

        int blockIndex = hashEntry.getFileBlockIndex();
        if (blockIndex < 0)
            throw new IllegalArgumentException("Illegal blockindex: " + blockIndex);

        MPQBlock blockTableEntry = this.blockTable.get(blockIndex);

        // Security check: Uncompressed files must have the same information on uncompressed and compressed file size
        if (!blockTableEntry.isCompressed() && !blockTableEntry.isImploded() && blockTableEntry.getSize() != blockTableEntry.getFileSizeUncompressed())
            throw new IllegalStateException("Compressed size does not match for uncompressed block: " + blockTableEntry);

        if (blockTableEntry.getSize() == 0)
            throw new IllegalStateException("Found empty block: " + blockTableEntry);

        // FIXME: Deal with the unsigned int
        // Create a ByteBuffer starting at the file's offset and ending after the file's (compressed) size
        int fileOffset = blockTableEntry.getOffset();
        src.position((int) fileOffset);
        final ByteBuffer fileBuffer = src.slice();
        fileBuffer.order(src.order());
        fileBuffer.limit(blockTableEntry.getSize());

        try
        {
            Long encryptionSeed;
            if (blockTableEntry.isEncrypted())
                encryptionSeed = MPQEncryptionUtils.generateFileKey(filepath);
            else
                encryptionSeed = null;
            int sectorSize = header.getSectorSize();
            return new MPQFile(blockTableEntry, fileBuffer, sectorSize, encryptionSeed);
        }
        catch (DataFormatException e)
        {
            throw new IllegalStateException(e);
        }
    }

    public void close()
    {
        try
        {
            raf.close();
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public String toString()
    {
        return "MPQArchive [srcFile=" + srcFile + ", raf=" + raf + ", header=" + header + ", blockTable=" + blockTable.count() + ", hashTable=" + hashTable.count() + "]";
    }
}

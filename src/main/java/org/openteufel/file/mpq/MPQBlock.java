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

/**
 * Class that represents an entry in a block table. It stores information about
 * a single file.
 */
public class MPQBlock
{
    private final static int FLAG_FILE_EXISTS             = 0x80000000;
    private final static int FLAG_CHECKSUM_EXISTS         = 0x04000000; // Flag that is set, if the file contains checksums in the last sector. (also called HAS_EXTRA)
    private final static int FLAG_DELETION_MARKER         = 0x02000000; // Flag that is set, if the file has been deleted
    private final static int FLAG_FILE_UNSPLIT            = 0x01000000; // Flag that is set, if the file is stored in a single sector.
    private final static int FLAG_ENCRYPTION_KEY_ADJUSTED = 0x00020000; // Flag that is set, if the each sector's encryption key is dependent on its offset. (also called FIXSEED)
    private final static int FLAG_FILE_ENCRYPTED          = 0x00010000;
    private final static int FLAG_FILE_COMPRESSED         = 0x00000200;
    private final static int FLAG_FILE_IMPLODED           = 0x00000100;

    private final int        offset;                                   // Offset of the file relative to the beginning of the archive.
    private final int        size;                                     // Size of the compressed file.
    private final int        fileSizeUncompressed;                     // Size of the uncompressed file.
    private final int        flags;                                    // Various flags

    /**
     * Creates a new <code>MPQBlockTableEntry</code> object from the specified
     * input data.
     * 
     * @param src
     *            Input.
     * @throws IOException
     *             If something goes wrong during parsing the entry.
     */
    protected MPQBlock(ByteBuffer src) throws IOException
    {
        offset = src.getInt();
        size = src.getInt();
        fileSizeUncompressed = src.getInt();
        flags = src.getInt();
    }

    /**
     * Returns the offset denoting the beginning of the block relative to the
     * beginning of the archive.
     * 
     * @return Unsigned int.
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * Returns the size of the compressed block within the archive file.
     * 
     * @return Unsigned int.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Returns the size of the uncompressed file.
     * 
     * @return Unsigned int.
     */
    public int getFileSizeUncompressed()
    {
        return fileSizeUncompressed;
    }

    /**
     * Returns whether the entry represents a file or not.
     * 
     * @return <code>true</code> if the entry represents a file.
     */
    public boolean isFile()
    {
        return ((flags & FLAG_FILE_EXISTS) != 0);
    }

    /**
     * Returns whether the file's last sector contains checksums or not.
     * 
     * @return <code>true</code> if the file's last sector contains checksums.
     */
    public boolean isChecksumsInlcuded()
    {
        return ((flags & FLAG_CHECKSUM_EXISTS) != 0);
    }

    /**
     * Returns whether the file has been deleted or not.
     * 
     * @return <code>true</code> if the file has been deleted.
     */
    public boolean isDeletionMarker()
    {
        return ((flags & FLAG_DELETION_MARKER) != 0);
    }

    /**
     * Returns whether the file is stored in a single sector or not.
     * 
     * @return <code>true</code> if the file is stored in a single sector.
     */
    public boolean isStoredUnsplit()
    {
        return ((flags & FLAG_FILE_UNSPLIT) != 0);
    }

    /**
     * Returns whether the encryption key of the file's sectors is dependent on
     * the corresponding sector's offset or not.
     * 
     * @return <code>true</code> if the encryption key are adjusted.
     */
    public boolean isEncryptionKeyVariable()
    {
        return ((flags & FLAG_ENCRYPTION_KEY_ADJUSTED) != 0);
    }

    /**
     * Returns whether the file is encrypted or not.
     * 
     * @return <code>true</code> if the file is encrypted.
     */
    public boolean isEncrypted()
    {
        return ((flags & FLAG_FILE_ENCRYPTED) != 0);
    }

    /**
     * Returns whether the file is compressed or not.
     * 
     * @return <code>true</code> if the file is compressed.
     */
    public boolean isCompressed()
    {
        return ((flags & FLAG_FILE_COMPRESSED) != 0);
    }

    /**
     * Returns whether the file is imploded or not.
     * 
     * @return <code>true</code> if the file is imploded.
     */
    public boolean isImploded()
    {
        return ((flags & FLAG_FILE_IMPLODED) != 0);
    }
    
    public boolean hasSectorOffsetTable()
    {
        return (!isStoredUnsplit()) && (isCompressed() || isImploded());
    }

    @Override
    public String toString()
    {
        return "MPQBlockTableEntry [offset=" + offset + ", size=" + size + ", fileSizeUncompressed=" + fileSizeUncompressed + ", flags=" + flags + "]";
    }
}

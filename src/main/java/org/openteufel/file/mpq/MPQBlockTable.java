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
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a block table in an mpq archive. It stores the entries
 * which contain information about the files in the archive.
 */
public class MPQBlockTable
{
    private final List<MPQBlock> entries;

    /**
     * Creates a new <code>MPQBlockTable</code> object at the specified offset
     * and with the specified number of entries.
     * 
     * @param offset
     *            The offset relative to the beginning of the ByteBuffer.
     * @param entryCount
     *            The number of entries contained in this block table.
     * @param src
     *            The input file.
     * @throws IOException
     *             If something went wrong during parsing the block table.
     */
    protected MPQBlockTable(long offset, int entryCount, ByteBuffer src) throws IOException
    {
        int posOld = src.position();
        src.position(posOld + (int) offset); // Position buffer at the beginning of the block table

        // Decrypt table
        ByteBuffer tableEncrypted = src.slice();
        tableEncrypted.order(ByteOrder.LITTLE_ENDIAN);
        tableEncrypted.limit(entryCount * 16);
        
        long seed = MPQEncryptionUtils.hash("(block table)", 0x300);
        ByteBuffer tableDecrypted = MPQEncryptionUtils.decrypt(tableEncrypted, seed);

        // Create entries
        List<MPQBlock> tmp = new ArrayList<MPQBlock>();
        for (int i = 0; i < entryCount; i++)
        {
            MPQBlock entry = new MPQBlock(tableDecrypted);
            tmp.add(entry);
        }
        this.entries = Collections.unmodifiableList(tmp);
        
        // TODO: Add support for extended block tables
        // Check for an extended block table
        /*
         * if (header.getOffsetExtendedBlockTable() > 0) { // Position stream at
         * beginning of the extended block table srcStream.reset();
         * srcStream.skip(header.getOffsetArchive() +
         * header.getOffsetExtendedBlockTable()); // TODO: read and decrypt
         * extended block table }
         */

        src.position(posOld);
    }

    /**
     * Returns a list with all entries contained in this block table. The number
     * of entries in a block table does not necessarily equal the number of
     * files stored in it.
     * 
     * @return Entries of the block table.
     */
    public List<MPQBlock> getEntries()
    {
        return entries;
    }

    /**
     * Returns the entry at the specified index.
     * 
     * @param index
     *            Index of the entry.
     * @return Entry at the specified index.
     */
    public MPQBlock get(int index)
    {
        return entries.get(index);
    }

    /**
     * Returns the number of files this block table refers to.
     * 
     * @return Number of files.
     */
    public int getFileCount()
    {
        int fileCount = 0;
        for (MPQBlock entry : entries)
        {
            if (entry.isFile())
            {
                fileCount++;
            }
        }
        return fileCount;
    }

    public int count()
    {
        return this.entries.size();
    }

    @Override
    public String toString()
    {
        return "MPQBlockTable [entries=" + entries + "]";
    }
}

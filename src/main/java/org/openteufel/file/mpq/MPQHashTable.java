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
 * Class that represents a hash table in an mpq archive. It stores hash table
 * entries which provide information on files.
 */
public class MPQHashTable
{
    private List<MPQHashEntry> entries;

    /**
     * Creates a new <code>MPQHashTable</code> object with the specified offset,
     * entry count and input data.
     * 
     * @param offset
     *            Offset of the hash table relative to the beginning of the
     *            archive.
     * @param entryCount
     *            Number of entries contained in the hash table.
     * @param src
     *            Input data to be parsed.
     * @throws IOException
     *             If something goes wrong during parsing the hash table.
     */
    protected MPQHashTable(int offset, int entryCount, ByteBuffer src) throws IOException
    {
        int posOld = src.position();
        src.position(offset);// Position buffer at the beginning of the block table

        // Decrypt table
        ByteBuffer tableEncrypted = src.slice();
        tableEncrypted.order(ByteOrder.LITTLE_ENDIAN);
        tableEncrypted.limit(entryCount * 16);
        
        long seed = MPQEncryptionUtils.hash("(hash table)", 0x300);
        ByteBuffer tableDecrypted = MPQEncryptionUtils.decrypt(tableEncrypted, seed);

        // Create entries
        List<MPQHashEntry> tmp = new ArrayList<MPQHashEntry>(entryCount);
        for (int i = 0; i < entryCount; i++)
        {
            MPQHashEntry entry = new MPQHashEntry(tableDecrypted);
            tmp.add(entry);
        }
        entries = Collections.unmodifiableList(tmp);

        src.position(posOld);
    }

    public List<MPQHashEntry> getEntries()
    {
        return entries;
    }

    public MPQHashEntry getEntry(long index)
    {
        return getEntry((int) index);
    }

    public MPQHashEntry getEntry(int index)
    {
        return entries.get(index & (this.entries.size() - 1));
    }

    public int count()
    {
        return entries.size();
    }

    @Override
    public String toString()
    {
        return "MPQHashTable [entries=" + entries + "]";
    }
}

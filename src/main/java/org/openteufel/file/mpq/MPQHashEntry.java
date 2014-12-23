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
 * Class that represents a hash table entry. It stores a files checksums, as
 * well as its index in the block table, the language and the platform.
 */
public class MPQHashEntry
{
    /**
     * Expected flags that are set if the entry is a null block. Null blocks
     * terminate the search for a file.
     */
    private static final int INDEX_NULL    = 0xFFFFFFFF;
    /** Expected flags that are set if the entry is empty. */
    private static final int INDEX_DELETED = 0xFFFFFFFE;

    // Data contained in the archive
    private final int        filePathHashA;
    private final int        filePathHashB;
    // TODO retrieve language
    /** The language of the file. */
    private final short      language;
    // TODO retrieve platform
    /** The platform the file is used for. */
    private final short      platform;
    /**
     * Represents the index in the block table which describes the file
     * represented by this entry.
     */
    private final int        fileBlockIndex;

    /**
     * Creates a new <code>MPQHashTableEntry</code> object of the specified
     * input data.
     * 
     * @param src
     *            Input data.
     * @throws IOException
     *             If something goes wrong during parsing the entry.
     */
    protected MPQHashEntry(ByteBuffer src) throws IOException
    {
        filePathHashA = src.getInt();
        filePathHashB = src.getInt();
        language = src.getShort();
        platform = src.getShort();
        fileBlockIndex = src.getInt();
    }

    /**
     * Returns the first hash of the file.
     * 
     * @return File path hash.
     */
    public int getFilePathHashA()
    {
        return filePathHashA;
    }

    /**
     * Returns the second hash of the file.
     * 
     * @return File path hash.
     */
    public int getFilePathHashB()
    {
        return filePathHashB;
    }

    /**
     * Returns the language the file is used for.
     * 
     * @return File language.
     */
    public short getLanguage()
    {
        return language;
    }

    /**
     * Returns the platform the file is used for.
     * 
     * @return File platform.
     */
    public short getPlatform()
    {
        return platform;
    }

    /**
     * Returns whether this entry is a null block or not.
     * 
     * @return <code>true</code>, if the entry is a null block.
     */
    public boolean isNullBlock()
    {
        return (fileBlockIndex == INDEX_NULL);
    }

    /**
     * Returns whether this entry is empty or not.
     * 
     * @return <code>true</code>, if the entry is empty.
     */
    public boolean isDeleted()
    {
        return (fileBlockIndex == INDEX_DELETED);
    }

    /**
     * Returns the file's index in the block table.
     * 
     * @return Unsigned int.
     */
    public int getFileBlockIndex()
    {
        return fileBlockIndex;
    }

    @Override
    public String toString()
    {
        return "MPQHashTableEntry [filePathHashA=" + filePathHashA + ", filePathHashB=" + filePathHashB + ", language=" + language + ", platform=" + platform + ", fileBlockIndex=" + fileBlockIndex + "]";
    }

}

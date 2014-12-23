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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Encryption utilities
 */
public class MPQEncryptionUtils
{
    public static final int     MPQ_HASH_TABLE_OFFSET = 0;
    public static final int     MPQ_HASH_NAME_A       = 0x100;          // 1;
    public static final int     MPQ_HASH_NAME_B       = 0x200;          // 2;
    public static final int     MPQ_HASH_FILE_KEY     = 0x300;

    private final static long   MASK_INT              = 0xFFFFFFFFL;
    private final static long[] stormBuffer           = new long[0x500];

    private MPQEncryptionUtils()
    {
    }

    static
    {
        long seed = 0x00100001L;
        int index1 = 0;
        int index2 = 0;

        for (int j = 0; j < stormBuffer.length; j++)
        {
            stormBuffer[j] = 0;
        }

        for (index1 = 0; index1 < 0x100; index1++)
        {
            int i;
            for (index2 = index1, i = 0; i < 5; i++, index2 += 0x100L)
            {
                long temp1, temp2;
                seed = (((seed * 125 + 3) & MASK_INT) % 0x2AAAABL);
                temp1 = ((seed & 0xFFFF) << 0x10L) & MASK_INT;

                seed = (((seed * 125 + 3) & MASK_INT) % 0x2AAAABL);
                temp2 = (seed & 0xFFFF);

                stormBuffer[index2] = (temp1 | temp2);
            }
        }
    }

    /**
     * Decrypts the specified buffer with the specified seed.
     * 
     * @param data
     *            Encrypted bytes.
     * @param seed
     *            Decryption key.
     * @return Decrypted bytes.
     */
    public static ByteBuffer decrypt(ByteBuffer data, long seed)
    {
        data.mark();
        int size = data.limit() / 4;
        int sizePlain = data.limit() % 4;
        ByteBuffer tableDecrypted = ByteBuffer.allocate(data.limit());
        tableDecrypted.order(ByteOrder.LITTLE_ENDIAN);

        /* some common variables. */
        long seed1;
        long seed2 = 0xEEEEEEEEL;

        /* one key character. */
        long ch;
        long uInt;

        seed1 = seed;

        /* decrypt it. */
        while (size-- > 0)
        {
            int bufferIndex = (int) (0x400 + (seed1 & 0xFF));
            long bufferValue = stormBuffer[bufferIndex] & MASK_INT;
            seed2 = (seed2 + bufferValue) & MASK_INT;
            uInt = ((long) data.getInt()) & MASK_INT;
            ch = (uInt ^ ((seed1 + seed2) & MASK_INT)) & MASK_INT;
            seed1 = ((((~seed1 << 0x15) & MASK_INT) + 0x11111111L) & MASK_INT) | ((seed1 >> 0x0BL) & MASK_INT);
            seed2 = (ch + seed2 + ((seed2 << 5) & MASK_INT) + 3) & MASK_INT;
            tableDecrypted.putInt((int) (ch & MASK_INT));
        }

        while (sizePlain-- > 0)
        {
            tableDecrypted.put(data.get());
        }
        data.reset();
        tableDecrypted.rewind();

        return tableDecrypted;
    }

    /**
     * Creates the hash of the specified string with the specified offset.
     * 
     * @param str
     *            String to be hashed.
     * @param offset
     *            Offset in the decryption buffer.
     * @return Hashed string.
     */
    public static long hash(String str, int offset)
    {
        long seed1 = 0x7FED7FEDL;
        long seed2 = 0xEEEEEEEEL;

        for (char c : str.toCharArray())
        {
            long ch = Character.toUpperCase(c);
            seed1 = stormBuffer[(int) (offset + ch)] ^ (seed1 + seed2);
            seed1 = seed1 & MASK_INT;
            long seed12 = (seed1 + seed2) & MASK_INT;
            long seed2Shifted = (seed2 << 5) & MASK_INT;
            seed2 = (ch + seed12 + seed2Shifted + 3) & MASK_INT;
        }

        return seed1 & MASK_INT;
    }

    //    unsigned long ComputeFileKey(const char *lpszFilePath, const BlockTableEntry &amp;blockEntry, unsigned long nArchiveOffset)
    //    {
    //    assert(lpszFilePath);
    //    // Find the file name part of the path
    //    const char *lpszFileName = strrchr(lpszFilePath, '\\');
    //    if (lpszFileName)
    //    lpszFileName++; // Skip the \
    //    else
    //    lpszFileName = lpszFilePath;
    //    // Hash the name to get the base key
    //    unsigned long nFileKey = HashString(lpszFileName, MPQ_HASH_FILE_KEY);
    //    // Offset-adjust the key if necessary
    //    if (blockEntry.Flags &amp; BLOCK_OFFSET_ADJUSTED_KEY)
    //    nFileKey = (nFileKey + blockEntry.BlockOffset) ^ blockEntry.FileSize;
    //    return nFileKey;
    //    }
}

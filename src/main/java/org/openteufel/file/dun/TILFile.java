package org.openteufel.file.dun;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * TIL files contain indices for rendering the pillars found in the MIN files.
*
 * Each 8 byte sequence in the TIL file describes the 4 quadrants of a square,
* at 2 bytes each. Each square quadrant index is an index into the pillar
* array of the MIN file.
*
* The square is arranged as follows:
*
* top [0]
* /\
* left [2] /\/\ right [1]
* \/\/
* \/
* bottom [3]
 */
// TIL files contain information about how to arrange the pillars, which are
// constructed based on the MIN format, in order to form a square. Below is a
// description of the TIL format:
//
// TIL format:
// squares []Square
//
// Square format:
// PillarNumTop uint16
// PillarNumRight uint16
// PillarNumLeft uint16
// PillarNumBottom uint16
//
// ref: Image (pillar arrangement illustration)
public class TILFile
{
    private final short[][] squares;

    public TILFile(final ByteBuffer in)
    {
        final int num = in.remaining() / 8;
        this.squares = new short[num][];
        for (int i = 0; i < num; i++)
        {
            short[] t = new short[4];
            for (int ii = 0; ii < 4; ii++)
                t[ii] = in.getShort();
            this.squares[i] = t;
        }
        if (in.remaining() > 0)
            throw new IllegalStateException();
    }

    /**
     * @param id
     * @return Returns 4 pillar IDs. Iterate x in inner loop.
     */
    public short[] getSquare(final int id)
    {
        return this.squares[id];
    }

    @Override
    public String toString()
    {
        return "TILFile [squares=" + this.squares.length + "]";
    }
}

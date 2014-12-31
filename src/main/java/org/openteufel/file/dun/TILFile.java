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
    private final TILSquare[] squares;

    public TILFile(final ByteBuffer in)
    {
        final int num = in.remaining() / 8;
        this.squares = new TILSquare[num];
        for (int i = 0; i < num; i++)
        {
            final short pillarTop = in.getShort();
            final short pillarRight = in.getShort();
            final short pillarLeft = in.getShort();
            final short pillarBottom = in.getShort();
            this.squares[i] = new TILSquare(pillarTop, pillarRight, pillarLeft, pillarBottom);
        }
        if (in.remaining() > 0)
            throw new IllegalStateException();
    }

    public TILSquare getSquare(final int id)
    {
        return this.squares[id];
    }

    @Override
    public String toString()
    {
        return "TILFile [squares=" + Arrays.toString(this.squares) + "]";
    }
}

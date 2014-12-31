package org.openteufel.file.dun;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
* MIN files contain information on how to arrange the frames
* in a CEL image, in order to form a vertical pillar. This allows
* for 'tall' configurations of images, such as for trees and roofs.
*
* A single pillar is arranged in the following configuration,
* vertically from top to bottom. Depending on the specific file,
* MIN files will have either 16 or 10 blocks.
*
* +----+----+
* | 0  | 1  |
* +----+----+
* | 2  | 3  |
* +----+----+
* | 4  | 5  |
* +----+----+
* | 6  | 7  |
* +----+----+
* | 8  | 9  |
* +----+----+
* | 10 | 11 |
* +----+----+
* | 12 | 13 |
* +----+----+
* | 14 | 15 |
* +----+----+
*/
// MIN files contain information about how to arrange the frames, in a CEL image
// level file, in order to form a pillar. Below is a description of the MIN
// format.
//
// MIN format:
// pillars []Pillar
//
// Pillar format:
// // blocks contains 10 blocks for l1.min, l2.min and l3.min and 16 blocks
// // for l4.min and town.min.
// //
// // ref: BlockRect (block arrangement illustration)
// blocks [blockCount]uint16
//
// Block format:
// // block is a bitfield containing both frameNumPlus1 and Type:
// // frameNumPlus1 := block & 0x0FFF
// // Type := block & 0x7000
// block uint16
public class MINFile
{
    private final MINPillar[] pillars;

    public MINFile(final ByteBuffer in, final int blockCountForLevel)
    {
        final int numPillars = in.remaining() / (2*blockCountForLevel);

        this.pillars = new MINPillar[numPillars];
        for(int i=0;i<numPillars;i++)
        {
            final short[] blocks = new short[blockCountForLevel];
            for(int ii=0;ii<blockCountForLevel;ii++)
                blocks[blockCountForLevel - 1 - ii] = in.getShort();
            this.pillars[i] = new MINPillar(blocks);
        }
        if(in.remaining() > 0)
            throw new IllegalStateException();
    }

    public MINPillar getPillar(final int id)
    {
        return this.pillars[id];
    }

    @Override
    public String toString()
    {
        return "MINFile [pillars=" + Arrays.toString(this.pillars) + "]";
    }
}

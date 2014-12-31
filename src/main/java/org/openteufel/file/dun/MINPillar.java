package org.openteufel.file.dun;

import java.util.Arrays;

// Block contains information about which CEL decode algorithm (Type) that
// should be used to decode a specific FrameNum in a CEL image level file.
public class MINPillar
{
    private final short[] blocks;

    public MINPillar(final short[] blocks)
    {
        this.blocks = blocks;
    }

    public int getFrameNum(final int i)
    {
        final int frameNumPlus1 = (int)(this.blocks[i] & 0x0FFF);
        return frameNumPlus1 - 1;
    }

    public int getType(final int i)
    {
        return (int)(this.blocks[i] & 0x7000) >> 12;
    }

    public boolean isValid(final int i)
    {
        final int frameNumPlus1 = (int)(this.blocks[i] & 0x0FFF);
        return frameNumPlus1 > 0;
    }

    @Override
    public String toString()
    {
        return "MINPillar [blocks=" + Arrays.toString(this.blocks) + "]";
    }
}

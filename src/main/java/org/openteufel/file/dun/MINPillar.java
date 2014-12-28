package org.openteufel.file.dun;

import java.util.Arrays;

// Block contains information about which CEL decode algorithm (Type) that
// should be used to decode a specific FrameNum in a CEL image level file.
public class MINPillar
{
    private final short[] blocks;
    
    public MINPillar(short[] blocks)
    {
        this.blocks = blocks;
    }
    
    public int getFrameNum(int i)
    {
        int frameNumPlus1 = (int)(blocks[i] & 0x0FFF);
        return frameNumPlus1 - 1;
    }
    
    public int getType(int i)
    {
        return (int)(blocks[i] & 0x7000) >> 12;
    }
    
    public boolean isValid(int i)
    {
        int frameNumPlus1 = (int)(blocks[i] & 0x0FFF);
        return frameNumPlus1 > 0;
    }

    @Override
    public String toString()
    {
        return "MINPillar [blocks=" + Arrays.toString(blocks) + "]";
    }
}

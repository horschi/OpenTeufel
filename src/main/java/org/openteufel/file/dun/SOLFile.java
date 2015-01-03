package org.openteufel.file.dun;

import java.nio.ByteBuffer;
import java.util.Arrays;

// SOL files contain information about various pillar properties, such as
// transparency and collision. Below is a description of the SOL format:
//
// SOL format:
// // sol is a bitfield containing ###, ###, ###, ###, ###, ###, ### and ###:
// // ### := sol & 0x01
// // ### := sol & 0x02
// // ### := sol & 0x04 // block range (missiles and summoning of monsters).
// // ### := sol & 0x08 // allow transparency
// // ### := sol & 0x10
// // ### := sol & 0x20
// // ### := sol & 0x40
// // ### := sol & 0x80
// solids []uint8
//
// The solid properties of a pillar can be obtained using the pillarNum as an
// offset into the solids array.
public class SOLFile
{
    public static final byte CHECK_COLLISION       = 0x01;
    public static final byte CHECK_COLLISION_RANGE = 0x04;
    public static final byte CHECK_TRANSPARENCY    = 0x08;

    private final byte[]     solids;

    public SOLFile(final ByteBuffer in)
    {
        final int num = in.remaining();
        this.solids = new byte[num];
        for (int i = 0; i < num; i++)
        {
            this.solids[i] = in.get();
        }
        if (in.remaining() > 0)
            throw new IllegalStateException();
    }

    public byte[] getSolids()
    {
        return this.solids;
    }

    public boolean getSolidBlock(final int pillarId)
    {
        return (this.solids[pillarId] & CHECK_COLLISION) != 0;
    }

    public boolean getSolidBlockRange(final int pillarId)
    {
        return (this.solids[pillarId] & CHECK_COLLISION_RANGE) != 0;
    }

    public boolean getSolidAllowTransparency(final int pillarId)
    {
        return (this.solids[pillarId] & CHECK_TRANSPARENCY) != 0;
    }

    @Override
    public String toString()
    {
        return "SOLFile [solids=" + Arrays.toString(this.solids) + "]";
    }
}

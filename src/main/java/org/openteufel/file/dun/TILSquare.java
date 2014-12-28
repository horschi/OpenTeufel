package org.openteufel.file.dun;

public class TILSquare
{
    private final short pillarTop;
    private final short pillarRight;
    private final short pillarLeft;
    private final short pillarBottom;
    
    
    public TILSquare(short pillarTop, short pillarRight, short pillarLeft, short pillarBottom)
    {
        this.pillarTop = pillarTop;
        this.pillarRight = pillarRight;
        this.pillarLeft = pillarLeft;
        this.pillarBottom = pillarBottom;
    }

    public short getPillarTop()
    {
        return pillarTop;
    }

    public short getPillarRight()
    {
        return pillarRight;
    }

    public short getPillarLeft()
    {
        return pillarLeft;
    }

    public short getPillarBottom()
    {
        return pillarBottom;
    }

    @Override
    public String toString()
    {
        return "TILSquare [pillarTop=" + pillarTop + ", pillarRight=" + pillarRight + ", pillarLeft=" + pillarLeft + ", pillarBottom=" + pillarBottom + "]";
    }
}

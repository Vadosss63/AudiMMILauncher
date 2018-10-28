package audi.mmi.launcher;

import java.util.Vector;

public class EncoderByteMainHeader
{
    private Vector<Byte> m_vectorHeader;

    public EncoderByteMainHeader(Vector<Byte> vectorHeader)
    {
        this.m_vectorHeader = vectorHeader;
    }

    public int GetCommand()
    {
        return m_vectorHeader.get(2);
    }

    public int GetSize()
    {
        return convertToInt(3);
    }

    private int convertToInt(int startByte)
    {
        int val = m_vectorHeader.get(startByte + 1);
        val |= m_vectorHeader.get(startByte) << 8;
        return val;
    }
}

package audi.mmi.launcher;

import java.util.Vector;

public class EncoderTimeTrack
{
    private Vector<Byte> m_dataByte = new Vector<>();

    public void AddHeader(int numberFolder)
    {
        m_dataByte.clear();
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
        convertToByte(numberFolder);
    }

    public void AddTrackNumber(int trackNumber)
    {
        convertToByte(trackNumber);
    }

    public void AddCurrentTimePosition(int msec)
    {
        convertIntToByte(msec);
    }

    public byte[] GetDataByte()
    {
        byte[] data = new byte[m_dataByte.size()];
        for(int i = 0; i < m_dataByte.size(); i++)
        {
            data[i] = m_dataByte.get(i);
        }
        return data;
    }

    public Vector<Byte> GetVectorByte()
    {
        return m_dataByte;
    }

    private void convertToByte(int val)
    {
        byte b0 = (byte) val;
        byte b1 = (byte) (val >> 8);
        m_dataByte.add(b1);
        m_dataByte.add(b0);
    }

    private void convertIntToByte(int val)
    {
        byte b0 = (byte) val;
        byte b1 = (byte) (val >> 8);
        byte b2 = (byte) (val >> 16);
        byte b3 = (byte) (val >> 24);

        m_dataByte.add(b3);
        m_dataByte.add(b2);
        m_dataByte.add(b1);
        m_dataByte.add(b0);
    }


}

package audi.mmi.launcher;

import java.util.Vector;

public class EncoderListTracks
{
    private Vector<Byte> m_dataByte = new Vector<>();

    public void AddHeader(int numberFolder)
    {
        m_dataByte.clear();
        convertToByte(numberFolder);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
    }

    public void AddTrackNumber(int trackNumber)
    {
        convertToByte(trackNumber);
        m_dataByte.add((byte) 0x02);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
    }

    public void AddName(String name)
    {
        // стартовый байт для названия папки
        m_dataByte.add((byte) 0x02);

        if(name.length() > 20) name = name.substring(0, 20);

        for(byte byteName : name.getBytes())
            m_dataByte.add(byteName);

        // конец названия папки
        m_dataByte.add((byte) 0x00);
    }

    public void AddEnd()
    {
        m_dataByte.add((byte)0xFF);
        m_dataByte.add((byte)0xFF);

        m_dataByte.add((byte)0x01);
        m_dataByte.add((byte)0x00);

        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);

        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);

        m_dataByte.add((byte) 0x01);
        m_dataByte.add((byte) 0x00);
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

}

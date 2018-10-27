package audi.mmi.launcher;

import java.util.Vector;

public class EncoderFolders
{
    private Vector<Byte> m_dataByte = new Vector<>();

    public void AddHeader()
    {
        m_dataByte.clear();
        // Заголовок из 2-х байт 0x0000
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
    }

    public void AddName(String name)
    {
        // стартовый байт для названия папки
        m_dataByte.add((byte) 0x02);

        for(byte byteName : name.getBytes())
            m_dataByte.add(byteName);

        // конец названия папки
        m_dataByte.add((byte) 0x00);
    }

    public void AddNumber(int number)
    {
        convertToByte(number);
    }

    public void AddNumberTracks(int numberTracks)
    {
        convertToByte(numberTracks);
        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);
    }

    public void AddParentNumber(int parentNumber)
    {
        convertToByte(parentNumber);
    }

    public void AddEnd()
    {
        m_dataByte.add((byte)0x01);
        m_dataByte.add((byte)0x00);

        m_dataByte.add((byte)0xFF);
        m_dataByte.add((byte)0xFF);

        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);

        m_dataByte.add((byte) 0x00);
        m_dataByte.add((byte) 0x00);

        m_dataByte.add((byte) 0x00);
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

package audi.mmi.launcher;

import java.util.Vector;

public class EncoderMainHeader
{
    private Vector<Byte> m_dataByte;
    private Vector<Byte> m_vectorHeader = new Vector<>();

    public EncoderMainHeader(Vector<Byte> dataByte)
    {
        this.m_dataByte = dataByte;
    }

    public void AddMainHeader(byte command)
    {
        byte crc = CountCRC();
        AddHeader();
        AddCommand(command);
        AddSize();

        m_dataByte.addAll(0, m_vectorHeader);
        //  добавляем в конец CRC
        m_dataByte.add(crc);
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

    private void AddHeader()
    {
        m_vectorHeader.clear();
        m_vectorHeader.add((byte) 0xAB);
        m_vectorHeader.add((byte) 0xBA);
    }

    private void AddCommand(byte command)
    {
        m_vectorHeader.add(command);
    }

    private void AddSize()
    {
        int size = m_dataByte.size();
        convertToByte(size);
    }

    private void convertToByte(int val)
    {
        byte b0 = (byte) val;
        byte b1 = (byte) (val >> 8);
        m_vectorHeader.add(b1);
        m_vectorHeader.add(b0);
    }

    // TODO как - то расчитать CRC
    private byte CountCRC()
    {
        byte crc = (byte) 0xFF;

        for(int i = 0; i < m_dataByte.size(); i++)
        {
            byte dat = m_dataByte.get(i);

            for(byte j = 0; j < 8; j++)
            {
                byte aux = (byte) ((dat ^ crc) & 0x01);
                if(aux == 1) crc ^= 0x18;
                crc >>= 1;
                crc |= (aux << 7);
                dat >>= 1;
            }
        }
        return crc;
    }

}

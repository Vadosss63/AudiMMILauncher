package audi.mmi.launcher;

import java.util.Vector;

public class EncoderTrack
{
    private Vector<Byte> m_dataByte;

    public EncoderTrack(Vector<Byte> dataByte)
    {
        m_dataByte = dataByte;
    }

    public int GetFolder()
    {
        int numberFolder;
        numberFolder = convertToInt(0);
        return numberFolder;
    }

    public int GetTrackNumber()
    {
        int trackNumber = 0;
        trackNumber = convertToInt(2);
        return trackNumber;
    }

    private int convertToInt(int startByte)
    {
        int val = (m_dataByte.get(startByte) << 8) & 0xff00 | (m_dataByte.get(startByte + 1)) & 0x00ff;
        return val;
    }

}

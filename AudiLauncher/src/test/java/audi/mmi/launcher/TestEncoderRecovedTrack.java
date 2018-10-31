package audi.mmi.launcher;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestEncoderRecovedTrack
{
    private EncoderTrack encoderTrack;

    @Before
    public void setUp() throws Exception
    {
        Vector<Byte> dataByte = new Vector<Byte>();
        Byte[] data = {0x00, 0x07, 0x01, 0x68, 0x00, 0x00, 0x00, 0x00};
        dataByte.addAll(Arrays.asList(data));
        encoderTrack = new EncoderTrack(dataByte);
    }

    @Test
    public void GetFolderTest() throws Exception
    {
        int folder = encoderTrack.GetFolder();
        assertEquals(7,folder);
    }
    @Test
    public void GetTrackNumberTest() throws Exception
    {
        int trackNumber = encoderTrack.GetTrackNumber();
        assertEquals(360,trackNumber);
    }

}

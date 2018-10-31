package audi.mmi.launcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestEncoderTimeTrack
{
    private EncoderTimeTrack encoderTimeTrack;

    @Before
    public void setUp() throws Exception
    {
        encoderTimeTrack = new EncoderTimeTrack();
    }

    @Test
    public void AddHeaderTest() throws Exception
    {
        encoderTimeTrack.AddHeader(300);
        byte[] dataHeader = new byte[]{0x00, 0x00, 0x01, (byte) 0x2C};
        byte[] actualDataHeader = encoderTimeTrack.GetDataByte();
        assertArrayEquals(dataHeader, actualDataHeader);
    }


    @Test
    public void AddTrackNumber() throws Exception
    {
        encoderTimeTrack.AddTrackNumber(500);
        byte[] dataAddTrackNumber = new byte[]{0x01, (byte)0xF4};
        byte[] actualAddTrackNumber = encoderTimeTrack.GetDataByte();
        assertArrayEquals(dataAddTrackNumber, actualAddTrackNumber);
    }

    @Test
    public void AddTime() throws Exception
    {
        encoderTimeTrack.AddCurrentTimePosition(90005544);
        byte[] dataCurrentTimePosition = new byte[]{0x05, 0x5D, 0x60, 0x28};
        byte[] actualCurrentTimePosition = encoderTimeTrack.GetDataByte();
        assertArrayEquals(dataCurrentTimePosition, actualCurrentTimePosition);
    }

    @Test
    public void AllTest() throws Exception
    {
        encoderTimeTrack.AddHeader(270);
        encoderTimeTrack.AddTrackNumber(259);
        encoderTimeTrack.AddCurrentTimePosition(70000);

        byte[] data = new byte[]{
                0x00, 0x00,
                0x01, 0x0E,
                0x01, 0x03,
                0x00, 0x01, 0x11, 0x70,
        };

        byte[] actualData = encoderTimeTrack.GetDataByte();
        assertArrayEquals(data, actualData);
    }

}

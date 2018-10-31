package audi.mmi.launcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestEncoderListTracks
{
    private EncoderListTracks encoderListTracks;

    @Before
    public void setUp() throws Exception
    {
        encoderListTracks = new EncoderListTracks();
    }

    @Test
    public void AddHeaderTest() throws Exception
    {
        encoderListTracks.AddHeader(2);
        byte[] dataHeader = new byte[]{ 0x00, 0x02, 0x00, 0x00, 0x00};
        byte[] actualDataHeader =  encoderListTracks.GetDataByte();
        assertArrayEquals(dataHeader, actualDataHeader);
    }


    @Test
    public void AddTrackNumber() throws Exception
    {
        encoderListTracks.AddTrackNumber(500);
        byte[] dataAddTrackNumber = new byte[]{ 0x01, (byte) 0xF4, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00 };
        byte[] actualAddTrackNumber =  encoderListTracks.GetDataByte();
        assertArrayEquals(dataAddTrackNumber, actualAddTrackNumber);
    }

    @Test
    public void AddName() throws Exception
    {
        encoderListTracks.AddName("Track 12657468747897508608909245423456787654");

        byte[] dataName = new byte[]{
                0x02,
                0x54, 0x72, 0x61, 0x63, 0x6B, 0x20, 0x31,
                0x00};

        byte[] actualNameHeader =  encoderListTracks.GetDataByte();
        assertArrayEquals(dataName, actualNameHeader);
    }

    @Test
    public void AddEnd() throws Exception
    {
        encoderListTracks.AddEnd();
        byte[] dataEnd = new byte[]{
                (byte) 0xFF, (byte) 0xFF,
                0x01, 0x00,
                0x00, 0x00,
                0x00, 0x00,
                0x01, 0x00
        };
        byte[] actualEnd =  encoderListTracks.GetDataByte();
        assertArrayEquals(dataEnd, actualEnd);
    }

    @Test
    public void AllTest() throws Exception
    {
        encoderListTracks.AddHeader(2);
        encoderListTracks.AddTrackNumber(1);
        encoderListTracks.AddName("Track 1");
        encoderListTracks.AddEnd();

        byte[] data = new byte[]{
                0x00, 0x02,
                0x00, 0x00, 0x00,

                0x00, 0x01,
                0x02, 0x00, 0x00, 0x00, 0x00, 0x00,

                0x02,
                0x54, 0x72, 0x61, 0x63, 0x6B, 0x20, 0x31,
                0x00,

                (byte) 0xFF, (byte) 0xFF,
                0x01, 0x00,
                0x00, 0x00,
                0x00, 0x00,
                0x01, 0x00
        };

        byte[] actualData = encoderListTracks.GetDataByte();
        assertArrayEquals(data, actualData);
    }

}

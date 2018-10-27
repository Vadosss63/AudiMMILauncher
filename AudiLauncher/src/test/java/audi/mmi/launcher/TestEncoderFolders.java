package audi.mmi.launcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEncoderFolders
{
    private EncoderFolders encoderFolders;

    @Before
    public void setUp() throws Exception
    {
        encoderFolders = new EncoderFolders();
    }

    @Test
    public void AddHeaderTest() throws Exception
    {
        encoderFolders.AddHeader();
        byte[] dataHeader = new byte[]{ 0x00, 0x00};
        byte[] actualDataHeader =  encoderFolders.GetDataByte();
        assertArrayEquals(dataHeader, actualDataHeader);
    }

    @Test
    public void AddName() throws Exception
    {
        encoderFolders.AddName("MyFolder");

        byte[] dataName = new byte[]{
                0x02,
                0x4D, 0x79, 0x46, 0x6F, 0x6C, 0x64, 0x65, 0x72,
                0x00};

        byte[] actualNameHeader =  encoderFolders.GetDataByte();
        assertArrayEquals(dataName, actualNameHeader);
    }

    @Test
    public void AddNumber() throws Exception
    {
        encoderFolders.AddNumber(1);
        byte[] dataNumber = new byte[]{ 0x00, 0x01 };
        byte[] actualDataNumber =  encoderFolders.GetDataByte();
        assertArrayEquals(dataNumber, actualDataNumber);
    }

    @Test
    public void AddNumberTracks() throws Exception
    {
        encoderFolders.AddNumberTracks(3);
        byte[] dataNumberTracks = new byte[]{
                0x00, 0x03,
                0x00, 0x00 };
        byte[] actualNumberTracks =  encoderFolders.GetDataByte();
        assertArrayEquals(dataNumberTracks, actualNumberTracks);
    }

    @Test
    public void AddParentNumber() throws Exception
    {
        encoderFolders.AddParentNumber(0);
        byte[] dataParentNumber = new byte[]{ 0x00, 0x00 };
        byte[] actualParentNumber =  encoderFolders.GetDataByte();
        assertArrayEquals(dataParentNumber, actualParentNumber);
    }

    @Test
    public void AddEnd() throws Exception
    {
        encoderFolders.AddEnd();
        byte[] dataEnd = new byte[]{
                0x01, 0x00,
                (byte) 0xFF, (byte) 0xFF,
                0x00, 0x00,
                0x00, 0x00,
                0x00, 0x00
        };
        byte[] actualEnd =  encoderFolders.GetDataByte();
        assertArrayEquals(dataEnd, actualEnd);
    }

    @Test
    public void AllTest() throws Exception
    {
        encoderFolders.AddHeader();
        encoderFolders.AddName("MyFolder");
        encoderFolders.AddNumber(1);
        encoderFolders.AddNumberTracks(3);
        encoderFolders.AddParentNumber(0);
        encoderFolders.AddEnd();

        byte[] data = new byte[]{
                0x00, 0x00,
                0x02,
                0x4D, 0x79, 0x46, 0x6F, 0x6C, 0x64, 0x65, 0x72,
                0x00,
                0x00, 0x01,
                0x00, 0x03,
                0x00, 0x00,
                0x00, 0x00,
                0x01, 0x00,
                (byte) 0xFF, (byte) 0xFF,
                0x00, 0x00,
                0x00, 0x00,
                0x00, 0x00};

        byte[] actualData = encoderFolders.GetDataByte();
        assertArrayEquals(data, actualData);
    }

    @Test
    public void AllMainHeaderTest() throws Exception
    {
        encoderFolders.AddHeader();
        encoderFolders.AddName("MyFolder");
        encoderFolders.AddNumber(1);
        encoderFolders.AddNumberTracks(3);
        encoderFolders.AddParentNumber(0);
        encoderFolders.AddEnd();

        EncoderMainHeader encoderMainHeader = new EncoderMainHeader(encoderFolders.GetVectorByte());
        encoderMainHeader.AddMainHeader((byte) 0x02);

        byte[] actualDataMainHeader = encoderMainHeader.GetDataByte();

        byte[] dataMainHeader = new byte[]{
                (byte) 0xAB, (byte) 0xBA,
                0x02,
                0x00, 0x1E,
                0x00, 0x00, 0x02,
                0x4D, 0x79, 0x46, 0x6F, 0x6C, 0x64, 0x65, 0x72,
                0x00,
                0x00, 0x01,
                0x00, 0x03,
                0x00, 0x00,
                0x00, 0x00,
                0x01, 0x00,
                (byte) 0xFF, (byte) 0xFF,
                0x00, 0x00,
                0x00, 0x00,
                0x00, 0x00,
                0x62
        };
        assertArrayEquals(dataMainHeader, actualDataMainHeader);
    }

}

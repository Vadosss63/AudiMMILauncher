package audi.mmi.launcher;


public class Track implements NodeDirectory
{
    private String name;
    private int number = -1;
    private int parentNumber = -1;
    private String m_path;


    public Track(String name)
    {
        this.name = name;
    }

    public void setPath(String path)
    {
        this.m_path = path;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public void setParentNumber(int parentNumber)
    {
        this.parentNumber = parentNumber;
    }

    @Override
    public String GetName() {
        return name;
    }

    @Override
    public String GetPathDir() {
        return null;
    }

    @Override
    public int GetNumber() {
        return -1;
    }

    @Override
    public int GetParentNumber() {
        return 0;
    }

    @Override
    public int GetNumberTracks() {
        return 0;
    }

    @Override
    public boolean IsFolder() {
        return false;
    }
}

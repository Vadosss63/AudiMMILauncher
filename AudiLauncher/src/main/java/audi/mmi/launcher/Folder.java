package audi.mmi.launcher;

public class Folder implements NodeDirectory
{
    private String name;
    private int number;
    private int parentNumber;
    private int numberTracks;
    private String m_path;

    public Folder(String name)
    {
        this.name = name;
    }

    public void setPath(String path) {
        this.m_path = path;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setParentNumber(int parentNumber) {
        this.parentNumber = parentNumber;
    }

    public void setNumberTracks(int numberTracks) {
        this.numberTracks = numberTracks;
    }

    @Override
    public void SetName(String name)
    {
        this.name = name;
    }

    @Override
    public String GetName()
    {
        return name;
    }

    @Override
    public String GetPathDir() {
        return m_path;
    }

    @Override
    public int GetNumber()
    {
        return number;
    }

    @Override
    public int GetParentNumber()
    {
        return parentNumber;
    }

    @Override
    public int GetNumberTracks()
    {
        return numberTracks;
    }

    @Override
    public boolean IsFolder()
    {
        return true;
    }
}

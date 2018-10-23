package audi.mmi.launcher;

public interface NodeDirectory
{
    void SetName(String name);
    String GetName();
    String GetPathDir();
    int GetNumber();
    int GetParentNumber();
    int GetNumberTracks();
    boolean IsFolder();
}

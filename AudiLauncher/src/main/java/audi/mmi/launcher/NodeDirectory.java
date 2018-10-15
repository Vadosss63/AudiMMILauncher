package audi.mmi.launcher;

public interface NodeDirectory
{
    String GetName();
    String GetPathDir();
    int GetNumber();
    int GetParentNumber();
    int GetNumberTracks();
    boolean IsFolder();
}

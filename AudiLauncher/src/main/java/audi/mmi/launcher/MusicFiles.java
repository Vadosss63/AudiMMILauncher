package audi.mmi.launcher;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class MusicFiles
{
    // Номер для новой папки
    int m_newFolderNumber = 0;
    // Мап для хранения папок
    private Vector<NodeDirectory> m_mapFolders = new Vector<>();
    // Мап для хранения треков
    private HashMap<NodeDirectory, Vector<NodeDirectory>> m_mapTracks = new HashMap<>();
    // Мап для папок
    private HashMap<Integer, Vector<NodeDirectory>> m_mapChaldeanFolders = new HashMap<>();
    // Мар с для доступа к по пути
    private HashMap<String, NodeDirectory> m_mapPaths = new HashMap<>();

    public MusicFiles(String rootPathFolder)
    {
        GetAllFiles(rootPathFolder, 0);
    }

    public String GetPathTrack(int parentNumber, int number)
    {
        if(parentNumber < m_mapFolders.size())
        {
            NodeDirectory folder = m_mapFolders.get(parentNumber);
            Vector<NodeDirectory> listTracks = m_mapTracks.get(folder);
            if( number < listTracks.size())
            {
                NodeDirectory track = listTracks.get(number);
                return track.GetPathDir();
            }
        }
        return "";
    }

    public Vector<NodeDirectory> GetFolders(int parentFolder)
    {
        if(m_mapChaldeanFolders.containsKey(parentFolder))
           return m_mapChaldeanFolders.get(parentFolder);

        return new Vector<NodeDirectory>();
    }

    public Vector<NodeDirectory> GetTracks(int parentFolder)
    {
        if(parentFolder < m_mapFolders.size())
            return m_mapTracks.get(m_mapFolders.get(parentFolder));

        return new Vector<NodeDirectory>();
    }

    public int GetParentNumber(String dirPath)
    {
        NodeDirectory node = m_mapPaths.get(dirPath);
        return (node != null) ?  node.GetParentNumber() :  -1;
    }

    public int GetNumber(String dirPath)
    {
        NodeDirectory node = m_mapPaths.get(dirPath);
        return (node != null) ?  node.GetNumber() :  -1;
    }

    public int GetNumberTracks(int number)
    {
        NodeDirectory folder = m_mapFolders.get(number);
        return folder.GetNumberTracks();
    }

    public int GetNumberTracks(String dirPath)
    {
        return GetNumberTracks(GetNumber(dirPath));
    }

    // выполняет чтение папок с музыкой
    private void GetAllFiles(String dirPath, int parentIndex)
    {
        // Читаем дерикторию Получаем список файлов
        File parentFile  = new File(dirPath);
        File[] listFiles = new File(dirPath).listFiles();
        // Заполняем родительскую папку
        Folder parentFolder = new Folder(parentFile.getName());
        // устонавливаем родительскую папку
        parentFolder.setParentNumber(m_newFolderNumber);
        m_newFolderNumber++;
        // устонавливаем номер папки
        parentFolder.setNumber(m_newFolderNumber);
        // устанавливаем путь к папке
        parentFolder.setPath(parentFile.getPath());
        // количество дорожек в папке
        int numberTracks = 0;

        // Задаем папку
        m_mapFolders.add(parentFolder);

        Vector<NodeDirectory> mapTracks = new Vector<>();
        // формируем список папок и файлов
        for (File file : listFiles)
        {
            // Работаем только с доступными папками и файлами
            if (file.isHidden() && !file.canRead())
                continue;

            // проверяем файл дериктория???
            if (file.isDirectory())
            {
                // Задаем нулевую папу
                if(!m_mapChaldeanFolders.containsKey(parentIndex))
                {
                    Vector<NodeDirectory> list = new Vector<NodeDirectory>();
                    m_mapChaldeanFolders.put(parentIndex, list);
                }
                m_mapChaldeanFolders.get(parentIndex).add(parentFolder);


                // спускаемся рекурсивно читая вложенное содержимое
                GetAllFiles(file.getPath(), parentFolder.GetNumber());
                continue;
            }
            // проверяем типы файлов
            String filename = file.getName();
            if (filename.endsWith(".mp3") || filename.endsWith(".wma") || filename.endsWith(".ogg"))
            {
                Track track = new Track(filename);
                track.setNumber(numberTracks);
                track.setParentNumber(parentFolder.GetNumber());
                track.setPath(file.getPath());
                mapTracks.add(track);
                m_mapPaths.put(file.getPath(), track);
                numberTracks++;
            }
        }
        // Устаналиваем количество треков в папке
        parentFolder.setNumberTracks(numberTracks);
        m_mapTracks.put(parentFolder, mapTracks);
        m_mapPaths.put(parentFolder.GetPathDir(), parentFolder);
    }
}

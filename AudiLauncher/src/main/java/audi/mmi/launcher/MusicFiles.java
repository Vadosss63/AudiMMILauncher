package audi.mmi.launcher;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class MusicFiles
{
    // Номер для новой папки
    int m_newFolderNumber = 0;
    // Мап для хранения папок
    private Vector<NodeDirectory> m_mapFolders = new Vector<>();
    // Мап для хранения треков
    private HashMap<Integer, Vector<NodeDirectory>> m_mapTracks = new HashMap<>();
    // Мап для папок
    private HashMap<Integer, Vector<NodeDirectory>> m_mapChaldeanFolders = new HashMap<>();
    // Мар с для доступа к по пути
    private HashMap<String, NodeDirectory> m_mapPaths = new HashMap<>();

    public Vector<NodeDirectory> GetFolders()
    {
        return m_mapFolders;
    }

    public MusicFiles(String rootPathFolder)
    {
        GetAllFiles(rootPathFolder, 0);
    }

    public NodeDirectory GetParentFolder(NodeDirectory childFolder)
    {
        if(childFolder == null) return null;

        if(childFolder.GetParentNumber() == 0) return null;

        return m_mapFolders.get(childFolder.GetParentNumber() - 1);
    }

    public String GetPathTrack(int parentNumber, int number)
    {
        if(m_mapTracks.containsKey(parentNumber))
        {
            Vector<NodeDirectory> listTracks = m_mapTracks.get(parentNumber);
            if(number < listTracks.size())
            {
                NodeDirectory track = listTracks.get(number);
                return track.GetPathDir();
            }
        }
        return "";
    }

    public NodeDirectory GetTrack(int parentNumber, int number)
    {
        if(m_mapTracks.containsKey(parentNumber))
        {
            Vector<NodeDirectory> listTracks = m_mapTracks.get(parentNumber);
            if(number < listTracks.size() && number >= 0)
            {
                NodeDirectory track = listTracks.get(number);
                return track;
            }
        }
        return null;
    }

    public Vector<NodeDirectory> GetFolders(int parentFolder)
    {
        if(m_mapChaldeanFolders.containsKey(parentFolder))
            return m_mapChaldeanFolders.get(parentFolder);

        return new Vector<>();
    }

    public Vector<NodeDirectory> GetAllFiles(int parentFolder)
    {
        Vector<NodeDirectory> dataFales = new Vector<NodeDirectory>();
        if(m_mapChaldeanFolders.containsKey(parentFolder))
            dataFales.addAll(m_mapChaldeanFolders.get(parentFolder));

        if(m_mapTracks.containsKey(parentFolder)) dataFales.addAll(m_mapTracks.get(parentFolder));

        return dataFales;
    }

    public Vector<NodeDirectory> GetTracks(int parentFolder)
    {
        if(m_mapTracks.containsKey(parentFolder)) return m_mapTracks.get(parentFolder);

        return new Vector<NodeDirectory>();
    }

    public int GetParentNumber(String dirPath)
    {
        NodeDirectory node = m_mapPaths.get(dirPath);
        return (node != null) ? node.GetParentNumber() : -1;
    }

    public int GetNumber(String dirPath)
    {
        NodeDirectory node = m_mapPaths.get(dirPath);
        return (node != null) ? node.GetNumber() : -1;
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
        File parentFile = new File(dirPath);
        File[] listFiles = new File(dirPath).listFiles();

        Arrays.sort(listFiles, fileComparator);

        // Заполняем родительскую папку
        Folder parentFolder = new Folder(parentFile.getName());
        // устонавливаем родительскую папку
        parentFolder.setParentNumber(parentIndex);
        m_newFolderNumber++;
        // устонавливаем номер папки
        parentFolder.setNumber(m_newFolderNumber);
        // устанавливаем путь к папке
        parentFolder.setPath(parentFile.getPath());
        // количество дорожек в папке
        int numberTracks = 0;

        // Задаем папку
        m_mapFolders.add(parentFolder);

        // Задаем нулевую папу
        if(!m_mapChaldeanFolders.containsKey(parentIndex))
        {
            Vector<NodeDirectory> list = new Vector<>();
            m_mapChaldeanFolders.put(parentIndex, list);
        }
        m_mapChaldeanFolders.get(parentIndex).add(parentFolder);

        Vector<NodeDirectory> mapTracks = new Vector<>();
        // формируем список папок и файлов
        for(File file : listFiles)
        {
            // Работаем только с доступными папками и файлами
            if(file.isHidden() && !file.canRead()) continue;

            // проверяем файл дериктория???
            if(file.isDirectory())
            {
                // спускаемся рекурсивно читая вложенное содержимое
                GetAllFiles(file.getPath(), parentFolder.GetNumber());
                continue;
            }
            // проверяем типы файлов
            String filename = file.getName();
            if(filename.endsWith(".mp3") || filename.endsWith(".wma") || filename.endsWith(".ogg"))
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
        m_mapTracks.put(parentFolder.GetNumber(), mapTracks);
        m_mapPaths.put(parentFolder.GetPathDir(), parentFolder);
    }

    // Компоратор для сортировки дерикторий музыкальных треков
    private Comparator<? super File> fileComparator = (Comparator<File>) (file1, file2)->{

        if(file1.isDirectory() && !file2.isDirectory()) return -1;

        if(file2.isDirectory() && !file1.isDirectory()) return 1;

        String pathLowerCaseFile1 = file1.getName().toLowerCase();
        String pathLowerCaseFile2 = file2.getName().toLowerCase();
        return String.valueOf(pathLowerCaseFile1).compareTo(pathLowerCaseFile2);
    };
}

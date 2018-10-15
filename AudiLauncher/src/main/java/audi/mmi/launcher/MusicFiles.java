package audi.mmi.launcher;

import java.io.File;
import java.util.HashMap;

public class MusicFiles
{
    // Имя корневой папки
    private String m_rootPathFolder;
    // Номер для новой папки
    int m_newFolderNumber = 0;
    // Мап для хранения папок
    private HashMap<Integer, NodeDirectory> m_mapFloderes = new HashMap<>();
    // Мап для хранения треков
    private HashMap<NodeDirectory, HashMap<Integer, NodeDirectory>> m_mapTracks = new HashMap<>();
    // Мар с для доступа к по пути
    private HashMap<String, NodeDirectory> m_mapPaths = new HashMap<>();

    public MusicFiles(String rootPathFolder)
    {
        this.m_rootPathFolder = rootPathFolder;
        GetAllFiles(m_rootPathFolder);
    }

    public String GetPath(int parentNumber, int number)
    {
        return "";
    }

    public int GetParentNumber(String dirPath)
    {
        return -1;
    }

    public int GetNumber(String dirPath)
    {
        return -1;
    }

    public int GetNumberTracks(int parentNumber, int number)
    {
        return -1;
    }

    public int GetNumberTracks(String dirPath)
    {
        return GetNumberTracks(GetParentNumber(dirPath), GetNumber(dirPath));
    }

    // выполняет чтение папок с музыкой
    private void GetAllFiles(String dirPath)
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
        HashMap<Integer, NodeDirectory> mapTrack = new HashMap<>();
        // формируем список папок и файлов
        for (File file : listFiles)
        {
            // Работаем только с доступными папками и файлами
            if (file.isHidden() && !file.canRead())
                continue;

            // роверяем файл дериктория???
            if (file.isDirectory())
            {
                // спускаемся рекурсивно читая вложенное содержимое
                GetAllFiles(file.getPath());
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
                mapTrack.put(numberTracks, track);
                m_mapPaths.put(file.getPath(), track);
                numberTracks++;
            }
        }
        // Устаналиваем количество треков в папке
        parentFolder.setNumberTracks(numberTracks);
        m_mapFloderes.put(parentFolder.GetNumber(), parentFolder);
        m_mapTracks.put(parentFolder, mapTrack);
        m_mapPaths.put(parentFolder.GetPathDir(), parentFolder);
    }
}

package audi.mmi.launcher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnCompletionListener {

    // текущие время
    private Time m_Time;
    private Handler m_handler;
    private Runnable m_runnable;
    private Runnable m_runnable2;
    // формат времени
    private int m_hours, m_minutes;
    // список отображения
    private List<String> m_pathList = null;
    private String m_dirRoot = "/"; // символ для корневого элемента

    // текущий трек
    private String m_dirAudioTrack;
    // Текущий номер дорожки в списке
    private int m_currentIndexAudioTrack = -1;
    //  плаер для воспроизведения
    private MPlayer m_MPlayer;
    // список востроизведения
    private ListView m_playList;
    ArrayAdapter<String> m_adapterPlayList;
    List<String> itemList = new ArrayList();
    // время воспроизведения
    TextView m_playTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mplayer);

        m_playList = (ListView) findViewById(R.id.PlayList);
        // Можно выводить на экран список
        m_adapterPlayList = new ArrayAdapter<String>(this, R.layout.list_item2, itemList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if(convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.list_item2, null);

                TextView trackLabel = (TextView)convertView.findViewById(R.id.textViewContent);
                trackLabel.setTypeface(Typeface.createFromAsset(getAssets(), "font2.ttf"));
                trackLabel.setText(itemList.get(position));
                ImageView imageView = (ImageView)convertView.findViewById(R.id.TrackSelected);
                TextView trackTime = (TextView)convertView.findViewById(R.id.TrackTime);
                trackTime.setTypeface(Typeface.createFromAsset(getAssets(), "font2.ttf"));


                if(m_dirAudioTrack == m_pathList.get(position))
                {
                    imageView.setSelected(true);
                    trackLabel.setSelected(true);
                    trackTime.setSelected(true);
                    m_playTime = trackTime;

                }
                else
                {
                    imageView.setSelected(false);
                    trackLabel.setSelected(false);
                    trackTime.setSelected(false);
                    trackTime.setText("");
                }
                return convertView;
            }
        };

        m_playList.setAdapter(m_adapterPlayList);
        m_playList.setOnItemClickListener(this);

        m_dirRoot = Environment.getExternalStorageDirectory().getPath();
        getDir(m_dirRoot); // выводим список файлов и папок в корневой папке системы

        SetDecorView();
        ImageView view = findViewById(R.id.view);
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim));

        m_Time = new Time();

        CreateTime();

        m_runnable2 = new Runnable()
        {
            @Override
            public void run() {
                SetDecorView();
                m_handler.postDelayed(m_runnable2, 4000);
            }
        };

        m_handler = new Handler();
        m_handler.post(m_runnable);
        m_handler.postDelayed(m_runnable2, 100);

        CreateButtons();

        m_MPlayer = new MPlayer();
        m_MPlayer.RegisterPlayer(this);

//        String dirPath = m_dirRoot + "/Music";
//        MusicFiles musicFiles = new MusicFiles(dirPath);

    }

    private void CreateButtons()
    {
        Button ltbutton = findViewById(R.id.ltbutton);
        ltbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        Button lbbutton = findViewById(R.id.lbbutton);
        lbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        Button rtbutton = findViewById(R.id.rtbutton);
        rtbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        Button rbbutton = findViewById(R.id.rbbutton);
        rbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        lbbutton.setOnClickListener(this);
    }

    private void CreateTime()
    {
        m_runnable = new Runnable()
        {
            @Override
            public void run()
            {
                m_Time.setToNow();
                m_hours = m_Time.hour;
                m_minutes = m_Time.minute;
                int second = m_Time.second;
                TextView myText = (TextView) findViewById(R.id.texttime);
                myText.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
                String timeText;
                if((second % 2) == 1)
                    timeText = String.format("%02d %02d", m_hours, m_minutes);
                else
                    timeText = String.format("%02d:%02d", m_hours, m_minutes);

                myText.setText(timeText);
                if(m_playTime != null)
                {
                    if(m_playTime.isSelected())
                    {
                        m_playTime.setText(m_MPlayer.GetCurrentTimePlay());
                    }
                }
                m_handler.postDelayed(m_runnable, 900);
            }
        };
    }

    private void SetDecorView()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility
                ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    // действие приповторном нажании на кнопку
    @Override
    public void onClick(View v)
    {
        if(v.getId() != R.id.lbbutton)
            return;

        BackToHome();
    }

    private void StartAnimation()
    {
        ImageView view = findViewById(R.id.view);
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim2));
    }

    @Override
    public void onBackPressed()
    {
        if(m_pathList.isEmpty())
            BackToHome();
        else
            getDir(m_pathList.get(0));
    }

    private void BackToHome()
    {
        StartAnimation();
        Intent intent = new Intent(this, Home.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_on,R.anim.alpha_off);
    }

    private void getDir(String dirPath)
    {
        File file = new File(dirPath);
        File[] filesArray = file.listFiles();
        // получаем список файлов
        itemList = new ArrayList<>();
        m_pathList = new ArrayList<>();
        // если мы не в корневой папке
        if (!dirPath.equals(m_dirRoot))
        {
            itemList.add(m_dirRoot);
            itemList.add("../");

            m_pathList.add(m_dirRoot);
            m_pathList.add(file.getParent());
        }

        Arrays.sort(filesArray, fileComparator);

        // формируем список папок и файлов для передачи адаптеру
        for (File aFilesArray : filesArray)
        {
            file = aFilesArray;
            String filename = file.getName();

            // Работаем только с доступными папками и файлами
            if (!file.isHidden() && file.canRead())
                if (file.isDirectory())
                {
                    m_pathList.add(file.getPath());
                    itemList.add(file.getName() + "/");
                }
                else if (filename.endsWith(".mp3") || filename.endsWith(".wma") || filename.endsWith(".ogg"))
                {
                    m_pathList.add(file.getPath());
                    itemList.add(file.getName());
                }
        }
        m_adapterPlayList.clear();
        m_adapterPlayList.addAll(itemList);
        m_adapterPlayList.notifyDataSetChanged();
    }

    // Компоратор для сортировки дерикторий музыкальных треков
    Comparator<? super File> fileComparator = new Comparator<File>()
    {
        public int compare(File file1, File file2)
        {
            if (file1.isDirectory() && !file2.isDirectory())
                return -1;

            if (file2.isDirectory() && !file1.isDirectory())
                return 1;

            String pathLowerCaseFile1 = file1.getName().toLowerCase();
            String pathLowerCaseFile2 = file2.getName().toLowerCase();
            return String.valueOf(pathLowerCaseFile1).compareTo(pathLowerCaseFile2);
        }
    };

    // Воспроизведение песни
    private void Play(int position)
    {
        m_playList.smoothScrollToPosition(position);
        m_adapterPlayList.notifyDataSetChanged();
        // пока у нас есть треки мы их воспроизводим
        File file = new File(m_pathList.get(position));
        if(CheckTypeFile(file)) {
            PlayMusic(file);
        }
    }

    // Проверяем файл песни
    private boolean CheckTypeFile(File file)
    {
        // если это папка
        if (file.isDirectory())
        {
            // если она доступна для просмотра, то заходим в неё
            if (file.canRead())
                getDir(file.getPath());
            else // если папка закрыта, то сообщаем об этом
                ErrorMassage(file);

            return false;
        }
        return true;
    }

    private void PlayMusic(File file)
    {
        // если элемент списка является файлом, то выводим его имя
        m_dirAudioTrack = file.getAbsolutePath();
        m_MPlayer.StartPlayer(m_dirAudioTrack);

    }

    // Сообщение об ошибке чтения файла
    private void ErrorMassage(File file)
    {
        String title = "[" + file.getName() + "] папка не доступна!";
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){}
                })
                .show();
    }

    // выполняетя врезультате окончания песни
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        // пытаемся воспроизвести следующий трек
        m_currentIndexAudioTrack++;
        Play(m_currentIndexAudioTrack);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        m_currentIndexAudioTrack  = position;
        // обработка нажатий на элементах списка
        Play(m_currentIndexAudioTrack);
    }
}

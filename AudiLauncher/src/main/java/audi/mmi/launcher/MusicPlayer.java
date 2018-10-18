package audi.mmi.launcher;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import java.util.Comparator;
import java.util.Vector;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnCompletionListener {

    // текущие время
    private Time m_Time;
    private Handler m_handler;
    private Runnable m_runnable;
    private Runnable m_runnable2;
    // формат времени
    private int m_hours, m_minutes;
    // текущий трек
    private String m_dirAudioTrack;
    // Текущий выбранный файл
    private NodeDirectory m_nodeDirectory;
    //  плаер для воспроизведения
    private MPlayer m_MPlayer;
    // список востроизведения
    private ListView m_playList;
    // дериктория для воспроизведения
    private  MusicFiles m_musicFiles;
    // время воспроизведения
    private  TextView m_playTime = null;
    // адаптер
    private ArrayAdapter<NodeDirectory> m_adapterPlayList;

    private void CreateAdapter()
    {
        m_adapterPlayList =  new ArrayAdapter<NodeDirectory>(this, R.layout.list_item2, m_musicFiles.GetAllFiles(1))
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if(convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.list_item2, null);

                TextView trackLabel = convertView.findViewById(R.id.textViewContent);
                trackLabel.setTypeface(Typeface.createFromAsset(getAssets(), "font2.ttf"));
                trackLabel.setText(getItem(position).GetName());

                ImageView imageView = convertView.findViewById(R.id.TrackSelected);
                TextView trackTime = convertView.findViewById(R.id.TrackTime);
                trackTime.setTypeface(Typeface.createFromAsset(getAssets(), "font2.ttf"));

                if(m_dirAudioTrack == getItem(position).GetPathDir())
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mplayer);

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
        CreatePlayer();
        CreateMusicFiles();
        CreateAdapter();
        CreatePlayList();
    }

    private void CreateMusicFiles()
    {
        String m_dirRoot = Environment.getExternalStorageDirectory().getPath();
        String dirPath = m_dirRoot + "/Music";
        m_musicFiles = new MusicFiles(dirPath);
    }

    private void CreatePlayList()
    {
        m_playList = findViewById(R.id.PlayList);
        m_playList.setAdapter(m_adapterPlayList);
        m_playList.setOnItemClickListener(this);
    }

    private void CreatePlayer()
    {
        m_MPlayer = new MPlayer();
        m_MPlayer.RegisterPlayer(this);
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
        BackToHome();
    }

    private void BackToHome()
    {
        StartAnimation();
        Intent intent = new Intent(this, Home.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_on,R.anim.alpha_off);
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
    private void Play()
    {
        int scrollPos = m_adapterPlayList.getPosition(m_nodeDirectory);
        m_playList.smoothScrollToPosition(scrollPos);
        m_adapterPlayList.notifyDataSetChanged();
        // пока у нас есть треки мы их воспроизводим
        if(m_nodeDirectory.IsFolder())
        {
            m_adapterPlayList.clear();
            Vector<NodeDirectory> files = m_musicFiles.GetAllFiles(m_nodeDirectory.GetNumber());
            m_adapterPlayList.addAll(files);
        }
        else
        {
            m_dirAudioTrack = m_nodeDirectory.GetPathDir();
            PlayMusic();
        }

    }

    private void PlayMusic()
    {
        m_MPlayer.StartPlayer(m_dirAudioTrack);
    }

    // выполняетя врезультате окончания песни
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        int indexTrack = m_adapterPlayList.getPosition(m_nodeDirectory) + 1;
        if(indexTrack < m_adapterPlayList.getCount())
        {
            m_nodeDirectory = m_adapterPlayList.getItem(indexTrack);
            Play();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {   // обработка нажатий на элементах списка
        m_nodeDirectory = (NodeDirectory)(parent.getItemAtPosition(position));
        Play();
    }
}

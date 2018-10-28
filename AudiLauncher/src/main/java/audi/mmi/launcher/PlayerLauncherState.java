package audi.mmi.launcher;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Vector;

public class PlayerLauncherState extends LauncherState implements AdapterView.OnItemClickListener, MediaPlayer.OnCompletionListener
{

    private UARTPort m_uartPort = new UARTPort();

    private Handler m_handler;
    private Runnable m_runnable;
    // текущий трек
    private String m_dirAudioTrack;
    // Текущий выбранный файл
    private NodeDirectory m_nodeDirectory;
    //  плаер для воспроизведения
    private MPlayer m_MPlayer;
    // дериктория для воспроизведения
    private MusicFiles m_musicFiles;
    // время воспроизведения
    private TextView m_playTime = null;
    // адаптер
    private ArrayAdapter<NodeDirectory> m_adapterPlayList;

    private static LauncherState m_instance = null;

    private PlayerLauncherState(Home home)
    {
        super(home);
        CreateTime();
        CreatePlayer();
        CreateMusicFiles();
        CreateAdapter();
        CreateUARTPort();

        if(m_uartPort.GetIsUartConfigured())
        {
            SendInfoFoldersToComPort();
            SendInfoTracksToComPort();
        }
    }

    private void SendInfoTracksToComPort()
    {
        Boolean isSend = false;

        Vector<NodeDirectory> folders = m_musicFiles.GetFolders();

        EncoderListTracks encoderListTracks = new EncoderListTracks();
        for(NodeDirectory folder : folders)
        {
            encoderListTracks.AddHeader(folder.GetNumber());
            Vector<NodeDirectory> tracks = m_musicFiles.GetTracks(folder.GetNumber());
            for(NodeDirectory track : tracks)
            {
                /// TODO уточнить
                encoderListTracks.AddTrackNumber(track.GetNumber() + 1);
                encoderListTracks.AddName(folder.GetName());
            }

            encoderListTracks.AddEnd();

            // Добавляем заголовок
            EncoderMainHeader headerData = new EncoderMainHeader(encoderListTracks.GetVectorByte());
            headerData.AddMainHeader((byte) 0x03);

            isSend = m_uartPort.WriteData(headerData.GetDataByte());
        }

        Toast toast;
        if(isSend)
        {
            toast = Toast.makeText(m_home, "Send", Toast.LENGTH_SHORT);
        } else
        {
            toast = Toast.makeText(m_home, "No Send", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    private void SendInfoFoldersToComPort()
    {
        Vector<NodeDirectory> folders = m_musicFiles.GetFolders();
        EncoderFolders encoderFolders = new EncoderFolders();
        encoderFolders.AddHeader();
        for(NodeDirectory folder : folders)
        {
            encoderFolders.AddName(folder.GetName());
            encoderFolders.AddNumber(folder.GetNumber());
            encoderFolders.AddNumberTracks(folder.GetNumberTracks());
            encoderFolders.AddParentNumber(folder.GetParentNumber());
        }
        encoderFolders.AddEnd();
        // Добавляем заголовок
        EncoderMainHeader headerData = new EncoderMainHeader(encoderFolders.GetVectorByte());
        headerData.AddMainHeader((byte) 0x02);

        Toast toast;
        if(m_uartPort.WriteData(headerData.GetDataByte()))
        {
            toast = Toast.makeText(m_home, "Send", Toast.LENGTH_SHORT);
        } else
        {
            toast = Toast.makeText(m_home, "No Send", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    static LauncherState Instance(Home home)
    {
        if(m_instance == null) m_instance = new PlayerLauncherState(home);
        return m_instance;
    }

    private void CreateUARTPort()
    {
        ///TODO для отладки
        Toast toast;
        if(m_uartPort.ConnectToManager(m_home))
        {
            m_uartPort.Connect();

            if(m_uartPort.GetIsUartConfigured())
            {
                toast = Toast.makeText(m_home, m_uartPort.GetTextLog(), Toast.LENGTH_SHORT);
                toast.show();
                m_uartPort.SetReadRunnable(()->{
                    ReadCommand();
                });
                m_uartPort.ReadData();
                // Запускаем прослушку команд управления
                if(m_uartPort.ReadData())
                {
                    toast = Toast.makeText(m_home, "Set Reader", Toast.LENGTH_SHORT);
                    toast.show();
                } else
                {
                    toast = Toast.makeText(m_home, "Reader " + m_uartPort.GetIsEnableRead(), Toast.LENGTH_SHORT);
                    toast.show();
                }


            } else
            {
                toast = Toast.makeText(m_home, "No conf", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else
        {
            toast = Toast.makeText(m_home, "Error", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // Обработка пришедших команд с порта
    private void ReadCommand()
    {
        byte[] data = m_uartPort.GetReadDataByte();
        Toast toast;
        toast = Toast.makeText(m_home, "Recive_" + data.length, Toast.LENGTH_LONG);
        toast.show();
        Vector<Byte> dataTrack = new Vector<Byte>();
        for(int i = 5; i < data.length - 1; i++)
        {
            dataTrack.add(data[i]);
        }
        EncoderTrack encoderTrack = new EncoderTrack(dataTrack);
        int folder = encoderTrack.GetFolder();
        int track = encoderTrack.GetTrackNumber() - 1;

        NodeDirectory trackNode = m_musicFiles.GetTrack(folder, track);
        if(trackNode != null)
        {
            // Преходим в папку
            if(m_musicFiles.GetParentFolder(trackNode) != null)
            {
                m_nodeDirectory = m_musicFiles.GetParentFolder(trackNode);

                Play();
            }
            // запускаем трек
            m_nodeDirectory = trackNode;
            Play();
        }
    }

    private void CreateAdapter()
    {
        m_adapterPlayList = new ArrayAdapter<NodeDirectory>(m_home, R.layout.music_track_item, m_musicFiles.GetAllFiles(1))
        {
            @SuppressLint ("InflateParams")
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent)
            {
                ///TODO подумать как сделать круче !!!
                if(convertView == null)
                    convertView = m_home.getLayoutInflater().inflate(R.layout.music_track_item, null);

                TextView trackLabel = convertView.findViewById(R.id.textViewContent);
                trackLabel.setTypeface(Typeface.createFromAsset(m_home.getAssets(), "font2.ttf"));

                ImageView folderImage = convertView.findViewById(R.id.folderImage);
                ImageView folderImageBack = convertView.findViewById(R.id.folderImageBack);
                folderImage.setVisibility(View.GONE);
                folderImageBack.setVisibility(View.GONE);
                trackLabel.setText(getItem(position).GetName());


                if(getItem(position).IsFolder())
                {
                    if(trackLabel.getText() == "вверх") folderImageBack.setVisibility(View.VISIBLE);
                    else folderImage.setVisibility(View.VISIBLE);
                }

                ImageView imageView = convertView.findViewById(R.id.TrackSelected);
                TextView trackTime = convertView.findViewById(R.id.TrackTime);
                trackTime.setTypeface(Typeface.createFromAsset(m_home.getAssets(), "font2.ttf"));

                if(m_dirAudioTrack == getItem(position).GetPathDir())
                {
                    imageView.setSelected(true);
                    trackLabel.setSelected(true);
                    trackTime.setSelected(true);
                    m_playTime = trackTime;
                } else
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
    public void PushLBButton()
    {
        SetSelectedButton(R.id.musicPlayer, false);
        m_home.StartAnimation();
        m_home.ChangeState(HomeLauncherState.Instance(m_home));
    }

    @Override
    public void PushRBButton()
    {
    }

    @Override
    public void PushLTButton()
    {
    }

    @Override
    public void PushRTButton()
    {
    }

    @Override
    public void ChangeAdapter()
    {
        TextButtonOff();
        LeftBottomTextOn();
        SetSelectedButton(R.id.musicPlayer, true);
        m_mainView.setAdapter(m_adapterPlayList);
        m_mainView.setOnItemClickListener(this);
        ScrollToSelectTrack();
    }

    @Override
    public void OnBackPressed()
    {
        NodeDirectory nodeDirectory = m_musicFiles.GetParentFolder(m_nodeDirectory);
        if(nodeDirectory != null)
        {
            m_nodeDirectory = nodeDirectory;
            Play();
        }
    }

    private void CreateMusicFiles()
    {
        String m_dirRoot = Environment.getExternalStorageDirectory().getPath();
        String dirPath = m_dirRoot + "/Music";
        m_musicFiles = new MusicFiles(dirPath);
    }

    private void CreatePlayer()
    {
        m_MPlayer = new MPlayer();
        m_MPlayer.RegisterPlayer(this);
    }

    private void CreateTime()
    {
        m_runnable = ()->{

            if(m_playTime != null)
                if(m_playTime.isSelected()) m_playTime.setText(m_MPlayer.GetCurrentTimePlay());

            m_handler.postDelayed(m_runnable, 900);
        };

        m_handler = new Handler();
        m_handler.post(m_runnable);
    }

    // Воспроизведение песни
    private void Play()
    {
        ScrollToSelectTrack();
        // пока у нас есть треки мы их воспроизводим
        if(m_nodeDirectory.IsFolder())
        {
            m_adapterPlayList.clear();
            Vector<NodeDirectory> files = new Vector<>();
            NodeDirectory back = m_musicFiles.GetParentFolder(m_nodeDirectory);
            if(back != null)
            {
                back.SetName("вверх");
                files.add(back);
            }
            files.addAll(m_musicFiles.GetAllFiles(m_nodeDirectory.GetNumber()));
            m_adapterPlayList.addAll(files);
        } else
        {
            m_dirAudioTrack = m_nodeDirectory.GetPathDir();
            PlayMusic();
        }

    }

    /// TODO сделать скрол при возвращении
    private void ScrollToSelectTrack()
    {
        int scrollPos = m_adapterPlayList.getPosition(m_nodeDirectory);
        m_mainView.smoothScrollToPosition(scrollPos, 4);
        m_adapterPlayList.notifyDataSetChanged();
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
        m_nodeDirectory = (NodeDirectory) (parent.getItemAtPosition(position));
        Play();
    }

}

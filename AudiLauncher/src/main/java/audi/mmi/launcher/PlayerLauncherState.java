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

import java.util.Vector;

public class PlayerLauncherState extends LauncherState implements AdapterView.OnItemClickListener, MediaPlayer.OnCompletionListener
{

    private int sendTracksInfo = 0;

    private UARTPort m_uartPort = new UARTPort();
    private Handler m_handler;
    private Runnable m_runnable;
    // текущий трек
    private String m_dirAudioTrack;
    // Текущий выбранный трек
    private NodeDirectory m_currentTrack;
    // Текущая деректория показа
    private NodeDirectory m_currentDirectory;
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

//        if(m_uartPort.GetIsUartConfigured())
//        {
//            SendInfoFoldersToComPort();
//            SendInfoTracksToComPort();
//        }
    }

    private void SendInfoTracksToComPort()
    {
        Boolean isSend = false;

        Vector<NodeDirectory> folders = m_musicFiles.GetFolders();

        EncoderListTracks encoderListTracks = new EncoderListTracks();
        ///TODO testUart
        //        for(NodeDirectory folder : folders)
        {

            NodeDirectory folder = folders.get(sendTracksInfo % folders.size());
            encoderListTracks.AddHeader(folder.GetNumber());
            Vector<NodeDirectory> tracks = m_musicFiles.GetTracks(folder.GetNumber());
            for(NodeDirectory track : tracks)
            {
                /// TODO уточнить
                encoderListTracks.AddTrackNumber(track.GetNumber() + 1);
                encoderListTracks.AddName(track.GetName());
            }

            encoderListTracks.AddEnd();

            // Добавляем заголовок
            EncoderMainHeader headerData = new EncoderMainHeader(encoderListTracks.GetVectorByte());
            headerData.AddMainHeader((byte) 0x03);

            isSend = m_uartPort.WriteData(headerData.GetDataByte());
        }

        String msg = isSend ? "Send-" + sendTracksInfo : "No Send";
        Toast toast = Toast.makeText(m_home, msg, Toast.LENGTH_SHORT);
        toast.show();
        sendTracksInfo++;
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

        String msg = m_uartPort.WriteData(headerData.GetDataByte()) ? "Send" : "No Send";
        Toast toast = Toast.makeText(m_home, msg, Toast.LENGTH_SHORT);
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
        String msg = null;
        if(m_uartPort.ConnectToManager(m_home))
        {
            m_uartPort.Connect();
            if(m_uartPort.GetIsUartConfigured())
            {
                m_uartPort.SetReadRunnable(()->{
                    ReadCommand();
                });
                // Запускаем прослушку команд управления
                m_uartPort.ReadData();
                msg = m_uartPort.GetTextLog();

            } else
            {
                msg = "No conf";
            }
        } else
        {
            msg = "Error";
        }

        Toast toast = Toast.makeText(m_home, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    // Обработка пришедших команд с порта
    private void ReadCommand()
    {
        byte[] data = m_uartPort.GetReadDataByte();
        Toast toast;
        toast = Toast.makeText(m_home, "Command  " + data[2], Toast.LENGTH_LONG);
        toast.show();

        /// TODO исправить прием команд
        if(data[2] == (byte) 0x05)
        {
            Vector<Byte> dataTrack = new Vector<>();

            for(int i = 5; i < data.length - 1; i++)
            {
                dataTrack.add(data[i]);
            }
            EncoderTrack encoderTrack = new EncoderTrack(dataTrack);
            int folder = encoderTrack.GetFolder();
            int track = encoderTrack.GetTrackNumber() - 1;

            SelectTrack(folder, track);
        } else if(data[2] == (byte) 0x06)
        {
            Play();
        } else if(data[2] == (byte) 0x07)
        {
            Pause();
        } else if(data[2] == (byte) 0x08)
        {
            PlayPrevious();
        } else if(data[2] == (byte) 0x09)
        {
            PlayNext();
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

        if(m_currentTrack == null || m_currentDirectory == null) return;

        if(m_currentTrack.GetParentNumber() != m_currentDirectory.GetNumber())
            BackToParentFolder(m_currentTrack);
    }

    @Override
    public void PushRBButton()
    {
    }

    @Override
    public void PushLTButton()
    {
        ///TODO test UART
        if(m_uartPort.GetIsUartConfigured()) SendInfoFoldersToComPort();
    }

    @Override
    public void PushRTButton()
    {
        ///TODO test UART
        if(m_uartPort.GetIsUartConfigured())
        {
            SendInfoTracksToComPort();
        }
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
        BackToParentFolder(m_currentDirectory);
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

    private void OpenDirectory()
    {
        m_adapterPlayList.clear();
        Vector<NodeDirectory> files = new Vector<>();
        NodeDirectory back = m_musicFiles.GetParentFolder(m_currentDirectory);
        if(back != null)
        {
            back.SetName("вверх");
            files.add(back);
        }
        files.addAll(m_musicFiles.GetAllFiles(m_currentDirectory.GetNumber()));
        m_adapterPlayList.addAll(files);
    }

    private void Play()
    {
        m_MPlayer.Play();
    }

    private void Pause()
    {
        m_MPlayer.Pause();
    }

    private void PlayNext()
    {
        if(m_currentTrack != null)
        {
            int indexTrack = m_currentTrack.GetNumber() + 1;
            SelectTrack(m_currentTrack.GetParentNumber(), indexTrack);
        }
    }

    private void PlayPrevious()
    {
        if(m_currentTrack != null)
        {
            int indexTrack = m_currentTrack.GetNumber() - 1;
            SelectTrack(m_currentTrack.GetParentNumber(), indexTrack);
        }
    }

    private void PlayMusic()
    {
        ScrollToSelectTrack();
        m_dirAudioTrack = m_currentTrack.GetPathDir();
        m_MPlayer.StartPlayer(m_dirAudioTrack);
    }

    private void SelectTrack(int folder, int track)
    {
        NodeDirectory trackNode = m_musicFiles.GetTrack(folder, track);

        if(trackNode != null)
        {
            BackToParentFolder(trackNode);
            // запускаем трек
            m_currentTrack = trackNode;
            PlayMusic();
        }
    }

    private void BackToParentFolder(NodeDirectory trackNode)
    {
        NodeDirectory nodeDirectory = m_musicFiles.GetParentFolder(trackNode);
        // Преходим в папку
        if(nodeDirectory == null) return;

        if(nodeDirectory == m_currentDirectory) return;

        m_currentDirectory = nodeDirectory;
        OpenDirectory();
    }

    /// TODO сделать скрол при возвращении
    private void ScrollToSelectTrack()
    {
        int scrollPos = m_adapterPlayList.getPosition(m_currentTrack);
        m_mainView.smoothScrollToPosition(scrollPos);
        m_adapterPlayList.notifyDataSetChanged();
    }

    // выполняетя врезультате окончания песни
    @Override
    public void onCompletion(MediaPlayer mp)
    {
        PlayNext();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {   // обработка нажатий на элементах списка
        NodeDirectory nodeDirectory = (NodeDirectory) (parent.getItemAtPosition(position));
        // пока у нас есть треки мы их воспроизводим
        if(nodeDirectory.IsFolder())
        {
            m_currentDirectory = nodeDirectory;
            OpenDirectory();
        } else
        {
            m_currentTrack = nodeDirectory;
            PlayMusic();
        }
    }

}

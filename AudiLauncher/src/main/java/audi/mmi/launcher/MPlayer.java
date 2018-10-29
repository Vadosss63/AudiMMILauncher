package audi.mmi.launcher;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;
import java.sql.Time;

public class MPlayer
{
    // Плаеер для воспроизведения
    private MediaPlayer m_mediaPlayer;
    // наблюдатель
    private OnCompletionListener m_musicPlayer;

    public String GetCurrentTimePlay()
    {
        String timeString = "00:00";
        if(m_mediaPlayer != null)
        {
            Time time = new Time(m_mediaPlayer.getCurrentPosition());
            timeString = String.format("%02d:%02d", time.getMinutes(), time.getSeconds());
        }
        return timeString;
    }

    public void RegisterPlayer(OnCompletionListener Player)
    {
        m_musicPlayer = Player;
    }

    public void StartPlayer(String audio)
    {
        // Перезапускаем плеер
        CreatePlayer();
        // Устанавливаем дорожу
        SetupPlayer(audio);
        // Запускаем
        m_mediaPlayer.start();
        // Устанавливаем наблюдателя по оканчанию дорожки
        m_mediaPlayer.setOnCompletionListener(m_musicPlayer);
    }

    private void SetupPlayer(String audio)
    {
        try
        {
            m_mediaPlayer.setDataSource(audio);
            m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            m_mediaPlayer.prepare();

        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void CreatePlayer()
    {
        if(m_mediaPlayer != null) m_mediaPlayer.release();

        m_mediaPlayer = new MediaPlayer();
    }

    public void Play()
    {
        if(m_mediaPlayer != null) if(!m_mediaPlayer.isPlaying()) m_mediaPlayer.start();
    }

    public void Pause()
    {
        if(m_mediaPlayer != null) if(m_mediaPlayer.isPlaying()) m_mediaPlayer.pause();
    }

}
package audi.mmi.launcher;

import android.content.Context;
import android.icu.text.IDNA;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.nfc.Tag;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PlayerInstrumentedTest
{
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        MPlayer mplayer = new MPlayer();
        String time = mplayer.GetCurrentTimePlay();
        assertEquals("00:00", time );
        mplayer.Play();
        assertEquals(false, mplayer.IsPlay());
        mplayer.Pause();
        assertEquals(false,  mplayer.IsPlay());

        String m_dirRoot = Environment.getExternalStorageDirectory().getPath();
        String dirPath = m_dirRoot + "/Music";

        MusicFiles musicFiles = new MusicFiles(dirPath);

        String pathTrack = musicFiles.GetPathTrack(2, 1);
        mplayer.StartPlayer(pathTrack);

        assertEquals(true, mplayer.IsPlay());
          mplayer.Pause();
        assertEquals(false,  mplayer.IsPlay());
        mplayer.Play();
        assertEquals(true, mplayer.IsPlay());

        mplayer.SetOnTimedTextListener((MediaPlayer mp, TimedText text)->
        {
            Log.d(String.valueOf(Log.INFO),  text.getText());
        });

        appContext.wait(50000);


    }
}

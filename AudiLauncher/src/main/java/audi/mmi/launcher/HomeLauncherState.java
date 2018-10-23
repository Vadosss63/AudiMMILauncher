package audi.mmi.launcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class HomeLauncherState extends LauncherState
{
    private static LauncherState m_instance = null;

    private HomeLauncherState(Home home)
    {
        super(home);
    }

    static LauncherState Instance(Home home)
    {
        if(m_instance == null)
            m_instance = new HomeLauncherState(home);
        return m_instance;
    }

    @Override
    public void PushLBButton()
    {
        m_home.StartAnimation();
        int permissionStatus = ContextCompat.checkSelfPermission(m_home, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
            m_home.ChangeState(PlayerLauncherState.Instance(m_home));
        else
        {
            ActivityCompat.requestPermissions(m_home,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    m_home.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void PushRBButton()
    {
        String s = "geo:";
        m_home.StartAnimation();
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        m_home.StartActivity(intent);
    }

    @Override
    public void PushLTButton()
    {
        String s = "http://audi.com";
        m_home.StartAnimation();
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        m_home.StartActivity(intent);
    }

    @Override
    public void PushRTButton()
    {
        TextButtonOff();
        m_home.StartAnimation();
        m_home.ChangeState(AppsMenuLauncherState.Instance(m_home));
    }

    @Override
    public void ChangeAdapter()
    {
        TextButtonOn();
        m_mainView.setAdapter(null);
        m_mainView.setOnItemClickListener(null);
    }

}

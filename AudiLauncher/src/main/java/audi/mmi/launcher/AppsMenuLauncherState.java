package audi.mmi.launcher;

import android.widget.Button;

public class AppsMenuLauncherState extends LauncherState
{
    public AppsMenuLauncherState(Home home)
    {
        super(home);
        TextButtonOff();
        RightTopTextOn();
        SetSelectedButton(R.id.mainMenu,true);
    }

    @Override
    public void PushLBButton()
    {
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
        SetSelectedButton(R.id.mainMenu, false);
        m_home.StartAnimation();
        m_home.ChangeState(new HomeLauncherState(m_home));
    }

}

package audi.mmi.launcher;

public class PlayerLauncherState extends LauncherState
{
    public PlayerLauncherState(Home home)
    {
        super(home);
        TextButtonOff();
        LeftBottomTextOn();
        SetSelectedButton(R.id.musicPlayer,true);
    }

    @Override
    public void PushLBButton()
    {
        SetSelectedButton(R.id.musicPlayer,false);
        m_home.StartAnimation();
        m_home.ChangeState(new HomeLauncherState(m_home));
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
}

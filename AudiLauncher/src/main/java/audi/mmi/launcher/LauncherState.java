package audi.mmi.launcher;

import android.widget.Button;
import android.widget.ListView;

public abstract class LauncherState
{
    Home m_home;
    ListView m_mainView;

    public LauncherState(Home home)
    {
        this.m_home = home;
        m_mainView = m_home.findViewById(R.id.mainList);
    }

    public abstract void PushLBButton();

    public abstract void PushRBButton();

    public abstract void PushLTButton();

    public abstract void PushRTButton();

    public abstract void ChangeAdapter();

    public void OnBackPressed(){}


//    public abstract

    protected void TextButtonOff()
    {
        TextOffButton(R.id.mainMenu);
        TextOffButton(R.id.musicPlayer);
        TextOffButton(R.id.browser);
        TextOffButton(R.id.maps);
    }

    private void TextOffButton(int idButton)
    {
        Button button = (Button) m_home.findViewById(idButton);
        button.setText("");
    }

    protected void TextButtonOn()
    {
        RightTopTextOn();
        LeftBottomTextOn();
        LeftTopTextOn();
        RightTopButtonTextOn();
    }

    private void RightTopButtonTextOn()
    {
        SetTextButton(R.id.maps, R.string.rightBottomButton);
    }

    protected void LeftTopTextOn()
    {
        SetTextButton(R.id.browser, R.string.leftTopButtonText);
    }

    protected void LeftBottomTextOn()
    {
        SetTextButton(R.id.musicPlayer, R.string.leftBottomButtonText);
    }

    protected void RightTopTextOn()
    {
        SetTextButton(R.id.mainMenu, R.string.rightTopButton);
    }

    private void SetTextButton(int idButton, int idText)
    {
        Button mainMenuButton = (Button) m_home.findViewById(idButton);
        mainMenuButton.setText(idText);
    }

    protected void SetSelectedButton(int idButton, boolean b )
    {
        Button button = (Button) m_home.findViewById(idButton);
        button.setSelected(b);
    }
}


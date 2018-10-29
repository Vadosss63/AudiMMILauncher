package audi.mmi.launcher;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends AppCompatActivity implements View.OnClickListener
{
    static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private Time m_time;
    private Handler m_handler;
    private Runnable m_runnable;
    private int m_hours;
    private int m_minutes;
    private LauncherState m_launcherState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ChangeState(HomeLauncherState.Instance(this));
        SetDecorView();
        CreateTime();
        CreateButtons();
    }

    public void ChangeState(LauncherState launcherState)
    {
        this.m_launcherState = launcherState;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ImageView view = findViewById(R.id.mainView);
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.mainMenu:
                m_launcherState.PushRTButton();
                break;
            case R.id.browser:
                m_launcherState.PushLTButton();
                break;
            case R.id.maps:
                m_launcherState.PushRBButton();
                break;
            case R.id.musicPlayer:
                m_launcherState.PushLBButton();
                break;
            default:
                break;
        }
        m_launcherState.ChangeAdapter();
    }

    @Override
    public void onBackPressed()
    {
        m_launcherState.OnBackPressed();
    }

    private void SetDecorView()
    {
        View decorView = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            decorView.setSystemUiVisibility
                    ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    public void StartActivity(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_on, R.anim.alpha_off);
    }

    private void CreateButtons()
    {
        Button LTButton = findViewById(R.id.browser);
        LTButton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button LBButton = findViewById(R.id.musicPlayer);
        LBButton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button RTButton = findViewById(R.id.mainMenu);
        RTButton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button RBButton = findViewById(R.id.maps);
        RBButton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        RTButton.setOnClickListener(this);
        LTButton.setOnClickListener(this);
        RBButton.setOnClickListener(this);
        LBButton.setOnClickListener(this);
    }

    public void StartAnimation()
    {
        ImageView view = findViewById(R.id.mainView);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.menu_anim2));
    }

    private void CreateTime()
    {
        m_time = new Time();
        m_runnable = (Runnable)()->
        {
            m_time.setToNow();
            m_hours = m_time.hour;
            m_minutes = m_time.minute;
            TextView textTime = findViewById(R.id.timeText);
            textTime.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
            String text = (m_time.second % 2) == 0 ? String.format("%02d:%02d", m_hours, m_minutes):
                    String.format("%02d %02d", m_hours, m_minutes);

            textTime.setText(text);
            m_handler.postDelayed(m_runnable, 900);
        };

        m_handler = new Handler();
        m_handler.post(m_runnable);
    }
}

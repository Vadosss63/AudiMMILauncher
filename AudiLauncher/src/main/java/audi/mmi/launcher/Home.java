package audi.mmi.launcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        ChangeState(new HomeLauncherState(this));
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
    }

    @Override
    public void onBackPressed()
    {
//        Intent intent = new Intent(this, Home.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.alpha_on,R.anim.alpha_off);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        if (requestCode != REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE)
//            return;
//
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            StartMusic();
//        else
//        {
//            Toast toast = Toast.makeText(getApplicationContext(),"В доступе отказано!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }

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

    public void StartActivity(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_on, R.anim.alpha_off);
    }

    private void CreateButtons()
    {
        Button ltbutton = findViewById(R.id.browser);
        ltbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button lbbutton = findViewById(R.id.musicPlayer);
        lbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button rtbutton = findViewById(R.id.mainMenu);
        rtbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button rbbutton = findViewById(R.id.maps);
        rbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        rtbutton.setOnClickListener(this);
        ltbutton.setOnClickListener(this);
        rbbutton.setOnClickListener(this);
        lbbutton.setOnClickListener(this);
    }

    public void StartAnimation()
    {
        ImageView view = findViewById(R.id.mainView);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.menu_anim2));
    }

    private void CreateTime()
    {
        m_time = new Time();
        m_runnable = new Runnable()
        {
            @Override
            public void run()
            {
                m_time.setToNow();
                m_hours = m_time.hour;
                m_minutes = m_time.minute;
                TextView textTime = findViewById(R.id.timeText);
                textTime.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
                String text;
                if((m_time.second % 2) == 0)
                    text = String.format("%02d:%02d", m_hours, m_minutes);
                else
                    text = String.format("%02d %02d", m_hours, m_minutes);
                textTime.setText(text);
                m_handler.postDelayed(m_runnable, 900);
            }
        };
        m_handler = new Handler();
        m_handler.post(m_runnable);
    }
}

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

public abstract class ViewActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private Time m_time;
    private Handler m_handler;
    private Runnable m_runnable;
    private int m_hours, m_minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        SetDecorView();

        StartAnimation1(R.anim.menu_anim);
        CreateTime();
        CreateButtons();
    }

    private void StartAnimation1(int menu_anim)
    {
        ImageView view = findViewById(R.id.view);
        view.startAnimation(AnimationUtils.loadAnimation(this, menu_anim));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode != REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE)
            return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // permission granted
            StartMusic();
        }
        else
        {
            // permission denied
            Toast toast = Toast.makeText(getApplicationContext(),"В доступе отказано!", Toast.LENGTH_SHORT);
            toast.show();
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

    private void PushLBButton()
    {
        StartAnimation();
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
            StartMusic();
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    private void PushRBButton(String s)
    {
        StartAnimation();
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        StartActivity(intent);
    }

    private void StartActivity(Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_on, R.anim.alpha_off);
    }

    private void StartAnimation()
    {
        StartAnimation1(R.anim.menu_anim2);
    }

    private void PushLTButton(String s)
    {
        StartAnimation();
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        StartActivity(intent);
    }

    private void PushRTButton()
    {
        StartAnimation();
        Intent intent = new Intent(this, ApplicationsMenu.class);
        StartActivity(intent);
    }

    private void StartMusic()
    {
        Intent intent = new Intent(this, MusicPlayer.class);
        StartActivity(intent);
    }

    private void CreateButtons()
    {
        Button ltbutton = findViewById(R.id.ltbutton);
        ltbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button lbbutton = findViewById(R.id.lbbutton);
        lbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button rtbutton = findViewById(R.id.rtbutton);
        rtbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        Button rbbutton = findViewById(R.id.rbbutton);
        rbbutton.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));

        rtbutton.setOnClickListener(this);
        ltbutton.setOnClickListener(this);
        rbbutton.setOnClickListener(this);
        lbbutton.setOnClickListener(this);
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
                TextView textTime = (TextView) findViewById(R.id.texttime);
                textTime.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
                String text = String.format("%02d:%02d", m_hours, m_minutes);
                textTime.setText(text);
                m_handler.postDelayed(m_runnable, 900);
            }
        };
        m_handler = new Handler();
        m_handler.post(m_runnable);
    }
}

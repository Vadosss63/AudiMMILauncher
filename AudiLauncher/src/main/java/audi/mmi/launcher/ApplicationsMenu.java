package audi.mmi.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ApplicationsMenu extends AppCompatActivity implements View.OnClickListener
{
    private Time m_time;
    private Handler m_handler;
    private Runnable m_runnable;
    private Runnable m_runnable2;
    private int m_hours, m_minutes;
    private PackageManager m_packageManager;
    private List<AppDetail> m_apps;
    private ListView m_listAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        LoadApps();
        LoadListView();
        AddClickListener();

        SetDecorView();
        ImageView view = findViewById(R.id.view);
        ListView view2 = findViewById(R.id.apps_list);
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim));
        view2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim));

        CreateTime();

        m_runnable2 = new Runnable() {
            @Override
            public void run() {
                SetDecorView();
                m_handler.postDelayed(m_runnable2, 4000);
            }
        };

        m_handler = new Handler();
        m_handler.post(m_runnable);
        m_handler.postDelayed(m_runnable2, 100);
        CreateButtons();
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
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                );
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
                TextView myText = (TextView) findViewById(R.id.texttime);
                myText.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
                String timetext = String.format("%02d:%02d", m_hours, m_minutes);
                myText.setText(timetext);
                m_handler.postDelayed(m_runnable, 900);
            }
        };
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rtbutton:
                ImageView view = findViewById(R.id.view);
                ListView view2 = findViewById(R.id.apps_list);
                view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim2));
                view2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim2));

                Intent intent = new Intent(this, Home.class);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.alpha_on,R.anim.alpha_off);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        ImageView view = findViewById(R.id.view);
        ListView view2 = findViewById(R.id.apps_list);
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim2));
        view2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.menu_anim2));

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.alpha_on,R.anim.alpha_off);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void LoadApps()
    {
        m_packageManager = getPackageManager();
        m_apps = new ArrayList<AppDetail>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = m_packageManager.queryIntentActivities(intent, 0);
        for(ResolveInfo resolveInfo : availableActivities)
        {
            AppDetail app = new AppDetail();
            app.label = resolveInfo.loadLabel(m_packageManager);
            app.name = resolveInfo.activityInfo.packageName;
            app.icon = resolveInfo.activityInfo.loadIcon(m_packageManager);
            String nameApp = app.name.toString();
            if (!Objects.equals(nameApp, "audi.mmi.launcher"))
                m_apps.add(app);
        }
    }

    private void LoadListView()
    {
        m_listAdapters = (ListView)findViewById(R.id.apps_list);

        final ArrayAdapter<AppDetail> adapter =
                new ArrayAdapter<AppDetail>(this, R.layout.list_item, m_apps)
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {
                        if(convertView == null)
                            convertView = getLayoutInflater().inflate(R.layout.list_item, null);

                        ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                        appIcon.setImageDrawable(m_apps.get(position).icon);
                        TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                        appLabel.setTypeface(Typeface.createFromAsset(getAssets(), "font2.ttf"));;
                        appLabel.setText(m_apps.get(position).label);
                        return convertView;
                    }
                };

        adapter.sort(new Comparator<AppDetail>()
        {
            @Override
            public int compare(AppDetail appDetail, AppDetail t1)
            {
                String app1 = appDetail.label.toString();
                String app2 = t1.label.toString();
                return app1.compareTo(app2);
            }
        });

        m_listAdapters.setAdapter(adapter);
    }

    private void AddClickListener()
    {
        m_listAdapters.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {
                Intent i = m_packageManager.getLaunchIntentForPackage(m_apps.get(pos).name.toString());
                ApplicationsMenu.this.startActivity(i);
            }
        });
    }
}

package audi.mmi.launcher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class AppDetail
{
    CharSequence label;
    CharSequence name;
    Drawable icon;
}

public class AppsMenuLauncherState extends LauncherState
{
    private PackageManager m_packageManager;
    private List<AppDetail> m_apps;

    private ArrayAdapter<AppDetail> m_appsAdapter;

    private static LauncherState m_instance = null;

    private AppsMenuLauncherState(Home home)
    {
        super(home);
        LoadApps();
        CreateAdapter();
        AddClickListener();
    }

    static LauncherState Instance(Home home)
    {
        if(m_instance == null)
            m_instance = new AppsMenuLauncherState(home);
        return m_instance;
    }

    @Override
    public void PushLBButton(){}

    @Override
    public void PushRBButton(){}

    @Override
    public void PushLTButton(){}

    @Override
    public void PushRTButton()
    {
        SetSelectedButton(R.id.mainMenu, false);
        m_home.StartAnimation();
        m_home.ChangeState(HomeLauncherState.Instance(m_home));
    }

    @Override
    public void ChangeAdapter()
    {
        TextButtonOff();
        RightTopTextOn();
        SetSelectedButton(R.id.mainMenu,true);
        m_mainView.setAdapter(m_appsAdapter);
    }

    private void LoadApps()
    {
        m_packageManager = m_home.getPackageManager();
        m_apps = new ArrayList<>();

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
            if (!nameApp.equals("audi.mmi.launcher"))
                m_apps.add(app);
        }
    }

    private void CreateAdapter()
    {
        m_appsAdapter = new ArrayAdapter<AppDetail>(m_home, R.layout.apps_item, m_apps)
        {
            @NonNull
            @SuppressLint ("InflateParams")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent)
            {
                if(convertView == null)
                    convertView = m_home.getLayoutInflater().inflate(R.layout.apps_item, null);

                ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(m_apps.get(position).icon);
                TextView appLabel = convertView.findViewById(R.id.item_app_label);
                appLabel.setTypeface(Typeface.createFromAsset(m_home.getAssets(), "font2.ttf"));
                appLabel.setText(m_apps.get(position).label);
                return convertView;
            }
        };

        m_appsAdapter.sort((appDetail, t1)->{
            String app1 = appDetail.label.toString();
            String app2 = t1.label.toString();
            return app1.compareTo(app2);
        });

        ChangeAdapter();
    }

    private void AddClickListener()
    {
        // Запуск выбранного приложения
        m_mainView.setOnItemClickListener((av, v, pos, id)->{
            Intent i = m_packageManager.getLaunchIntentForPackage(m_apps.get(pos).name.toString());
            m_home.startActivity(i);
        });
    }
}

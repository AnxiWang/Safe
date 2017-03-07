package service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.wang.anxi.safe.R;

import java.util.Timer;
import java.util.TimerTask;

import engine.ProcessInfoProvider;
import receiver.MyAppWidgetProvider;

import static com.wang.anxi.safe.activity.ProcessManagerActivity.tag;

/**
 * Created by anxi on 2017/3/1.
 */
public class UpdateWidgetService extends Service {

    private Timer mTimer;
    private InnerReceiver mInnerReceiver;
    private static final String tag = "UpdateWidgetService";

    @Override
    public void onCreate() {
        startTimer();
        //手机锁屏不维护小部件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);

        super.onCreate();
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startTimer();
            } else {
                cancelTimer();
            }
        }
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAppWidget();
//                Log.i(tag, "5秒一次。。。。。。。");
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.example_appwidget);

        int processCount = ProcessInfoProvider.getProcessCount(this);
        remoteViews.setTextViewText(R.id.tv_widget_process_count, "进程总数："+ processCount);
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));

        remoteViews.setTextViewText(R.id.tv_widget_process_memory, "可用内存：" + strAvailSpace);
//        Log.i("进程总数：", totalProcess);
//        Log.i("可用内存：", strAvailSpace);

        //点击窗口小部件跳转到应用程序主界面, 延期意图
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        Intent broadCastIntent = new Intent("android.intent.action.KILLPROCESS");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast
                (this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcastPendingIntent);

        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        aWM.updateAppWidget(componentName, remoteViews);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver != null){
            unregisterReceiver(mInnerReceiver);
        }
        cancelTimer();
        super.onDestroy();
    }


}

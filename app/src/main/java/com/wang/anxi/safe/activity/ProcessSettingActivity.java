package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wang.anxi.safe.R;

import service.LockScreenService;
import utils.ConstantValue;
import utils.ServiceUtil;
import utils.SpUtils;

/**
 * Created by anxi on 2017/2/14.
 */
public class ProcessSettingActivity extends Activity {

    private CheckBox cb_show_system;
    private CheckBox cb_is_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initShowSystem();
        initLockScreenClean();
    }

    //锁屏清理
    private void initLockScreenClean() {
        cb_is_lock = (CheckBox) findViewById(R.id.cb_is_lock);
        boolean running = ServiceUtil.isRunning(this, "service.LockScreenService");
        cb_is_lock.setChecked(running);

        cb_is_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));

                }
            }
        });
    }

    //显示系统进程
    private void initShowSystem() {

        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        boolean showSystem = SpUtils.getBoolean(getApplication(), ConstantValue.SHOW_SYSTEM, false);
        cb_show_system.setChecked(showSystem);

        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SpUtils.putBoolean(getApplication(), ConstantValue.SHOW_SYSTEM, isChecked);
            }
        });
    }
}

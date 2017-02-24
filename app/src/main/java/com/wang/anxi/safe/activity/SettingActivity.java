package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wang.anxi.safe.R;

import service.AddressService;
import service.BlackNumberService;
import utils.ConstantValue;
import utils.ServiceUtil;
import utils.SpUtils;
import view.SettingClickView;
import view.SettingItemView;

import static android.view.View.*;

/**
 * Created by anxi on 16-10-15.
 */
public class SettingActivity extends Activity {

    private String[] mToastStyleDes;
    private int mToastStyle;
    private SettingClickView scv_toast_style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
        initAddress();
        initToastStyle();
        initToastLocation();
        initBlackNumber();
        
    }

    /*
    * 拦截黑名单中的电话和短信
    * */
    private void initBlackNumber() {
        final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceUtil.isRunning(this, "service.BlackNumberService");

        siv_blacknumber.setCheck(isRunning);

        siv_blacknumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_blacknumber.isCheck();
                siv_blacknumber.setCheck(!isCheck);
                if (!isCheck) {
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    private void initToastLocation() {
        SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_toast_location.setTitle("归属地提示框位置");
        scv_toast_location.setDes("设置归属地提示框位置");
        scv_toast_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        mToastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        mToastStyle = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        scv_toast_style.setDes(mToastStyleDes[mToastStyle]);

        scv_toast_style.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("请选择归属地样式");
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                dialog.dismiss();
                scv_toast_style.setDes(mToastStyleDes[which]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /*
    * 是否显示号码归属地
    * */
    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

        boolean isRunning = ServiceUtil.isRunning(this, "service.AddressService");
        siv_address.setCheck(isRunning);

        siv_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_address.isCheck();
                siv_address.setCheck(!isCheck);
                if (!isCheck) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    /*
    * 版本更新
    * */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
        
        //获取已有的状态,用作显示
        boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        //是否选中根据上一次存储的结果去做决定
        siv_update.setCheck(open_update);

        siv_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}

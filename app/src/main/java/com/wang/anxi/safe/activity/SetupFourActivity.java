package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wang.anxi.safe.R;

import utils.ConstantValue;
import utils.SpUtils;
import utils.ToastUtil;

/**
 * Created by anxi on 16-10-23.
 */
public class SetupFourActivity extends BaseSetupActivity{
    private CheckBox cb_box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_four);
        initUI();
    }

    @Override
    public void showNextPage() {
        boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putBoolean(this, ConstantValue.SETUP_OVER, true);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "请开启防盗保护");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupThreeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

    }

    private void initUI() {
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_box.setChecked(open_security);
        if (open_security) {
            cb_box.setText("安全设置已开启");
        } else {
            cb_box.setText("安全设置已关闭");
        }
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                if (isChecked) {
                    cb_box.setText("安全设置已开启");
                } else {
                    cb_box.setText("安全设置已关闭");
                }
            }
        });
    }
}

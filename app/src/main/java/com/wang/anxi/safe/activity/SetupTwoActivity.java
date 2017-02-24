package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.wang.anxi.safe.R;

import utils.ConstantValue;
import utils.SpUtils;
import utils.ToastUtil;
import view.SettingItemView;

/**
 * Created by anxi on 16-10-23.
 */
public class SetupTwoActivity extends BaseSetupActivity {
    private SettingItemView siv_sim_bound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_two);
        initUI();
    }

    @Override
    public void showNextPage() {
        String serialNumber = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(getApplicationContext(), SetupThreeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请绑定SIM卡");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupOneActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }
        //给整个SettingItemView设置监听器
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_sim_bound.isCheck();
                siv_sim_bound.setCheck(!isCheck);
                if (!isCheck) {
                    //这里要注册读取手机状态的权限,READ_PHONE_STATE
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtils.putString(getApplicationContext(),ConstantValue.SIM_NUMBER, simSerialNumber);
                } else {
                    //直接删除静态节点
                    SpUtils.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

}

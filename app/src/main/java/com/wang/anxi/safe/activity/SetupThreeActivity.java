package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wang.anxi.safe.R;

import utils.ConstantValue;
import utils.SpUtils;
import utils.ToastUtil;

/**
 * Created by anxi on 16-10-23.
 */
public class SetupThreeActivity extends BaseSetupActivity {

    private EditText et_phone_number;
    private Button bt_select_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_three);
        
        initUI();
    }

    @Override
    public void showNextPage() {
        String phone = et_phone_number.getText().toString();
//        String contact_phone = SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(getApplicationContext(), SetupFourActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请输入电话号码");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupTwoActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

    }

    private void initUI() {
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        //回显联系电话
        String phone = SpUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);

        bt_select_number = (Button) findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null) {
            String phone = data.getStringExtra("phoneNum");
            phone = phone.replace("-", "").replace(" ", "").replace("+86","").trim();
            et_phone_number.setText(phone);
            SpUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

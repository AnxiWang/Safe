package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.HashMap;

import engine.AddressDao;

/**
 * Created by anxi on 16-11-20.
 */
public class QueryAddressActivity extends Activity{

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private String mAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_query_result.setText(mAddress);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initUI();
    }

    private void initUI() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
        //点击按钮之后再开始查询
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    query(phone);//获取号码归属地，耗时操作，使用子线程
                } else {
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    et_phone.startAnimation(shake);
                    //插补器也可以自己定义效果相同，只需要修改返回的函数即可
//                    shake.setInterpolator(new Interpolator() {
//                        @Override
//                        public float getInterpolation(float input) {
//                            return fun(******);
//                        }
                    //手机的震动效果
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    vibrator.vibrate(2000);//一直震动
                    vibrator.vibrate(new long[]{2000, 2000, 2000, 2000}, 2);//带规则的震动

                }
            }
        });
        //实时查询
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_phone.getText().toString();
                query(phone);
            }
        });
    }

    private void query(final String phone) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}

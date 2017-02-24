package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import org.w3c.dom.Text;

import java.io.File;

import engine.SmsBackUp;

/**
 * Created by anxi on 16-11-20.
 */
public class AToolActivity extends Activity{

    private TextView tv_query_address;
    private TextView tv_sms_backup;
    private TextView tv_number_query;
    //    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        
        initPhoneAddress();
        initSmsBackUp();
        initCommonNumberQuery();
    }

    private void initCommonNumberQuery() {
        tv_number_query = (TextView) findViewById(R.id.tv_number_query);
        tv_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
//        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackUpDialog();
            }
        });
    }

    private void showBackUpDialog() {
        //创建一个带进度条的对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("短信备份");
        //设置进度条为水平的
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //显示进度条
        progressDialog.show();
        //短信数量可能很大，耗时操作
        new Thread() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "smsSafe.xml";
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
//                        pb_bar.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
//                        pb_bar.setProgress(index);
                    }
                });
//                SmsBackUp.backup(getApplicationContext(), path, pb_bar);
                progressDialog.dismiss();
            }
        }.start();

    }

    private void initPhoneAddress() {
        tv_query_address = (TextView) findViewById(R.id.tv_query_address);
        tv_query_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}

package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import utils.ConstantValue;
import utils.SpUtils;

/**
 * Created by anxi on 16-10-22.
 */
public class SetupOverActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setup_over);
            initUI();
        } else {
            Intent intent = new Intent(this, SetupOneActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initUI() {
        TextView tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
        TextView tv_safe_again = (TextView) findViewById(R.id.tv_safe_again);

        tv_safe_number.setText(SpUtils.getString(this, ConstantValue.CONTACT_PHONE, ""));

        tv_safe_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SetupOneActivity.class));
                finish();
            }
        });
    }
}

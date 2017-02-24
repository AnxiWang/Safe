package com.wang.anxi.safe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.wang.anxi.safe.R;

/**
 * Created by anxi on 16-10-22.
 */
public class SetupOneActivity extends BaseSetupActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_one);
    }

    @Override
    public void showNextPage() {
        Intent intent = new Intent(getApplicationContext(), SetupTwoActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void showPrePage() {

    }

}

package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by anxi on 16-10-30.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建手势识别器的对象
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            //监听方法
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getRawX()-e2.getRawX()>100) {
                    showNextPage();
                }
                if (e2.getRawX()-e1.getRawX()>100) {
                    showPrePage();
                }
                return super.onFling(e1,e2,velocityX,velocityY);
            }
        });
    }

    public abstract void showNextPage();
    public abstract void showPrePage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void nextPage(View view) {
        showNextPage();
    }

    public void previousPage(View view) {
        showPrePage();
    }

}

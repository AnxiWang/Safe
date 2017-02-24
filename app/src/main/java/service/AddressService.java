package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.anxi.safe.R;

import engine.AddressDao;
import utils.ConstantValue;
import utils.SpUtils;

/**
 * Created by anxi on 16-12-1.
 */
public class AddressService extends Service {

    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private String mAddress;
    private TextView tv_toast;
    private int[] mDrawableIds;
    private int mScreenHeight;
    private int mScreenWidth;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Override
    public void onCreate() {
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver, intentFilter);

        super.onCreate();
    }

    class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String resultData = getResultData();
            showToast(resultData);
        }
    }
    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWM != null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void showToast(String incomingNumber) {
//        Toast.makeText(getApplicationContext(), incomingNumber, Toast.LENGTH_SHORT).show();

        //设置toast的样式
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.CENTER;

        mViewToast = View.inflate(this, R.layout.toast_view, null);
        //将上面设置的样式应用到mViewToast中
        mWM.addView(mViewToast, mParams);

        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            //对不同的事件做不同的逻辑处理
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.y + disX;
                        params.y = params.y + disY;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth - mViewToast.getWidth()) {
                            params.x = mScreenWidth - mViewToast.getWidth();
                        }
                        if (params.y > mScreenHeight - mViewToast.getHeight() - 22) {
                            params.y = mScreenHeight - mViewToast.getHeight() - 22;
                        }

                        mWM.updateViewLayout(mViewToast, params);

                        //3,重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
                        SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);

                        break;
                }
                //在当前的情况下返回false不响应事件,返回true才会响应事件
                return true;
            }
        });

        params.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        query(incomingNumber);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

        mDrawableIds = new int[]{
                R.color.colorWhite,
                R.color.colorOrange,
                R.color.colorBlue,
                R.color.colorGray,
                R.color.colorGreen};

        int toastStyleIndex = SpUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

    }

    private void query(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mTM != null && mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mInnerOutCallReceiver != null) {
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }


}

package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import db.dao.BlackNumberDao;

/**
 * Created by anxi on 16-12-15.
 */

public class BlackNumberService extends Service{

    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private MyContentObserver mContentObserver;

    @Override
    public void onCreate() {
        mDao = BlackNumberDao.getInstance(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);

        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);

        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String incomingNumber) {
//        ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

        int mode = mDao.getMode(incomingNumber);
        if (mode == 2 || mode == 3) {
            try {
                //反射调用
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                Method method = clazz.getMethod("getService", String.class);
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mContentObserver = new MyContentObserver(new Handler(), incomingNumber);
            //观察数据库是否发生变化
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mContentObserver);
        }
    }

    class MyContentObserver extends ContentObserver{
        private String phone;
        public MyContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            //删除通话记录
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
            super.onChange(selfChange);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //注销广播
        if (mInnerSmsReceiver!= null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        //注销内容观察者
        if (mContentObserver!= null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        //取消对电话状态的监听
        if (mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //组件对象可以作为是否激活的判断标志

            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                int mode = mDao.getMode(originatingAddress);
                if (mode == 1 || mode == 3) {
                    abortBroadcast();
                }
            }
        }
    }


}

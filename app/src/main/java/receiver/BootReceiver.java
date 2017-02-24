package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import utils.ConstantValue;
import utils.SpUtils;

/**
 * Created by anxi on 16-10-31.
 */

/*
* 手机开机时会发送一个广播，在这个广播中可以获取到当前手机的sim卡卡号
* 当开启手机防盗的功能时，会将开启时手机的sim卡卡号存在SpUtils中
* 两者进行比对，如果不同则向安全号码发送一条sim 改变的短信。
* */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String spSimNum = SpUtils.getString(context, ConstantValue.SIM_NUMBER, "");

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        if (!spSimNum.equals(simSerialNumber)) {
            SmsManager sm = SmsManager.getDefault();
            String phone = SpUtils.getString(context,ConstantValue.CONTACT_PHONE, "");
            sm.sendTextMessage(phone, null, "sim change", null,null);
        }

    }
}

package receiver;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.wang.anxi.safe.R;

import service.LocationService;
import utils.ConstantValue;
import utils.SpUtils;

/**
 * Created by anxi on 16-11-5.
 */

/*
* 获取手机接收到短信的广播，然后根据手机是否开启安全防护
* 如果开启就并识别短信中是否包含某些特定的字符串
* 根据包含的字符串去做相应的操作
* */

public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager devicePolicyManager ;
    private ComponentName componentName ;

    @SuppressWarnings("WrongConstant")
    @SuppressLint("ShowToast")
    @Override
    public void onReceive(Context context, Intent intent) {

        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE) ;
        componentName = new ComponentName(context, DeviceAdmin.class);

        boolean open_security = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //组件对象可以作为是否激活的判断标志

            for (Object object: objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (messageBody.contains("#*location*#")) {
                    context.startService(new Intent(context, LocationService.class));
                }
                if (messageBody.contains("#*lockscreen*#")) {

                    if(devicePolicyManager.isAdminActive(componentName)){
                        //激活--->锁屏
                        devicePolicyManager.lockNow();
                        //锁屏同时去设置密码
//                        devicePolicyManager.resetPassword("123", 0);
                    }else{
                        Toast.makeText(context.getApplicationContext(), "请先激活", 0).show();
                    }
                }
                if (messageBody.contains("#*wipedata*#")) {
                    if(devicePolicyManager.isAdminActive(componentName)){
                        devicePolicyManager.wipeData(0);//手机数据
//					devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//手机sd卡数据
                    }else{
                        Toast.makeText(context.getApplicationContext(), "请先激活", 0).show();
                    }
                }
            }
        }
    }
}

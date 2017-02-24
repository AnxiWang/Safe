package utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by anxi on 16-12-2.
 * 根据是否开启开启服务，来确定现实的文字内容。
 */

public class ServiceUtil {
    public static boolean isRunning(Context ctx, String serviceName) {
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //监听手机的所有服务，然后进行匹配，匹配到返回true
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServicesInfo:runningServices) {
            if (serviceName.equals(runningServicesInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}

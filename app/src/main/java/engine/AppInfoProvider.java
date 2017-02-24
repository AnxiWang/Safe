package engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import db.domain.AppInfo;

/**
 * Created by anxi on 16-12-27.
 */

public class AppInfoProvider {
    /*
    * 返回当前手机的应用信息
    * */
    public static List<AppInfo> getAppInfoList(Context context) {

        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        List<AppInfo> appInfoList = new ArrayList<>();
        for (PackageInfo packageInfo: packageInfoList) {
            AppInfo appInfo = new AppInfo();
            //获取应用的包名
            appInfo.packageName = packageInfo.packageName;
            //应用名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(packageManager).toString();
            //获取图标
            appInfo.icon = applicationInfo.loadIcon(packageManager);
            //判断是否为系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //系统应用
                appInfo.isSystem = true;
            } else {
                //非系统应用
                appInfo.isSystem = false;
            }
            //判断是否为SD卡应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                //sd卡应用
                appInfo.isSdCard = true;
            } else {
                //非sd卡应用
                appInfo.isSdCard = false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
        
        
    }
}

package engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.util.Log;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.wang.anxi.safe.R;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.domain.ProcessInfo;

import static com.wang.anxi.safe.activity.ProcessManagerActivity.tag;

/**
 * Created by anxi on 2017/1/9.
 */

public class ProcessInfoProvider {

    public static int getProcessCount(Context ctx) {
        //获取activityManager对象
//        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程的集合
        List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
        //返回集合的总数
        return runningAppProcesses.size();
    }

    public static long getAvailSpace(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //给memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //返回对象中相应的可用内存大小
        return memoryInfo.availMem;
    }

    public static long getTotalSpace(Context ctx) {
//        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//        return memoryInfo.totalMem;   API 16以上才可以使用该方法

        //内存大小写入文件，从文件中读取
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            char[] charArray = lineOne.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : charArray) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null && bufferedReader != null) {
                    fileReader.close();
                    bufferedReader.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return 0;
    }

    public static List<ProcessInfo> getProcessInfo(Context ctx) {
        //获取进程相关信息
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();

        //安卓5.1之后只能这么写，不然读取之后只有当前进程
        List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
        List<ProcessInfo> processInfoList = new ArrayList<>();
        Log.i(tag, "大小：" + runningAppProcesses.size());

        for (AndroidAppProcess info: runningAppProcesses) {

            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.name;
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                processInfo.icon = applicationInfo.loadIcon(pm);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.name = info.name;
                processInfo.icon = ctx.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }

    //杀死一个进程
    public static void killProcess(Context ctx, ProcessInfo processInfo) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //杀死进程
        am.killBackgroundProcesses(processInfo.packageName);
    }

    //杀死所有进程
    public static void killAll(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();

        for (AndroidAppProcess info: runningAppProcesses) {
            if (info.name.equals(ctx.getPackageName())){
                continue;
            }
            am.killBackgroundProcesses(info.name);
        }
    }
}

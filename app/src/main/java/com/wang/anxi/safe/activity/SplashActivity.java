package com.wang.anxi.safe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.wang.anxi.safe.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import utils.ConstantValue;
import utils.SpUtils;
import utils.StreamUtil;
import utils.ToastUtil;

public class SplashActivity extends Activity {

    protected static final String tag = "SplashActivity";
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;

    private TextView tv_version_name;
    private RelativeLayout rl_root;
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(SplashActivity.this, "URL异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "IO异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(SplashActivity.this, "JSON解析异常");
                    enterHome();
                    break;
            }
        }
    };

    /*
    * 升级弹窗提示
    * */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /*
    * 下载新版本的apk
    * 下载链接和放置apk位置,该程序还存在问题,apk文件下载不下来
    * */
    private void downloadApk() {
        RequestParams params = new RequestParams(mDownloadUrl);
        params.setAutoRename(true);//断点下载
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Safe.apk";
        params.setSaveFilePath(path);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.i(tag, "更新失败");
            }

            @Override
            public void onFinished() {
                Log.i(tag, "下载结束");
            }

            @Override
            public void onSuccess(File file) {
                Log.i(tag, "下载成功");
                installApk(file);
            }

            @Override
            public void onLoading(long arg0, long arg1, boolean arg2) {
                Log.i(tag, "更新中.....");
            }

            @Override
            public void onStarted() {
                Log.i(tag, "开始更新");
            }

            @Override
            public void onWaiting() {
            }
        });
    }

    /*
    * 安装下载好的APk文件
    * */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
//                startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * 进入主界面
    */
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    * 主程序入口
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 第一种，必须继承的时Activity类,这种情况下也可以在AndroidManifest.xml文件中修改
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 第二种，必须继承的时AppCompatActivity类
//        ActionBar actionBar=getSupportActionBar();
//        actionBar.hide();
        // 第三种，必须继承的时AppCompatActivity类
        setContentView(R.layout.activity_splash);
        initUI();
        initData();
        initAnimation();
        initDB();

        if (!SpUtils.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
            initShortCut();
        } else {
            Toast.makeText(getApplicationContext(), "快捷方式已存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void initShortCut() {

//        String name = context.getResources().getString(R.string.shortcut);
//        if(hasShortcut(context,name)){
//            return;
//        }
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        // 快捷方式的名称
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全管家");
//        shortcut.putExtra("duplicate", false); // 不允许重复创建
//        ComponentName comp = new ComponentName(context.getPackageName(), HomeActivity.class.getName());
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_VIEW).setComponent(comp));
//        // 快捷方式的图标
//        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher);
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//        context.sendBroadcast(shortcut);

        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全管家");
        //隐式意图
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        sendBroadcast(intent);
        SpUtils.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
//        if (SpUtils.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
//            Toast.makeText(getApplicationContext(), "生成快捷方式", 0).show();
//        }
    }

    //初始化数据库的操作
    private void initDB() {
        initAddressDB("address.db");
        initCommonNumDB("commonnum.db");
    }

    /*
    * 将项目中放在raw下的address数据库文件读取出来
    * 并在data/data/com.wang.anxi.safe/files文件夹下新建一个同名数据库文件
    * */

    private void initAddressDB(String dbName) {
        //在file文件夹下新建一个同名数据库文件
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
//            stream = getAssets().open(dbName);
            stream= getApplicationContext().getResources().openRawResource(R.raw.address);
            fos = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs))!=-1){
                fos.write(bs,0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream!= null && fos!= null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initCommonNumDB(String dbName) {
        //在file文件夹下新建一个同名数据库文件
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            stream= getApplicationContext().getResources().openRawResource(R.raw.commonnum);
            fos = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs))!=-1){
                fos.write(bs,0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream!= null && fos!= null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }

    /*
    * 初始化数据
    */
    private void initData() {
        tv_version_name.setText("版本名称：" + getVersionName());
        mLocalVersionCode = getVersionCode();
        if (SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
//            enterHome();//直接调用会让进入程序非常快
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,4000);
            //在发送消息四秒后再处理ENTER_HOME状态吗对应的消息
        }
    }

    /*
    *获取服务器上的version
    */
    private void checkVersion() {
        new Thread() {
            public void run() {
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //172.16.44.51
                    //172.16.44.64
                    //10.0.2.2
                    URL url = new URL("http://172.16.44.64:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setReadTimeout(2000);
                    connection.setConnectTimeout(2000);
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String json = StreamUtil.streamToString(inputStream);
                        Log.i(tag, json);
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        String versionCode = jsonObject.getString("versionCode");
                        mVersionDes = jsonObject.getString("versionDes");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        /*Log.i(tag,versionName);
                        Log.i(tag,versionCode);
                        Log.i(tag,mVersionDes);
                        Log.i(tag,downloadUrl);
                        */
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            message.what = UPDATE_VERSION;
                        } else {
                            message.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    /*
    *获取版本号
    * @return
    */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo activityInfo = pm.getPackageInfo(getPackageName(), 0);
            return activityInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*
    *获取版本名称
    * @return
    */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo activityInfo = pm.getPackageInfo(getPackageName(), 0);
            return activityInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * 初始化UI
    */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }
}

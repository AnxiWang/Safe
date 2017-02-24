package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.ArrayList;
import java.util.List;

import db.domain.AppInfo;
import engine.AppInfoProvider;
import utils.ToastUtil;

/**
 * Created by anxi on 16-12-18.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private List<AppInfo> mAppInfoList;
    private ListView lv_app_list;
    private MyAdapter mAdapter;
    private List<AppInfo> mSystemInfo;
    private List<AppInfo> mUserInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new MyAdapter();
            lv_app_list.setAdapter(mAdapter);

            if (tv_des != null || mUserInfo != null) {
                tv_des.setText("用户应用(" + mUserInfo.size() + ")");
            }
        }
    };
    private TextView tv_des;
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;

    @Override
    public void onClick(View v) {
        /*
        *
        * */
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem) {
                    ToastUtil.show(getApplication(), "此应用不可以被卸载");
                } else {
//                    Intent intent = new Intent("android.intent.action.DELETE");这个有问题，获取不到卸载的权限
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                PackageManager packageManager = getPackageManager();
                //通过Launch开启指定包名
                Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtil.show(getApplication(), "此应用不能被开启");
                }
                break;
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为" + mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    class MyAdapter extends BaseAdapter{
        //获取数据适配器中条目类型总个数，修改成两种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserInfo.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mUserInfo.size() + mSystemInfo.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mUserInfo.size() + 1) {
                return null;
            } else {
                if (position < mUserInfo.size() + 1) {
                    return mUserInfo.get(position - 1);
                } else {
                    return mSystemInfo.get(position - mUserInfo.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);

            if (type == 0) {
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户应用(" + mUserInfo.size() + ")");
                } else {
                    holder.tv_title.setText("系统应用(" + mSystemInfo.size() + ")");
                }
                return convertView;
            } else {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                if (getItem(position).isSdCard) {
                    holder.tv_path.setText("sd卡应用");
                } else {
                    holder.tv_path.setText("手机应用");
                }
                return convertView;
            }
        }
    }
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initList();
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemInfo = new ArrayList<>();
                mUserInfo = new ArrayList<>();

                for (AppInfo appInfo: mAppInfoList) {
                    if (appInfo.isSystem) {
                        mSystemInfo.add(appInfo);
                    } else {
                        mUserInfo.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initList() {
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        tv_des = (TextView) findViewById(R.id.tv_des);

        //滚动ListView的时候显示标题
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mSystemInfo != null && mUserInfo != null) {
                    if (firstVisibleItem >= mUserInfo.size() + 1) {
                        tv_des.setText("系统应用(" + mSystemInfo.size() + ")");
                    } else {
                        tv_des.setText("用户应用(" + mUserInfo.size() + ")");
                    }
                }

            }
        });
        //ListView点击事件
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mUserInfo.size() + 1) {
                    return;
                } else {
                    if (position < mUserInfo.size() + 1) {
                        mAppInfo = mUserInfo.get(position - 1);
                    } else {
                        mAppInfo = mSystemInfo.get(position - mUserInfo.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

        //动画集合
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);


        mPopupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAsDropDown(view, 100, - view.getHeight());

        popupView.startAnimation(animationSet);
    }

    private void initTitle() {
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        long availSpace = getAvailSpace(path);
        String memoryAvailSize = Formatter.formatFileSize(this, availSpace);
        long sdAvailSpace = getAvailSpace(sdPath);
        String sdMemoryAvailSize = Formatter.formatFileSize(this, sdAvailSpace);

        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

        tv_memory.setText("磁盘可用：" + memoryAvailSize);
        tv_sd_memory.setText("sd卡可用：" + sdMemoryAvailSize);
    }

    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long count = statFs.getAvailableBlocks();
        long size = statFs.getBlockSize();
        return count*size;
    }
}

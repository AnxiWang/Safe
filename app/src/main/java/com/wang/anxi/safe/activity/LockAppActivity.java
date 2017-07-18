package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.ArrayList;
import java.util.List;

import db.dao.AppLockDao;
import db.domain.AppInfo;
import engine.AppInfoProvider;

/**
 * Created by anxi on 2017/3/7.
 */
public class LockAppActivity extends Activity{

    private Button btn_lock, btn_unlock;
    private LinearLayout ll_lock;
    private LinearLayout ll_unlock;
    private TextView tv_lock;
    private TextView tv_unlock;
    private ListView lv_lock;
    private ListView lv_unlock;
    private List<AppInfo> mAppInfoList;
    private ArrayList<AppInfo> mLockList;
    private ArrayList<AppInfo> mUnLockList;
    private AppLockDao mDao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            mUnLockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(mUnLockAdapter);

        }
    };
    private MyAdapter mLockAdapter, mUnLockAdapter;
    private TranslateAnimation mTranslateAnimation;

    class MyAdapter extends BaseAdapter{
        private boolean isLock;

        public MyAdapter(boolean isLock) {
            this.isLock = isLock;

        }
        @Override
        public int getCount() {
            if (isLock) {
                tv_lock.setText("已加锁应用：" + mLockList.size());
                return mLockList.size();
            } else {
                tv_unlock.setText("未加锁应用：" + mUnLockList.size());
                return mUnLockList.size();
            }
        }

        @Override
        public AppInfo getItem(int i) {
            if (isLock) {
                return mLockList.get(i);
            } else {
                return mUnLockList.get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_lock_name = (TextView) view.findViewById(R.id.tv_lock_name);
                holder.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final AppInfo appInfo = getItem(i);
            final View animationView = view;
            holder.iv_icon.setBackgroundDrawable(appInfo.icon);
            holder.tv_lock_name.setText(appInfo.name);
            if (isLock){
                holder.iv_lock.setBackgroundResource(R.mipmap.lock);
            } else {
                holder.iv_lock.setBackgroundResource(R.mipmap.unlock);
            }

            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animationView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock) {
                                mLockList.remove(appInfo);
                                mUnLockList.add(appInfo);
                                mDao.delete(appInfo.packageName);
                                mLockAdapter.notifyDataSetChanged();
                            } else {
                                mLockList.add(appInfo);
                                mUnLockList.remove(appInfo);
                                mDao.insert(appInfo.packageName);
                                mUnLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });
            return view;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_lock_name;
        ImageView iv_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_lock_app);
        super.onCreate(savedInstanceState);

        initUI();
        initData();
        initAnimation();

    }

    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mLockList = new ArrayList<>();
                mUnLockList = new ArrayList<>();
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appinfo:mAppInfoList) {
                    //循环到的应用包名在数据库中，则说明是已加锁应用
                    if (lockPackageList.contains(appinfo.packageName)) {
                        mLockList.add(appinfo);
                    } else {
                        mUnLockList.add(appinfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);

                btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
            }
        });

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);

                btn_lock.setBackgroundResource(R.drawable.tab_right_default);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);

            }
        });
    }
}

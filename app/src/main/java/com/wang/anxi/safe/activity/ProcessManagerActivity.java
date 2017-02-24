package com.wang.anxi.safe.activity;

import android.app.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.ArrayList;
import java.util.List;

import db.domain.ProcessInfo;
import engine.ProcessInfoProvider;
import utils.ConstantValue;
import utils.SpUtils;
import utils.ToastUtil;

/**
 * Created by anxi on 2017/1/9.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_process_count;
    private TextView tv_memory_info;
    private ListView lv_process_info;
    private Button bt_all;
    private Button bt_reverse;
    private Button bt_clean;
    private Button bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;
    private List<ProcessInfo> mSystemList;
    private List<ProcessInfo> mUserInfo;

    public static final String tag = "ProcessManagerActivity";

    private MyAdapter myAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myAdapter = new MyAdapter();
            lv_process_info.setAdapter(myAdapter);
            if (tv_des != null && mUserInfo != null) {
                tv_des.setText("用户应用（" + mUserInfo.size() + "）");
            }

        }
    };
    private TextView tv_des;
    private ProcessInfo mProcessInfo;
    private long mAvailSpace;
    private long mTotalSpace;


    private class MyAdapter extends BaseAdapter{

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
            if (SpUtils.getBoolean(getApplication(), ConstantValue.SHOW_SYSTEM, false)) {
                return mUserInfo.size() + mSystemList.size() + 2;
            } else {
                return mUserInfo.size() + 1;
            }

        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mUserInfo.size() + 1) {
                return null;
            } else {
                if (position < mUserInfo.size() + 1) {
                    return mUserInfo.get(position - 1);
                } else {
                    return mSystemList.get(position - mUserInfo.size() - 2);
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
                    holder.tv_title.setText("用户进程(" + mUserInfo.size() + ")");
                } else {
                    holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_process_box = (CheckBox) convertView.findViewById(R.id.cb_process_box);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                holder.tv_memory_info.setText(strSize);

                //如果是当前的应用则不可以被选中
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_process_box.setVisibility(View.GONE);
                } else {
                    holder.cb_process_box.setVisibility(View.VISIBLE);
                }

                holder.cb_process_box.setChecked(getItem(position).isCheck);

                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_process_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initTitleData();
        initListData();

    }

    private void initListData() {
        getData();
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
//                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplication());

                mSystemList = new ArrayList<ProcessInfo>();
                mUserInfo = new ArrayList<ProcessInfo>();

                for (ProcessInfo info: mProcessInfoList) {
                    if (info.isSystem) {
                        //系统进程
                        mSystemList.add(info);
                    } else {
                        mUserInfo.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数：" + mProcessCount);

        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

        mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, mTotalSpace);

        tv_memory_info.setText("剩余／总共：" + strAvailSpace + "／" + strTotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
        tv_des = (TextView) findViewById(R.id.tv_des);

        lv_process_info = (ListView) findViewById(R.id.lv_process_info);

        bt_all = (Button) findViewById(R.id.bt_all);
        bt_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_clean = (Button) findViewById(R.id.bt_clean);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener( this);

        lv_process_info.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mSystemList != null && mUserInfo != null) {
                    if (firstVisibleItem >= mUserInfo.size() + 1) {
                        tv_des.setText("系统进程(" + mSystemList.size() + ")");
                    } else {
                        tv_des.setText("用户进程(" + mUserInfo.size() + ")");
                    }
                }

            }
        });

        lv_process_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0 || position == mUserInfo.size() + 1) {
                    return;
                } else {
                    if (position < mUserInfo.size() + 1) {
                        mProcessInfo = mUserInfo.get(position - 1);
                    } else {
                        mProcessInfo = mSystemList.get(position - mUserInfo.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
                            mProcessInfo.isCheck = ! mProcessInfo.isCheck;
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_process_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });

    }
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_all:
                selectAll();
                break;
            case R.id.bt_reverse:
                selectReverse();
                break;
            case R.id.bt_clean:
                cleanAll();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }
    }

    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cleanAll() {
        List<ProcessInfo> killProcessList = new ArrayList<>();
        for (ProcessInfo processInfo: mUserInfo) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (processInfo.isCheck) {
                //记录要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }
        for (ProcessInfo processInfo: mSystemList) {
            if (processInfo.isCheck) {
                //记录要杀死的系统进程
                killProcessList.add(processInfo);
            }
        }
        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo: killProcessList) {
            if (mUserInfo.contains(processInfo)) {
                mUserInfo.remove(processInfo);
            }
            if (mSystemList.contains(processInfo)) {
                mSystemList.remove(processInfo);
            }
            ProcessInfoProvider.killProcess(this, processInfo);
            //记录释放总空间
            totalReleaseSpace += processInfo.memSize;
        }
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        mProcessCount = mProcessCount - killProcessList.size();
        mAvailSpace = totalReleaseSpace + mAvailSpace;

        tv_process_count.setText("进程总数：" + mProcessCount);

        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

        String strTotalSpace = Formatter.formatFileSize(this, mTotalSpace);

        String strTotalReleaseSpace = Formatter.formatFileSize(this, totalReleaseSpace);

        tv_memory_info.setText("剩余／总共：" + strAvailSpace + "／" + strTotalSpace);

        String killToast = String.format("杀死了%d个进程，释放了%s空间", killProcessList.size(), strTotalReleaseSpace);
        ToastUtil.show(getApplicationContext(), killToast);
    }

    private void selectReverse() {
            for (ProcessInfo processInfo: mUserInfo) {
                if (processInfo.getPackageName().equals(getPackageName())) {
                    continue;
                }
                processInfo.isCheck = !processInfo.isCheck;
            }
            for (ProcessInfo processInfo: mSystemList) {
                processInfo.isCheck = !processInfo.isCheck;
            }
            //通知数据适配器进行数据更新
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
    }

    private void selectAll() {
        for (ProcessInfo processInfo: mUserInfo) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }
        for (ProcessInfo processInfo: mSystemList) {
            processInfo.isCheck = true;
        }
        //通知数据适配器进行数据更新
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }




}

package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import utils.ConstantValue;
import utils.Md5Util;
import utils.SpUtils;
import utils.ToastUtil;

/**
 * Created by anxi on 16-10-10.
 */
public class HomeActivity extends Activity{

    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        initData();
    }

    private void initData() {
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };
        mDrawableIds = new int[]{
                R.mipmap.home_safe, R.mipmap.home_callmsgsafe,
                R.mipmap.home_apps, R.mipmap.home_taskmanager,
                R.mipmap.home_netmanager, R.mipmap.home_trojan,
                R.mipmap.home_sysoptimize, R.mipmap.home_tools,
                R.mipmap.home_settings
        };
        gv_home.setAdapter(new MyAdapter());

        //添加点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        //跳转到高级工具的列表界面
                        startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
                        break;
                    case 7:
                        //跳转到高级工具的列表界面
                        startActivity(new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetPsdDialog();//第一次进入手机防盗设置密码
        } else {
            showConfirmPsdDialog();//再次进入输入密码
        }
    }

    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //把一个xml文件返回为一个view对象
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
//        dialog.setView(view);
        //去掉左上右下的四个内边距,适合低版本的android
        dialog.setView(view, 0,0,0,0);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(confirmPsd)) {

                    //将之前存储的密码拿出来
                    String psd = SpUtils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD,"");
                    if (psd.equals(Md5Util.encoder(confirmPsd))) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
//        dialog.setView(view);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        SpUtils.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(confirmPsd));
                    } else {
                        ToastUtil.show(getApplicationContext(),"确认密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView tv_icon = (ImageView) view.findViewById(R.id.tv_icon);
            tv_title.setText(mTitleStr[position]);
            tv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}

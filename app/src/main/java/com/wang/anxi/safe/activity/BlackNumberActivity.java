package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.List;

import db.dao.BlackNumberDao;
import db.domain.BlackNumberInfo;
import utils.ToastUtil;

/**
 * Created by anxi on 16-12-6.
 */
public class BlackNumberActivity extends Activity{

    private Button bt_add;
    private ListView lv_black_number;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private int mode = 1;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                lv_black_number.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }

        }
    };
    private MyAdapter mAdapter;
    //防止重复加载
    private boolean isLoad = false;
    private int count;

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            //优化一：复用convertView来对List进行优化
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);

                holder = new ViewHolder();
                //优化二：findViewById的次数太多可以进行优化，使用ViewHolder
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDao.delete(mBlackNumberList.get(position).phone);
                    mBlackNumberList.remove(position);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            holder.tv_phone.setText(mBlackNumberList.get(position).phone);
            mode= Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode) {
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
            }
            //返回值一定要写，不然会空指针异常
            return convertView;
        }
    }

    //优化三：定义为静态不会创建多个对象
    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        
        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mDao = BlackNumberDao.getInstance(getApplication());
//                mBlackNumberList = mDao.findAll();
                //优化四：每一次加载20条数据，当拖到底部的时候再加载后面的20条
                mBlackNumberList = mDao.find(0);
                count = mDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add = (Button) findViewById(R.id.bt_add);
        lv_black_number = (ListView) findViewById(R.id.lv_black_number);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        //对展示的LIstView的滚定状态进行监听，当状态为空闲且最后一条显示的时候加载后20条
        lv_black_number.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumberList != null) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_black_number.getLastVisiblePosition() >= mBlackNumberList.size() - 1
                            && !isLoad) {
                        if (count > mBlackNumberList.size()) {
                            new Thread() {
                                @Override
                                public void run() {
                                    mDao = BlackNumberDao.getInstance(getApplication());
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                                    mBlackNumberList.addAll(moreData);
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_blackPhone = (EditText) view.findViewById(R.id.et_blackphone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //记录拦截类型
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_blackPhone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    mDao.insert(phone, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    mBlackNumberList.add(0, blackNumberInfo);
                    if (mAdapter!= null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

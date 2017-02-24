package com.wang.anxi.safe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wang.anxi.safe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {
    protected static final String tag = "ContactListActivity";
    private ListView lv_contact;
    private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    private MyAdapter mAdapter;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //8,填充数据适配器

            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initUI();
        initData();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);

            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

//            tv_name.setText(getItem(position).get("name"));
//            tv_phone.setText(getItem(position).get("phone"));

            //这里使用的名称必须和插入Hashmap时候的名称相同，不然会读取失败
            tv_name.setText(getItem(position).get("peopleName"));
            tv_phone.setText(getItem(position).get("phoneNum"));

            return view;
        }

    }

    /**
     * 获取系统联系人数据方法
     */
    private void initData() {
        //因为读取系统联系人,可能是一个耗时操作,放置到子线程中处理
        new Thread() {
            public void run() {
                //1,获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //2,做查询系统联系人数据库表过程(读取联系人权限)
                Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);
//                Cursor cursor = contentResolver.query(
//                        Uri.parse("content://com.android.contacts/raw_contacts"),
//                        new String[]{"contact_id"},
//                        null, null, null);
                contactList.clear();
                //3,循环游标,直到没有数据为止
                while (cursor.moveToNext()) {
//                    String id = cursor.getString(0);
////					Log.i(tag, "id = "+id);
//                    //4,根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
//                    Cursor indexCursor = contentResolver.query(
//                            Uri.parse("content://com.android.contacts/data"),
//                            new String[]{"data1","mimetype"},
//                            "raw_contact_id = ?", new String[]{id}, null);
                    //5,循环获取每一个联系人的电话号码以及姓名,数据类型
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    //与视频中使用的方法不同，但是可以实现显示的效果
                    int indexPeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME); // people name
                    int indexPhoneNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);   // phone number
                    String strPeopleName = cursor.getString(indexPeopleName);
                    String strPhoneNum = cursor.getString(indexPhoneNum);
                    if (!TextUtils.isEmpty(strPeopleName)) {
                        hashMap.put("peopleName", strPeopleName);
                    }
                    if (!TextUtils.isEmpty(strPhoneNum)) {
                        hashMap.put("phoneNum", strPhoneNum);
                    }
//                    while(indexCursor.moveToNext()){
//                        String data = indexCursor.getString(0);
//                        String type = indexCursor.getString(1);
//
//                        //6,区分类型去给hashMap填充数据
//                        if(type.equals("vnd.android.cursor.item/phone_v2")){
//                            //数据非空判断
//                            if(!TextUtils.isEmpty(data)){
//                                hashMap.put("phone", data);
//                            }
//                        }else if(type.equals("vnd.android.cursor.item/name")){
//                            if(!TextUtils.isEmpty(data)){
//                                hashMap.put("name", data);
//                            }
//                        }
//                    }
//                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                //7,消息机制,发送一个空的消息,告知主线程可以去使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            }

            ;
        }.start();

    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1,获取点中条目的索引指向集合中的对象
                if (mAdapter != null) {
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    //2,获取当前条目指向集合对应的电话号码
                    String phone = hashMap.get("phoneNum");
                    //3,此电话号码需要给第三个导航界面使用

                    //4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phoneNum", phone);
                    setResult(0, intent);

                    finish();
                }
            }
        });
    }
}
package com.wang.anxi.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.wang.anxi.safe.R;

import java.util.List;

import engine.CommonnumDao;

/**
 * Created by anxi on 2017/2/18.
 */
public class CommonNumberQueryActivity extends Activity{

    private ExpandableListView elv_common_number;
    private List<CommonnumDao.Group> mGroup;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    private void initData() {
        CommonnumDao commonnumDao = new CommonnumDao();
        mGroup = commonnumDao.getGroup();

        mAdapter = new MyAdapter();

        elv_common_number.setAdapter(mAdapter);

        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                startCall(mAdapter.getChild(groupPosition, childPosition).number);
                return false;
            }
        });


    }

    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);

    }

    class MyAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childList.size();
        }

        @Override
        public CommonnumDao.Group getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public CommonnumDao.Child getChild(int groupPosition, int childPosition) {
            return mGroup.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("  " + getGroup(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
            View childView = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_child_name = (TextView) childView.findViewById(R.id.tv_child_name);
            TextView tv_child_number = (TextView) childView.findViewById(R.id.tv_child_number);
            tv_child_name.setText(getChild(groupPosition, childPosition).name);
            tv_child_number.setText(getChild(groupPosition, childPosition).number);
            return childView;
        }

        //孩子节点是否响应点击事件
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

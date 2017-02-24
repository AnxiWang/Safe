package view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.anxi.safe.R;

/**
 * Created by anxi on 16-10-16.
 */

public class SettingItemView extends RelativeLayout {

    private CheckBox cb_box;
    private TextView tv_des;
    private static final String tag ="SettingItemView";
    private static final String NAMESPACE ="http://schemas.android.com/apk/res/com.wang.anxi.safe";

    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //最后的一个this是判断是否挂载在父控件上
        View.inflate(context, R.layout.setting_item_view,this);
        //下面两句话的意思和上面的一句话意思是相同的
//        View view = View.inflate(context, R.layout.setting_item_view, null);
//        this.addView(view);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

        //获取自定义以及原生的属性操作都AttributeSet attrs对象中获取
        initAttr(attrs);

        tv_title.setText(mDestitle);
    }

    /*
    * attrs 构造方法中维护好的属性集合
    * 返回属性集合中的自定义属性属性值
    * */
    private void initAttr(AttributeSet attrs) {
//        Log.i(tag, "Total number=" + attrs.getAttributeCount());
//        for (int i=0; i<attrs.getAttributeCount();i++) {
//            Log.i(tag,"name=" + attrs.getAttributeName(i));
//            Log.i(tag, "value=" + attrs.getAttributeValue(i));
//        }
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    /*
    * 返回当前的checkbox是否被选中,判断是否开启
    * */
    public boolean isCheck() {
        return cb_box.isChecked();
    }
    /*
    * 切换选中状态
    * */
    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if (isCheck) {
            tv_des.setText(mDeson);
        } else {
            tv_des.setText(mDesoff);
        }
    }


}

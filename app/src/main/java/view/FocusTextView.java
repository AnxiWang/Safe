package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by anxi on 16-10-14.
 * 设置TextView自动获取焦点
 */

public class FocusTextView extends TextView {
    public FocusTextView(Context context) {
        super(context);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}

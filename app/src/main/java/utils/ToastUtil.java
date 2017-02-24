package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by anxi on 16-10-12.
 */

public class ToastUtil {
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx, msg, 0).show();
    }
}

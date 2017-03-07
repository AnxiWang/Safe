package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import engine.ProcessInfoProvider;

/**
 * Created by anxi on 2017/3/6.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killAll(context);
    }
}

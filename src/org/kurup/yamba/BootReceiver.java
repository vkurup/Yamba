package org.kurup.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
        public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UpdaterService.class));
        Log.d("BootReceiver", "onRecieved");
    }
}

package com.commonsware.cwac.locpoll.Demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HeartBeatReceiver extends BroadcastReceiver {
    String LOGCAT="logueo";
    public HeartBeatReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.i(LOGCAT,"en broadcastreceiver alarm manager heartbeat");
        LocationPollerDemo.getInstance().sendHeartBeat();
    }
}

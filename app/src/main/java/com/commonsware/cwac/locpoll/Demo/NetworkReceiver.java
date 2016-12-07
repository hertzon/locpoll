package com.commonsware.cwac.locpoll.Demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
    public NetworkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        String LOGTAG="logueo";

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.i(LOGTAG,"Cambio estado conexion red!!!!!");
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                Log.d(LOGTAG, "Internet YAY");
                context.sendBroadcast(new Intent("INTERNET_RECOVER"));

            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                Log.d(LOGTAG, "No internet :(");
                context.sendBroadcast(new Intent("INTERNET_LOST"));
            }
        }
    }

}

package com.commonsware.cwac.locpoll.Demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.commonsware.cwac.locpoll.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

public class LocationPollerDemo extends AppCompatActivity {
    //private static final int PERIOD=1800000; 	// 30 minutes
    private static final int PERIOD=90000; 	// 1minuto y medio
    private PendingIntent pi=null;
    private AlarmManager mgr=null;
    String LOGTAG="logueo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_poller_demo);
        mgr=(AlarmManager)getSystemService(ALARM_SERVICE);
        Log.d(LOGTAG,"Starting....");
        Intent i=new Intent(this, LocationPoller.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(new Intent(this, LocationReceiver.class));
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        parameter.setTimeout(60000);
        i.putExtras(bundle);


        pi=PendingIntent.getBroadcast(this, 0, i, 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                PERIOD,
                pi);
//        pi=PendingIntent.getBroadcast(this, 0, i, 0);
//        mgr.setRepeating(AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis(),
//                PERIOD,
//                pi);

        Toast
                .makeText(this,
                        "Location polling every 30 minutes begun",
                        Toast.LENGTH_LONG)
                .show();
    }
    public void omgPleaseStop(View v) {
        mgr.cancel(pi);
        finish();
    }
}

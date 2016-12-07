package com.commonsware.cwac.locpoll.Demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.telephony.TelephonyManager;
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
    public TCPClient mTcpClient;
    public boolean isConected=false;
    private static LocationPollerDemo instance;

    public static Context contextOfApplication;

    public boolean isLOAD=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        contextOfApplication = getApplicationContext();
        setContentView(R.layout.activity_location_poller_demo);
        mgr=(AlarmManager)getSystemService(ALARM_SERVICE);
        Log.d(LOGTAG,"Starting....");

        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_LOST"));
        registerReceiver(broadcastReceiverRecover, new IntentFilter("INTERNET_RECOVER"));




        Intent i=new Intent(this, LocationPoller.class);

        SharedPreferences sharedPreferences=getSharedPreferences("AppPreferences",LocationPollerDemo.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isConected",false);
        editor.commit();



        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        //Log.d(LOGTAG,"IMEI: "+imei);
        // connect to the server
        if (isNetwork()){
            //Log.d(LOGTAG,"Conectando a Server");
            //new connectTask().execute("");
        }

        if (mTcpClient != null) {
            //mTcpClient.sendMessage("##"+"imei:"+imei+','+"A;");
        }
        try {
            Thread.sleep(500);
            //mTcpClient.sendMessage("##"+"imei:"+imei+','+"A;");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(new Intent(this, LocationReceiver.class));
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        parameter.setTimeout(60000);
        i.putExtras(bundle);
        //i.putExtra("imei",device_id);



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
    public static LocationPollerDemo getInstance() {
        return instance;
    }
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
    public void omgPleaseStop(View v) {
        mgr.cancel(pi);
        finish();
    }

    public  boolean isNetwork(){
        //Determinamos si hay conectividad de red....
        boolean isConnected=false;
        Context applicationContext=LocationPollerDemo.getContextOfApplication();
        ConnectivityManager cm =
                (ConnectivityManager)applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null){
            isConnected = activeNetwork.isConnected();
        }else {
            isConnected=false;
        }
        return isConnected;
    }
    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(LOGTAG,"Rx en LocationPollerDemo: "+values[0]);
            if (values[0].equals("LOAD")){
                Log.i(LOGTAG,"Llego LOAD");
                isLOAD=true;
            }

//            if (values[0].equals("LOAD")){
//                Context applicationContext=LocationPollerDemo.getContextOfApplication();
//                SharedPreferences sharedPreferences=applicationContext.getSharedPreferences("AppPreferences",LocationPollerDemo.MODE_PRIVATE);
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.putBoolean("isConected",true);
//                editor.commit();
//
//                Log.i(LOGTAG,"Llego load!!!, parando cliente");
//                isLoad=true;
//                //mTcpClient.stopClient();
//            }
        }

        @Override
        protected void onPostExecute(TCPClient tcpClient) {
            super.onPostExecute(tcpClient);
//            if (isNetwork()){
//                Log.d(LOGTAG,"ReConectando a Server");
//                isLOAD=false;
//                //new connectTask().execute("");
//            }
        }
    }
    public void reconectar(){
        Log.d(LOGTAG,"ReConectando a Server...");
        isLOAD=false;
        new connectTask().execute("");
    }
    public void  desconectar(){
        Log.d(LOGTAG,"Desconectando por falta de red!!!");
        mTcpClient.stopClient();
        mTcpClient.out.flush();
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
        }
    };
    BroadcastReceiver broadcastReceiverRecover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
            try {
                Thread.sleep(1000);
                reconectar();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };
}

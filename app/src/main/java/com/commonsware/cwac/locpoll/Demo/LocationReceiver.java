package com.commonsware.cwac.locpoll.Demo;

/**
 * Created by Nelson Rodriguez on 01/12/2016.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import com.commonsware.cwac.locpoll.LocationPollerResult;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class LocationReceiver extends BroadcastReceiver {
    String LOGTAG="logueo";

    @Override
    public void onReceive(Context context, Intent intent) {
        File log=
                new File(Environment.getExternalStorageDirectory(),
                        "LocationLog.txt");

        try {
            BufferedWriter out=
                    new BufferedWriter(new FileWriter(log.getAbsolutePath(),
                            log.exists()));

            out.write(new Date().toString());
            out.write(" : ");

            Bundle b=intent.getExtras();

            LocationPollerResult locationResult = new LocationPollerResult(b);

            Location loc=locationResult.getLocation();
            String msg;
            int nsatelites=0;
            if (loc==null) {
                loc=locationResult.getLastKnownLocation();
                nsatelites=99;
                if (loc==null) {
                    msg=locationResult.getError();
                }
                else {

                    msg="TIMEOUT, lastKnown="+loc.toString();
                }
            }
            else {
                msg=loc.toString();
                nsatelites=loc.getExtras().getInt("satellites");
            }

            if (msg==null) {
                msg="Invalid broadcast received!";
            }
            //Save in db
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            double latitud=loc.getLatitude();
            double longitud=loc.getLongitude();
            double altitud=loc.getAltitude();
            double velocidad=loc.getSpeed();
            double gpsTime=loc.getTime();
            double bearing =loc.getBearing();
            String provider=loc.getProvider();


            Log.d(LOGTAG,"Timestamp: "+timestamp+" Latitud: "+latitud+" Longitud: "+longitud+" Altitud: "+altitud+" GpsTime: "+gpsTime+" Velocidad: "+velocidad+" Bearing: "+bearing+" Nsatelites: "+nsatelites+" Provider: "+provider);
            String output=timestamp+","+latitud+","+longitud+","+altitud+","+gpsTime+","+velocidad+","+bearing+","+nsatelites+","+provider;
            Log.d(LOGTAG,"msg: "+msg+" nsatelites: "+nsatelites);
            out.write(output);
            out.write("\n");
            out.close();
        }
        catch (IOException e) {
            Log.e(getClass().getName(), "Exception appending to log file", e);
        }
    }
}

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
import java.util.TimeZone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import com.commonsware.cwac.locpoll.LocationPollerResult;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class LocationReceiver extends BroadcastReceiver {
    String LOGTAG="logueo";
    static int velocidad=0;
    private TCPClient mTcpClient;
    private TCPClient mtcp;

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
            Context applicationContext=LocationPollerDemo.getContextOfApplication();
            SharedPreferences preferences = applicationContext.getSharedPreferences("AppPreferences", LocationPollerDemo.MODE_PRIVATE);
            Boolean isConected=preferences.getBoolean("isConected",false);

            if (isConected){
                Log.i(LOGTAG,"Status:Conectado...");
            }else {
                Log.i(LOGTAG,"Status:No Conectado...");
            }



            //Save in db
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            double latitud=loc.getLatitude();
            double longitud=loc.getLongitude();
            double altitud=loc.getAltitude();
            velocidad=(int)(loc.getSpeed()/1000)*3600;

            TelephonyManager tManager = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            String imei = tManager.getDeviceId();
            Log.i(LOGTAG,"imeiOnLocationReceiver: "+imei);

            String coordenadas=locacion_formateada(latitud,longitud);
            Log.i(LOGTAG,"Locacion Formateada: "+coordenadas);

            Date d=new Date(System.currentTimeMillis());
            String  s= (String) DateFormat.format("dd/MM/yyyy HH:mm:ss", d.getTime());
            s=GetUTCdatetimeAsString();
            String ano=s.substring(0 ,4);
            String dia=s.substring(8,10);
            String mes=s.substring(5, 7);
            String hora=s.substring(11, 13);
            String minuto=s.substring(14, 16);
            String segundo=s.substring(17, 19);
            String trama_pos="imei:"+imei+','+"tracker"+','+ano+mes+dia+hora+minuto+','+','+'F'+','+hora+minuto+segundo+".000"+','+'A'+','+coordenadas+','+velocidad+','+"0"+';';
            Log.i(LOGTAG,"Trama pos: "+trama_pos);

            if (isConected) {

                //new connectTask().execute("");
                if (LocationPollerDemo.getInstance().mTcpClient != null) {
                    Log.i(LOGTAG,"Sendinga data on LocationReceiver");
                    LocationPollerDemo.getInstance().mTcpClient.sendMessage(trama_pos);

                }else {
                    Log.i(LOGTAG,"mtcp null");
                }

            }






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
    public static String GetUTCdatetimeAsString()
    {
        String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }
    public static String locacion_formateada(double latitude, double longitude) {

        try {
            //float latSeconds = (float) Math.round(latitude * 3600);
            float latSeconds = (float) latitude * 3600;
            int latDegrees = (int) (latSeconds / 3600);
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = (int) (latSeconds / 60);
            latSeconds %= 60;
            float longSeconds = (float) longitude * 3600;
            int longDegrees = (int) (longSeconds / 3600);
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = (int) (longSeconds / 60);
            longSeconds %= 60;

            velocidad=(int) (velocidad/1.852);
            String latDegree = latDegrees >= 0 ? "N" : "S";
            //String lonDegrees = latDegrees >= 0 ? "E" : "W";
            String longDegree = longDegrees >= 0 ? "E" : "W";
            float lati = Math.abs(latDegrees) * 100 + latMinutes + latSeconds / 60;
            float longi=Math.abs(longDegrees)*100+longMinutes+longSeconds/60;
            String latiString=String.format("%02.4f", lati);
            String longiString=String.format("%02.4f", longi);
            if (Math.abs(latDegrees)<10){
                latiString="0"+latiString;
            }
            if (Math.abs(longDegrees)<100){
                longiString="0"+longiString;
            }
            latiString=latiString.replace(',','.')+','+latDegree;
            longiString=longiString.replace(',','.')+','+longDegree;

            return latiString+','+longiString;
        } catch (Exception e) {
            Log.i("Debug","Error....");

            return ""+ String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude) ;
        }
    }
    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mtcp = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mtcp.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(LOGTAG,"Rx en LocationReceiver: "+values[0]);

            if (values[0].equals("LOAD")){
                //SharedPreferences sharedPreferences=getSharedPreferences("AppPreferences",LocationPollerDemo.MODE_PRIVATE);
                //SharedPreferences.Editor editor=sharedPreferences.edit();
                //editor.putBoolean("isConected",true);
                //editor.commit();
                //mTcpClient.stopClient();
            }
        }
    }
}

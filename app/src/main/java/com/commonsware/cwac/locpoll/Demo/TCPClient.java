package com.commonsware.cwac.locpoll.Demo;

/**
 * Created by Nelson Rodriguez on 05/12/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    private String serverMessage;
    //public static final String SERVERIP = "104.236.203.72"; //your computer IP address
    public static final String SERVERIP = "107.170.62.116"; //your computer IP address
    public static final int SERVERPORT = 31272;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    String LOGCAT="logueo";
    public boolean isConected=false;
    public boolean error=false;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }
    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.d(LOGCAT, "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);


            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.d(LOGCAT, "C: Sent.");

                Log.d(LOGCAT, "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                out.flush();
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }
//                Context applicationContext=LocationPollerDemo.getContextOfApplication();
//                SharedPreferences preferences = applicationContext.getSharedPreferences("AppPreferences", LocationPollerDemo.MODE_PRIVATE);
//                SharedPreferences.Editor editor=preferences.edit();
//
//
//
//                if (serverMessage.equals("LOAD")){
//                    Log.d(LOGCAT,"Conectado...");
//                    editor.putBoolean("isConected",true);
//
//                    editor.commit();
//                    isConected=true;
//                }

                Log.d(LOGCAT, "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.d(LOGCAT, "S: Error", e);
                error=true;


            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.d(LOGCAT, "Cerrando socket");
                socket.close();
            }

        } catch (Exception e) {

            Log.d(LOGCAT, "C: Error", e);

        }


    }


    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}

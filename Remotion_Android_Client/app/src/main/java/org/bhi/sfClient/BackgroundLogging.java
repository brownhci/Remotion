package org.bhi.sfClient;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.bhi.sfClient.orientationProvider.CalibratedGyroscopeProvider;
import org.bhi.sfClient.representation.Quaternion;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iagarwa1 on Jun/21/17.
 * Modified by Jing Qian
 * Remotion Background logging
 */

public class BackgroundLogging extends IntentService {


    private static final String TAG = "Background Logging";
    protected final Quaternion quaternion = new Quaternion();
    private  Quaternion pquaternion = new Quaternion();
    public long timestamp = 0;
    /* preset logging time in milli seconds*/
    public final long timeDuration = 3000*1000;
    public long startTime;
    public long unixTimestamp = 0;
    private WebSocketClient mWebSocketClient;
    private int websocketCount = 0;
    private boolean websocketConnected = false;

    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BackgroundLogging() {
        super("Background Logging");
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    private void connectWebSocket(String ip) {
        URI uri;
        try {
            uri = new URI("ws://"+ip+":9999");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                websocketConnected = true;
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }
            @Override
            public void onMessage(String s) {

            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        connectWebSocket(workIntent.getStringExtra("ip"));
        SensorManager mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        CalibratedGyroscopeProvider or = new CalibratedGyroscopeProvider(mSensorManager);
        or.start();
        try {
            File outputDir = this.getCacheDir(); // context being the Activity pointer
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);

            //mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/"+fDate+".mp4");
           // String filename = "Remotion_sensor_data_"+fDate;
            String filename = "Remotion_sensor_data___";
            File outputFile = File.createTempFile(filename, ".csv", Environment.getExternalStorageDirectory());
            outputFile.createNewFile();
            OutputStream os = new FileOutputStream(outputFile);

            Log.i("output filepath", outputFile.getAbsolutePath());

            startTime = System.currentTimeMillis();

            String quatString = "";
            float timediff = System.currentTimeMillis() - startTime;

            while (timediff < timeDuration) {
                timediff = System.currentTimeMillis() - startTime;
                if (timestamp < or.timestamp) {
                    or.getQuaternion(quaternion);
                    unixTimestamp = System.currentTimeMillis();
                    //quatString = unixTimestamp + "," + or.timestamp+",Quaternion,"+quaternion.toString()+"\n";
                    quatString = quaternion.toString()+"\n";
                    Long etalong = System.currentTimeMillis()- startTime;
                    String eta =  " eta: "+etalong;
                    timestamp = or.timestamp;
                    os.write(quatString.getBytes());
                }

                if(websocketCount % 5000 == 0 && websocketConnected){
                        mWebSocketClient.send(quaternion.toString()  + or.rawData);
                        websocketCount = 0;
                }
                pquaternion = quaternion;
                websocketCount ++;
            }
            os.close();
            Log.d("loggin-stopped","message logging stopped. Duration is " + (timeDuration / 1000) + " seconds.");
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
    }
}
package org.bhi.sfClient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity, that displays a single WebView with the text shown under the section About in the settings
 * 
 * @author  Alexander Pacha
 * modified by iagarwa1
 * Remotion Android client
 * 
 */
public class AboutActivity extends Activity {
    private static final int REQUEST_CODE = 5110;
    private static final int VIDEO_WIDTH = 720;
    private static final int VIDEO_HEIGHT = 1280;
    private int mScreenDensity;
    MenuItem item;
    private static final String DEFAULT_REMOTION_SERVER_IP = "10.38.15.9";
    private static final String TAG = "Log Button ";
    private static Intent mServiceIntent;
    EditText mEdit;
    Button btn_action;

    /* screen recording parameters*/
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private PCallBack callBack;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;

    /* orientation array */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int REQUEST_PERMISSION_KEY = 1;
    boolean isRecording = false;

    /*Checks if ip address string is valid"*/
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public void startIntent(){
       String ip = mEdit.getText().toString();
        if(!validIP(ip)) {
            ip= DEFAULT_REMOTION_SERVER_IP;
        }
        Log.i("Text Box: ",ip);

        mServiceIntent = new Intent(this, BackgroundLogging.class);
        mServiceIntent.putExtra("ip",ip);
        this.startService(mServiceIntent);

    }

    public void stopIntent(){
        if(mServiceIntent != null){
            this.stopService(mServiceIntent); //DOESN'T WORK
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        mEdit   = (EditText)findViewById(R.id.editText);

        /* get permissions */
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

         /* deal with screen recording session */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

         /* creating media recorder */
        mediaRecorder = new MediaRecorder();
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        /* binding button to screen recording function */
        btn_action = (Button) findViewById(R.id.btn_action);
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleScreenShare();
            }
        });

        /* deal with sensor logging */
        Log.i(TAG, "ToggleButton "+toggle.isChecked());
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
                    Log.i(TAG, "ToggleButton "+toggle.isChecked());
                    startIntent();
                } else {
                    Log.i(TAG, "ToggleButton "+toggle.isChecked());
                    stopIntent();
                }
            }
        });
            String localPrefix = Locale.getDefault().getLanguage().substring(0, 2).toLowerCase(Locale.US);
            WebView webView = (WebView) findViewById(R.id.webViewAbout);
            webView.loadUrl("file:///android_asset/about/" + localPrefix + "/index.html");
    }

    public void onToggleScreenShare() {
        if (!isRecording) {
            initRecorder();
            shareScreen();
        } else {
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopScreenSharing();
        }
    }
    private void shareScreen() {
        if (mediaProjection == null) {
            startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
        isRecording = true;
        toggleRecordButton();
    }

    private void stopScreenSharing() {
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
        isRecording = false;
        toggleRecordButton();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("MainActivity", VIDEO_WIDTH, VIDEO_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
            mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/"+fDate+".mp4");
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoEncodingBitRate(3000000);
            mediaRecorder.setOrientationHint(ORIENTATIONS.get(getWindowManager().getDefaultDisplay().getRotation() + 90));
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyMediaProjection() {
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(callBack);
            mediaProjection.stop();
            mediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    public void toggleRecordButton() {
        if (isRecording) {
            btn_action.setText("Stop Recording");
        } else {
            btn_action.setText("Start Recording");
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            isRecording = false;
            toggleRecordButton();
            return;
        }
        callBack = new PCallBack();
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        mediaProjection.registerCallback(callBack, null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
        isRecording = true;
        toggleRecordButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY:
            {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    onToggleScreenShare();
                } else {
                    isRecording = false;
                    toggleRecordButton();
                }
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();
    }

    private class PCallBack extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (isRecording) {
                isRecording = false;
                toggleRecordButton();
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopScreenSharing();
        }
    }

    protected boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

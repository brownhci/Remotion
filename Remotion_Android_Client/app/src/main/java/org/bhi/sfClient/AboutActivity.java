package org.bhi.sfClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.hitlabnz.sensor_fusion_demo.R;

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
    MenuItem item;
    private static final String DEFAULT_REMOTION_SERVER_IP = "10.38.15.9";
    private static final String TAG = "Log Button ";
    private static Intent mServiceIntent;
    EditText mEdit;

/*    public void helloworld() {
        Log.i(TAG, "HelloWorld");
    }
*/
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
        this.stopService(mServiceIntent); //DOESN'T WORK
        /* Need to fix this part, stop an intent once it
        goes into the background
         */
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sensor_selection);
//
//        item = (MenuItem) findViewById(R.id.action_about);
//
//        Log.i(TAG, " "+item.isChecked());
        setContentView(R.layout.activity_about);
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        mEdit   = (EditText)findViewById(R.id.editText);


        Log.i(TAG, "ToggleButton "+toggle.isChecked());
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              //  helloworld();
//
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


}

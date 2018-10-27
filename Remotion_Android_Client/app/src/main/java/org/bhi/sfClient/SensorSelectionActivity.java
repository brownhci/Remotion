package org.bhi.sfClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity where the user can select which sensor-fusion he wants to try out
 * 
 * @author Alexander Pacha
 * 
 */
public class SensorSelectionActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative,
     * which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_selection);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Check if device has a hardware gyroscope
        SensorChecker checker = new HardwareChecker((SensorManager) getSystemService(SENSOR_SERVICE));
        if(!checker.IsGyroscopeAvailable()) {
        	// If a gyroscope is unavailable, display a warning.
        	displayHardwareMissingWarning();
        }
    }

    private void displayHardwareMissingWarning() {
    	AlertDialog ad = new AlertDialog.Builder(this).create();  
    	ad.setCancelable(false); // This blocks the 'BACK' button    
    	ad.setTitle(getResources().getString(R.string.gyroscope_missing)); 
    	ad.setMessage(getResources().getString(R.string.gyroscope_missing_message));
    	ad.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {  
    	    @Override  
    	    public void onClick(DialogInterface dialog, int which) {  
    	        dialog.dismiss();                      
    	    }  
    	});  
    	ad.show();  
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sensor_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_about:
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Initialises a new sectionPagerAdapter
         * 
         * @param fm the fragment Manager
         */
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new OrientationVisualisationFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

}

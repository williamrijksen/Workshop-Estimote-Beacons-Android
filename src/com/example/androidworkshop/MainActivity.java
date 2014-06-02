package com.example.androidworkshop;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity<LocalBinder> extends Activity {

	private static final String TAG = "ESPWNED";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
			null, null, null);
	private BeaconManager beaconManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        AndroidWorkshopApplication app = (AndroidWorkshopApplication)getApplication();
        beaconManager = app.getBeaconManager();
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {

			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
				if (beacons.size() != 0) {
					TextView powerView = (TextView) findViewById(R.id.power);
					powerView.setText("" + beacons.get(0).getMeasuredPower());
					TextView distanceview = (TextView) findViewById(R.id.distance);
					distanceview.setText(Utils.computeProximity(beacons.get(0)).name());
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					Log.d(TAG, "startRanging");
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
				} catch (RemoteException e) {
					Log.e(TAG, "Cannot start ranging", e);
				}
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
			beaconManager.disconnect();
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot stop but it does not matter now", e);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

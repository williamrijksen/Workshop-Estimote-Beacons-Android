package com.example.androidworkshop;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BackgroundService extends IntentService {
	private static final String TAG = "ESPWNED";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
			null, null, null);
	private BeaconManager beaconManager;
	private String lastText = "";
	
	public BackgroundService() {
	    super("BackgroundService");
	}

	 public int onStartCommand(Intent intent, int flags, int startId) 
	 {
	     return super.onStartCommand(intent,flags,startId);
	 }

	@Override
	protected void onHandleIntent(Intent intent) {
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
        AndroidWorkshopApplication app = (AndroidWorkshopApplication)getApplication();
        beaconManager = app.getBeaconManager();
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
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				BackgroundService.this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Search beacon...")
				.setContentText("Search beacon...");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {

			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
		    	if (beacons.size() != 0) {
					String contentText = "Power "
							+ beacons.get(0).getMeasuredPower()
							+ " distance "
							+ Utils.computeProximity(
									beacons.get(0)).name();
					if (!lastText.equals(contentText)) {
						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								BackgroundService.this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle("Beacon found!")
								.setContentText(contentText);
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(1, mBuilder.build());
						lastText = contentText;
					}
				} else {
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.cancel(1);
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot stop but it does not matter now", e);
		}
	}
}

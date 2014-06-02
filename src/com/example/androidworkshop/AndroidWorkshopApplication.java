package com.example.androidworkshop;

import com.estimote.sdk.BeaconManager;

import android.app.Application;

public class AndroidWorkshopApplication extends Application {

	private BeaconManager beaconManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		setBeaconManager(new BeaconManager(this));
	}

	public BeaconManager getBeaconManager() {
		if (beaconManager == null) {
        	beaconManager = new BeaconManager(this);
		}
		return beaconManager;
	}

	public void setBeaconManager(BeaconManager beaconManager) {
		this.beaconManager = beaconManager;
	}
	
}
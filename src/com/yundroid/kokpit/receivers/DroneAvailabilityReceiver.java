package com.yundroid.kokpit.receivers;

import com.yundroid.kokpit.intents.DroneAvailabilityManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DroneAvailabilityReceiver extends BroadcastReceiver{

	private DroneAvailabilityListener listener;
	
	public DroneAvailabilityReceiver(DroneAvailabilityListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if( listener != null ){
			boolean available = intent.getBooleanExtra(DroneAvailabilityManager.DRONE_AVAILABLE_ON_NETWORK, false);
			listener.onDroneAvailabilityChanged(available);
		} else{
			Log.w("DroneAvailabiltyReceiver", "Interface was not set");
		}
	}

	
}

package com.yundroid.kokpit.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkChangedReceiver extends BroadcastReceiver{

	private NetworkChangedListener i;
	
	public NetworkChangedReceiver(NetworkChangedListener i){
		this.i = i;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(i != null){
			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			i.onNetworkChanged(info);
		}
	}

}

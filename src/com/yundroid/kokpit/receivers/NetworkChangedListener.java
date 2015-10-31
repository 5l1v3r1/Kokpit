package com.yundroid.kokpit.receivers;

import android.net.NetworkInfo;

public interface NetworkChangedListener {
	public void onNetworkChanged(NetworkInfo info);
}

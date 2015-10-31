package com.yundroid.kokpit.navdata.listeners;

import com.yundroid.kokpit.navdata.DroneState;

public interface StateListener {
	public void onStateChanged(DroneState state);
}

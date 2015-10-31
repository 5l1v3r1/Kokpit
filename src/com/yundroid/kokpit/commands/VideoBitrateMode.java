package com.yundroid.kokpit.commands;

public enum VideoBitrateMode {
	VBC_MODE_DISABLED,		//Bitrate set to video:max_bitrate
	VBC_MODE_DYNAMIC,		//Video bitrate varies in [250:video:max_bitrate] kbps
	VBC_MANUAL;				//Video stream bitrate is fixed by the video:bitrate_key
}

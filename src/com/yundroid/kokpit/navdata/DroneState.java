package com.yundroid.kokpit.navdata;

public class DroneState {
	//SDK line 218 / config.h
	private final int state;
	
	public DroneState(int state){
		this.state = state;
	}
	
	public boolean isFlying(){
		return (state & (1 << 0)) != 0;
	}
	
	public boolean isVideoEnabled(){
		return (state & (1 << 1)) != 0;
	}
	
	public boolean isVisionEnabled(){
		return (state & (1 << 2)) != 0;
	}
	
	public boolean controlAlgo(){
		return (state & (1 << 3)) != 0;
	}
	
	public boolean isAltitudeControlActive(){
		return (state & (1 << 4)) != 0;
	}
	
	public boolean isUserFeedbackOn(){
		return (state & (1 << 5)) != 0;
	}
	
	public boolean isControlAckReceived(){
		return (state & (1 << 6)) != 0;
	}
	
	public boolean isCameraEnabled(){
		return (state & (1 << 7)) != 0;
	}
	
	public boolean isTravellingEnabled(){
		return (state & (1 << 8)) != 0;
	}
	
	public boolean isUsbKeyReady(){
		return (state & (1 << 9)) != 0;
	}
	
	public boolean isNavDataDemo(){
		return (state & (1 << 10)) != 0;
	}
	
	public boolean isNavDataBootstrapped(){
		return (state & (1 << 11)) != 0;
	}
	
	public boolean isMotorsDown(){
		return (state & (1 << 12)) != 0;
	}
	
	public boolean isComLost(){
		return (state & (1 << 13)) != 0;
	}
	
	//TODO find out what mask 14 does
	
	public boolean isBatteryLow(){
		return (state & (1 << 15)) != 0;
	}
	
	public boolean isUserEmergencyLanding(){
		return (state & (1 << 16)) != 0;
	}
	
	public boolean isTimerElapsed(){
		return (state & (1 << 17)) != 0;
	}
	
	public boolean isMagnetCalibNeeded(){
		return (state & (1 << 18)) != 0;
	}
	
	public boolean isAngelsOutOfRange(){
		return (state & (1 << 19)) != 0;
	}
	
	public boolean isTooMuchWind(){
		return (state & (1 << 20)) != 0;
	}
	
	public boolean isUltrasonicDeaf(){
		return (state & (1 << 21)) != 0;
	}
	
	public boolean isCutoutSystemDetected(){
		return (state & (1 << 22)) != 0;
	}
	
	public boolean isPICVNumberOk(){
		return (state & (1 << 23)) != 0;
	}
	
	public boolean isATCThreadOn(){
		return (state & (1 << 24)) != 0;
	}
	
	public boolean isNavDataThreadOn(){
		return (state & (1 << 25)) != 0;
	}
	
	public boolean isVideoThreadOn(){
		return (state & (1 << 26)) != 0;
	}
	
	public boolean isAcquisitionThreadOn(){
		return (state & (1 << 27)) != 0;
	}
	
	public boolean isCOMWDGDelayed(){
		return (state & (1 << 28)) != 0;
	}
	
	public boolean isADCWDGDelayed(){
		return (state & (1 << 29)) != 0;
	}
	
	public boolean isComProblemOccured(){
		return (state & (1 << 30)) != 0;
	}
	
	public boolean isEmergency(){
		return (state & (1 << 31)) != 0;
	}
}

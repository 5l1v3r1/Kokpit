package com.yundroid.kokpit;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.yundroid.kokpit.commands.Animation;
import com.yundroid.kokpit.commands.CommandSender;
import com.yundroid.kokpit.commands.ControlLevel;
import com.yundroid.kokpit.commands.DetectionTag;
import com.yundroid.kokpit.commands.DetectionType;
import com.yundroid.kokpit.commands.EnemyColor;
import com.yundroid.kokpit.commands.FlyingMode;
import com.yundroid.kokpit.commands.LEDAnimation;
import com.yundroid.kokpit.commands.UltrasoundFrequency;
import com.yundroid.kokpit.commands.UserBox;
import com.yundroid.kokpit.commands.VideoBitrateMode;
import com.yundroid.kokpit.commands.VideoChannel;
import com.yundroid.kokpit.commands.VideoCodec;
import com.yundroid.kokpit.commands.WifiMode;
import com.yundroid.kokpit.navdata.NavDataReceiver;
import com.yundroid.kokpit.navdata.listeners.AltitudeListener;
import com.yundroid.kokpit.navdata.listeners.AttitudeListener;
import com.yundroid.kokpit.navdata.listeners.BatteryListener;
import com.yundroid.kokpit.navdata.listeners.StateListener;
import com.yundroid.kokpit.navdata.listeners.VelocityListener;

public class ARDrone {
	private final static String TAG = "ARDrone";
	
	private final static String DRONE_IP = "192.168.1.1";
	
	private final int PORT_AT 		= 5556;
	private final int PORT_VIDEO 	= 5555;
	private final int PORT_NAVDATA 	= 5554;
	
	private InetAddress 	inetAddress;
	
	private CommandSender 	commandSender = null;
	private NavDataReceiver navDataReceiver = null;
		
	private Thread commandThread;
	private Thread navDataThread;
	
	//Flight parameters
	private float speed = 1.0F;
	private float pitch = 0;
	private float roll 	= 0;
	private float gaz 	= 0;
	private float yaw	= 0;
	
	public ARDrone() throws UnknownHostException{
		this(DRONE_IP);
	}
	
	public ARDrone(String inetAddress) throws UnknownHostException{
		this.inetAddress = InetAddress.getByName(inetAddress);
	}
	
	public void connect() throws IOException{
		if(commandSender == null){
			commandSender = new CommandSender(inetAddress);			
		}	
	}
	
	public void connectNavData() throws SocketException{
		if(navDataReceiver == null){
			navDataReceiver = new NavDataReceiver(inetAddress, commandSender);
		}
	}
	
	public void setAltitudeListener(AltitudeListener altitudeListener){
		if(navDataReceiver != null)
			navDataReceiver.setAltitudeListener(altitudeListener);
	}
	
	public void setAttitudeListener(AttitudeListener attitudeListener){
		if(navDataReceiver != null)
			navDataReceiver.setAttitudeListener(attitudeListener);
	}
	
	public void setBatteryListener(BatteryListener batteryListener){
		if(navDataReceiver != null)
			navDataReceiver.setBatteryListener(batteryListener);
	}
	
	public void setStateListener(StateListener stateListener){
		if(navDataReceiver != null)
			navDataReceiver.setStateListener(stateListener);
	}
	
	public void setVelocityListener(VelocityListener velocityListener){
		if(navDataReceiver != null)
			navDataReceiver.setVelocityListener(velocityListener);
	}

	public void connectVideo(){
		//TODO lots to do
	}
	
	public void disconnect() throws IOException{
		if(commandThread != null)
			commandThread.interrupt();
		
		if(commandSender != null){
			commandSender.quit();
			commandSender.close();
			commandSender = null;
		}
		
		if(navDataThread != null){
			navDataThread.interrupt();
		}
		
		if(navDataReceiver != null){
			navDataReceiver.close();
			navDataReceiver = null;
		}
	}
	
	public void start() throws Exception{
		if(commandSender != null){
			commandThread = new Thread(commandSender);
			commandThread.start();
			init();
		}
		
		if(navDataReceiver != null){
			navDataThread = new Thread(navDataReceiver);
			navDataThread.start();
			Log.d(TAG,"navdata thread started");
		}
	}
	
	public void init(){
		resetCOMWDG();
		setPMODE(2);
		setMisc(2, 20, 2000, 3000);
		land();
		setControlLevel(ControlLevel.ACE);
		setUltrasoundFrequency(UltrasoundFrequency.ULTRASOUND_25Hz);
		land();
		hover();
		setNavDataDemo(true);
		commandSender.quit();
	}
	
	public void takeOff(){
		if(commandSender != null)
			commandSender.takeOff();
	}
	
	public void land(){
		if(commandSender != null)
			commandSender.land();
	}
	
	public void emergeny(){
		if(commandSender != null)
			commandSender.emergency();
	}
	
	public void goForward(){
		if(commandSender != null)
			commandSender.forward(speed);
	}
	
	public void goBackward(){
		if(commandSender != null)
			commandSender.backward(speed);
	}
	
	public void goRight(){
		if(commandSender != null)
			commandSender.goRight(speed);
	}
	
	public void goLeft(){
		if(commandSender != null)
			commandSender.goLeft(speed);
	}
	
	public void goHigher(){
		if(commandSender != null)
			commandSender.higher(speed);
	}
	
	public void goLower(){
		if(commandSender != null)
			commandSender.lower(speed);
	}
	
	public void rotateRight(){
		if(commandSender != null)
			commandSender.rotateRight(speed);
	}
		
	public void rotateLeft(){
		if(commandSender != null)
			commandSender.rotateLeft(speed);
	}
	
	public void hover(){
		if(commandSender != null)
			commandSender.hover();
	}
	
	public void move(float pitch, float roll, float gaz, float yaw){
		if(commandSender != null)
			commandSender.move(pitch, roll, gaz, yaw);
	}
	
	public void move(){
		if(commandSender != null)
			commandSender.move(pitch, -roll, gaz, yaw);
		
		
	}
	
	public void setARDroneName(String name){
		if(commandSender != null)
			commandSender.setARDroneName(name);
	}
	
	public void setNavDataDemo(boolean b){
		if(commandSender != null)
			commandSender.setNavDataDemo(b);
	}
	
	public void setNavDataOptions(int value){
		if(commandSender != null)
			commandSender.setNavDataOptions(value);
	}
	
	public void setControlLevel(ControlLevel controlLevel){
		if(commandSender != null)
			commandSender.setControlLevel(controlLevel);
	}
	
	public void setMaxEulerAngle(float value){
		if(commandSender != null)
			commandSender.setMaxEulerAngle(value);
	}
	
	public void setMaxAltitude(int value){
		if(commandSender != null)
			commandSender.setMaxAltitude(value);
	}
	
	public void setMinAltitude(){
		if(commandSender != null)
			commandSender.setMinAltitude();
	}
	
	public void setMaxVerticalSpeed(int value){
		if(commandSender != null)
			commandSender.setMaxVerticalSpeed(value);
	}
	
	public void setMaxYawSpeed(float value){
		if(commandSender != null)
			commandSender.setMaxYawSpeed(value);
	}
	
	public void setOutdoorFlight(boolean b){
		if(commandSender != null)
			commandSender.setOutdoorFlight(b);
	}
	
	public void setFlightWithoutShell(boolean b){
		if(commandSender != null)
			commandSender.setFlightWithoutShell(b);
	}
	
	public void setFlyingMode(FlyingMode mode){
		if(commandSender != null)
			commandSender.setFlyingMode(mode);
	}
	
	public void setHoveringRange(int value){
		if(commandSender != null)
			commandSender.setHoveringRange(value);
	}
	
	public void launchAnimation(Animation anim){
		if(commandSender != null)
			commandSender.launchAnimation(anim);
	}
	
	public void setSSIDSinglePlayer(String ssid){
		if(commandSender != null)
			commandSender.setSSIDSinglePlayer(ssid);
	}
	
	public void setSSIDMultiPlayer(String ssid){
		if(commandSender != null)
			commandSender.setSSIDMultiPlayer(ssid);
	}
	
	public void setWifiMode(WifiMode mode){
		if(commandSender != null)
			commandSender.setWifiMode(mode);
	}
	
	public void setOwnerMac(String mac){
		if(commandSender != null)
			commandSender.setOwnerMac(mac);
	}
	
	public void setUltrasoundFrequency(UltrasoundFrequency uf){
		if(commandSender != null)
			commandSender.setUltrasoundFrequency(uf);
	}
	
	public void setCodecFps(int value){
		if(commandSender != null)
			commandSender.setCodecFPS(value);
	}
	
	public void setVideoCodec(VideoCodec codec){
		if(commandSender != null)
			commandSender.setVideoCodec(codec);
	}
	
	public void setVideoBitrate(int value){
		if(commandSender != null)
			commandSender.setVideoBitrate(value);
	}
	
	public void setMaxBitrate(int value){
		if(commandSender != null)
			commandSender.setMaxBitrate(value);
	}
	
	public void setBitrateControlMode(VideoBitrateMode mode){
		if(commandSender != null)
			commandSender.setBitrateControlMode(mode);
	}
	
	public void setVideoChannel(VideoChannel vc){
		if(commandSender != null)
			commandSender.setVideoChannel(vc);
	}
	
	public void setVideoOnUsb(boolean b){
		if(commandSender != null)
			commandSender.setVideoOnUsb(b);
	}
	
	public void launchLedsAnimation(LEDAnimation anim, float frequency, int duration){
		if(commandSender != null)
			commandSender.launchLedsAnimation(anim, frequency, duration);
	}
	
	public void launchLedsAnimation(int anim, float frequency, int duration){
		if(commandSender != null)	
			commandSender.launchLedsAnimation(anim, frequency, duration);
	}
	
	public void setEnemyColors(EnemyColor color){
		if(commandSender != null)
			commandSender.setEnemyColors(color);
	}
	
	public void setEnemyDetectionWithoutShell(int value){
		if(commandSender != null)
			commandSender.setEnemyDetectionWithoutShell(value);
	}
	
	public void setDetectionCamera(DetectionType dt){
		if(commandSender != null)
			commandSender.setDetectionCamera(dt);
	}
	
	public void setHorizontalCameraDetection(DetectionTag dt){
		if(commandSender != null)
			commandSender.setHorizontalCamDetection(dt);
	}
	
	public void setHorizontalAndVerticalSynchronously(DetectionTag dt){
		if(commandSender != null)
			commandSender.setHorizontalAndVerticalSynchronously(dt);
	}
	
	public void setVerticalCameraDetection(DetectionTag dt){
		if(commandSender != null)
			commandSender.setVerticalCamDetection(dt);
	}
	
	public void setUserBoxCmd(UserBox ub){
		if(commandSender != null)
			commandSender.setUserBoxCmd(ub);
	}
	
	public void setGPSLatitude(double value){
		if(commandSender != null)
			commandSender.setGPSLatitude(value);
	}
	
	public void setGPSLongitude(double value){
		if(commandSender != null)
			commandSender.setGPSLongitude(value);
	}
	
	public void setGPSAltitude(double value){
		if(commandSender != null)
			commandSender.setGPSAltitude(value);
	}
	
	public void setLocation(double latitude, double longitude, double altitude){
		if(commandSender != null){
			commandSender.setGPSLatitude(latitude);
			commandSender.setGPSLongitude(longitude);
			commandSender.setGPSAltitude(altitude);
		}
	}
	
	public void setApplicationID(String appID){
		if(commandSender != null)
			commandSender.setApplicationID(appID);
	}
	
	public void setApplicationDescription(String appDesc){
		if(commandSender != null)
			commandSender.setApplicationDescription(appDesc);
	}
	
	public void setProfileID(String profileID){
		if(commandSender != null)
			commandSender.setProfileID(profileID);
	}
	
	public void setProfileDescription(String profileDesc){
		if(commandSender != null)
			commandSender.setProfileDescription(profileDesc);
	}
	
	public void setSessionID(String sessionID){
		if(commandSender != null)
			commandSender.setSessionID(sessionID);
	}
	
	public void setSessionDescription(String sessionDesc){
		if(commandSender != null)
			commandSender.setSessionDescription(sessionDesc);
	}
	
	public void resetCOMWDG(){
		commandSender.resetCOMWDG();
	}
	
	public void FTRIM(){
		commandSender.FTRIM();
	}
	
	public void setPMODE(int value){
		commandSender.setPMODE(value);
	}
	
	public void setMisc(int arg1, int arg2, int arg3, int arg4){
		commandSender.setMisc(arg1, arg2, arg3, arg4);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setPitch(float pitch){
		this.pitch = pitch;
	}
	
	public void setRoll(float roll){
		this.roll = roll;
	}
	
	public void setGaz(float gaz){
		this.gaz = gaz;
	}
	
	public void setYaw(float yaw){
		this.yaw = yaw;
	}
	
	public void setCombinedYaw(boolean b){
		if(commandSender != null)
			commandSender.setCombinedYaw(b);
	}
	
	public boolean getCombinedYaw(){
		if(commandSender != null)
			return commandSender.getCombinedYaw();
		else
			return false;
	}
}



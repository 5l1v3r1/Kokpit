package com.yundroid.kokpit.commands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import android.util.Log;
import android.widget.Button;

import com.yundroid.kokpit.communication.managers.UDPManager;
public class CommandSender extends UDPManager{
	
	private static final String TAG = "CommandSender";
	
	private final int PORT_AT = 5556;
	
	private ATCommand atc;
	
	private boolean isSticky;
	
	private boolean combinedYawFlag = false;
	
	private byte[] currentCommand = null;
	
	private static final String sessionID 	= "d2e081a3";
	private static final String profileID 	= "be27e2e4";
	private static final String appID		= "d87f7e0c";
	
	public CommandSender(InetAddress inetAddress) throws IOException {
		this.inetAddress = inetAddress;
		atc = new ATCommand();
		connect(PORT_AT);
	}
	
	public void initDrone(){
		try{
			setNavDataDemo(true); 					
			setPMODE(2); 			
			setMisc(2, 20, 2000, 3000); 
			
			setConfigIDS(sessionID, profileID, appID);
			setSessionID(sessionID);	
			
			setConfigIDS(sessionID, profileID, appID);
			setProfileID(profileID);
			
			setConfigIDS(sessionID, profileID, appID);
			setApplicationID(appID);
			
			setConfigIDS(sessionID, profileID, appID);
			setBitrateControlMode(0);
			
			setConfigIDS(sessionID, profileID, appID);
			setVideoCodec(VideoCodec.H264_720P_CODEC);
			
			setConfigIDS(sessionID, profileID, appID);
			setVideoChannel(VideoChannel.ZAP_CHANNEL_HORI);
			
			FTRIM();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//General Configureation
	public void setARDroneName(String name){
		currentCommand = atc.ConfigureCommand("general:ardrone_name", name);
		isSticky = false;
	}

	/** The drone can either send a reduced set of navigation data (navdata) to its clients, or send all the available information
		which contain many debugging information that are useless for everyday flights.*/
	public void setNavDataDemo(boolean b){
		currentCommand = atc.ConfigureCommand("general:navdata_demo", b);
		isSticky = false;
	}
	
	public void setNavDataOptions(int value){
		currentCommand = atc.ConfigureCommand("general:navdata_options", value);
		isSticky = false;
	}
	
	/*	TODO there are video_enable and vision_enable functions which are reserved for future use
	 	consider putting them in the code*/
	
	//Control Configuration
	public void setControlLevel(ControlLevel controlLevel){
		currentCommand = atc.ConfigureCommand("control:control_level", controlLevel.ordinal());
		isSticky = false;
	}
	
	public void setMaxEulerAngle(float value){
		currentCommand = atc.ConfigureCommand("control:euler_angle_max", limit(value, 0F, 0.52F));
		isSticky = false;
	}
	
	public void setMaxAltitude(int value){
		currentCommand = atc.ConfigureCommand("control:altitude_max", value);
		isSticky = false;
	}
	
	/** Should be left to default value, for control stabilities issues. */
	public void setMinAltitude(){
		currentCommand = atc.ConfigureCommand("control:altitude_min", 50);
		isSticky = false;
	}
	
	public void	setMaxVerticalSpeed(int value){
		currentCommand = atc.ConfigureCommand("control:control_vz_max", limit(value, 200, 2000));
		isSticky = false;
	}
	
	public void setMaxYawSpeed(float value){
		currentCommand = atc.ConfigureCommand("control:control_yaw", limit(value, 0.7F, 6.11F));
		isSticky = false;
	}
	
	public void setOutdoorFlight(boolean b){
		currentCommand = atc.ConfigureCommand("control:outdoor", b);
		isSticky = false;
	}
	
	public void setFlightWithoutShell(boolean b){
		currentCommand = atc.ConfigureCommand("control:flight_without_shell", b);
		isSticky = false;
	}
	
	public void setFlyingMode(FlyingMode mode){
		currentCommand = atc.ConfigureCommand("control:flying_mode", mode.ordinal());
		isSticky = false;
	}
	
	/** This setting is used when flying mode is set to HOVER_ON_TOP_OF_ORIENTED_ROUNDEL. It gives
	 *  the AR.Drone the maximum distance(in millimeters) allowed between AR.Drone and the oriented roundel */
	public void setHoveringRange(int value){
		currentCommand = atc.ConfigureCommand("control:hovering_range", value);
		isSticky = false;
	}
	
	public void launchAnimation(Animation anim){
		currentCommand = atc.ConfigureCommand("control:flight_anim", anim.ordinal() + "," + anim.getDefaultDuration());		isSticky = false;
	}
	
	//Network Configuration
	/** The AR.Drone SSID. Changes are applied on reboot.*/
	public void setSSIDSinglePlayer(String ssid){
		currentCommand = atc.ConfigureCommand("network:ssid_single_player", ssid);
		isSticky = false;
	}
	
	public void setSSIDMultiPlayer(String ssid){
		currentCommand = atc.ConfigureCommand("network:ssid_multi_player", ssid);
		isSticky = false;
	}
	
	/** This value should not be changed for users applications.*/
	public void setWifiMode(WifiMode mode){
		currentCommand = atc.ConfigureCommand("network:wifi_mode", mode.ordinal());
		isSticky = false;
	}
	
	/** Mac addres paired with the AR.Drone. Set to "00:00:00:00:00:00" to unpair the AR.Drone.*/
	public void setOwnerMac(String mac){
		currentCommand = atc.ConfigureCommand("network:owner_mac", mac);
		isSticky = false;
	}
	
	//Nav-Board Configuration
	/** Only two frequencies are available : 22:22 and 25 Hz.*/
	public void setUltrasoundFrequency(UltrasoundFrequency uf){
		currentCommand = atc.ConfigureCommand("pic:ultrasound_frequency", uf.getValue());
		isSticky = false;
	}
	
	//Video Configuration
	public void setCodecFPS(int value){
		currentCommand = atc.ConfigureCommand("video:codec_fps", limit(value, 0, 30));
		isSticky = false;
	}
	
	public void setVideoCodec(VideoCodec codec){
		currentCommand = atc.ConfigureCommand("video:video_codec", codec.getValue());
		isSticky = false;
	}
	
	public void setVideoBitrate(int value){
		currentCommand = atc.ConfigureCommand("video:video_bitrate", limit(value, 500, 4000));
		isSticky = false;
	}
	
	public void setMaxBitrate(int value){
		currentCommand = atc.ConfigureCommand("video:max_bitrate", limit(value, 1000, 4000));
		isSticky = false;
	}
	
	public void setBitrateControlMode(int mode){
		currentCommand = atc.ConfigureCommand("video:bitrate_control_mode", mode);
		isSticky = false;
	}
	
	public void setBitrateControlMode(VideoBitrateMode mode){
		currentCommand = atc.ConfigureCommand("video:bitrate_control_mode", mode.ordinal());
		isSticky = false;
	}
	
	public void setVideoChannel(VideoChannel vc){
		currentCommand = atc.ConfigureCommand("video:video_channel", vc.ordinal());
		isSticky = false;	
	}
	
	public void setVideoOnUsb(boolean b){
		currentCommand = atc.ConfigureCommand("video:video_on_usb", String.valueOf(b));
		isSticky = false;
	}
	
	//Leds Configuration
	public void launchLedsAnimation(LEDAnimation anim, float frequency, int duration){
		currentCommand = atc.ConfigureCommand("leds:leds_anim", "" + anim.ordinal() + ","
					+ atc.intOfFloat(frequency) + "," + duration);
		isSticky = false;
	}
	
	public void launchLedsAnimation(int anim, float frequency, int duration){
		currentCommand = atc.ConfigureCommand("leds:leds_anim", anim + "," + atc.intOfFloat(frequency) + "," + duration);
		isSticky = false;
	}
	
	//Detection Configuration
	public void setEnemyColors(EnemyColor color){
		currentCommand = atc.ConfigureCommand("detect:enemy_color", color.getValue());
		isSticky = false;
	}
	
	public void setEnemyDetectionWithoutShell(int value){
		currentCommand = atc.ConfigureCommand("detect:enemy_without_shell", value);
		isSticky = false;
	}
	
	public void setDetectionCamera(DetectionType dt){
		currentCommand = atc.ConfigureCommand("detect:detect_type", dt.getValue());
		isSticky = false;
	}
	
	public void setHorizontalCamDetection(DetectionTag dt){
		currentCommand = atc.ConfigureCommand("detect:detections_select_h", dt.ordinal());
		isSticky = false;
	}
	
	public void setHorizontalAndVerticalSynchronously(DetectionTag dt){
		currentCommand = atc.ConfigureCommand("detect:detections_select_v_hsync", dt.ordinal());
		isSticky = false;
	}
	
	public void setVerticalCamDetection(DetectionTag dt){
		currentCommand = atc.ConfigureCommand("detect:detections_select_v", dt.ordinal());
		isSticky = false;
	}
	
	//Userbox Section
	public void setUserBoxCmd(UserBox ub){
		currentCommand = atc.ConfigureCommand("userbox:userbox_cmd", ub.ordinal());
		isSticky = false;
	}
	
	//GPS Section
	public void setGPSLatitude(double value){
		currentCommand = atc.ConfigureCommand("gps:latitude", value);
		isSticky = false;
	}
	
	public void setGPSLongitude(double value){
		currentCommand = atc.ConfigureCommand("gps:longitude", value);
		isSticky = false;
	}
	
	public void setGPSAltitude(double value){
		currentCommand = atc.ConfigureCommand("gps:altitude", value);
		isSticky = false;
	}
	
	//Custom Section - Multiconfig Support
	public void setApplicationID(String appID){
		currentCommand = atc.ConfigureCommand("custom:application_id", appID);
		isSticky = false;
	}
	
	public void setApplicationDescription(String appDesc){
		currentCommand = atc.ConfigureCommand("custom:application_desc", appDesc);
		isSticky = false;
	}
	
	public void setProfileID(String profileID){
		currentCommand = atc.ConfigureCommand("custom:profile_id", profileID);
		isSticky = false;
	}
	
	public void setProfileDescription(String profileDesc){
		currentCommand = atc.ConfigureCommand("custom:profile_desc", profileDesc);
		isSticky = false;
	}
	
	public void setSessionID(String sessionID){
		currentCommand = atc.ConfigureCommand("custom:session_id", sessionID);
		isSticky = false;
	}
	
	public void setSessionDescription(String sessionDesc){
		currentCommand = atc.ConfigureCommand("custom:session_desc", sessionDesc);
		isSticky = false;
	}
	
	public void setConfigIDS(String csid, String cuid, String caid){
		currentCommand = atc.ConfigureIdentifiersCommand(csid, cuid, caid);
	}
	
	//Control Functions
	public void takeOff(){
		FTRIM();
		currentCommand = atc.REFCommand(true, false);
		isSticky = true;
	}
	
	public void land(){
		currentCommand = atc.REFCommand(false, false);
		isSticky = true;
	}
	
	public void emergency(){
		currentCommand = atc.REFCommand(false, true);
		isSticky = true;
	}
	
	public void forward(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, -speed, 0F, 0F);
		isSticky = true;
	}
	
	public void backward(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, speed, 0F, 0F);
		isSticky = true;
	}
	
	public void goRight(float speed){
		currentCommand = atc.PCMDCommand(1, speed, 0F, 0F, 0F);
		isSticky = true;
	}
	
	public void goLeft(float speed){
		currentCommand = atc.PCMDCommand(1, -speed, 0F, 0F, 0F);
		isSticky = true;
	}
	
	public void higher(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, 0F, speed, 0F);
		isSticky = true;
	}
	
	public void lower(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, 0F, -speed, 0F);
		isSticky = true;
	}
	
	public void rotateRight(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, 0F, 0F, speed);
		isSticky = true;
	}
	
	public void rotateLeft(float speed){
		currentCommand = atc.PCMDCommand(1, 0F, 0F, 0F, -speed);
		isSticky = true;
	}
	
	public void move(float pitch, float roll, float gaz, float yaw){
		if(combinedYawFlag == true){
			currentCommand = atc.PCMDCommand(1, pitch, roll, gaz, yaw);
		}		
		isSticky = true;
	}
	
	public void moveMag(float pitch, float roll, float gaz, float yaw, float magnetoPsi, float magnetoPsiAccuracy){
		if(combinedYawFlag == true){
			currentCommand = atc.PCMDMAGCommand(1, pitch, roll, gaz, yaw, magnetoPsi, magnetoPsiAccuracy);
		}	
		isSticky = true;
	}
	
	public void hover(){
		currentCommand = atc.PCMDCommand(0, 0F, 0F, 0F, 0F);
		isSticky = true;
	}
	
	public void stop(){
		currentCommand = atc.PCMDCommand(1, 0, 0, 0, 0);
		isSticky = true;
	}
	
	public void sendControlAck(boolean b){
		if(b){
			sendControlAck();
			isSticky = false;
		}		
	}
	
	public void sendControlAck(){
		currentCommand = atc.ControlCommand(0);
		isSticky = false;
	}

	public void setMisc(int arg1, int arg2, int arg3, int arg4){
		currentCommand = atc.MiscCommand(arg1, arg2, arg3, arg4);
		isSticky = false;
	}
	
	public void setPMODE(int value){
		currentCommand = atc.PModeCommand(value);
		isSticky = false;
	}
	
	public void resetCOMWDG(){
		currentCommand = atc.COMWDG();
		isSticky = false;
	}
	
	public void FTRIM(){
		currentCommand = atc.FTRIM();
		isSticky = false;
	}
	
	public void quit(){
		currentCommand = null;
		isSticky = false;
	}
	
	private synchronized void sendATC(byte[] atc){
		final DatagramPacket packet = new DatagramPacket(atc, atc.length, inetAddress, PORT_AT);
		
		try{
			datagramSocket.send(packet);
			Log.d(TAG, "Command Sent : ");
		} catch(IOException e){
			Log.d(TAG, "Command could not send");
			e.printStackTrace();
		}
					
	}
	
	@Override
	public void run() {
		while(true){
			if(currentCommand != null){
				sendATC(currentCommand);
				if(!isSticky){
					currentCommand = null;
				}
			}
			
			try{
				Thread.sleep(20);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			
		}
	}
	
	private int limit(int i, int min, int max){
		return (i > max ? max : (i < min ? min : i));
	}
	
	private float limit(float f, float min, float max){
		return (f > max ? max : (f < min ? min : f));
	}
	
	public void setCombinedYaw(boolean b){
		this.combinedYawFlag = b;
	}
	
	public boolean getCombinedYaw(){
		return combinedYawFlag;
	}	
}

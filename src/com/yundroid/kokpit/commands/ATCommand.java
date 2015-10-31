package com.yundroid.kokpit.commands;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.util.Log;

public class ATCommand {
	private static final String TAG = "ATCommand";
	
	private int sequence = 1;
	
	private ByteBuffer	bb = ByteBuffer.allocate(4);
	private FloatBuffer fb = bb.asFloatBuffer();
	private IntBuffer 	ib = bb.asIntBuffer();
	
	public byte[] ConfigureCommand(String option, String value){
		return getATC("CONFIG", "\"" + option + "\",\"" + value + "\"");
	}
	
	public byte[] ConfigureCommand(String option, int value){
		return getATC("CONFIG", "\"" + option + "\",\""  + value + "\"");
	}
	
	public byte[] ConfigureCommand(String option, float value){
		return getATC("CONFIG", "\"" + option + "\",\"" + value + "\"");
	}
	
	public byte[] ConfigureCommand(String option, double value){
		return getATC("CONFIG", "\"" + option + "\",\"" + Double.doubleToLongBits(value) + "\"");
	}
	
	public byte[] ConfigureCommand(String option, boolean value){
		return getATC("CONFIG", "\"" + option + "\",\"" + Boolean.toString(value) + "\"");
	}
	
	/** While in multiconfiguration, you must send this command before every AT*CONFIG. The config
	will only be applied if the ids must match the current ids on the AR.Drone.
	@param csid Current Session ID
	@param cuid Current User ID
	@param caid Current Application ID**/
	public byte[] ConfigureIdentifiersCommand(String csid, String cuid, String caid){
	    return getATC("CONFIG_IDS",csid + "," + cuid + "," + caid);
	}
	
	public byte[] ControlCommand(int value){
		return getATC("CTRL", String.valueOf(value));
	}
	
	public byte[] ControlCommand(int mode, int value){
		return getATC("CTRL", mode + "," + value );
	}
	
	public byte[] ControlCommand(ControlMode mode, int value){
		return getATC("CTRL", mode.ordinal() + "," + value);
	}
	
	public byte[] COMWDG(){
		return getATC("COMWDG");
	}
	
	public byte[] FTRIM(){
		return getATC("FTRIM");
	}
	
	public byte[] MiscCommand(int arg1, int arg2, int arg3, int arg4){
		return getATC("MISC", arg1 + "," + arg2 + "," + arg3 + "," + arg4);//TODO find out what these args means
	}
	
	public byte[] PCMDCommand(int enable, float pitch, float roll, float gaz, float yaw ){
		return getATC("PCMD", enable + "," + intOfFloat(pitch) + "," + intOfFloat(roll) + "," + 
						intOfFloat(gaz) + "," + intOfFloat(yaw));
	}
	
	public byte[] PCMDMAGCommand(int enable, float pitch, float roll, float gaz, float yaw, float magnetoPsi, float magnetoPsiAccuracy){
		return getATC("PCMDMAG", enable + "," + pitch + "," + roll + "," + gaz + "," + yaw + "," + magnetoPsi + "," + magnetoPsiAccuracy);
	}
	
	public byte[] PModeCommand(int value){
		return getATC("PMODE", String.valueOf(value));
	}
	
	public byte[] REFCommand(boolean takeOff, boolean emergency){
		int value = (1 << 18) | (1 << 20) | (1 << 22) | (1 << 24) | (1 << 28);
		if(emergency){
			value |= (1 << 8);
		}
		
		if(takeOff){
			value |= (1 << 9);
		}
		return getATC("REF", String.valueOf(value));
	}
	
	public byte[] getATC(String ID, String parameter){
		Log.d(TAG, ID + parameter);
		return ("AT*" + ID + "=" + getSeq() + "," + parameter + "\r").getBytes() ;
	}
	
	public byte[] getATC(String ID){
		return ("AT*" + ID + "=" + getSeq() + "\r").getBytes();
	}
	
	public synchronized int getSeq(){
		return sequence++;
	}
	
	public int intOfFloat(float f){
		fb.put(0, f);
		return ib.get(0);
	}
	
}

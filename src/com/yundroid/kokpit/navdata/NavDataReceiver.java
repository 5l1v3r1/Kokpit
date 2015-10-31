package com.yundroid.kokpit.navdata;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.yundroid.kokpit.commands.CommandSender;
import com.yundroid.kokpit.communication.managers.UDPManager;
import com.yundroid.kokpit.navdata.listeners.AltitudeListener;
import com.yundroid.kokpit.navdata.listeners.AttitudeListener;
import com.yundroid.kokpit.navdata.listeners.BatteryListener;
import com.yundroid.kokpit.navdata.listeners.StateListener;
import com.yundroid.kokpit.navdata.listeners.VelocityListener;

public class NavDataReceiver extends UDPManager{
	
	private static final int PORT_NAVDATA = 5554;
	
	private CommandSender commandSender = null;
	
	//NavData demo
	private AttitudeListener 	attitudeListener;
	private AltitudeListener 	altitudeListener;
	private BatteryListener 	batteryListener;
	private StateListener		stateListener;
	private VelocityListener 	velocityListener;
	
	private long lastSequence = 1;
	
	public NavDataReceiver(InetAddress inetAddress, CommandSender commandSender) throws SocketException {
		this.inetAddress = inetAddress;
		this.commandSender = commandSender;
	}

	@Override
	public void run() {
		startNavDataStream();
		boolean bootstrap = true;
		boolean controlAck = false;
		
		while(true){
			try{
				ticklePort(PORT_NAVDATA);
				DatagramPacket pack = new DatagramPacket(new byte[2048], 2048, inetAddress, PORT_NAVDATA);
				
				datagramSocket.receive(pack);
				ByteBuffer bb = ByteBuffer.wrap(pack.getData(), 0, pack.getLength());

				DroneState state = parseNavData(bb);
				
				if(bootstrap){
					controlAck = state.isControlAckReceived();
					commandSender.sendControlAck(controlAck);
					
					if(state.isNavDataDemo()){
						commandSender.setNavDataDemo(true);
					}
					bootstrap = false;
				}
				
				boolean newControlAck = state.isControlAckReceived();
				if(newControlAck != controlAck){
					commandSender.sendControlAck(newControlAck);
					controlAck = newControlAck;
				}
				
				if(state.isComProblemOccured()){
					commandSender.resetCOMWDG();
				}
			} catch(IOException e){
				e.printStackTrace();
			} catch (NavDataException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void startNavDataStream(){
		connect(PORT_NAVDATA);
		ticklePort(PORT_NAVDATA);
	}
	
	private DroneState parseNavData(ByteBuffer bb) throws NavDataException{
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int magic = bb.getInt();
		
		int state = bb.getInt();
		
		long sequence = bb.getInt() & 0xFFFFFFFFL;
		
		int vision = bb.getInt();
		
		//Consider checking sequences in case we receive the same navdata packets
		/*if(sequence <= lastSequence && sequence != 1){
			return;
		}*/
		lastSequence = sequence;
		
		DroneState ds = new DroneState(state);
		
		if(stateListener != null){
			stateListener.onStateChanged(new DroneState(state));
		}
		
		while(bb.position() < bb.limit()){
			int tag = bb.getShort() & 0xFFFF;
			int payloadSize  = (bb.getShort() & 0xFFF) - 4;
			
			ByteBuffer optionData = bb.slice().order(ByteOrder.LITTLE_ENDIAN);
			payloadSize = Math.min(payloadSize, optionData.remaining());
			optionData.limit(payloadSize);
			parseOption(tag, optionData);
			bb.position(bb.position() + payloadSize);
			
		}
		
		return ds;
	}
	
	private void parseOption(int tag, ByteBuffer optionData){
		switch(tag){
		case 0:
			parseNavDataDemo(optionData);
			break;
		}
	}
	
	private void parseNavDataDemo(ByteBuffer optionData){
		
		int controlState = optionData.getInt();
		int batteryLevel = optionData.getInt();
		
		float pitch	= optionData.getFloat() / 1000;
		float roll	= optionData.getFloat() / 1000;
		float yaw 	= optionData.getFloat() / 1000;
		
		int altitude = optionData.getInt();
		
		float vX = optionData.getFloat();
		float vY = optionData.getFloat();
		float vZ = optionData.getFloat();
		
		if(attitudeListener != null){
			attitudeListener.onAttitudeReceived(pitch, roll, yaw);
		}
		
		if(altitudeListener != null){
			altitudeListener.onAltitudeChanged(altitude);
		}
		
		if(batteryListener != null){
			batteryListener.onBatteryLevelChanged(batteryLevel);
		}
		
		if(velocityListener != null){
			velocityListener.onVelocityChanged(vX, vY, vZ);
		}		
	}

	public void setAttitudeListener(AttitudeListener attitudeListener){
		this.attitudeListener = attitudeListener;
	}
	
	public void setAltitudeListener(AltitudeListener altitudeListener){
		this.altitudeListener = altitudeListener;
	}
	
	public void setBatteryListener(BatteryListener batteryListener){
		this.batteryListener = batteryListener;
	}
	
	public void setStateListener(StateListener stateListener){
		this.stateListener = stateListener;
	}
	
	public void setVelocityListener(VelocityListener velocityListener){
		this.velocityListener = velocityListener;
	}	
}

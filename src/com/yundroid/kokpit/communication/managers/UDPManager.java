package com.yundroid.kokpit.communication.managers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class UDPManager implements Runnable{

	protected DatagramSocket 	datagramSocket;
	protected InetAddress 		inetAddress;
	
	public void connect(int port){		
			try{			
				datagramSocket = new DatagramSocket(port);
				datagramSocket.setSoTimeout(3000);
			} catch(SocketException e){
				e.printStackTrace();
			}		
	}
	
	public void close(){
		if(datagramSocket != null){
			datagramSocket.close();
		}
	}
 
	protected void ticklePort(int port){
		byte[] buf = {0x01, 0x00, 0x00, 0x00};
		DatagramPacket pack = new DatagramPacket(buf, buf.length, inetAddress, port);
		
		try{
			datagramSocket.send(pack);
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
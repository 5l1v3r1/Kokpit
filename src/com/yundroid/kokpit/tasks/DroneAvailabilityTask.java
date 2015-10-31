package com.yundroid.kokpit.tasks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;

public class DroneAvailabilityTask extends AsyncTask<Context, Integer, Boolean>{

	@Override
	protected Boolean doInBackground(Context... params) {
		String DRONE_IP = "192.168.1.1";
		
		try{
			if(!InetAddress.getByName(DRONE_IP).isReachable(1000)){
				return Boolean.FALSE;						
			}
		} catch(UnknownHostException e){
			return Boolean.FALSE;			
		} catch(IOException e){
			return Boolean.FALSE;					
		}
		
		if(isCancelled())
			return Boolean.FALSE;
		
		return Boolean.TRUE;
	}

}

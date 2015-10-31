package com.yundroid.kokpit.activities;

import com.yundroid.kokpit.intents.DroneAvailabilityManager;
import com.yundroid.kokpit.receivers.DroneAvailabilityListener;
import com.yundroid.kokpit.receivers.DroneAvailabilityReceiver;
import com.yundroid.kokpit.receivers.NetworkChangedListener;
import com.yundroid.kokpit.receivers.NetworkChangedReceiver;
import com.yundroid.kokpit.tasks.DroneAvailabilityTask;
import com.yundroid.yundrone.R;

import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ConnectScreenActivity extends Activity 
									implements 	OnClickListener, 
												DroneAvailabilityListener,
												NetworkChangedListener{
	

	private static boolean isAvailable  = false;
	
	private TextView txtStatus;
	
	private ImageButton btnCockpit;
	
	private BroadcastReceiver droneAvailabilityReceiver;
	private BroadcastReceiver networkChangedReceiver;
	
	private DroneAvailabilityTask droneAvailabilityTask;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        
        droneAvailabilityReceiver = new DroneAvailabilityReceiver(this);
        networkChangedReceiver = new NetworkChangedReceiver(this);
        
        initUI();  
    }
	
	private void checkIfAvailable(){
    	if(droneAvailabilityTask != null && droneAvailabilityTask.getStatus() != Status.FINISHED){
    		droneAvailabilityTask.cancel(true);
    	}
    	
    	droneAvailabilityTask = new DroneAvailabilityTask(){

			@Override
			protected void onPostExecute(Boolean success) {
				onDroneAvailabilityChanged(success);
			}
    		
    	};
    	
    	if(Build.VERSION.SDK_INT >= 11){
    		droneAvailabilityTask.executeOnExecutor(DroneAvailabilityTask.THREAD_POOL_EXECUTOR, this);
    	} else{
    		droneAvailabilityTask.execute(this);
    	}
    }
	
	private void initUI(){    	
    	btnCockpit = (ImageButton)findViewById(R.id.btnCockpit);
    	txtStatus = (TextView)findViewById(R.id.txtStatus);
    }
    
    private void initListeners(){
    	btnCockpit.setOnClickListener(this);
    }
    
    private void registerReceivers(){
    	LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
    	bm.registerReceiver(droneAvailabilityReceiver, new IntentFilter(DroneAvailabilityManager.ACTION_DRONE_STATE_CHANGED));
    	
    	registerReceiver(networkChangedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    	    			
    }
    
    private void unregisterReceivers(){
    	LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
    	bm.unregisterReceiver(droneAvailabilityReceiver);
    	
    	unregisterReceiver(networkChangedReceiver);
   
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}    
    
    @Override
    protected void onDestroy(){
    	//unregisterReceivers();
    	super.onDestroy();
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceivers();
		checkIfAvailable();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

	@Override
	public void onClick(View view) {

		switch(view.getId()){
		case R.id.btnCockpit:
			if(isAvailable){				
				Intent cockpit = new Intent(ConnectScreenActivity.this, CockpitActivity.class);
				startActivity(cockpit);
			}
			break;
		}		
	}

	@Override
	public void onDroneAvailabilityChanged(boolean isDroneAvailable) {
		if(isDroneAvailable){
			initListeners();
			isAvailable = true;
			txtStatus.setTextColor(Color.GREEN);
			txtStatus.setText(R.string.ready);
		} else {
			txtStatus.setTextColor(Color.RED);
			txtStatus.setText(R.string.connectionFailed);
		}
	}

	@Override
	public void onNetworkChanged(NetworkInfo info) {
		if(!info.isConnected()){
			txtStatus.setTextColor(Color.RED);
			txtStatus.setText(R.string.connectionFailed);
		} else{
			checkIfAvailable();
		}
	}
	
}

package com.yundroid.kokpit.activities;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;

import java.io.IOException;

import com.yundroid.kokpit.ARDrone;
import com.yundroid.kokpit.commands.Animation;
import com.yundroid.kokpit.navdata.DroneState;
import com.yundroid.kokpit.navdata.listeners.AltitudeListener;
import com.yundroid.kokpit.navdata.listeners.AttitudeListener;
import com.yundroid.kokpit.navdata.listeners.BatteryListener;
import com.yundroid.kokpit.navdata.listeners.StateListener;
import com.yundroid.kokpit.navdata.listeners.VelocityListener;
import com.yundroid.kokpit.receivers.NetworkChangedListener;
import com.yundroid.kokpit.receivers.NetworkChangedReceiver;
import com.yundroid.kokpit.tasks.DroneAvailabilityTask;
import com.yundroid.kokpit.ui.Joystick;
import com.yundroid.kokpit.ui.MultiDirectionSlidingDrawer;
import com.yundroid.kokpit.ui.OnJoystickMovedListener;
import com.yundroid.yundrone.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CockpitActivity extends Activity implements SurfaceHolder.Callback,
														OnBufferingUpdateListener,
														OnCompletionListener,
														OnPreparedListener,
														OnVideoSizeChangedListener,
														OnClickListener,
														AltitudeListener,
														AttitudeListener,
														BatteryListener,
					 									NetworkChangedListener,
					 									StateListener,
					 									VelocityListener{
	
	private static final String videoPath = "tcp://192.168.1.1:5555";
	
	private ARDrone drone;

	private DroneAvailabilityTask droneAvailabilityTask;
	
	private BroadcastReceiver networkChangedReceiver;
	
	private View decorView;
	
	//Video Config
	private MediaPlayer mediaPLayer;
	
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	
	private Bundle extras;
	
	private int videoWidth;
	private int videoHeight;
	
	private boolean isVideoSizeKnown = false;
	private boolean isVideoReady = false;
	
	private RelativeLayout leftJLayout;
	private RelativeLayout rightJLayout;
	
	private Joystick leftJoystick;
	private Joystick rightJoystick;
	
	private ImageButton btnLauncher;
	private ImageView 	imgBattery;
	
	private TextView txtConnection;
	private TextView txtBattery;
	private TextView txtStatus;
	private TextView txtAltitude;
	private TextView txtVelocity;
	
	private Button btnEmergency;
	private Button btnFlip;
	private Button btnDance;
	private Button btnDance2;	
	
	//Animation Menu
	private MultiDirectionSlidingDrawer mdsDrawer;
	private Button openButton;
	
	private ImageButton[] animButtons;
	private ImageButton anim1;
	private boolean isFlying 	= false;	
	
	private boolean leftJoystickPressed 	= false;
	private boolean rightJoystickPressed 	= false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
				
		if(!LibsChecker.checkVitamioLibs(this))
			return;
		
		setContentView(R.layout.cockpit_activity);		
		
		hideNavigationBar();
		initReceivers();
		initJoysticks();
		initUI();
		initAnimationMenu();
		connect();
		startVideo();	
	}
	
	private void hideNavigationBar(){		
		decorView = getWindow().getDecorView();	
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
						}
					}, 2000);
				}
				
			}
		});	
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		decorView.setSystemUiVisibility(uiOptions);
		
	}
	
	private void initAnimationMenu(){
		
		openButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!mdsDrawer.isOpened()){
					mdsDrawer.animateOpen();
				}
			}
		});
	}

	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		openButton = (Button)findViewById(R.id.button_open);
	}

	private void initReceivers(){
		networkChangedReceiver = new NetworkChangedReceiver(this);
	}
	
	private void initJoysticks(){
		leftJLayout = (RelativeLayout)findViewById(R.id.leftLayout);
		rightJLayout = (RelativeLayout)findViewById(R.id.rightLayout);
		

		leftJoystick = new Joystick(getApplicationContext(), leftJLayout, R.drawable.knob);
		leftJoystick.setKnobSize(300, 300);
		leftJoystick.setJoystickSize(300, 300);
		leftJoystick.setAlphaJoystick(50);
		leftJoystick.setAlphaKnob(100);
		leftJoystick.setOffset(80);
		leftJoystick.setMinDistance(50);
		
		rightJoystick = new Joystick(getApplicationContext(), rightJLayout, R.drawable.knob);
		rightJoystick.setKnobSize(300, 300);
		rightJoystick.setJoystickSize(300, 300);
		rightJoystick.setAlphaJoystick(50);
		rightJoystick.setAlphaKnob(100);
		rightJoystick.setOffset(80);
		rightJoystick.setMinDistance(50);
			
		
		leftJLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				leftJoystick.drawKnob(event);				
				return true;
			}
		});
		
		leftJLayout.setOnDragListener(new View.OnDragListener() {
			
			@Override
			public boolean onDrag(View view, DragEvent event) {
				return false;
			}
		});
		
		rightJLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				rightJoystick.drawKnob(event);
				return true;
			}
		});		
		
		rightJLayout.setOnDragListener(new View.OnDragListener() {
			
			@Override
			public boolean onDrag(View view, DragEvent event) {
				return false;
			}
		});
		
		leftJoystick.setOnJoystickMovedListener(new OnJoystickMovedListener() {
			
			@Override
			public void onMoved(float x, float y) {
				
				leftJoystickPressed = true;
				drone.setCombinedYaw(true);
				drone.setPitch(x);
				drone.setRoll(y);
				drone.move();				
			}

			@Override
			public void onReleased() {
				leftJoystickPressed = false;
				if(rightJoystickPressed){
					drone.setPitch(0);
					drone.setRoll(0);
				}else{
					drone.setPitch(0);
					drone.setRoll(0);
					drone.hover();
				}					
			}
		});
		
		rightJoystick.setOnJoystickMovedListener(new OnJoystickMovedListener() {
			
			@Override
			public void onMoved(float x, float y) {
				rightJoystickPressed = true;
				drone.setGaz(y);
				drone.setYaw(x);
				drone.move();
					
			}

			@Override
			public void onReleased() {
				rightJoystickPressed = false;
				if(leftJoystickPressed){
					drone.setGaz(0);
					drone.setYaw(0);
				} else{
					drone.setGaz(0);
					drone.setYaw(0);
					drone.hover();
				}
			}
		});
	}
	
	private void initUI(){
		txtConnection = (TextView)findViewById(R.id.txtStatus);
		txtBattery = (TextView)findViewById(R.id.txtBattery);
		txtStatus = (TextView)findViewById(R.id.txtst);
		txtAltitude = (TextView)findViewById(R.id.txtAltitude);
		txtVelocity = (TextView)findViewById(R.id.txtVelocity);
				
		btnEmergency = (Button)findViewById(R.id.btnEmergency);
		
		btnLauncher = (ImageButton)findViewById(R.id.btnLauncher);
		imgBattery = (ImageView)findViewById(R.id.imgBattery);
		
		animButtons = new ImageButton[20];
		anim1 = (ImageButton)findViewById(R.id.anim1);
		animButtons[0] = (ImageButton)findViewById(R.id.anim1);
		animButtons[1] = (ImageButton)findViewById(R.id.anim2);
		animButtons[2] = (ImageButton)findViewById(R.id.anim3);
		animButtons[3] = (ImageButton)findViewById(R.id.anim4);
		animButtons[4] = (ImageButton)findViewById(R.id.anim5);
		animButtons[5] = (ImageButton)findViewById(R.id.anim6);
		animButtons[6] = (ImageButton)findViewById(R.id.anim7);
		animButtons[7] = (ImageButton)findViewById(R.id.anim8);
		animButtons[8] = (ImageButton)findViewById(R.id.anim9);
		animButtons[9] = (ImageButton)findViewById(R.id.anim10);
		animButtons[10] = (ImageButton)findViewById(R.id.anim11);
		animButtons[11] = (ImageButton)findViewById(R.id.anim12);
		animButtons[12] = (ImageButton)findViewById(R.id.anim13);
		animButtons[13] = (ImageButton)findViewById(R.id.anim14);
		animButtons[14] = (ImageButton)findViewById(R.id.anim15);
		animButtons[15] = (ImageButton)findViewById(R.id.anim16);
		animButtons[16] = (ImageButton)findViewById(R.id.anim17);
		animButtons[17] = (ImageButton)findViewById(R.id.anim18);
		animButtons[18] = (ImageButton)findViewById(R.id.anim19);
		animButtons[19] = (ImageButton)findViewById(R.id.anim20);
	}
	
	private void initListeners(){
		btnLauncher.setOnClickListener(this);
		btnFlip.setOnClickListener(this);
		btnDance.setOnClickListener(this);
		btnDance2.setOnClickListener(this);
		btnEmergency.setOnClickListener(this);
		anim1.setOnClickListener(this);
		
		for(int i = 0; i < 20; i++){
			animButtons[i].setOnClickListener(this);
		}
	}
	
	private void registerReceivers(){
		registerReceiver(networkChangedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
	}
	
	private void unregisterReceivers(){
		unregisterReceiver(networkChangedReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceivers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceivers();
	}

	@Override
	protected void onDestroy() {
		try {
			if(isFlying){
				drone.land();
			}
			drone.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	private void startVideo(){
		surfaceView = (SurfaceView)findViewById(R.id.surface);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.RGBA_8888);
		extras = getIntent().getExtras();
		
		
	}
	
	private void connect(){
		try{
			drone = new ARDrone();
			drone.connect();
			drone.connectNavData();
			setListeners();
			drone.start();
			drone.launchLedsAnimation(3, 5, 2);
			initListeners();
			txtConnection.setTextColor(Color.GREEN);
			//txtConnection.setText(R.string.connected);
		} catch(Exception e){
			txtConnection.setTextColor(Color.RED);
			//txtConnection.setText(R.string.connectionError);
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnLauncher:
			if(!isFlying){
				btnLauncher.destroyDrawingCache();
				btnLauncher.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.land));
				drone.takeOff();
			} else{
				btnLauncher.destroyDrawingCache();
				btnLauncher.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.take_off));
				drone.land();
			}
			break;
			
		case R.id.btnEmergency:
			drone.emergeny();
			break;
			
		case R.id.anim1:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_PHI_M30_DEG);
				Toast.makeText(this, "Animating ANIM_PHI_M30_DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim2:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_PHI_30_DEG);
				Toast.makeText(this, "Animating ANIM_PHI_30_DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim3:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_THETA_M30_DEG);
				Toast.makeText(this, "Animating ANIM_THETA_M30_DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim4:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_THETA_30_DEG);
				Toast.makeText(this, "Animating ANIM_THETA_30_DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim5:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_THETA_20DEG_YAW_200DEG);
				Toast.makeText(this, "Animating ANIM_THETA_20DEG_YAW_200DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim6:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_THETA_20DEG_YAW_M200DEG);
				Toast.makeText(this, "Animating ANIM_THETA_20DEG_YAW_M200DEG", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim7:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_TURNAROUND);
				Toast.makeText(this, "Animating ANIM_TURNAROUND", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim8:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_TURNAROUND_GODOWN);
				Toast.makeText(this, "Animating ANIM_TURNAROUND_GODOWN", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim9:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_YAW_SHAKE);
				Toast.makeText(this, "Animating ANIM_YAW_SHAKE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim10:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_YAW_DANCE);
				Toast.makeText(this, "Animating ANIM_YAW_DANCE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim11:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_PHI_DANCE);
				Toast.makeText(this, "Animating ANIM_PHI_DANCE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim12:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_THETA_DANCE);
				Toast.makeText(this, "Animating ANIM_THETA_DANCE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim13:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_VZ_DANCE);
				Toast.makeText(this, "Animating ANIM_VZ_DANCE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim14:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_WAVE);
				Toast.makeText(this, "Animating ANIM_WAVE", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim15:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_PHI_THETA_MIXED);
				Toast.makeText(this, "Animating ANIM_PHI_THETA_MIXED", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim16:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_DOUBLE_PHI_THETA_MIXED);
				Toast.makeText(this, "Animating ANIM_DOUBLE_PHI_THETA_MIXED", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim17:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_FLIP_AHEAD);
				Toast.makeText(this, "Animating ANIM_FLIP_AHEAD", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim18:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_FLIP_BEHIND);
				Toast.makeText(this, "Animating ANIM_FLIP_BEHIND", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim19:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_FLIP_LEFT);
				Toast.makeText(this, "Animating ANIM_FLIP_LEFT", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.anim20:
			if(isFlying){
				drone.launchAnimation(Animation.ARDRONE_ANIM_FLIP_RIGHT);
				Toast.makeText(this, "Animating ANIM_FLIP_RIGHT", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	@Override
	public void onNetworkChanged(NetworkInfo info) {
		if(!info.isConnected()){
			txtConnection.setTextColor(Color.RED);
			//txtConnection.setText(R.string.connectionLost);			
		}else{
			droneAvailabilityTask = new DroneAvailabilityTask(){
				
				@Override
				protected void onPostExecute(Boolean result){
					if(result){
						txtConnection.setTextColor(Color.GREEN);
						//txtConnection.setText(R.string.connected);
					}
				}
			};
			
			droneAvailabilityTask.executeOnExecutor(DroneAvailabilityTask.THREAD_POOL_EXECUTOR, this);
		}
		
	}

	private void setListeners(){
		drone.setAltitudeListener(this);
		drone.setBatteryListener(this);
		drone.setStateListener(this);
		drone.setVelocityListener(this);
	}
	
	@Override
	public void onBatteryLevelChanged(final int level) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				txtBattery.setText("" + level +  "%");
			}
		});
		
		if(level > 95){
			changeBatteryImage(R.drawable.battery_5);
		} else if(level > 80){
			changeBatteryImage(R.drawable.battery_4);
		} else if(level > 60){
			changeBatteryImage(R.drawable.battery_3);
		} else if(level > 40){
			changeBatteryImage(R.drawable.battery_2);
		} else if(level > 20){
			changeBatteryImage(R.drawable.battery_1);
		} else if(level < 20){
			changeBatteryImage(R.drawable.battery_0);
		}
	}

	public void changeBatteryImage(final int id){			
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				imgBattery.destroyDrawingCache();
				imgBattery.setImageBitmap(BitmapFactory.decodeResource(getResources(), id));
			}
		});
	}
	
	@Override
	public void onStateChanged(final DroneState state) {
		updateUI(state);
		
	}
	
	public void updateUI(DroneState state){
		if(state.isFlying()){
			isFlying = true;
		}else{
			isFlying = false;
		}
		
		if(state.isBatteryLow()){	
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					txtStatus.setTextColor(Color.RED);
					txtStatus.setText(R.string.batteryLow);
				}
			});
		}
		
		if(state.isAngelsOutOfRange()){
			
		}
	}
	
	@Override
	public void onVelocityChanged(final float vX, final float vY, final float vZ) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				double x= Math.round(vX);
				double y= Math.round(vY);
				double z= Math.round(vZ);
				long vGeneralkm = Math.round(Math.sqrt(x*x + y*y + z*z) * 0.0036); // km / h
				vGeneralkm = Integer.valueOf(String.valueOf(vGeneralkm));
				long vGeneralm = Math.round(Math.sqrt(x*x + y*y + z*z) * 3.6); // m / h
				vGeneralm = Integer.valueOf(String.valueOf(vGeneralm));
				txtVelocity.setText("Velocity: " + vGeneralkm + "," + vGeneralm);
				
			}
		});
	}

	@Override
	public void onAttitudeReceived(float pitch, float roll, float yaw) {
		//TODO find out if we can make use of this
	}
	
	//FIXME put an img to screen for altitude
	@Override
	public void onAltitudeChanged(final int altitude) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				txtAltitude.setText("Altitude: " + altitude);
				
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		//Left unimplemented
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		//Left unimplemented		
	}
	
	private void playVideo(){
		mediaPLayer = new MediaPlayer(this);
		
		try{
			mediaPLayer.setDataSource(videoPath);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		mediaPLayer.setDisplay(surfaceHolder);
		mediaPLayer.prepareAsync();
		mediaPLayer.setOnBufferingUpdateListener(this);
		mediaPLayer.setOnCompletionListener(this);
		mediaPLayer.setOnPreparedListener(this);
		mediaPLayer.setOnVideoSizeChangedListener(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		if(width == 0 || height == 0){
			return;
		}
		
		isVideoSizeKnown = true;
		videoWidth = width;
		videoHeight = height;
		
		if(isVideoReady & isVideoSizeKnown){
			startVideoPlayback();
		}
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		isVideoReady = true;
		if(isVideoReady && isVideoSizeKnown){
			startVideoPlayback();
		}
	}

	private void startVideoPlayback(){
		surfaceHolder.setFixedSize(videoWidth, videoHeight);
		mediaPLayer.start();
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		//Left unimplemented
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		//Left unimplemented
	}

}

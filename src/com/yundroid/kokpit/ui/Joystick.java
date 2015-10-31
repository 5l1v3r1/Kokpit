package com.yundroid.kokpit.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class Joystick {

	public enum Directions{
		NONE,
		UP,
		UP_RIGHT,
		RIGHT,
		DOWN_RIGHT,
		DOWN,
		DOWN_LEFT,
		LEFT,
		UP_LEFT;
	}
	
	private OnJoystickMovedListener listener;
	
	private LayoutParams layoutParams;
	private ViewGroup 	 viewGroup;
	private Context 	 context;
	
	private Directions directon;
	
	private Bitmap 	knob;
	private Bitmap 	bg;
	
	private Drawer 	drawer;
	private Paint	paint;
	
	private int	knobW;
	private int knobH;
	
	private int posX = 0;
	private int posY = 0;
	private int minDistance  = 0;
	
	private int alphaKnob		= 50;
	private int alphaJoystick	= 50;
	private int offset			= 0;
	
	private float distance 	= 0;
	private float angle		= 0;	 
	
	private boolean isTouched = false;

	
	public Joystick(Context context, ViewGroup viewGroup, int knobID){
		this.context = context;
		
		knob 	= BitmapFactory.decodeResource(context.getResources(), knobID);
		
		knobW 	= knob.getWidth();
		knobH 	= knob.getHeight();
		
		drawer 	= new Drawer(context);
		paint 	= new Paint();
		
		this.viewGroup = viewGroup; 
		
		layoutParams = viewGroup.getLayoutParams();
	}
	
	public void drawKnob(MotionEvent event){
		posX = (int)(event.getX() - (layoutParams.width/ 2 ));
		posY = (int)(event.getY() - (layoutParams.height/ 2));
		
		distance = (float)Math.sqrt(Math.pow(posX, 2) + Math.pow(posY, 2));
		angle = (float)calculateAngle(posX, posY);
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(distance <= (layoutParams.width/ 2)){
				drawer.position(event.getX(), event.getY());
				draw();
				notifyListener(posX, -posY);
				isTouched = true;
			}
		} else if(event.getAction() == MotionEvent.ACTION_MOVE && isTouched){
			if(distance <= (layoutParams.width / 2) - offset){
				drawer.position(event.getX(), event.getY());
				draw();
				notifyListener(posX, -posY);
			} else if(distance > (layoutParams.width / 2) - offset){
				float x = (float)(Math.cos(Math.toRadians(calculateAngle(posX, posY))) * ((layoutParams.width / 2) - offset));
				float y = (float)(Math.sin(Math.toRadians(calculateAngle(posX, posY))) * ((layoutParams.height / 2) - offset));
				
				x += (layoutParams.width / 2);
				y += (layoutParams.height / 2);
				
				drawer.position(x, y);
				draw();
				notifyListener(posX, -posY);
			} else{
				viewGroup.removeView(drawer);
			}
		} else if(event.getAction() == MotionEvent.ACTION_UP){
			viewGroup.removeView(drawer);
			isTouched = false;
			notifyListener(0, 0);
		}
	}
	
	public int [] getPosition(){
		if(distance > minDistance && isTouched){
			return new int[] { posX, posY};
		}
		return new int[] {0, 0};
	}
	
	public int getX(){
		if(distance > minDistance && isTouched){
			return posX;
		}
		return 0;
	}
	
	public int getY(){
		if(distance > minDistance && isTouched){
			return posY;
		}
		return 0;
	}
	
	public float getAngle(){
		if(distance > minDistance && isTouched){
			return angle;
		}
		return 0;
	}
	
	public float getDistance(){
		if(distance > minDistance && isTouched){
			return distance;
		}
		return 0;
	}
	
	public void setMinDistance(int minDistance){
		this.minDistance = minDistance;
	}
	
	public int getMinDistance(){
		return minDistance;
	}
	
	public Directions getInterCardinalDirections(){
		if(distance > minDistance && isTouched){
			if(angle >= 247.5 && angle < 292.5)
				return Directions.UP;
			else if(angle >= 292.5 && angle < 337.5)
				return Directions.UP_RIGHT;
			else if(angle >= 337.5 || angle < 22.5)
				return Directions.RIGHT;
			else if(angle >= 22.5 && angle < 67.5)
				return Directions.DOWN_RIGHT;
			else if(angle >= 67.5 && angle < 112.5)
				return Directions.DOWN;
			else if(angle >= 112.5 && angle < 157.5)
				return Directions.DOWN_LEFT;
			else if(angle >= 157.5 && angle < 202.5)
				return Directions.LEFT;
			else if(angle >= 202.5 && angle < 247.5)
				return Directions.UP_LEFT;
		} else if(distance <= minDistance && isTouched){
			return Directions.NONE;
		}
		return Directions.NONE;
	}
	
	public Directions getCardinalDirections(){
		if(distance > minDistance && isTouched){
			if(angle >= 225 && angle < 315)
				return Directions.UP;
			else if(angle >= 315 || angle < 45)
				return Directions.RIGHT;
			else if(angle >= 45 && angle < 135)
				return Directions.DOWN;
			else if(angle >= 135 && angle < 225)
				return Directions.LEFT;			
		} else if(distance <= minDistance && isTouched)
			return Directions.NONE;
		
		return Directions.NONE;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public void setAlphaKnob(int alphaKnob){
		this.alphaKnob = alphaKnob;
	}
	
	public int getAlphaKnob(){
		return alphaKnob;
	}
	
	public void setAlphaJoystick(int alphaJoystick){
		this.alphaJoystick = alphaJoystick;
	}
	
	public int getAlphaJoystick(){
		return alphaJoystick;
	}
	
	public void setKnobSize(int width, int height){
		knob = Bitmap.createScaledBitmap(knob, width, height, false);
		knobW = knob.getWidth();
		knobH = knob.getHeight();
		
	}
	
	public void setKnobWidth(int width){
		knob = Bitmap.createScaledBitmap(knob, width, knobH, false);
		knobW = knob.getWidth();
	}
	
	public void setKnobHeight(int height){
		knob = Bitmap.createScaledBitmap(knob, knobW, height, false);
		knobH = knob.getHeight();
	}
	
	public int getKnobWidth(){
		return knobW;
	}
	
	public int getKnobHeight(){
		return knobH;
	}
	
	public void setJoystickSize(int width, int height){
		layoutParams.width = width;
		layoutParams.height = height;
	}
	
	public int getLayoutWidth(){
		return layoutParams.width;
	}
	
	public int getLayoutHeight(){
		return layoutParams.height;
	}
	
	
	private double calculateAngle(float x, float y){
		if(x >= 0 && y >= 0)
			return Math.toDegrees(Math.atan(y / x));
		else if(x < 0 && y >= 0)
	    	return Math.toDegrees(Math.atan(y / x)) + 180;
	    else if(x < 0 && y < 0)
	    	return Math.toDegrees(Math.atan(y / x)) + 180;
	    else if(x >= 0 && y < 0) 
	    	return Math.toDegrees(Math.atan(y / x)) + 360;
		return 0;
	}
	
	private void draw(){
		try{
			viewGroup.removeView(drawer);
		} catch(Exception e){
			
		}
		viewGroup.addView(drawer);
		
	}
	
	private void notifyListener(float x, float y){
		if(x == 0 && y == 0){
			listener.onReleased();
		} else{
			if(x < -25){
				x = -1.0F;
			} else if(x > 25){
				x = 1.0F;
			} else{
				x = 0;
			}
			 
			if(y < -25){
				y = -1.0F;
			} else if(y > 25){
				y = 1.0F;
			} else{
				y = 0;
			}
			listener.onMoved(x, y);
		}
		
	}

	public void setOnJoystickMovedListener(OnJoystickMovedListener listener){
		this.listener = listener;
	}
	
	private class Drawer extends View{

		float x;
		float y;
		
		public Drawer(Context context) {
			super(context);
		}
		
		public void onDraw(Canvas canvas){
			canvas.drawBitmap(knob, x, y, paint);
		}
		
		private void position(float x, float y){
			this.x = x - (knobW / 2);
			this.y = y - (knobH / 2); 
		}
		
	}
	
}

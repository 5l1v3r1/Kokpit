package com.yundroid.kokpit.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class EnhancedGestureDetector extends GestureDetector{

	private static final int DOUBLE_TAP_TIMESTAMP_DELTA = 200;
	private static final int COORDINATE_DELTA = 50;
	
	private long timestampLast;
	
	private float xLast;
	private float yLast;
	
	private OnDoubleTapListener listener;
	
	public EnhancedGestureDetector(Context context, OnGestureListener listener) {
		super(context, listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if(ev.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
			long currentTimestamp = ev.getEventTime();
			
			if(ev.getPointerCount() > 1){
				if(currentTimestamp - timestampLast 
						< DOUBLE_TAP_TIMESTAMP_DELTA &&
						Math.abs(ev.getX(1) - xLast) < COORDINATE_DELTA &&
						Math.abs(ev.getY(1) - yLast) < COORDINATE_DELTA){
					if(listener != null){
						return listener.onDoubleTap(ev);
					}
				}
				
				xLast = ev.getX(1);
				yLast = ev.getY(1);
				
				timestampLast = ev.getEventTime();
					
			}
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
		super.setOnDoubleTapListener(onDoubleTapListener);
		
		this.listener = onDoubleTapListener;
	}
	
	

}

package com.yundroid.kokpit.commands;

public enum UltrasoundFrequency
{
	ULTRASOUND_22Hz(7),
	ULTRASOUND_25Hz(8);  
  
	private int value;
  
	private UltrasoundFrequency(int value){
		this.value = value;
	}
  
	public int getValue(){
		return this.value;
	}
}

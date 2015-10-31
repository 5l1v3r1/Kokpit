package com.yundroid.kokpit.commands;

public enum EnemyColor {
	ORANGE_GREEN(0x10),
	YELLOW_BLUE(0x11);
	
	private int value;
	
	private EnemyColor(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}

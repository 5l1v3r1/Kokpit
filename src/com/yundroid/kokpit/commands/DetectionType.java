package com.yundroid.kokpit.commands;

import com.yundroid.kokpit.navdata.NavDataException;


public enum DetectionType{
	
		CAD_TYPE_NONE(3),
		CAD_TYPE_ORIENTED_COCARDE_BW(5),
		CAD_TYPE_MULTIPLE_DETECTION_MODE(10),
		CAD_TYPE_VISION_V2(13);	  
	  
		private int value;
	  
		private DetectionType(int value){
			this.value = value;
		}
	  
		public static DetectionType fromInt(int value) throws NavDataException{
	    
			switch (value){
			default: 
				throw new NavDataException("Invalid vision tag type " + value);
			case 3: 
				return CAD_TYPE_NONE;
			case 5: 
				return CAD_TYPE_ORIENTED_COCARDE_BW;
			case 10: 
				return CAD_TYPE_MULTIPLE_DETECTION_MODE;
			}
	  }
	  
	  public int getValue()
	  {
	    return this.value;
	  }

}

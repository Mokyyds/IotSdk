package com.iotsdk.error;

import android.content.Context;


public class KinderError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	private Context context;
	
	/**
	 * sd卡不可用
	 */
    public static final int ERROR_SDCARD_UNAVAILABLE= -11;
	
	
	public KinderError(int errorCode, String errormsg)  {
		super(errormsg);
		this.errorCode = errorCode;
	}
	
	
	public KinderError(int errorCode){
		this.errorCode=errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
		
	
}

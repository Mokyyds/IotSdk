package com.iotsdk.application;

import android.app.Application;

public class IotSdkAppliction extends Application{
	public static IotSdkAppliction app;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app=this;
	}

}

package com.iostdk.utils;

import com.iotsdk.application.IotSdkAppliction;
import com.iotsdk.error.IotSdkException;



public class Utils {
	/**
	 * �?��sdcard是否可用
	 * 
	 * @throws IotSdkAppliction
	 * 
	 * @throws SdcardNotAvailableException
	 */
	public static void checkSdcard() throws IotSdkException {
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			throw new IotSdkException("sdcad error");
		}
	}

	public static boolean isSdcardEnable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}

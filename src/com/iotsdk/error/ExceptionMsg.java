package com.iotsdk.error;

import java.util.HashMap;
import java.util.Map;

import com.iot.iotsdk.R;



import android.content.Context;

public class ExceptionMsg {

	private Map<String, String> errmsgMap;

	private final static ExceptionMsg MSG = new ExceptionMsg();

	public static ExceptionMsg getInstance() {
		return MSG;
	}

	private ExceptionMsg() {
		errmsgMap = new HashMap<String, String>();
	}

	public void createErrorMsgMap(Context context) {
		String[] msg = context.getResources().getStringArray(R.array.error_msg);
		String[] msgCode = context.getResources().getStringArray(
				R.array.error_msg_code);

		int length = Math.min(msg.length, msgCode.length);
		for (int i = 0; i < length; i++) {
			errmsgMap.put(msgCode[i], msg[i]);
		}
	}

	public String getMsg(String msgCode) {
		return errmsgMap.containsKey(msgCode) ? errmsgMap.get(msgCode) : null;
	}

}

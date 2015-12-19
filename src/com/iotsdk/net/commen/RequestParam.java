package com.iotsdk.net.commen;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.iostdk.utils.Utils;



import android.os.Bundle;
import android.util.Log;


public abstract class RequestParam implements RequestConfig, Serializable {

	private static final long serialVersionUID = 1L;

	private final static String TAG = "RequestParam";
	// client_secret
	// private static String client_secret = Kinder.CLIENT_SECRET;

	private String api_sign;
	private String decodUrl;

	private final Map<String, Object> params = new HashMap<String, Object>();

	/**
	 * 璇锋眰鍙傛暟
	 * 
	 * @return
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	public void addParam(String key, String value) {
		if (!params.containsKey(key))
			params.put(key, value);
	}

	public void addParam(String key, int value) {
		if (!params.containsKey(key))
			params.put(key, value);
	}

	public void addParam(String key, long value) {
		if (!params.containsKey(key))
			params.put(key, value);
	}

	/**
	 * 璇锋眰鏂瑰紡
	 * 
	 * @return
	 */
	abstract public String geMethod();

	/**
	 * 璇锋眰鍦板潃
	 * 
	 * @return
	 */
	abstract public String getRequestUrl();

	public String decod() {
		if (decodUrl != null)
			return decodUrl;

		final Map<String, Object> params = this.params;

		Set<String> keySet = params.keySet();
		String[] keys = new String[keySet.size()];
		keySet.toArray(keys);
		Arrays.sort(keys);

		StringBuffer sign_strBuff = new StringBuffer();
		StringBuffer queryStringBuffer = new StringBuffer();
		StringBuffer paramStrBuff = new StringBuffer();
		StringBuffer urlEncodeBuff = new StringBuffer();
		for (String key : keys) {
			paramStrBuff.setLength(0);
			Object oValue = params.get(key);
			paramStrBuff.append(key).append("=").append(oValue);
			if (oValue != null && !oValue.toString().equals("")) {
				sign_strBuff.append(paramStrBuff);
			}
			queryStringBuffer.append(paramStrBuff).append("&");
		}
		sign_strBuff.append(RequestConfig.CLIENT_SECRET);
		Log.v(TAG, geMethod() + ":" + getRequestUrl());
		Log.v(TAG, "befor--" + new String(sign_strBuff));
		api_sign = NetUtils.md5(new String(sign_strBuff));

		queryStringBuffer.append("api_sign").append("=").append(api_sign);
		// Log.v("decod", new String(queryStringBuffer));

		// oValue闇?杩涜URLEncode缂栫爜
		for (String key : keys) {
			paramStrBuff.setLength(0);
			Object oValue = params.get(key);
			String sValue = "";
			if (oValue != null) {
				sValue = oValue.toString();
			}
			// 涓嶆嫾鎺ョ┖鍙傛暟
			if (sValue.equals(""))
				continue;
			paramStrBuff.append(key).append("=")
					.append(URLEncoder.encode(sValue));
			urlEncodeBuff.append(paramStrBuff).append("&");
		}
		urlEncodeBuff.append("api_sign").append("=").append(api_sign);
		decodUrl = new String(urlEncodeBuff);

		return decodUrl;
	}

	public String getApiSign() {
		return api_sign;
	}

	public void compute() {
	}

}

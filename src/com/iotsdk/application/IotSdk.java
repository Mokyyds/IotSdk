package com.iotsdk.application;

import com.iostdk.utils.Utils;
import com.iotsdk.error.IotSdkException;
import com.iotsdk.error.UnKnownNetwork;
import com.iotsdk.net.commen.NetUtils;
import com.iotsdk.net.commen.RequestParam;
import com.iotsdk.net.commen.file.FileFormRequest;

import android.content.Context;
import android.content.SharedPreferences;


public class IotSdk {
	private final String client_id /* = CLIENT_ID */;
	private final String client_secret /* = CLIENT_SECRET */;
	private final String client_version /* = CLIENT_VERSION */;
	private final String device_sn;
	private  String uid;

	private static String session_key;

	/**
	 * 
	 * @param context
	 */
	public IotSdk(String asession_key, String uid, String client_id,
			String client_secret, String client_version, String device_sn) {

		this.client_id = client_id;
		this.client_secret = client_secret;
		this.client_version = client_version;
		this.uid = uid;
		session_key = asession_key;
		this.device_sn = device_sn;
	}

	public String getClient_id() {
		return client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public String getClient_version() {
		return client_version;
	}

	public String getDevice_sn() {
		return device_sn;
	}

	public String getSession_key() {
		return session_key;
	}

	public Context getApplicationContext() {
		return IotSdkAppliction.app;
	}

	public int lastts() {

		SharedPreferences share =IotSdkAppliction.app.getApplicationContext()
				.getSharedPreferences("msgconfig", 0);
		return share.getInt("lastts" + uid, 0);
	}

	public void updataLastts(int lastts) {

		SharedPreferences share = IotSdkAppliction.app.getApplicationContext()
				.getSharedPreferences("msgconfig", 0);
		share.edit().putInt("lastts" + uid, lastts).commit();
	}

	/**
	 * 
	 * 除了{@link RegistParam} 请求前需 {@link #addSession(RequestParam)}
	 * 
	 * @param param
	 * @return
	 * @throws Throwable
	 */
	public String request(RequestParam param) throws IotSdkException {

		preparSysParams(param);
		String res = null;
		try {

			if (param instanceof FileFormRequest) {
				res = NetUtils.formRequest((FileFormRequest) param,
						IotSdkAppliction.app);
			} else {
				res = NetUtils.openUrl(IotSdkAppliction.app, param.getRequestUrl(),
						param.decod(), param.geMethod());
			}
		} catch (IotSdkException e) {
			if (!NetUtils.checkNetworkAvailable(IotSdkAppliction.app)) {
				throw new UnKnownNetwork();
			} else {
				throw e;
			}
		}
		return res;
	}

	/**
	 * 添加session
	 * 
	 * @param param
	 */
	public void preparSession(RequestParam param) {
		if (session_key != null) {
			param.addParam("session_key", session_key);
		}
	}

	private void preparSysParams(RequestParam param) {
		param.addParam("api_time",
				String.valueOf(System.currentTimeMillis() / 1000));
		param.addParam("client_id", client_id);
		param.addParam("client_version", client_version);
		// param.addParam("client_secret", client_secret);
		param.addParam("device_sn", device_sn);
	}

	public String getUid() {
		return this.uid;
	}

	public String getSession() {
		return this.session_key;
	}

	public boolean isSessionAvailable() {
		return session_key != null && !session_key.equals("");
	}

	@Override
	public String toString() {
		return "Kinder [client_id=" + client_id + ", client_secret="
				+ client_secret + ", client_version=" + client_version
				+ ", device_sn=" + device_sn + ", uid=" + uid
				+ ", session_key=" + session_key + "]";
	}

}


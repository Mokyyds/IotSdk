package com.iotsdk.net.commen;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.iostdk.utils.Utils;
import com.iot.iotsdk.R;
import com.iotsdk.config.SdkConfig;
import com.iotsdk.error.IotSdkException;
import com.iotsdk.net.commen.file.FileFormRequest;
import com.iotsdk.net.commen.file.FileFormRequest.FILE_UP_STATE;
import com.iotsdk.net.commen.file.FileFormRequest.FileMode;
import com.iotsdk.net.commen.file.UpLoadFileAble;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



public class NetUtils {

	private final static int CTIMEOUT = 30 * 1000;
	private final static int RTIMEOUT = 60 * 1000;
	private static AppOperateListener appOperateListener;

	private final static String SESSION_KEY_INVALID = "SESSION_KEY_INVALID";
	private final static String GONE = "GONE";

	public static String openUrl(Context c, String url, String decodParams,
			String method) throws IotSdkException {
		Log.v(SdkConfig.TAG, "request" + method + ":" + url + "?" + decodParams);
		if (RequestParam.GET.equals(method)) {
			url = url + "?" + decodParams;
		}
		String response = "";
		String enterNewline = "\r\n";
		try {
			FakeX509TrustManager.allowAllSSL();
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			/*
			 * KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
			 * keyStore.load(c.getAssets().open("smime.p7s"), null);
			 * 
			 * TrustManagerFactory tmf =
			 * TrustManagerFactory.getInstance("X509"); tmf.init(keyStore);
			 * 
			 * SSLContext context = SSLContext.getInstance("TLS");
			 * context.init(null, tmf.getTrustManagers(), null);
			 * 
			 * conn.setSSLSocketFactory(context.getSocketFactory());
			 */
			// conn.setRequestProperty("Connection", "keep-alive");
			conn.setConnectTimeout(CTIMEOUT);
			conn.setReadTimeout(RTIMEOUT);
			conn.setDoInput(true);
			if (!RequestParam.GET.equals(method)) {

				conn.setDoOutput(true);
				// 此方法在正式链接之前设置才有效。
				conn.setRequestMethod("POST");
				conn.setUseCaches(false);
				conn.setDoOutput(true);

				conn.getOutputStream().write(decodParams.getBytes("UTF-8"));
			}
			conn.connect();
			InputStream is = null;
			int responseCode = conn.getResponseCode();
			if (responseCode >= 200 && responseCode < 300) {
				is = conn.getInputStream();
			} else {
				is = conn.getErrorStream();
			}
			response = read(is);
			conn.disconnect();
			Log.v(SdkConfig.TAG, "res:" + response);
			NetUtils.checkResponse(response);
		} catch (IotSdkException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException) {
				throw new IotSdkException(0, "0",
						c.getString(R.string.sdk_network_error));
			} else if (e instanceof UnknownHostException) {
				throw new IotSdkException(0, "0",
						c.getString(R.string.sdk_network_error));
			} else {
				throw new IotSdkException(0, "0", e.getMessage());
			}

		}
		return response;
	}

	public static String openss() {
		HttpClient c = new DefaultHttpClient();
		try {
			HttpPost p = new HttpPost();
			c.execute(new HttpGet());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String formRequest(FileFormRequest param, Context context)
			throws IotSdkException {
		// 开始上传
		param.compute();
		param.setState(FILE_UP_STATE.PROCESSING);

		String response = null;
		try {
			String enterNewline = "\r\n";
			String fix = "--";
			String boundary = "######";
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(param.getRequestUrl());

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(CTIMEOUT);
			con.setReadTimeout(RTIMEOUT);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ ";boundary=" + boundary);

			// ***处理参数，添加api_sign****************/
			final Map<String, Object> params = new HashMap<String, Object>();
			params.putAll(param.getParams());
			param.decod();
			params.put("api_sign", param.getApiSign());
			Set<String> keySet = params.keySet();
			Iterator<String> it = keySet.iterator();
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			// ****参数写入流********************/
			while (it.hasNext()) {
				String key = it.next();
				if (params.get(key) == null)
					continue;
				String value = params.get(key).toString().trim();
				ds.writeBytes(fix + boundary + enterNewline);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\""
						+ key + "\"" + enterNewline);
				ds.writeBytes(enterNewline);
				byte[] by = value.getBytes("UTF-8");
				ds.write(by);
				ds.writeBytes(enterNewline);
			}

			// ***写入文件****************/

			final UpLoadFileAble[] upLoadFiles = param.getUploadFiles();

			final int upLoadFileCount = upLoadFiles != null ? upLoadFiles.length
					: 0;

			String fileForm = null;
			if (FileMode.MULTIFILES == param.fileMode()) {
				fileForm = "Content-Disposition: form-data; "
						+ "name=\"uploadfile[]\"; filename=\"%1$s\" \r\n "
						+ "Content-Type: %2$s\r\n"
						+ "Content-Transfer-Encoding: Binary\r\n\r\n";
			} else if (FileMode.SINIGLEFILE == param.fileMode()) {
				fileForm = "Content-Disposition: form-data; "
						+ "name=\"uploadfile\"; filename=\"%1$s\" \r\n "
						+ "Content-Type: %2$s\r\n"
						+ "Content-Transfer-Encoding: Binary\r\n\r\n";
			}

			if (upLoadFileCount > 0) {
				for (UpLoadFileAble uf : upLoadFiles) {

					ds.writeBytes(fix + boundary + enterNewline);
					ds.writeBytes(String.format(fileForm, uf.getName(),
							uf.getContentType()));

					InputStream fis = uf.getContent();
					if (fis == null)
						continue;
					int len = 0;
					byte[] buf = new byte[1024];
					while ((len = fis.read(buf)) > 0) {
						ds.write(buf, 0, len);
						// 写入文件进度
						param.writeLength(len);
					}
					ds.writeBytes(enterNewline);
				}
			}
			// **写文件完成/

			ds.writeBytes(fix + boundary + fix + enterNewline);
			System.out.println(fix + boundary + fix + enterNewline);
			ds.flush();
			ds.close();
			InputStream is = null;
			int responseCode = con.getResponseCode();
			if (responseCode >= 200 && responseCode < 300) {
				is = con.getInputStream();
			} else {
				is = con.getErrorStream();
			}
			response = read(is);
			Log.v(SdkConfig.TAG, "fileUpRes:" + response);
			NetUtils.checkResponse(response);
			// 上传文件成功
			param.setState(FILE_UP_STATE.DONE);
		} catch (IotSdkException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			param.setState(FILE_UP_STATE.FAILED);
			if (e instanceof SocketTimeoutException) {
				throw new IotSdkException(0, "0",
						context.getString(R.string.sdk_network_error));
			} else {
				throw new IotSdkException(0, "0", e.getMessage());
			}
		}
		return response;
	}

	/**
	 * inputString to String
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	public static void checkResponse(String response) throws IotSdkException {

		if (response == null) {
			throw new IotSdkException(0, "0", "unknow exception null response!");
		} else {
			try {
				JSONObject json = new JSONObject(response);
				int status = json.getInt("status");
				if (!(status >= 200 && status < 300)) {
					String code = json.getString("code");
					String msg = json.getString("msg");
					Log.e(SdkConfig.TAG, "error status:" + status + "  code:"
							+ code + "   msg:" + msg);

					if (SESSION_KEY_INVALID.equals(msg)) {
						if (appOperateListener != null)
							appOperateListener.onSessionKeyInvalid();
					} else if (GONE.equals(msg)) {
						if (appOperateListener != null)
							appOperateListener.gone();
					}
					throw new IotSdkException(status, code, msg);
				}
			} catch (JSONException e) {
				throw new IotSdkException(0, IotSdkException.JSONO_ERROR,
						"服务器返回数据格式错误");
			}

		}
	}

	/**
	 * string to md5
	 * 
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
		if (string == null || string.trim().length() < 1) {
			return null;
		}
		try {
			return getMD5(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String getMD5(byte[] source) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void setOnAppOperateListener(
			AppOperateListener appOperateListener) {
		NetUtils.appOperateListener = appOperateListener;
	}

	/**
	 * 
	 * 应用操作接口
	 * 
	 * @author DuJun
	 * 
	 */
	public interface AppOperateListener {

		/**
		 * call when session key invalid, at this time application need relogin.
		 */
		public void onSessionKeyInvalid();

		/**
		 * 预留接口，为了服务器告诉app擦出本地数据
		 */
		public void gone();
	}

	public static boolean checkNetworkAvailable(Context context) {
		boolean isNetworkAvailable = false;
		android.net.ConnectivityManager connManager = (android.net.ConnectivityManager) context
				.getApplicationContext().getSystemService(
						android.content.Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			try {
				isNetworkAvailable = connManager.getActiveNetworkInfo()
						.isAvailable();
			} catch (java.lang.NullPointerException e) {

			}
		}
		return isNetworkAvailable;
	}

	/**
	 * 检查当前网络是否 是wifi
	 * 
	 * @return
	 */
	public static boolean checkIsWifi(Context context) {
		boolean isWifi = false;
		String type = "";
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				if (info.getTypeName() != null) {
					type = info.getTypeName().toLowerCase(); // WIFI/MOBILE
				}

				if (type.equalsIgnoreCase("wifi")) {
					isWifi = true;
				}
			}
		}

		return isWifi;
	}

	public static int CopySdcardFile(String fromFile, String toFile) {
		Log.d("Utils", "copy start frome" + fromFile + " to " + toFile);
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			Log.d("Utils", "copy done frome" + fromFile + " to " + toFile);
			return 0;
		} catch (Exception ex) {
			return -1;
		}
	}
}


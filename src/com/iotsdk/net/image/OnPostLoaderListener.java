package com.iotsdk.net.image;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.iotsdk.net.image.ImageLoader.LoaderProgess;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;



public abstract class OnPostLoaderListener implements LoaderProgess {

	private final static Handler handler = new Inte();

	private long threadId;

	private final static int START = 1;
	private final static int PROGESS = 2;
	private final static int DONE = 3;
	private final static int ERROR = 4;

	/**
	 * 在UI线程创建监听器,异步结果可返回到UI线程
	 */
	public OnPostLoaderListener() {
		threadId = Thread.currentThread().getId();
		if (Looper.getMainLooper().getThread().getId() != threadId)
			throw new RuntimeException("为了确保该方法正确执行，请在UI线程中创建该对象");
	}

	@Override
	public void onStrat(String key) {
		if (Thread.currentThread().getId() != threadId) {
			Message msg = new Message();
			msg.what = START;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("listener", this);
			map.put("obj", key);
			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			onPostStart(key);
		}
	}

	@Override
	public void onProgess(String key, int progess) {
		if (Thread.currentThread().getId() != threadId) {
			Message msg = new Message();
			msg.what = PROGESS;
			msg.arg1 = progess;

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("listener", this);
			map.put("obj", key);
			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			onPostProgess(key, progess);
		}
	}

	@Override
	public void onLoaderDone(String key, File imageFile) {
		if (Thread.currentThread().getId() != threadId) {
			Message msg = new Message();
			msg.what = DONE;

			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("key", key);
			obj.put("imageFile", imageFile);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("listener", this);
			map.put("obj", obj);
			
			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			onPostLoaderDone(key, imageFile);
		}
	}

	@Override
	public void onLoaderError(String key, String error) {
		if (Thread.currentThread().getId() != threadId) {
			Message msg = new Message();
			msg.what = ERROR;
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("key", key);
			obj.put("error", error);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("listener", this);
			map.put("obj", obj);

			msg.obj = map;
			handler.sendMessage(msg);
		} else {
			onPostLoaderError(key, error);
		}
	}

	@SuppressWarnings("unchecked")
	static class Inte extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Map<String, Object> map = (Map<String, Object>) msg.obj;
			OnPostLoaderListener listener = ((OnPostLoaderListener) map
					.get("listener"));
			switch (msg.what) {
			case START:
				listener.onPostStart(map.get("obj").toString());
				break;
			case PROGESS:
				listener.onPostProgess(map.get("obj").toString(), msg.arg1);
				break;
			case DONE:
				Map<String, Object> obj = (Map<String, Object>) map.get("obj");
				listener.onPostLoaderDone(obj.get("key").toString(),
						(File) obj.get("imageFile"));
				break;
			case ERROR:
				Map<String, Object> eobj = (Map<String, Object>) map.get("obj");
				listener.onPostLoaderError(eobj.get("key").toString(),
						(String) eobj.get("error"));
				break;
			default:
				break;
			}
		}
	}

	public abstract void onPostStart(String key);

	public abstract void onPostProgess(String key, int progess);

	public abstract void onPostLoaderDone(String key, File imageFile);

	public abstract void onPostLoaderError(String key, String error);

}

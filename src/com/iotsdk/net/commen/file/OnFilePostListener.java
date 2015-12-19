package com.iotsdk.net.commen.file;

import java.util.HashMap;
import java.util.Map;

import com.iotsdk.net.commen.file.FileFormRequest.OnFileProcessListener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;



public abstract class OnFilePostListener implements OnFileProcessListener {

	private final static Handler handler = new InterHandler();
	private long id;

	public OnFilePostListener() {
		id = Thread.currentThread().getId();
		if (Looper.getMainLooper().getThread().getId() != id) {
			throw new RuntimeException("为了确保该方法正确执行，请在UI线程中创建该对象");
		}
	}

	@Override
	public void onProcess(FileFormRequest request) {
		if (Thread.currentThread().getId() == id) {
			onPostProcess(request);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("obj", request);
			map.put("listener", this);

			Message msg = new Message();
			msg.obj = map;
			handler.sendMessage(msg);
		}
	}

	private static class InterHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) msg.obj;
			((OnFilePostListener) map.get("listener"))
					.onPostProcess((FileFormRequest) map.get("obj"));
		}
	}

	public abstract void onPostProcess(FileFormRequest request);
}

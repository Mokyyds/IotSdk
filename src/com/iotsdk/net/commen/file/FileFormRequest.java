package com.iotsdk.net.commen.file;

import com.iotsdk.net.commen.RequestParam;

import android.util.Log;


/**
 * 文件表单方式上传请求参数
 * 
 * @author DuJun
 * 
 */
public abstract class FileFormRequest extends RequestParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum FileMode {
		NONEFLIE, SINIGLEFILE, MULTIFILES
	}

	public final static String IMAGE_CONTENT = "image/jpeg";
	public final static String ADUIO_CONTENT = "audio/amr";
	private final UpLoadFileAble[] upLoadFiles;
	/**
	 * 进度监听
	 */
	private OnFileProcessListener listener;

	/** 进度 */
	private long process;
	/** 简单的上传大小，只计算文件大小 */
	private long simpleSize;

	/** 文件上状态 */
	public enum FILE_UP_STATE {
		WAIT, PROCESSING, DONE, FAILED
	}

	private FILE_UP_STATE state;

	private int rate;

	public FileFormRequest(UpLoadFileAble[] uploadFiles) {
		this.upLoadFiles = uploadFiles;
		changeState(FILE_UP_STATE.WAIT);
	}

	@Override
	public void compute() {
		simpleSize();
	}

	@Override
	final public String geMethod() {
		return POST;
	}

	public UpLoadFileAble[] getUploadFiles() {
		return upLoadFiles;
	}

	private void simpleSize() {
		if (upLoadFiles == null) {
			return;
		}
		simpleSize = 0;
		for (UpLoadFileAble upf : upLoadFiles) {
			if (upf != null)
				simpleSize += upf.size();
		}
	}

	public void setOnFileProcessListener(OnFileProcessListener l) {
		listener = l;
	}

	public FILE_UP_STATE getState() {
		return state;
	}

	public int getProcessRate() {
		return rate;
	}

	private void computeProcessRate() {
		rate = 0;
		switch (state) {
		case WAIT:
			// 都为0
		case FAILED:
			rate = 0;
			break;
		case DONE:
			rate = 100;
			break;
		case PROCESSING:
			if (simpleSize <= 0) {
				rate = 100;
			} else {
				rate = (int) (process * 100 / simpleSize);
			}
			break;
		default:
			rate = 0;
			break;
		}
	}

	public void writeLength(long length) {
		process += length;
		onChanged();
	}

	public void reset() {
		process = 0;
		state = FILE_UP_STATE.WAIT;
		onChanged();
	}

	public void setState(FILE_UP_STATE state) {
		changeState(state);
	}

	private void changeState(FILE_UP_STATE state) {
		this.state = state;
		onChanged();
	}

	private void onChanged() {
		computeProcessRate();
		Log.v("FileFormRequest", "onchagne l:" + simpleSize + "   w:" + process
				+ "   r:" + rate);
		switch (state) {
		case DONE:
			Log.v("FileFormRequest", "state:" + "DONE");
			break;
		case FAILED:
			Log.v("FileFormRequest", "state:" + "FAILED");
			break;
		case PROCESSING:
			Log.v("FileFormRequest", "state:" + "PROCESSING");
			break;
		case WAIT:
			Log.v("FileFormRequest", "state:" + "WAIT");
			break;
		default:
			break;
		}
		if (listener != null)
			listener.onProcess(this);
	}

	public interface OnFileProcessListener {
		public void onProcess(FileFormRequest request);
	}

	public abstract FileMode fileMode();

}

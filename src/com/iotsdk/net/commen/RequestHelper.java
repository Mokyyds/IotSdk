package com.iotsdk.net.commen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.iotsdk.application.IotSdk;
import com.iotsdk.error.IotSdkException;


/**
 * 
 * <p>
 * 通讯请求辅助类，使用该辅助内每个接口至少实现�?��请求参数封装类{@link RequestParam}
 * </p>
 * <p>
 * 如关心返回结果，请继�?{@link ResponseBean}实现具体的解析，以及实现{@link #getResponseBean(String)}
 * 返回结果�?
 * </p>
 * 
 * @author DuJun
 * 
 * @param <T>请求参数�?
 *        <p>
 *        不同接口的具体请求参数封�?
 *        </p>
 * @param <R>返回结果�?
 *        <p>
 *        只关心成功�?失败（失败错误信�?可使用默认结果集{@link DefaultResponseBean}或构造虚�?
 *        {@link ResponseBean}
 *        </p>
 */
public abstract class RequestHelper<T extends RequestParam, R extends ResponseBean> {

	private final static ExecutorService pool = Executors.newFixedThreadPool(4);

	private IotSdk mIotSdk;

	private long lastRequestTime;

	private RequestListener<R> listener;

	private boolean processing = false;;

	public RequestHelper(IotSdk IotSdk) {
		this.mIotSdk = IotSdk;
	}

	/**
	 * 同步请求
	 * 
	 * @param param
	 * @return
	 * @throws IotSdkException
	 */
	public R request(T param) throws IotSdkException {
		lastRequestTime = System.currentTimeMillis();
		mIotSdk.preparSession(param);
		String res = mIotSdk.request(param);
		return getResponseBean(res);
	}
	
	
	/**
	 * 同步请求 并监听返�?
	 * 
	 * @param param
	 * @return
	 * @throws IotSdkException
	 */
	
	public void request(final T param, final RequestListener<R> listener) {
		this.listener = listener;
		if (this.listener != null)
			this.listener.onPostStart();
		processing = true;
		try {
			R responseBean = request(param);
			if (RequestHelper.this.listener != null)
				RequestHelper.this.listener.onComplete(responseBean);
		} catch (IotSdkException e) {
			e.printStackTrace();
			if (RequestHelper.this.listener != null)
				RequestHelper.this.listener.onError(e);
		}
		processing = false;
	}


	/**
	 * 请求结果�?
	 * 
	 * @param res
	 * @return
	 */
	public abstract R getResponseBean(String res);

	/**
	 * 异步请求
	 * 
	 * @param param
	 * @param listener
	 */
	public void asyncRequest(final T param, final RequestListener<R> listener) {
		this.listener = listener;
		if (this.listener != null)
			this.listener.onPostStart();
		processing = true;
		pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					R responseBean = request(param);
					if (RequestHelper.this.listener != null)
						RequestHelper.this.listener.onComplete(responseBean);
				} catch (IotSdkException e) {
					e.printStackTrace();
					if (RequestHelper.this.listener != null)
						RequestHelper.this.listener.onError(e);
				}
				processing = false;
			}
		});
	}

	/**
	 * 任务是否正在进行
	 * 
	 * @return
	 */
	public boolean isProcessing() {
		return processing;
	}

	public void setRequestListener(RequestListener<R> listener) {
		this.listener = listener;
	}

	public IotSdk getIotSdk() {
		return mIotSdk;
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public void reset() {
		lastRequestTime = 0;
	}
}

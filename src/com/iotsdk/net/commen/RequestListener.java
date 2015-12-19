package com.iotsdk.net.commen;

import com.iotsdk.error.IotSdkException;



/**
 * 
 * 请在UI线程创建监听�?
 * <p>
 * {@link #onPostStart()} 请在执行请求前调用UI线程
 * <p>
 * 可使用{@link onPostListenr} 线程安全
 * 
 * @author DuJun
 * 
 * @param <T>
 */
public interface RequestListener<T extends ResponseBean> {

	public void onPostStart();

	/**
	 * 注意不要直接在此回调中操作UI
	 * 
	 * @param responseBean
	 */
	public void onComplete(T responseBean);

	/**
	 * 注意不要直接在此回调中操作UI
	 * 
	 * @param error
	 */
	public void onError(IotSdkException error);

}

package com.iotsdk.net.commen;

/**
 * 
 * 接口返回结果�?
 * 
 * @author DuJun
 * 
 */
public abstract class ResponseBean {

	private String responseStr;

	/**
	 * 以json字符串构�?
	 * 
	 * @param responseStr
	 */
	public ResponseBean(String responseStr, Object... objects) {
		this.responseStr = responseStr;
		inintParseParams(objects);
		onParseJson();
	}

	/**
	 * 请求参数
	 * 
	 * @return
	 */
	public String getResponseStr() {
		return responseStr;
	}

	/**
	 * 解析json,无数据成功失败可不进行解�?
	 */
	abstract protected void onParseJson();

	protected void inintParseParams(Object... objects) {

	}
}

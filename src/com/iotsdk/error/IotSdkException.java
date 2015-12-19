package com.iotsdk.error;

public class IotSdkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String JSONO_ERROR = String.valueOf(0xff0001);

	private String msgCode;

	/**
	 * 
	 * @param status
	 * @param code
	 * @param msg
	 *            error message for show
	 */
	public IotSdkException(int status, String code, String msgCode) {
		super(getMsg(msgCode));
		this.msgCode = msgCode;
	}

	public IotSdkException(KinderError error) {
		super(error.getMessage());
	}

	public String getMsgCode() {
		return msgCode;
	}

	/**
	 * error message for show
	 * 
	 * @return
	 */
	public IotSdkException(String error) {
		super(error);

	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		return msg != null ? msg : "unKonwn error";
	}
	
	public String detailMsg() {
		return getMessage();
	}

	private static String getMsg(String msgCode) {
		String msg = ExceptionMsg.getInstance().getMsg(msgCode);
		return msg != null ? msg : msgCode;
	}
}

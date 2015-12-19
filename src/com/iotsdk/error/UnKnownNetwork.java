package com.iotsdk.error;

public class UnKnownNetwork extends IotSdkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static String NO_NECTWORK= "NO_NECTWORK";
	
	public UnKnownNetwork() {
		super(0, "", NO_NECTWORK);
	}

}

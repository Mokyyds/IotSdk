package com.iotsdk.net.commen.file;

import java.io.InputStream;
import java.io.Serializable;

public abstract class UpLoadFileAble implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract String getContentType();

	public abstract InputStream getContent();

	public abstract long size();

	public abstract String getName();

}

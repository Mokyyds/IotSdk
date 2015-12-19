package com.iotsdk.net.commen.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 
 * 上传文件
 * 
 * 
 * @author DuJun
 * 
 * @deprecated 照片上传使用{@link UploadPhoto}
 */
public class UpLoadFile extends UpLoadFileAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File file;
	private String contentType;

	/**
	 * 
	 * @param file
	 * @param contentType
	 *            {@link FileFormRequest#IMAGE_CONTENT}
	 */
	public UpLoadFile(File file, String contentType) {
		this.file = file;
		this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getContent() {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return in;
	}

	@Override
	public long size() {
		return file != null ? file.length() : 0;
	}

	@Override
	public String getName() {
		return file != null ? file.getName() : "null";
	}
	

}

package com.iotsdk.config;

import java.text.SimpleDateFormat;

import android.os.Environment;

public class SdkConfig {
 public static String TAG="sdk config";
 public static String IOSDK_SD_DB_DEFAULT_PATH="";
 public final static String SHAREDPR_ENAME = "kinder_sdk_config";
	
	public final static String ROOT = Environment.getExternalStorageDirectory()
			.getPath() + "/nuagesys/";

	public final static String THUMB_PATH = ROOT + "thumb/";
	public final static String PHOTO_TAKE_PATH = ROOT + "photo/";
	public final static String ADUIO_TAKE_PATH = ROOT + "aduio/";
	public final static String DATABASE_PATH = ROOT + "database/";
	public final static String APP_PATH = ROOT + "app/";
	public final static String PDF_PATH = ROOT + "pdf/";
	
	public final static SimpleDateFormat DEFAULT_DATA_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public final static String SKEY_LAST_READ_NOTICE = "last_read_notice_time:user%1$s";	
	
	public static final String NUAGE_PIC_JPG_SUFFIX      = ".jpg";
	/**
	 * 图片的压缩质量
	 */
	public static final int PHOTO_QUALITY = 80;
	/**
	 * 缩略图的压缩质量
	 */
	public static final int PHOTO_THUMBNAIL_QUALITY = 60;
	/**
	 * 缩略图默认大小
	 */
	public static final int MEDIA_THUMBNAIL_DEFAULT_WIDTH = 256;
	public static final int MEDIA_THUMBNAIL_DEFAULT_HEIGHT = 256;
	/**
	 * 相机拍照图默认大小
	 */
	public static final int MEDIA_DEFAULT_WIDTH = 640;
	public static final int MEDIA_DEFAULT_HEIGHT = 960;

}

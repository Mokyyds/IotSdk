package com.iotsdk.net.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import com.iotsdk.config.SdkConfig;
import com.iotsdk.error.IotSdkException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * 
 * 文件操作
 * 
 * @author DuJun
 * 
 */
public class FileUtils {
	static String TAG="image_error";
	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File createSDFile(String path) throws IOException {
		File file = new File(path);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public static File createSDDir(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			Log.v("FileUtils", "mkdir:" + dirName);
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * input InputStream写入本地文件
	 * 
	 * @param path
	 * @param input
	 * @return
	 */
	public static File write2SDFromInput(String path, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(new File(path).getParentFile().getAbsolutePath());
			file = createSDFile(path);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer, 0, buffer.length);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static File write2SDFromBitmap(String path, Bitmap bitmap) {
		return write2SDFromBitmap(path, bitmap, 100);
	}

	public static File write2SDFromBitmap(String path, Bitmap bitmap,
			int quality) {
		if (path == null || bitmap == null)
			return null;

		File file = null;
		OutputStream output = null;
		try {
			createSDDir(new File(path).getParentFile().getAbsolutePath());
			file = createSDFile(path);
			output = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, quality, output);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	public static int getStroeBitmapSize(Bitmap bitmap, int quality) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.JPEG, quality, stream);
		return stream.size();
	}

	public static File write2SdFile(String path, byte[] data) {

		createSDDir(new File(path).getParentFile().getAbsolutePath());
		File file = null;
		FileOutputStream output = null;
		try {
			file = createSDFile(path);
			output = new FileOutputStream(file);
			output.write(data);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return file;
	}
    
	
	/**
	 * 根据指定大图路径 在指定缩略图路径  生成对应的缩略图
	 * @return
	 * @throws NuageException 
	 */
	public static void createNuagethumb(String path,String thumbName,String thumbPath) throws IotSdkException{
		Bitmap bitmap = null;
		thumbPath = thumbPath + thumbName
				+ SdkConfig.NUAGE_PIC_JPG_SUFFIX;
		bitmap = getBitmapByDemandWidthHeight(
				SdkConfig.MEDIA_THUMBNAIL_DEFAULT_WIDTH,
				SdkConfig.MEDIA_THUMBNAIL_DEFAULT_HEIGHT, path);
		saveImage(bitmap, thumbPath);
		bitmap.recycle();
	}
	
	/**
	 * 根据指定大图路径 在指定图路径  按照图片尺寸生成对应的图(返回图片名字)
	 * @return
	 * @throws IotSdkException 
	 */
	public static String createNuagePhoto(String path,String newParentPath) throws IotSdkException{
		Bitmap bitmap = null;
		bitmap = getBitmapByDemandWidthHeight(
				SdkConfig.MEDIA_DEFAULT_WIDTH,
				SdkConfig.MEDIA_DEFAULT_HEIGHT, path);
		String name=file2Md5(path);
		newParentPath=newParentPath+name+".jpg";
		saveImage(bitmap, newParentPath);
		bitmap.recycle();
		return name;
	}
	
	
	/**
	 * 获取文件md5
	 * 
	 * @param filePath
	 * @return
	 */
	public static String file2Md5(String filePath) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filePath);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
	
	/**
	 * Md5
	 */

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(Integer.toHexString((b[i] & 0xf0) >>> 4));
			sb.append(Integer.toHexString(b[i] & 0x0f));
		}
		return sb.toString();
	}
	
	
	
	
	
	public static String saveImage(Bitmap bm, String path) {
		FileOutputStream fOut = null;
		try {
			File f = new File(path);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, SdkConfig.PHOTO_QUALITY,
					fOut);
			fOut.flush();
			bm.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			path = null;
		} catch (IOException e) {
			e.printStackTrace();
			path = null;
		} finally {
			if (fOut != null)
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return path;
	}
	
	
	public static Bitmap getBitmapByDemandWidthHeight(int dWidth, int dHeight,
			String srcPath) throws IotSdkException {
		Bitmap bmp = null;
		Bitmap tmpBitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			final int degree = ImageUtil.getExifOrientation(srcPath);
			BitmapFactory.decodeFile(srcPath, opts);
			int srcW = opts.outWidth;
			int srcH = opts.outHeight;
			float fw = 1f;
			float fh = 1f;
			int nWidth = 0;
			int nHeight = 0;
			opts.inJustDecodeBounds = false;
			if (srcW / dWidth > 1) {
				opts.inSampleSize = srcW / dWidth;
				tmpBitmap = BitmapFactory.decodeFile(srcPath, opts);
				if (tmpBitmap != null) {
					srcW = tmpBitmap.getWidth();
					srcH = tmpBitmap.getHeight();
				}
			} else if (srcH / dHeight > 1) {
				opts.inSampleSize = srcH / dHeight;
				tmpBitmap = BitmapFactory.decodeFile(srcPath, opts);
				if (tmpBitmap != null) {
					srcW = tmpBitmap.getWidth();
					srcH = tmpBitmap.getHeight();
				}
			}
			if (tmpBitmap == null) {
				tmpBitmap = BitmapFactory.decodeFile(srcPath);
			}
			if (srcW > dWidth) {
				if (srcH <= dHeight) {
					fw = (float) dWidth / srcW;
					fh = fw;
					nWidth = dWidth;
					nHeight = (int) (srcH * fw);
				} else {
					float fx = (float) srcW / dWidth;
					float fy = (float) srcH / dHeight;
					if (fx > fy) {
						fw = (float) dWidth / srcW;
						fh = fw;
						nWidth = dWidth;
						nHeight = (int) (srcH * fw);
					} else {
						fh = (float) dHeight / srcH;
						fw = fh;
						nHeight = dHeight;
						nWidth = (int) (srcW * fh);
					}
				}
			} else {
				if (srcH <= dHeight) {
					nWidth = srcW;
					nHeight = srcH;
				} else {
					fh = (float) dHeight / srcH;
					fw = fh;
					nHeight = dHeight;
					nWidth = (int) (srcW * fh);
				}
			}
			if (tmpBitmap == null) {
				throw new IotSdkException("图片解码失败");
			}
			Matrix matrix = new Matrix();
			if (degree > 0) {
		     	matrix.setRotate(degree);
			}else{
				matrix.postScale(fw, fh);
			}
			bmp = Bitmap.createBitmap(tmpBitmap, 0, 0, srcW, srcH, matrix,
					false);
			Log.v(TAG, "getBitmapByDemandWidthHeight-NewWidth:" + nWidth
					+ ",NewHeight:" + nHeight);
		} catch (OutOfMemoryError e) {
			throw e;
		}
		if (bmp == null) {
			throw new IotSdkException("图片解码失败");
		}
		return bmp;
	}
	
	
	public interface OnFileLoadListener {
		public void onProgess(int progess);
	}
}

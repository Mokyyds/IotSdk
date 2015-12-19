package com.iotsdk.net.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class ImageUtil {

	/**
	 * bitmap local cache
	 */
	private final static Map<String, SoftReference<Bitmap>> LOCAL_CACHE = new HashMap<String, SoftReference<Bitmap>>();

	// width*height*key
	private final static String CACHE_KEY = "%1$d*%2$d:%3$s";

	private final static String IMAGE_MIME = "image/jpeg";

	/**
	 * url to inputStream
	 * 
	 * @param urlStr
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStream(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.connect();
		InputStream inputStream = conn.getInputStream();

		return inputStream;
	}
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			Log.e("exif", "cannot read exif", ex);
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			Log.e("exif", "orientation:" + orientation);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
				Log.e(" exif", "degree:" + degree);
			}
		}
		return degree;
	}
	/**
	 * 
	 * store image frome url
	 * 
	 * @param urlStr
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File storeImage(String urlStr, String path)
			throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.connect();
		int contentLength = conn.getContentLength();
		// 是否图片
		if (!IMAGE_MIME.equals(conn.getContentType())) {
			throw new IOException("not a image file url:" + urlStr);
		}
		InputStream inputStream = conn.getInputStream();

		File f = new File(path);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		f.createNewFile();
		OutputStream out = new FileOutputStream(f);
		byte[] b = new byte[1024];

		int length = 0;
		int byteread = 0;
		while ((byteread = inputStream.read(b)) != -1) {
			out.write(b, 0, byteread);
			length = length + byteread;
		}
		Log.i("ImageUtil", "filelength:" + contentLength + " readlength:"
				+ length);
		out.flush();
		out.close();
		inputStream.close();
		return f;
	}

	public static File storeImage(InputStream inputStream, String path)
			throws IOException {

		File f = new File(path);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		f.createNewFile();
		OutputStream out = new FileOutputStream(f);
		byte[] b = new byte[1024];
		int byteread = 0;
		while ((byteread = inputStream.read(b)) != -1) {
			out.write(b, 0, byteread);
		}
		out.flush();
		out.close();
		inputStream.close();
		return f;
	}

	/**
	 * 
	 * 自动使用内存缓存
	 * 
	 * @param pathName
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getLocalBitmap(String pathName, int width, int height) {
		return getLocalBitmap(pathName, width, height, true);
	}

	public static Bitmap getLocalBitmap(String pathName, int width, int height,
			boolean usCache) {

		final String key = String.format(CACHE_KEY, width, height, pathName);

		Bitmap bm = null;
		if (usCache)
			bm = getInCache(key);

		if (bm != null) {
			return bm;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			final int degree = getExifOrientation(pathName);

			BitmapFactory.decodeFile(pathName, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = calculateInSampleSize(options, width, height);

			Log.v("ImageUtil", "options.inSampleSize:" + options.inSampleSize);
			InputStream is = new FileInputStream(pathName);
			Bitmap lbm = BitmapFactory.decodeStream(is, null, options);

			if (lbm == null) {
				return lbm;
			}

			if (degree > 0) {
				Matrix m = new Matrix();
				m.setRotate(degree);
				bm = Bitmap.createBitmap(lbm, 0, 0, lbm.getWidth(),
						lbm.getHeight(), m, true);
				lbm.recycle();
			} else {
				bm = lbm;
			}
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		if (usCache && bm != null) {
			LOCAL_CACHE.put(key, new SoftReference<Bitmap>(bm));
		}
		return bm;
	}
    
	
	private final static String TAG = "PhotoOrUtils";

	
	private static Bitmap currentImage(Bitmap bm, int ori) {
		boolean isFront = false;
		int degree = 0;
		switch (ori) {
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			isFront = true;
		case ExifInterface.ORIENTATION_NORMAL:
			degree = 0;
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			isFront = true;
		case ExifInterface.ORIENTATION_ROTATE_90:
			degree = 90;
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			isFront = true;
		case ExifInterface.ORIENTATION_ROTATE_180:
			degree = 180;
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			isFront = true;
		case ExifInterface.ORIENTATION_ROTATE_270:
			degree = 270;
			break;
		}
		if (degree == 0 && !isFront)
			return bm;

		Matrix m = new Matrix();
		m.setRotate(degree);
		if (isFront) {
			Camera camera = new Camera();
			camera.save();
			camera.rotateZ(180);
			camera.getMatrix(m);
			camera.restore();
			// 设置翻转中心点

			int centerX = bm.getWidth() / 2;
			int centerY = bm.getHeight() / 2;
			m.preTranslate(-centerX, -centerY);
			m.postTranslate(centerX, centerY);
		}
		Bitmap nBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
				bm.getHeight(), m, true);
		bm.recycle();

		return nBm;
	}

	private static Bitmap getInCache(String key) {
		SoftReference<Bitmap> ob = LOCAL_CACHE.get(key);
		if (ob != null) {
			return ob.get();
		}
		return null;
	}

	/**
	 * 
	 * @param path
	 * @param maxSize
	 *            图片允许最大空间 单位：KB
	 * @return
	 */
	public static Bitmap imageZoom(String path, double maxSize) {
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap bitMap = BitmapFactory.decodeFile(path);
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		Bitmap bitmap = bitMap;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitmap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
					bitMap.getHeight() / Math.sqrt(i));
		}
		// bitMap.recycle();
		return bitmap;
	}

	public static Bitmap imageZoom(String path, int width, int height) {

		Bitmap bitmap = getLocalBitmap(path, width, height);
		if (bitmap == null)
			return null;
		
		if (bitmap.getWidth() <= width && bitmap.getHeight() <= height) {
			return bitmap;
		}
		float sw = ((float) width / bitmap.getWidth());
		float sh = ((float) height / bitmap.getHeight());
		float sa = Math.min(sw, sh);

		Matrix matrix = new Matrix();
		matrix.postScale(sa, sa);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmap.getWidth(),
				(int) bitmap.getHeight(), matrix, true);

		Log.e("ImageUtil", "zoomed photo w:" + bitmap.getWidth() + "  h:"
				+ bitmap.getHeight());
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static InputStream zoomPhotoStream(String path, int width, int height) {
		Bitmap bitmap = getLocalBitmap(path, width, height);
		int quality = 100;
		if (bitmap.getWidth() <= width && bitmap.getHeight() <= height) {
			quality = 100;
		} else {
			float sw = ((float) width / bitmap.getWidth());
			float sh = ((float) height / bitmap.getHeight());
			float sa = Math.min(sw, sh);

			quality = 65;
			Matrix matrix = new Matrix();
			matrix.postScale(sa, sa);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmap.getWidth(),
					(int) bitmap.getHeight(), matrix, true);

			Log.e("ImageUtil", "zoomed photo w:" + bitmap.getWidth() + "  h:"
					+ bitmap.getHeight());
		}

		ByteArrayInputStream isBm = null;
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 得到输出流
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
			// 转输入流
			isBm = new ByteArrayInputStream(baos.toByteArray());
		}
		return isBm;
	}

	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		bgimage.recycle();
		return bitmap;
	}

}

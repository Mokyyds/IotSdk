package com.iotsdk.net.image;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.iostdk.utils.Utils;
import com.iotsdk.net.commen.NetUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * 图片下载管理
 * 
 * @author DuJun
 * 
 */
public class ImageLoader {

	public final static String FOLDER_IMAGE = "image";

	public final static String FOLDER_THUMB = "thumb";
    /**
     * 表示是否正在下载
     */
	public boolean isDownLoading=false;
	/**
	 * bitmap cache key:md5
	 */
	private final Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	private final int cacheSize = 4 * 1024 * 1024; // 4MiB
	// private final LruCache<String, Bitmap> lruCache = new LruCache<String,
	// Bitmap>(
	// cacheSize);
	/**
	 * url to loaderProgess
	 */
	private final Map<String, List<LoaderProgess>> loader = new HashMap<String, List<LoaderProgess>>();

	/**
	 * 
	 */
	private final Executor pool = Executors.newFixedThreadPool(5);

	private String root = Environment.getExternalStorageDirectory().getPath()
			+ "/nuagesys/";

	/**
	 * 多线程程锁
	 */
	private final Object lock = 1;

	public static ImageLoader IMAGE_lOADER;

	public static ImageLoader getInstance() {
		if (IMAGE_lOADER == null) {
			IMAGE_lOADER = new ImageLoader();
		}
		return IMAGE_lOADER;
	}

	private ImageLoader() {

	}

	/**
	 * 
	 * @param cachePath
	 *            图片缓存目录,建议使用{@link Context#getCacheDir()} 作为所有本地缓存文件根目录
	 */
	public void setCacheRoot(String cacheRoot) {
		Log.v("ImageLoader", "cachePath:" + cacheRoot);
		this.root = cacheRoot;
	}
	
	public String getRoot() {
		return root;
	}

	/**
	 * return Bitmap ,null 如果内存或本地没有缓存，从网络下载通过{@link LoaderProgess}异步返回
	 * 
	 * @param url
	 * @param loaderProgess
	 * @return
	 * @deprecated 使用 {{@link #getBitmap(String, String, LoaderProgess)}
	 */
	public File getBitmap(final String url, LoaderProgess loaderProgess) {
		return getBitmap(url, null, FOLDER_IMAGE, loaderProgess);
	}

	public File getBitmap(final String url, String md5, boolean isThumb,
			final LoaderProgess loaderProgess) {
		return getBitmap(url, md5, isThumb ? ImageLoader.FOLDER_THUMB
				: ImageLoader.FOLDER_IMAGE, loaderProgess);
	}

	final static boolean showImage = true;
     
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param url
	 * @param md5
	 * @param imageType
	 * @param loaderProgess
	 * @return
	 */
	public File getBitmap(final String url, String md5, String imageType,
			final LoaderProgess loaderProgess) {
		if (!showImage)
			return null;
		if (url == null || url.length() == 0)
			return null;
		if (md5 == null || md5.length() == 0) {
			md5 = NetUtils.md5(url);
		}
		final String finalmd5 = md5;
		final String key = getCacheKey(finalmd5, imageType);

		// Bitmap b = null;
		// **内存中****去掉内存缓，只做文件下载*************/
		// b = getImageFromeMemory(dmd5);
		// if (b != null) {
		// return b;
		// }
		// **本地加载图片************/
		// b = getLocalImage(dmd5);
		// if (b != null) {
		// cacheInMemory(dmd5, b);
		// return b;
		// }

		File f = getLocalFile(finalmd5, imageType);
		// **本地文件存在*****************/
		if (f != null) {
			return f;
		}

		// **从网络*******************/
		boolean isLoading = addListener(key, loaderProgess);
		if (!isLoading) {
			loaderImage(url, finalmd5, imageType, loaderProgess);
		}
		return null;
	}

	public String getLocalPath(final String url, String md5, boolean isThumb) {
		if (url == null || url.length() == 0)
			return null;
		if (md5 == null || md5.length() == 0) {
			md5 = NetUtils.md5(url);
		}
		final String finalmd5 = md5;

		String p = getLocalPath(finalmd5, isThumb ? ImageLoader.FOLDER_THUMB
				: ImageLoader.FOLDER_IMAGE);

		return p;
	}

	/**
	 * frome memory cache
	 * 
	 * @param url
	 * @return null if not cache in memory
	 * 
	 */
	@SuppressWarnings("unused")
	private Bitmap getImageFromeMemory(String md5) {
		synchronized (lock) {
			return cache.get(md5).get();
		}
	}
    
	
	
	/**
	 * 批量下载一组图片
	 * @param imageList 下载的图片列表
	 * @param root      下载到本地的 文件夹目录
	 * @param loaderProgess 进度监听
	 */
	public void loadManyImages(final List<LoadImage> imageList,final String root,final LoaderProgess loaderProgess){			
		pool.execute(new Runnable() {

			@Override
			public void run() {
				loaderProgess.onStrat("image start");
			for(int i=0;i<imageList.size();i++){
				LoadImage image=imageList.get(i);
				String localPath=root+ File.separatorChar + image.getName()+".jpg";
			//	final String key = getCacheKey(md5, imageType);
				isDownLoading=true;
				File f = null;
				try {
					f = ImageUtil.storeImage(image.getUrl(), localPath);
					Log.i("ImageLoader", "md5:" + image.getMd5() + "\ntype:" + image.getImageType()
							+ "\nlocal:" + localPath);
					if (f != null && f.exists()) {
						//进度值回调
						int progess=((i+1)*100)/imageList.size();
						    loaderProgess.onProgess("image progess", progess);
						if(i==imageList.size()-1){
							loaderProgess.onLoaderDone("image down", f);
							isDownLoading=false;
						}
					} else {
						isDownLoading=false;
						throw new IOException("decod image unknown error.url:"
								+ image.getUrl() + "local:" + localPath);
						     
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (f != null && f.exists()) {
						f.delete();
					}
					loaderProgess.onLoaderError("load error", e.getMessage());
					 isDownLoading=false;
					//onLoaderError(key, e.getMessage());
				}
			}
			}
		});
	}
     /**
      * 标识当前是否正在下载	
      * @return
      */
	public boolean isDownLoading() {
		return isDownLoading;
	}


	/**
	 * 
	 * 异步加载图片
	 * 
	 * @param url
	 * @param loaderProgess
	 */
	private void loaderImage(final String url, final String md5,
			final String imageType, final LoaderProgess loaderProgess) {
		pool.execute(new Runnable() {

			@Override
			public void run() {
				final String key = getCacheKey(md5, imageType);
				File f = null;
				try {
					// InputStream in = ImageUtil.getInputStream(url);
					// File f = ImageUtil.storeImage(in,
					// getLocalPath(md5, imageType));
					f = ImageUtil.storeImage(url, getLocalPath(md5, imageType));
					Log.i("ImageLoader", "md5:" + md5 + "\ntype:" + imageType
							+ "\nlocal:" + getLocalPath(md5, imageType));
					if (f != null && f.exists()) {
						onLoaderDone(key, f);
					} else {
						throw new IOException("decod image unknown error.url:"
								+ url + "local:" + getLocalPath(md5, imageType));
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (f != null && f.exists()) {
						f.delete();
					}
					onLoaderError(key, e.getMessage());
				}
			}
		});
	}

	/**
	 * 将图片缓存到内存
	 * 
	 * @param md5
	 * @param bitmap
	 * @deprecated 该类不做bitmap缓存
	 */
	private void cacheInMemory(String md5, Bitmap bitmap) {
		synchronized (lock) {
			cache.put(md5, new SoftReference<Bitmap>(bitmap));
		}
	}

	/**
	 * 将图片缓存到本地
	 * 
	 * @param md5
	 * @param bitmap
	 * @deprecated 该类只保存本地
	 */
	private void cacheInLocal(String md5, Bitmap bitmap) {
		synchronized (lock) {
			final String p = getLocalPath(md5, FOLDER_IMAGE);
			if (!FileUtils.isFileExist(p)) {
				FileUtils.write2SDFromBitmap(p, bitmap);
			}
		}
	}

	/**
	 * 异步下载图片添加到监听管理容器中
	 * 
	 * @param key
	 * @param l
	 * @return 是否已存在下载任务中，去掉不必要的重复加载
	 */
	private boolean addListener(String key, LoaderProgess l) {
		if (l == null)
			throw new RuntimeException("请设置异步图片加载监听LoaderProgess");
		boolean isLoading = true;
		synchronized (lock) {
			List<LoaderProgess> ls = loader.get(key);
			if (ls == null) {
				isLoading = false;
				ls = new ArrayList<LoaderProgess>();
				loader.put(key, ls);
			}
			if (!ls.contains(l)) {
				ls.add(l);
				l.onStrat(key);
			}
		}
		return isLoading;
	}

	/**
	 * 暂时不用,图片下载进度
	 * 
	 * @param url
	 * @param progess
	 */
	@SuppressWarnings("unused")
	private void onProgess(String key, int progess) {
		synchronized (lock) {
			final List<LoaderProgess> ls = loader.get(key);
			if (ls != null) {
				for (LoaderProgess l : ls) {
					l.onProgess(key, progess);
				}
			}
		}
	}

	/**
	 * 异步通知url文件下载完成，自动清理监听
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void onLoaderDone(String key, File bitmap) {
		synchronized (lock) {
			final List<LoaderProgess> ls = loader.get(key);
			Log.v("ImageLoader", "s:" + (ls != null ? ls.size() : 0)
					+ "url load done:" + key);
			if (ls != null) {
				for (LoaderProgess l : ls) {
					l.onLoaderDone(key, bitmap);
				}
			}
			loader.remove(key);
		}
	}

	/**
	 * 异步下载url文件失败，自动清理监听
	 * 
	 * @param key
	 * @param error
	 */
	private void onLoaderError(String key, String error) {
		synchronized (lock) {
			final List<LoaderProgess> ls = loader.get(key);
			if (ls != null) {
				for (LoaderProgess l : ls) {
					l.onLoaderError(key, error);
				}
			}
			loader.remove(key);
		}
	}

	private String getLocalPath(String md5, String imageType) {
		return root + imageType + File.separatorChar + md5;
	}

	private File getLocalFile(String md5, String imageType) {
		String path = getLocalPath(md5, imageType);
		File f = new File(path);
		return f.exists() ? f : null;
	}

	public static String getCacheKey(String md5, String imageType) {
		return imageType + md5;
	}

	public interface LoaderProgess {

		public void onStrat(String key);

		public void onProgess(String key, int progess);

		public void onLoaderDone(String key, File imageFile);

		public void onLoaderError(String key, String error);
	}

}

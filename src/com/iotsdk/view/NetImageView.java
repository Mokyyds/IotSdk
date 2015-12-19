package com.iotsdk.view;

import java.io.File;

import com.iot.iotsdk.R;
import com.iotsdk.net.commen.NetUtils;
import com.iotsdk.net.image.ImageLoader;
import com.iotsdk.net.image.ImageLoader.LoaderProgess;
import com.iotsdk.net.image.ImageUtil;
import com.iotsdk.net.image.OnPostLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


/**
 * 
 * 网络图片加载
 * <p>
 * 继承{@link ImageView}添加接口{@link #setImageUrl(String, String, boolean)},
 * {@link #setImageFile(File, boolean)}
 * </p>
 * 
 * @author DuJun
 * 
 */
public class NetImageView extends ImageView {

	private final static int DEFAULT_IMAGE_WIDTH = 200;
	private Animation loadAnim;
	private final boolean useLoadAnimation = false;

	/**
	 * 圆角大小
	 */
	private float[] roundRadii;
	private Path roundPath;
	private RectF roundRect;

	private int framWidth;
	private int framColor;

	private Paint mFramePaint;
	private Bitmap frontBit;

	private final Object lock = 1;

	public NetImageView(Context context) {
		super(context);
		inint(context, null, 0);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inint(context, attrs, 0);
	}

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inint(context, attrs, defStyle);
	}

	private void inint(Context context, AttributeSet attrs, int defStyle) {
		if (useLoadAnimation)
			loadAnim = AnimationUtils.loadAnimation(getContext(),
					R.anim.kinder_sdk_loader);

		roundRadii = new float[2];
		roundPath = new Path();
		roundRect = new RectF();

		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		mFramePaint.setFilterBitmap(true);
	}

	public void setroundRadius(int x, int y) {
		roundRadii[0] = x;
		roundRadii[1] = y;
		invalidate();
	}

	public void setFrame(int frameWidth, int frameColor) {
		this.framWidth = frameWidth;
		this.framColor = frameColor;
		invalidate();
	}

	public void setFrontMark(int resouseId) {
		frontBit = ((BitmapDrawable) getResources().getDrawable(resouseId))
				.getBitmap();
		invalidate();
	}

	/**
	 * @deprecated 用于url动态地址 cache 改为固定的MD5值，建议使用{
	 *             {@link #setImageUrl(String, String)}
	 * @param url
	 */
	private void setImageUrl(String url) {
		setImageUrl(url, null, false);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		roundPath.reset();
		roundRect.set(0, 0, w, h);
		roundPath.addRoundRect(roundRect, roundRadii[0], roundRadii[1],
				Direction.CCW);

	}

	/**
	 * 
	 * @param url
	 *            null 情况图片同时清除回调标准
	 * @param md5
	 *            照片md5值 不一定是url加密所得，如为null，MD5由url加密
	 * @param isThumb
	 *            是否缩略图
	 */
	public void setImageUrl(String url, String md5, boolean isThumb) {
		synchronized (lock) {
			if (url == null) {
				setTag("null");
				imageFile = null;
				setImageBitmap(null);
			} else {
				if (md5 == null || md5.length() == 0) {
					md5 = NetUtils.md5(url);
				}
			}
			String imageType = isThumb ? ImageLoader.FOLDER_THUMB
					: ImageLoader.FOLDER_IMAGE;
			setTag(ImageLoader.getCacheKey(md5, imageType));

			File f = ImageLoader.getInstance().getBitmap(url, md5, imageType,
					loaderlistener);

			if (f != null) {
				clearAnimation();
				setImageFile(f, useCache);
				// setBackgroundColor(0xffeeeeee);
			} else {
				setImageBitmap(null);
				// setBackgroundColor(getResources().getColor(
				// android.R.color.darker_gray));
			}
		}
	}

	private final LoaderProgess loaderlistener = new OnPostLoaderListener() {

		@Override
		public void onPostStart(String key) {
			// TODO start load image
			synchronized (lock) {
				if (key.equals(getTag().toString())) {
					if (useLoadAnimation) {
						setAnimation(loadAnim);
						loadAnim.start();
					}
				}
			}
		}

		@Override
		public void onPostProgess(String key, int progess) {
			// TODO load image progess 未实现
		}

		@Override
		public void onPostLoaderError(String key, String error) {
			// TODO load image error
			// Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
			synchronized (lock) {
				if (key.equals(getTag().toString())) {
					imageFile = null;
					if (useLoadAnimation) {
						clearAnimation();
					}
				}
			}
		}

		@Override
		public void onPostLoaderDone(String key, File file) {
			Log.v("NetImageView", "msg loader done tag:" + getTag().toString());
			synchronized (lock) {
				if (key.equals(getTag().toString())) {
					if (useLoadAnimation) {
						clearAnimation();
					}
					imageFile = file;
					setImageFile(file, useCache);
					// setBackgroundColor(0xffeeeeee);
				}
			}
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		if (VISIBLE != getVisibility()) {
			return;
		}

		if (roundRadii[0] > 0 || roundRadii[1] > 0) {
			try {
				canvas.clipPath(roundPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onDraw(canvas);
		} else {
			super.onDraw(canvas);
		}
		if (frontBit != null) {
			canvas.drawBitmap(frontBit, getWidth() - frontBit.getWidth(), 0,
					mFramePaint);
		}
		if (framWidth > 0) {
			mFramePaint.setColor(framColor);
			mFramePaint.setStrokeWidth(framWidth);
			mFramePaint.setStyle(Style.STROKE);
			canvas.drawPath(roundPath, mFramePaint);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (imageFile != null && imageFile.exists())
			setImageFile(imageFile, useCache);
	}

	private File imageFile;
	private boolean useCache = true;

	/**
	 * 自动使用图片内存缓存
	 * 
	 * @param imageFile
	 */
	public void setImageFile(File imageFile) {
		setImageFile(imageFile, useCache);
	}

	public File getImageFile() {
		return imageFile;
	}

	private int imageHeight;
	private int imageWidth;

	protected int imageHeight() {
		return imageHeight;
	}

	protected int imageWidth() {
		return imageWidth;
	}

	/**
	 * 显示图片，按视图大小解码图片
	 * 
	 * @param imageFile
	 * @param useCache
	 *            是否使用图片内存缓存
	 */
	private void setImageFile(File imageFile, boolean useCache) {
		this.imageFile = imageFile;
		if (getWidth() <= 0)
			return;
		if (imageFile != null && imageFile.exists()) {
			int width = getWidth() - getPaddingLeft() - getPaddingRight();
			int height = getHeight() - getPaddingTop() - getPaddingBottom();
			width = width > 0 ? width : DEFAULT_IMAGE_WIDTH;
			height = height > 0 ? height : DEFAULT_IMAGE_WIDTH;
			Bitmap bitmap = ImageUtil.getLocalBitmap(
					imageFile.getAbsolutePath(), width, height, useCache);
			if (bitmap != null) {
				imageHeight = bitmap.getHeight();
				imageWidth = bitmap.getWidth();
			}
			setImageBitmap(bitmap);
		} else {
			setImageBitmap(null);
		}
	}

}

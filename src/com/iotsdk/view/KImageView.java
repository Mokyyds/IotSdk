package com.iotsdk.view;

import java.io.File;

import com.iotsdk.net.image.ImageUtil;



import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class KImageView extends ImageView {

	private final static int DEFAULT_IMAGE_WIDTH = 200;

	public KImageView(Context context) {
		super(context);
	}

	public KImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 显示图片，按视图大小解码图片
	 * 
	 * @param imageFile
	 */
	public void setImageFile(File imageFile) {
		if (imageFile.exists()) {
			int width = getWidth();
			int height = getHeight();
			width = width > 0 ? width : DEFAULT_IMAGE_WIDTH;
			height = height > 0 ? height : DEFAULT_IMAGE_WIDTH;
			setImageBitmap(ImageUtil.getLocalBitmap(imageFile.getAbsolutePath(), width,
					height));
		} else {
			setImageBitmap(null);
		}
	}
}

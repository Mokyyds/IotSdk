package com.iotsdk.net.image;

import android.graphics.Bitmap;

public interface ImageViewLoadListener {

	public void OnLoadDone(Bitmap bitmap);
	public void OnLoadeError(String e);
	
}

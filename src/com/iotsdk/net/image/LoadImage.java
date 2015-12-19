package com.iotsdk.net.image;
/**
 * 图片下载的封装
 * @author jeff
 *
 */
public class LoadImage{
		private String url;
		private String md5;
		private String imageType;
		private String name;
		/**
		 * 
		 * @param url 下载url地址
		 * @param md5   图片md5 值
		 * @param imageType 
		 *      类型是 大图  ImageLoader.FOLDER_IMAGE
		 *      还是缩略图    ImageLoader.FOLDER_THUMB
		 */
		public LoadImage(String url,String md5,String imageType,String imageName){
			this.url=url;
			this.md5=md5;
			this.imageType=imageType;
			this.name=imageName;
		}		
		public String getUrl() {
			return url;
		}		
		public String getMd5() {
			return md5;
		}	
		public String getImageType() {
			return imageType;
		}
		/**
		 * 图片名字
		 * @return
		 */
		public String getName() {
			return name;
		}

}

package com.iotsdk.net.commen.file;

import com.iotsdk.application.IotSdk;
import com.iotsdk.net.commen.RequestHelper;
import com.iotsdk.net.commen.ResponseBean;



/**
 * 
 * 文件表单方式上传辅助类
 * 
 * @author DuJun
 * 
 */
public class UploadFileHelper extends
		RequestHelper<FileFormRequest, ResponseBean> {

	public UploadFileHelper(IotSdk iotSdk) {
		super(iotSdk);
	}

	@Override
	public ResponseBean getResponseBean(String res) {
		return new ResponseBean(res) {
			@Override
			protected void onParseJson() {

			}
		};
	}

}

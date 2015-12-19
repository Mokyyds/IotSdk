package com.iotsdk.net.commen;

/**
 * 
 * �?��请求进度监听器，用于班级管理多种加载监听
 * 
 * @author DuJun
 * 
 */
public interface RequestProcessListener {

	public void start();

	public void complete();

	public void error();
	

}

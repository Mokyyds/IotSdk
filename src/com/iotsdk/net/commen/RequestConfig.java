package com.iotsdk.net.commen;

/**
 * 
 * 请求配置
 * 
 */
public interface RequestConfig {

	public final static String GET = "get";
	public final static String POST = "post";
	public static final String CLIENT_SECRET = "1bc9f4b5f1c56b7e2d5b17c27dad280f";
	public static final String CLIENT_ID = "1000000007";
	public static final int CLIENT_VERSION = 13;
	// *****地址*******/
	public final static String DEV = "http://devapi.nuagebebe.com";
	public final static String STA = "http://staapi.nuagebebe.com";
	public final static String INT = "http://intapi.nuagebebe.com";
	public final static String INTs = "https://intapi.nuagebebe.com";
	public final static String PRO = "http://api.nuagebebe.com";

	public final static String URL_BASE = INT;
	/** regist */
	public final static String URL_REGIST = URL_BASE + "/1/auth/signup";
	/** login */
	public final static String URL_LOGIN = URL_BASE + "/1/auth/signin";
	/** auth forgot */
	public final static String URL_FORGOT = URL_BASE + "/1/auth/forgot";
	/** group list */
	public final static String URL_CLASS = URL_BASE + "/1/user/baby/group/list";
	/** group album */
	public final static String URL_ALBUM = URL_BASE + "/1/group/album/list";
	/** family list */
	public final static String URL_FAMILY = URL_BASE + "/1/user/family/list";
	/** life post */
	public final static String URL_LIFE_URL_POST = URL_BASE
			+ "/1/apps/life/post";
	/** life list */
	public final static String URL_LIFE_LIST = URL_BASE + "/1/apps/life/list";
	/** grade photo list */
	public final static String URL_GRADE_PHOTO = URL_BASE
			+ "/1/group/album/photo/list";
	/** baby delete */
	public final static String URL_FAMILY_MEMBER_DELETE = URL_BASE
			+ "/1/family/member/delete";
	/** set user profile */
	public final static String URL_SET_USER_PROFILE = URL_BASE
			+ "/1/set/user/profile";
	/** set user password */
	public final static String URL_SET_USER_PASSWORD = URL_BASE
			+ "/1/set/user/password";

	/** group notice list */
	public final static String URL_GROUP_NOTICE_LIST = URL_BASE
			+ "/1/group/notice/list";
	/** group notice list */
	public final static String URL_GROUP_NOTICE_LIST2 = URL_BASE
			+ "/2/group/notice/list";
	/** group notice list */
	public final static String URL_GROUP_NOTICE_HISTORY_LIST= URL_BASE
			+ "/2/group/notice/history";
	/** family baby add */
	public final static String URL_FAMILY_BABY_ADD = URL_BASE
			+ "/1/family/baby/add";
	/** family member add */
	public final static String URL_FAMILY_MEMBER_ADD = URL_BASE
			+ "/1/family/member/add";

	public final static String URL_APPS_SLEEP_POST = URL_BASE
			+ "/1/apps/sleep/post";

	public final static String URL_APPS_Cashbook_POST = URL_BASE
			+ "/1/apps/cashbook/post";

	public final static String URL_APPS_Doctor_POST = URL_BASE
			+ "/1/apps/doctor/post";
	public final static String URL_APPS_FEED_POST = URL_BASE
			+ "/1/apps/feed/post";
	public final static String URL_APPS_MEASURE_POST = URL_BASE
			+ "/1/apps/measure/post";
	public final static String URL_APPS_MILESTONE_POST = URL_BASE
			+ "/1/apps/milestone/post";
	public final static String URL_APPS_PEEPOO_POST = URL_BASE
			+ "/1/apps/peepoo/post";
	public final static String URL_APPS_VACCINATION_POST = URL_BASE
			+ "/1/apps/vaccination/post";

	public final static String URL_SET_USER_FACE = URL_BASE
			+ "/1/set/user/face";
	/**
	 * 宝宝资料
	 */
	public final static String URL_SET_BABY_PROFILE = URL_BASE
			+ "/1/set/baby/profile";
	/**
	 * 宝宝头像
	 */
	public final static String URL_SET_BABY_FACE = URL_BASE
			+ "/1/set/baby/face";

	/**
	 * 删除life记录
	 */
	public final static String URL_LIFE_DELETE = URL_BASE
			+ "/1/apps/life/delete";

	/** 测评记录添加 */
	public final static String URL_APPS_CDT_POST = URL_BASE
			+ "/1/apps/cdt/post";

	/** 测评记录列表 */
	public final static String URL_APPS_CDT_LIST = URL_BASE
			+ "/1/apps/cdt/list";
    
	
	/** 发送班级通知接口（教师端） */
	public final static String URL_APPS_NOTICE_ADD = URL_BASE
			+ "/1/group/notice/add";
	
	/** 添加照片并创建班级相册 */
	public final static String URL_APPS_ALBUM_POST = URL_BASE
			+ "/1/group/album/photo/post";
	
	/** 跟相册中添加照片 */
	public final static String URL_APPS_PHOTO_UPLOAD = URL_BASE
			+ "/1/group/album/photo/upload"; 
	/** 创建空相册 */
	public final static String URL_APPS_ALBUM_CREATE_POST = URL_BASE
	+ "/1/group/album/create"; 
	
	/** 删除相册 */
	public final static String URL_APPS_DELETE_ALBUM_POST = URL_BASE
	+ "/1/group/album/delete"; 
	/** 删除相册中照片 */
	public final static String URL_APPS_DELETE_PHOTO_POST = URL_BASE
	+ "/1/group/album/photo/delete"; 
	
	/** 添加消息 */
	public final static String URL_USER_MESSAGE_POST = URL_BASE
			+ "/1/user/chat/post";
	/** 用户私信内容查看接口 */
	public final static String URL_USER_MESSAGE_INFO  = URL_BASE
			+ "/1/user/chat/stream";
	/** 用户私信列表接口 */
	public final static String URL_USER_MESSAGE_LIST  = URL_BASE
			+ "/1/user/chat/list";
	/** 获取教师列表接口 */
	public final static String URL_USER_TEACHER_LIST  = URL_BASE
			+ "/1/user/baby/teacher/list";
	/** 获取推送的 token接口 */
	public final static String URL_TOKEN = URL_BASE + "/1/client/token/jpush";
	/** 获取应用通知接口 */
	public final static String URL_CLIENT_NOTICE = URL_BASE + "/1/client/notice/list";
	
	
	/**
	 *  登陆2接口（支持手机号登陆）
	 */
	public final static String URL_SIGNIN  = INTs + "/2/auth/signin";
	/**
	 * 注册帐户激活接口（支持手机号）
	 */
	public final static String URL_VERIFY  = URL_BASE + "/2/auth/verify";
	/**
	 * 注册2接口（ 支持手机号注册）
	 */
	public final static String URL_SIGNUP = URL_BASE + "/2/auth/signup";
	/**
	 * 重置密码（支持手机号）
	 */
	public final static String URL_RESET =URL_BASE + "/2/auth/reset"; 
    /**
     * 获取用户激活码（支持手机号）
     * 
     */
	public final static String URL_CODE =URL_BASE + "/2/auth/code"; 
	/**
	 * 忘记密码2接口 （支持手机号）
	 */	
	public final static String URL_FOREOT = URL_BASE + "/2/auth/forgot";
	/**
	 * 获取动态
	 */
	public final static String URL_NEWS_LIST = URL_BASE + "/2/group/news/list";
	/**
	 * 根据baby获取动态
	 */
	public final static String URL_NEWS_BABY_LIST = URL_BASE + "/2/baby/news/list";
	/**
	 * 发送动态内容
	 */
	public final static String URL_NEWS_POST = URL_BASE + "/2/group/news/post";
    /**
     * 发送私信聊天接口（支持图片跟语音）
     */
	public final static String URL_CHAT_POST = URL_BASE + "/2/chat/message/send";
	
	
	/**
     * 获取周边商家信息列表
     */
	public final static String URL_MERCHANT_GET = URL_BASE + "/1/location/merchant/list";
	
	

}

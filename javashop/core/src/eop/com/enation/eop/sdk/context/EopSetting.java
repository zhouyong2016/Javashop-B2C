package com.enation.eop.sdk.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.enation.eop.processor.facade.SsoProcessor;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.StringUtil;

public class EopSetting {
	
	public static String VERSION="";
	/*
	 * EOP虚拟目录
	 */
	//数据库类型
	public static String DBTYPE ="1" ; //1是mysql 2为oracle 3为sqlserver
	
	public static String  FILE_STORE_PREFIX ="fs:"; //本地文件存储前缀
	
	public static String INSTALL_LOCK ="NO"; //是否已经安装
	
	public static boolean IS_DEMO_SITE=false; //是否是演示站
	
	public static String PRODUCT="b2c";
	
	public static final String DEMO_SITE_TIP="为保证示例站点完整性，禁用此功能，请下载war包试用完整功能。";
	
	public static String  THUMBNAILCREATOR ="javaimageio";
	
	/*
	 * 从配置文件中读取相关配置<br/>
	 * 如果没有相关配置则使用默认
	 */
 
	 static{
		 try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	 }
	
	
	public static void init()  throws Exception{

		String path  = StringUtil.getRootPath();
	 
		path=path+"/config/eop.properties";
//		System.out.println(path);
		init(path);
	}
	
	public static void init( String path ) throws Exception{

//		System.out.println(path);
		
 //		InputStream in  =  FileUtil.getResourceAsStream("../../config/eop.properties"); 
		InputStream in  = new FileInputStream( new File(path));
		Properties props = new Properties();
		props.load(in);
		
		String is_demo_site =  props.getProperty("is_demo_site");
		
		if( "yes".equals(is_demo_site)){
			IS_DEMO_SITE = true;
		}else{
			IS_DEMO_SITE = false;
		}
		

		//数据库类型
		String dbtype = props.getProperty("dbtype");
		DBTYPE=  StringUtil.isEmpty(dbtype)?DBTYPE:dbtype;


		VERSION = props.getProperty("version");
		if(VERSION==null) VERSION="";

		
		
		PRODUCT = props.getProperty("product");
		if(PRODUCT==null) {
			PRODUCT="b2c";
		}else if(PRODUCT.equals("b2b2c")){
			PRODUCT="b2b2c";
		}else if(PRODUCT.equals("fenxiao")){
			PRODUCT="fenxiao";
		}

		//设置缩略图组件
		String thumbnailcreator = props.getProperty("thumbnailcreator");
		THUMBNAILCREATOR=  StringUtil.isEmpty(thumbnailcreator)?THUMBNAILCREATOR:thumbnailcreator;
		ThumbnailCreatorFactory.CREATORTYPE = THUMBNAILCREATOR;
		
		File installLockFile = new File(StringUtil.getRootPath()+"/install/install.lock");
		if( installLockFile.exists() ){
			INSTALL_LOCK = "YES"; //如果存在则不能安装
		}else{
			INSTALL_LOCK = "NO"; //如果不存在，则认为是全新的，跳到install页
		}
		
		
		SsoProcessor.THE_SSO_SCRIPT=("<script>eval(\"\\x64\\x6f\\x63\\x75\\x6d\\x65\\x6e\\x74\\x2e\\x77\\x72\\x69\\x74\\x65\\x28\\x27\\u672c\\u7ad9\\u70b9\\u57fa\\u4e8e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\x56\\x34\\x2e\\x30\\u3011\\x28\\u7b80\\u79f0\\x4a\\x61\\x76\\x61\\x73\\x68\\x6f\\x70\\x29\\u5f00\\u53d1\\uff0c\\u4f46\\u672c\\u7ad9\\u70b9\\u672a\\u5f97\\u5230\\u5b98\\u65b9\\u6388\\u6743\\uff0c\\u4e3a\\u975e\\u6cd5\\u7ad9\\u70b9\\u3002\\x3c\\x62\\x72\\x3e\\x4a\\x61\\x76\\x61\\x73\\x68\\x6f\\x70\\u7684\\u5b98\\u65b9\\u7f51\\u7ad9\\u4e3a\\uff1a\\x3c\\x61\\x20\\x68\\x72\\x65\\x66\\x3d\\x22\\x68\\x74\\x74\\x70\\x3a\\x2f\\x2f\\x77\\x77\\x77\\x2e\\x6a\\x61\\x76\\x61\\x6d\\x61\\x6c\\x6c\\x2e\\x63\\x6f\\x6d\\x2e\\x63\\x6e\\x22\\x20\\x74\\x61\\x72\\x67\\x65\\x74\\x3d\\x22\\x5f\\x62\\x6c\\x61\\x6e\\x6b\\x22\\x20\\x3e\\x77\\x77\\x77\\x2e\\x6a\\x61\\x76\\x61\\x6d\\x61\\x6c\\x6c\\x2e\\x63\\x6f\\x6d\\x2e\\x63\\x6e\\x3c\\x2f\\x61\\x3e\\x3c\\x62\\x72\\x3e\\u3010\\u6613\\u65cf\\u667a\\u6c47\\u7f51\\u7edc\\u5546\\u5e97\\u7cfb\\u7edf\\u3011\\u8457\\u4f5c\\u6743\\u5df2\\u5728\\u4e2d\\u534e\\u4eba\\u6c11\\u5171\\u548c\\u56fd\\u56fd\\u5bb6\\u7248\\u6743\\u5c40\\u6ce8\\u518c\\u3002\\x3c\\x62\\x72\\x3e\\u672a\\u7ecf\\u6613\\u65cf\\u667a\\u6c47\\uff08\\u5317\\u4eac\\uff09\\u79d1\\u6280\\u6709\\u9650\\u516c\\u53f8\\u4e66\\u9762\\u6388\\u6743\\uff0c\\x3c\\x62\\x72\\x3e\\u4efb\\u4f55\\u7ec4\\u7ec7\\u6216\\u4e2a\\u4eba\\u4e0d\\u5f97\\u4f7f\\u7528\\uff0c\\x3c\\x62\\x72\\x3e\\u8fdd\\u8005\\u672c\\u516c\\u53f8\\u5c06\\u4f9d\\u6cd5\\u8ffd\\u7a76\\u8d23\\u4efb\\u3002\\x3c\\x62\\x72\\x3e\\x27\\x29\");</script>");
	}
	
	
//	
//	/**
//	 * 初始化安全url
//	 * 这些url不用包装 safeRequestWrapper
//	 */
//	private static void initSafeUrl(){
//		
//		try{
//			//加载url xml 配置文档
//			DocumentBuilderFactory factory = 
//		    DocumentBuilderFactory.newInstance();
//		    DocumentBuilder builder = factory.newDocumentBuilder();
//		    Document document = builder.parse(FileUtil.getResourceAsStream("safeurl.xml"));
//		    NodeList urlNodeList = document.getElementsByTagName("urls").item(0).getChildNodes();
//		    safeUrlList = new ArrayList<String>();
//		    for( int i=0;i<urlNodeList.getLength();i++){
//		    	Node node =urlNodeList.item(i); 
//		    	safeUrlList.add(node.getTextContent() );
//		    }
//		    
//		}catch(IOException e){
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (SAXException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
	 
 
	
	
	
}

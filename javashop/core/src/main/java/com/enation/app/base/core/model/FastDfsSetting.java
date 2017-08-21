package com.enation.app.base.core.model;

import java.io.Serializable;
import java.util.Map;

import com.enation.app.base.core.service.ISettingService;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * fastdfs 设置类
 * @author Chopper
 * @version v1.0
 * @since v6.1.1
 * 2017年1月18日 下午3:44:13 
 *
 */
public class FastDfsSetting implements Serializable {
  
	private static final long serialVersionUID = -2741711290453046320L;
	
	//系统设置分组
	public static final String fastdfs_key="fastdfs";
	
	// 是否开启文件分发  1 开启，0 关闭
	private static int fdfs_open=0;




	/**
	 * 重新加载fastdfs 配置
	 */
	public static void load(){
		ISettingService settingService= SpringContextHolder.getBean("settingDbService");
		Map<String,String> fastdfs = settingService.getSetting(fastdfs_key);
		//参数无效 则附默认值
		if(fastdfs!=null){
			String  fdfs_open_str = fastdfs.get("fdfs_open");
			fdfs_open= StringUtil.toInt(fdfs_open_str,0);
		}


	}


	/**
	 * 获取默认配置
	 * @param defaultDomain
	 * @return fastdfs 配置
	 */
	public static FastDfsSetting defaultSetting(){
		FastDfsSetting fastdfsSetting = new FastDfsSetting();
		fastdfsSetting.setFdfs_open(0);
		return fastdfsSetting;
	}


	public static int getFdfs_open() {
		return fdfs_open;
	}


	public static void setFdfs_open(int fdfs_open) {
		FastDfsSetting.fdfs_open = fdfs_open;
	}

	
	



}

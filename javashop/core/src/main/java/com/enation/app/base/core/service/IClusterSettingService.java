package com.enation.app.base.core.service;


import com.enation.app.base.core.model.ClusterSetting;

public interface IClusterSettingService {

	/**
	 * 获取集群配置
	 * @return
	 */
	public ClusterSetting getSetting();
	
	/**
	 * 保存集群配置
	 * @param clusterSetting
	 */
	public void save(ClusterSetting clusterSetting);
	
}

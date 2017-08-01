package com.enation.app.cms.core.plugin;

import java.util.Map;

import com.enation.app.cms.core.model.DataModel;

/**
 * 数据保存事件
 * 
 * @author kingapex 2010-10-19下午04:27:49
 */
public interface IDataSaveEvent {
	public static final int DATASAVE_ADD = 1;
	public static final int DATASAVE_EDIT = 2;

	/**
	 * 数据保存接口定义
	 * 
	 * @param data
	 */
	public void onSave(Map<String, Object> data, DataModel dataModel, int dataSaveType);
}

package com.enation.app.cms.component.plugin;

import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.DataLog;
import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.plugin.IDataSaveEvent;
import com.enation.eop.resource.IDataLogManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * cms数据日志记录插件
 * 
 * @author kingapex 2010-10-19下午03:23:38
 */
@Component
public class CmsDataLogPlugin extends AutoRegisterPlugin implements	IDataSaveEvent {

	private IDataLogManager dataLogManager;

	/**
	 * 响应文章保存事件 取出data中所有的数据，形成文本为datalog的content<br>
	 * 其中以fs:开头的数据则认为是图片
	 */
	public void onSave(Map<String, Object> data, DataModel dataModel, int dataSaveType) {
		Iterator<String> iter = data.keySet().iterator();
		StringBuffer content = new StringBuffer();
		StringBuffer pics = new StringBuffer();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object v = data.get(key);
			if (!(v instanceof String))
				continue;

			String value = (String) v;

			if (value != null) {
				if (value.startsWith(EopSetting.FILE_STORE_PREFIX)) {
					if (pics.length() != 0) {
						pics.append(",");
					}
					pics.append(value + "|" + value);
				} else {
					content.append(key + ":" + value + "<br>");
				}
			}
		}

		DataLog datalog = new DataLog();
		datalog.setContent(content.toString());
		datalog.setPics(pics.toString());
		datalog.setLogtype("文章");
		if (dataSaveType==IDataSaveEvent.DATASAVE_ADD)
			datalog.setOptype("添加");
		else if (dataSaveType==IDataSaveEvent.DATASAVE_EDIT)
			datalog.setOptype("修改");
		else
			datalog.setOptype("未定义");
		this.dataLogManager.add(datalog);
	}

	public IDataLogManager getDataLogManager() {
		return dataLogManager;
	}

	public void setDataLogManager(IDataLogManager dataLogManager) {
		this.dataLogManager = dataLogManager;
	}

}

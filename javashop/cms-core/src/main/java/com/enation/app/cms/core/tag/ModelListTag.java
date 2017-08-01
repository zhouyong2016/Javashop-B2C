package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 模型列表标签
 * @author liuzy
 *
 */
@Component
@Scope("prototype")
public class ModelListTag extends BaseFreeMarkerTag {
	private IDataModelManager dataModelManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return dataModelManager.list();
	}

	public IDataModelManager getDataModelManager() {
		return dataModelManager;
	}

	public void setDataModelManager(IDataModelManager dataModelManager) {
		this.dataModelManager = dataModelManager;
	}

}

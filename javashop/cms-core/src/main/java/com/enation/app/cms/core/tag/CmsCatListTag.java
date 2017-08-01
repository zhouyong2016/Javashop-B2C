package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 通过ID，读取该ID下分类
 * @author wanghongjun
 *
 */
@Component
@Scope("prototype")
public class CmsCatListTag  extends BaseFreeMarkerTag{
	private IDataManager dataManager;
	private Integer catid;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return dataManager.list(catid);
	}

	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

}

package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 文章数据搜索标签
 * @author kingapex
 *2013-10-26下午5:04:05
 */
@Component
public class DataSearchTag extends BaseFreeMarkerTag {
	@Autowired
	private IDataManager dataManager;
	
	/**
	 * 根据模型的字段值
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String connector =(String) params.get("connector");
		
		if( StringUtil.isEmpty(connector)) {
			connector = " and ";
		}
		
		
		Integer modelid = (Integer)params.get("modelid");
		if(modelid==null){
			throw new TemplateModelException("modelid 参数不能为空");
		}
		
		Integer pageNo =(Integer) params.get("pageNo");
		if(pageNo==null){
			pageNo=1;
		}
		Integer pageSize = (Integer)params.get("pageSize");
		if(pageSize==null){
			pageSize=10;
		}
		
		Integer catid = (Integer)params.get("catid");
		String cat_id=null;
		if(catid!=null){
			cat_id=catid.toString();
		}
		
		Page dataPage = dataManager.search(pageNo, pageSize, modelid, connector,cat_id);
		
		return dataPage;
	}

	
	

}

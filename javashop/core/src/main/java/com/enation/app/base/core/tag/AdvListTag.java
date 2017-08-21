package com.enation.app.base.core.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 广告列表标签
 * @author lina
 * 2013-12-20
 */
@Component
@Scope("prototype")
public class AdvListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IAdvManager advManager;
	
	@Autowired
	private IAdColumnManager adColumnManager;
	/**
	 * @param acid 广告位id
	 * @return Map广告信息数据，其中key结构为
	 * adDetails:广告位详细信息 {@link AdColumn}
	 * advList:广告列表 {@link Adv}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String acid = (String) params.get("acid");
		acid = acid == null ? "0" : acid;
		java.util.Map<String, Object> data =new HashMap();
		try{
			AdColumn adDetails = adColumnManager.getADcolumnDetail(Long.valueOf(acid));
			List<Adv> advList = null;
			
			if( adDetails!=null){
				advList = advManager.listAdv(Long.valueOf(acid));
			}
		 
			advList = advList == null ? new ArrayList<Adv>():advList;
			//if(!advList.isEmpty()){
				data.put("adDetails", adDetails);//广告位详细信息
				data.put("advList", advList);//广告列表
			//} 
		}catch(RuntimeException e){
			if(this.logger.isDebugEnabled()){
				this.logger.error(e.getStackTrace());
			}
		}
		return data;
	}
	
	
}

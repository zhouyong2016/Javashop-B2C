/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：文章列表Tag
 *  修改人：Sylow
 *  修改时间：2015-10-26
 *  修改内容：制定初版
 */
package com.enation.app.shop.front.tag.goods;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 文章列表Tag
 * @author Sylow
 * @version v1.0,2015-10-26
 * @since v5.2
 * 
 */
@Component
public class DataListTag extends BaseFreeMarkerTag {

	private IDataManager dataManager;
	
	/**
	 * 
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		// TODO Auto-generated method stub
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
		
		Page dataPage = dataManager.list(pageNo, pageSize, modelid, connector,cat_id);
		
		return dataPage;
	}

	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

}

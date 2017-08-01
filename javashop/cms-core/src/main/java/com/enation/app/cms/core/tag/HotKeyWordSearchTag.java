/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：页面热门关键字列表
 *  修改人：whj
 *  修改时间：2015-12-18
 *  修改内容：制定初版
 */


package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 页面热门关键字搜索
 * 
 * @author whj
 * @version v1.0,   2015-12-18
 * @since trunk
 */
@Component
@Scope("prototype")
public class HotKeyWordSearchTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IDataManager dataManager;
	
	/**
	 *  文章列表标签
	 *  @param catid:文章分类ID
	 *  @return 该ID下的文章列表
	 */
	
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid = (Integer)params.get("catid");
		return dataManager.list(catid);
	}

	

	
}

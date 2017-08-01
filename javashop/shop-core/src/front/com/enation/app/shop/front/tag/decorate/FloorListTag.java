package com.enation.app.shop.front.tag.decorate;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 前台页面楼层展示标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
@SuppressWarnings(value={"rawtypes"})
public class FloorListTag extends BaseFreeMarkerTag {

	@Autowired
	private IFloorManager floorManager;
	/**
	 * 返回所有楼层信息
	 * @param params
	 * @return 楼层列表
	 * @throws TemplateModelException
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Object page_id_obj=params.get("pageid");
		if(page_id_obj==null){
			page_id_obj=1;
		}
		Integer pageid=Integer.valueOf(page_id_obj.toString());
		List<Map> list=this.floorManager.getTopFloorAndStyle(pageid);
		
		
		return list;
	}

}

package com.enation.app.shop.front.tag.decorate;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 楼层商品集合标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
@SuppressWarnings(value={"rawtypes"})
public class FloorGoodsListTag extends BaseFreeMarkerTag {

	@Autowired
	private IFloorManager floorManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		// TODO Auto-generated method stub
		String goods_ids=params.get("goods_ids").toString();
		if(StringUtil.isEmpty(goods_ids)){
			return new ArrayList();
		}
		Map goods_id_map=JsonUtil.toMap(goods_ids);
		return this.floorManager.getGoodsListByGoods_ids(goods_id_map);
	}

}

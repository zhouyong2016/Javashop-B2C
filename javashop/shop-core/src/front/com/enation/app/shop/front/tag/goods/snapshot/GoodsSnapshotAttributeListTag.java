package com.enation.app.shop.front.tag.goods.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IGoodsSnapshotManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * (商品快照属性列表标签) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午3:45:47
 */

@Component
@Scope("prototype")
public class GoodsSnapshotAttributeListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	@Autowired
	private IGoodsSnapshotManager goodsSnapshotManager;
	/**
	 * @param snapshot_id 商品快照Id
	 *  type_id 商品分类Id
	 *  attribute.name 分类名称
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer snapshot_id = (Integer) params.get("snapshot_id");
		Map goodsmap =this.goodsSnapshotManager.get(snapshot_id);
		Integer typeid = (Integer) goodsmap.get("type_id");
		
		List<Attribute> list = this.goodsTypeManager.getAttrListByTypeId(typeid);
		List attrList = new ArrayList();
		
		Map attrmap= null; 
		attrmap = new HashMap();
		attrmap.put("attrName", "计价单位");
		attrmap.put("attrValue", goodsmap.get("unit"));
		attrList.add(attrmap);

		Map weightMap = new HashMap();
		weightMap.put("attrName", "品牌");
		weightMap.put("attrValue", goodsmap.get("brand_name"));
		attrList.add(weightMap);
		
		int i=1;
		for(Attribute attribute:list){
			attrmap = new HashMap();
			
			// 如果是选择项的
			if(attribute.getType()==3){
				String[] s = attribute.getOptionAr();
				String p = (String) goodsmap.get("p"+i);
				Integer num = 0;
				if(!StringUtil.isEmpty(p)){
					num = Integer.parseInt(p);
					attrmap.put("attrValue", s[num]);
				}
				attrmap.put("attrName", attribute.getName());
				// 输入项
			} else{
				attrmap.put("attrName", attribute.getName());
				attrmap.put("attrValue", goodsmap.get("p"+i));
			}
			attrList.add(attrmap);
			i++;
		}	
		return attrList;
	}
	

}

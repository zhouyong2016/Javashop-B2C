package com.enation.app.shop.front.tag.decorate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * 特定楼层标签标签
 * 
 * @author jianghongyan
 * @version 1.0.0,2016年6月20日
 * @since v6.1
 */
@Component
@Scope("prototype")
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class FloorTag extends BaseFreeMarkerTag {

	@Autowired
	private IFloorManager floorManager;

	/**
	 * 获得特定楼层信息
	 * 
	 * @param params
	 *            一般为楼层id
	 * @return 特定楼层信息集合
	 * @throws TemplateModelException
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer floor_id = Integer.valueOf(params.get("floor_id").toString());
		// 获取楼层内容信息
		Floor floor = this.floorManager.getFloorById(floor_id);
		Integer limit = Integer.valueOf(params.get("limit").toString());
		Integer pageid = (Integer) params.get("pageid");
		if (pageid == null || pageid == 0) {
			pageid = 1;
		}
		// 获取子楼层和子主题信息
		List<Map> childFloors = this.floorManager.getChildFloorAndStyleById(floor_id, limit, pageid);
		// 检测商品是否合规
		for (Map map : childFloors) {
			if (map.get("goods_ids") != null) {
				String goods_id_json = map.get("goods_ids").toString();
				if (!StringUtil.isEmpty(goods_id_json)) {
					Map jsonmap = JsonUtil.toMap(goods_id_json);
					map.put("goods_ids", jsonmap);
					map.put("goods_ids_json", goods_id_json);
				} else {
					map.remove("goods_ids");
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("floor", floor);
		map.put("childFloors", childFloors);

		return map;
	}

}

package com.enation.app.shop.front.tag.decorate;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;

import freemarker.template.TemplateModelException;

/**
 * 品牌获取标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
public class FloorBrandListByCatidTag  extends BaseFreeMarkerTag{

	@Autowired
	private IDaoSupport daoSupport;
	
	/**
	 * 根据tagid获得该分类下的品牌列表
	 */
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String brand_ids  =params.get("brand_ids").toString();
		Map<String,Object> brand_ids_Map=JsonUtil.toMap(brand_ids);
		StringBuffer brand_id_sb=new StringBuffer();
		for(Integer i=0;i<brand_ids_Map.size();i++){
			String value=brand_ids_Map.get(i.toString()).toString();
			brand_id_sb.append(value+",");
		}
		String brand_id_str=brand_id_sb.substring(0, brand_id_sb.length()-1);
		List<Brand> brandList=null;
		if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
			String sql="select * from es_brand where brand_id in ("+brand_id_str+") order by instr('"+brand_id_str+"',brand_id)";
			brandList =this.daoSupport.queryForList(sql);
		}else{
			String sql="select * from es_brand where brand_id in ("+brand_id_str+") order by charindex(','+convert(varchar,brand_id)+',',',"+brand_id_str+",')";
			brandList =this.daoSupport.queryForList(sql);
		}
		return brandList;
	}

}

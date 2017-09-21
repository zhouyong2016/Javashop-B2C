package com.enation.app.shop.front.tag.decorate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Adv;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 
 * 广告获取标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
@SuppressWarnings(value={"unchecked", "rawtypes"})
public class FloorAdvListTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		String aid_json=params.get("aid").toString();
		String position=params.get("position").toString();
		if(StringUtil.isEmpty(aid_json)){
			return new ArrayList();
		}
		Map<String,Object> aid_json_Map1=JsonUtil.toMap(aid_json);
		Map<String,Object> aid_json_Map=(Map<String, Object>) aid_json_Map1.get(position);
		if(aid_json_Map==null||aid_json_Map.size()==0){
			return new ArrayList();
		}
		StringBuffer sb=new StringBuffer();
		for(Integer i=0;i<aid_json_Map.size();i++){
			sb.append(aid_json_Map.get(i.toString()).toString());
			sb.append(",");
		}
		String aid_str=sb.substring(0, sb.length()-1);
		String sql="";
		if("1".equals(EopSetting.DBTYPE)||"2".equals(EopSetting.DBTYPE)){
			sql="select * from es_adv where aid in ("+aid_str+") order by instr('"+aid_str+"',aid)";
		}else{
			sql="select * from es_adv where aid in ("+aid_str+") order by charindex(','+convert(varchar,aid)+',',',"+aid_str+",')";
		}
		List<Adv> list=this.daoSupport.queryForList(sql);
		return list;
	}

}

package com.enation.app.shop.front.tag.goods.snapshot;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.service.GoodsTypeUtil;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IGoodsSnapshotManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * (商品快照参数标签) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午3:03:35
 */
@Component
@Scope("prototype")
public class GoodsSnapshotParametersTag extends BaseFreeMarkerTag {

	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	@Autowired
	private IGoodsSnapshotManager goodsSnapshotManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	/**
	 * 商品参数标签
	 * 获得 商品快照参数的  参数组
	 * 例如：goods表中params字段中有如下json。name[{"name":"基本参数","paramList":[{"name":"规格","value":"口径18cm，高8.28cm","valueList":[]},{"name":"上架时间","value":"","valueList
	 * ${node.name}得到结果：“基本参数”
	 * 要想得到paramList下的json中，则继续试用第二层list循环
	 * html详情参考default/detail/goods_props.html.中的<div class="rer_para paramList">
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		HttpServletRequest request  = this.getRequest();
		Map goodsmap =this.goodsSnapshotManager.get(Integer.parseInt(params.get("snapshot_id").toString()));
		Integer typeid = (Integer) goodsmap.get("type_id");
		
		Map result = new HashMap(); 
		if(this.daoSupport.queryForInt("select have_parm from es_goods_type where type_id = ?", typeid)==0){
			result.put("hasParam", false);
			return result;
		}
		Map snapshot =(Map)request.getAttribute("goods");
		String goodParams  =(String)snapshot.get("params");
		
		if(goodParams!=null && !goodParams.equals("")){
			ParamGroup[] paramList =GoodsTypeUtil.converFormString(goodParams); 
			result.put("paramList", paramList); 
			if(paramList!=null && paramList.length>0)
				result.put("hasParam", true);
			else
				result.put("hasParam", false);
		}else{
			result.put("hasParam", false);
		}

		return result;
		
	}

}

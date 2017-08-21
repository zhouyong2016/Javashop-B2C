package com.enation.app.shop.front.tag.goods;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.service.GoodsTypeUtil;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品参数标签
 * @author whj
 *2014-07-02下午23:13:00
 */
@Component
@Scope("prototype")
public class GoodsParametersTag extends BaseFreeMarkerTag {

	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	@Autowired
	private IGoodsManager goodsManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	/**
	 * 商品参数标签
	 * 获得 商品参数的  参数组
	 * 例如：goods表中params字段中有如下json。name[{"name":"基本参数","paramList":[{"name":"规格","value":"口径18cm，高8.28cm","valueList":[]},{"name":"上架时间","value":"","valueList
	 * ${node.name}得到结果：“基本参数”
	 * 要想得到paramList下的json中，则继续试用第二层list循环
	 * html详情参考default/detail/goods_props.html.中的<div class="rer_para paramList">
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		HttpServletRequest request  = this.getRequest();
		Map goodsmap =this.goodsManager.get(Integer.parseInt(params.get("goodsid").toString()));
		Integer typeid = (Integer) goodsmap.get("type_id");
		
		Map result = new HashMap(); 
		if(this.daoSupport.queryForInt("select have_parm from es_goods_type where type_id = ?", typeid)==0){
			result.put("hasParam", false);
			return result;
		}
		Map goods =(Map)request.getAttribute("goods");
		String goodParams  =(String)goods.get("params");
		
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

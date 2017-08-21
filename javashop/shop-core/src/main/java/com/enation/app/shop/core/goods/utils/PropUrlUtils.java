/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.Map;

import com.enation.app.shop.core.goods.service.Separator;
import com.enation.framework.util.StringUtil;

/**
 * 属性url生成工具
 * @author kingapex
 *2015-4-29
 */
public class PropUrlUtils {
	
	
	/**
	 * 在原有的url基础上根据参数名和值生成新的属性url<br>
	 * 如原url为 search.html?cat=1&prop=p1_1，生成新的url:search.html?cat=1&prop=p1_1@name_value
	 * @param name
	 * @param value
	 * @return
	 */
	public static String createPropUrl(String name,String value){
		Map<String,String> params= ParamsUtils.getReqParams();
		String prop  = params.get("prop");
		if(!StringUtil.isEmpty(prop)){
			prop=prop+Separator.separator_prop;
		}else{
			prop="";
		}
		prop=prop+name+Separator.separator_prop_vlaue+value;
		params.put("prop", prop);
		return ParamsUtils.paramsToUrlString(params);
	}
	
	
	/**
	 * 排除某个属性的方式生成属性字串<br>
	 * search.html?cat=1&prop=p1_1@p2_2 传入p2和2则返回search.html?cat=1&prop=p1_1<br>
	 * 用于生成已经选择的selector的url
	 * @param name
	 * @param value
	 * @return
	 */
	public static  String createPropUrlWithoutSome(String name,String value){
		Map<String,String> params= ParamsUtils.getReqParams();
		String prop  = params.get("prop");
		if(!StringUtil.isEmpty(prop)){
			prop = prop.replaceAll("("+Separator.separator_prop+"?)"+name+Separator.separator_prop_vlaue+value+"("+Separator.separator_prop+"?)", "");
		}else{
			prop="";
		}
		params.put("prop", prop);
		return ParamsUtils.paramsToUrlString(params);
	}
	

	
	
}

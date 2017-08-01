package com.enation.app.shop.core.decorate.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 品牌选择器controller
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/brand-select")
public class BrandSelectController extends GridController{

	@Autowired
	private IBrandManager brandManager;
	
	/**
	 * 获取品牌列表
	 * @param catid 品牌分类Id,Integer
	 * @param brandlist 品牌列表,List
	 * @return 品牌列表Json
	 */
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/list-brand-by-id")
	public GridJsonResult listBrandById(){
		Map brandMap = new HashMap();
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword = request.getParameter("keyword");
		
		brandMap.put("keyword", keyword);
		
		Page brandlist = brandManager.searchBrand(brandMap, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(brandlist);
	}
}

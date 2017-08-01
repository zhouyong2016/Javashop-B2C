package com.enation.app.shop.core.decorate.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.service.IAdvManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;

/**
 * 广告选择器controller
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/adv-select")
public class AdvSelectController extends GridController{

	@Autowired
	private IAdvManager advManager;
	/**
	 * 获取品牌列表
	 * @param catid 品牌分类Id,Integer
	 * @param advlist 品牌列表,List
	 * @return 品牌列表Json
	 */
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/list-adv-by-id")
	public GridJsonResult listAdvById(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword = request.getParameter("keyword");
		Page advlist = advManager.search(keyword,null, this.getPage(), this.getPageSize(), null);
		return JsonResultUtil.getGridJson(advlist);
	}
}

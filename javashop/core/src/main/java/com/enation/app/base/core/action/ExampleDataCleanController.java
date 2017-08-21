
package com.enation.app.base.core.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * 示例数据清除action
 * @author kingapex
 *2015-6-2
 * @author Kanon 2015-9-30 version 1.1 添加注释
 */

@Controller
@RequestMapping("/core/admin/example-data-clean")
public class ExampleDataCleanController {
	
	@Autowired
	private IExampleDataCleanManager exampleDataCleanManager;
	
	/**
	 * 跳转至清除演示数据页面
	 * @return 清除演示数据页面
	 */
	@RequestMapping("/info")
	public String execute(){
		return "/core/admin/data/clean";
	}
	
	/**
	 * 清除演示数据
	 * @param EopSetting.IS_DEMO_SITE 是否为演示站点
	 * @param moudels 清除表
	 * @return 清除状态
	 */
	@ResponseBody
	@RequestMapping(value="/clean")
	public JsonResult clean(String[] moudels){
		try {
			//判断是否为演示站点
			if(EopSetting.IS_DEMO_SITE){
				return JsonResultUtil.getErrorJson(EopSetting.DEMO_SITE_TIP);
			}
			//清除演示数据
			this.exampleDataCleanManager.clean(moudels);
			return JsonResultUtil.getSuccessJson("清除成功");
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("清除失败", e);
			return JsonResultUtil.getSuccessJson("清除失败");
		}
	}
}

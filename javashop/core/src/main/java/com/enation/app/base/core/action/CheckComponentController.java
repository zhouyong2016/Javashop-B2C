/**
 * 
 */
package com.enation.app.base.core.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.framework.action.JsonResult;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * 检测某组件Action
 * @author kingapex
 *2015-5-7
 * @author kanon 2015-9-24 version1.1 添加注释
 */
@Controller
@RequestMapping("/core/admin/setting/check-component")
public class CheckComponentController   {
 
	/**
	 * 根据bean id检测组件是否存在
	 * @param id bean id
	 * @return 组件是否存在 
	 */
	@ResponseBody
	@RequestMapping(value="/exist")
	public JsonResult exist(String id){
		try {
			Object obj = SpringContextHolder.getBean(id);
			if(obj==null){
				return JsonResultUtil.getErrorJson("不存在");
			}else{
				return JsonResultUtil.getSuccessJson("存在");
			}
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("不存在");
		}
	}
 

}

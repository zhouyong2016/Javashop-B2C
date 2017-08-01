package com.enation.app.base.core.action.api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.app.base.core.service.ISmsManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 短信API
 * @author xulipeng version 1.0
 * @author kanon 2015-9-22 version 1.1 添加注释
 * @author Kanon 2016-2-25;6.0版本改造
 * @deprecated 过期，不使用 add_by_Sylow 2016-07-07
 */
public class SmsApiController {

	@Autowired
	private ISmsManager smsManager;
	/**
	 * 发送信息
	 * @param phone 手机号码
	 * @param content 发送内容
	 * @return 发送状态
	 */
	public JsonResult send(String phone,String content){
		try {
			boolean flag = this.smsManager.send(phone, content,null);
			if(flag){
				return JsonResultUtil.getSuccessJson("发送成功");
			}else{
				return JsonResultUtil.getErrorJson("发送失败");
			}
		}catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("短信发送失败", e);
			return JsonResultUtil.getErrorJson("发送失败，错误消息："+e.getMessage());
		}
	}

	
}

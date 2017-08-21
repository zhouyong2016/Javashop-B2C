package com.enation.app.shop.front.api.member;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.util.SmsUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.TestUtil;

/**
 * 统一发送短信api
 * @author Sylow
 * @version v1.0,2016年7月7日
 * @since v6.1
 */
@Controller
@RequestMapping("/api/shop/sms")
public class SmsApiController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsApiController.class);

	/**
	 * 统一发送短信验证码API
	 * @param mobile 手机号
	 * @param key 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/send-sms-code",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult sendSmsCode(String mobile, String key, Integer isCheckRegister) {
		try {
			
			Object isCanSendObj = ThreadContextHolder.getSession().getAttribute("is_can_send");
			// 过滤
			if (isCanSendObj == null) {
				return JsonResultUtil.getErrorJson("禁止发送");
			}
			if (!Boolean.parseBoolean(isCanSendObj.toString())) {
				return JsonResultUtil.getErrorJson("禁止发送");
			}
			
			// 如果没有值  就是0
			if (isCheckRegister == null) {
				isCheckRegister = 0;
			}
			
			Map<String, Object> sendResult = SmsUtil.sendMobileSms(mobile, key, isCheckRegister);
			int stateCode = Integer.parseInt(sendResult.get("state_code").toString());
			
			// 根据状态码分别返回信息
			switch (stateCode) {
				case 0:
					return JsonResultUtil.getErrorJson(sendResult.get("msg").toString());
				case 1:
					return JsonResultUtil.getSuccessJson(sendResult.get("msg").toString());
				case 2:
					return JsonResultUtil.getErrorJson(sendResult.get("msg").toString());
				default:
					return JsonResultUtil.getSuccessJson(sendResult.get("msg").toString());
			}
		}catch(RuntimeException e) {
			TestUtil.print(e);
			LOGGER.debug("发送短信验证码出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 校验是否可以发送短信ajax
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/sms-safe",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult smsSafe() {
		try {
			//这里是通过客户端加载出html，确认是真实浏览器访问，而不是直接请求api，从而加大伪造客户端的难度，实现验证
			HttpSession session = ThreadContextHolder.getSession();
			session.setAttribute("is_can_send", true);
			
			return JsonResultUtil.getSuccessJson("成功");
		}catch(RuntimeException e) {
			TestUtil.print(e);
			LOGGER.debug("发送短信验证码出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 测试验证用，验证仿照这个写法 演示用
	 * @param code
	 * @param mobile
	 * @param key
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/test-validate",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult testValidate(String code, String mobile, String key) {
		try {
			
			boolean result = SmsUtil.validSmsCode(code, mobile, key);
			
			if (result) {
				return JsonResultUtil.getSuccessJson("验证通过");
			} else {
				return JsonResultUtil.getErrorJson("短信验证码错误");
			}
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
		
		
	}
	
}

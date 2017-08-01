package com.enation.app.base.core.action;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 短消息action
 * @author kingapex
 * @author Kanon 2015-10-14 version 1.1 添加注释
 *
 */
@Controller 
@RequestMapping("/core/admin/short-msg")
public class ShortMsgController  {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	
	@Autowired
	private IShortMsgManager shortMsgManager ;
 
	
	
	/**
	 * 读取所有未读消息
	 * @param msgList 未读的消息列表
	 * @return 未读的消息列表
	 */
	@ResponseBody
	@RequestMapping(value="/list-new")
	public JsonResult listNew(){
		try{
			List<ShortMsg> msgList = this.shortMsgManager.listNotReadMessage();
			return JsonResultUtil.getObjectJson(msgList);
		}catch(Exception e ){
			e.printStackTrace();
			logger.error("获取消息出错", e);
			return JsonResultUtil.getErrorJson("获取消息出错");
		}
	}

	 
}

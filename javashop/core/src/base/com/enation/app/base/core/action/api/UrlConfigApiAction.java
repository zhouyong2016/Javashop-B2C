package com.enation.app.base.core.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.service.ISystemSettingManager;
import com.enation.framework.util.JsonUtil;

/**
 * url与server 对照系统配置Api 
 * 
 * @author Sylow
 * @version v2.0,2016年2月29日
 * @since v6.0
 */
@Controller
@RequestMapping("/api/base/config")
public class UrlConfigApiAction {
	
	@Autowired
	private ISystemSettingManager systemSettingManager;
	
	/**
	 * 获得一些url对照表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-url-config",produces = MediaType.APPLICATION_JSON_VALUE)
	public String get(){
		
		Map<String,String> map = new HashMap<String,String>();
		List<Map> list = this.systemSettingManager.getUrlConfig();
		
		for(Map tempMap : list) {
			String url = tempMap.get("url").toString();
			String server = tempMap.get("server").toString();
			map.put(url, server);
		}
		
		String json = "var URL_SETTING = " + JsonUtil.MapToJson(map);
		return json;
	}
}

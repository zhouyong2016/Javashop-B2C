package com.enation.app.base.core.action.api;

import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.util.JsonResultUtil;
/**
 * 地区API
 * @author lina 2014-2-21 version 1.0
 * @author kanon 2015-9-22 version 1.1 添加注释
 * @author Kanon 2016-2-25; 6.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/api/base/region")
public class RegionApiController {
	
	@Autowired
	private IRegionsManager regionsManager;
	/**
	 * 获取该地区的子
	 * @param regionid 地区Id int型
	 * @return 地区子列表JSON
	 */
	@ResponseBody
	@RequestMapping("/get-children")
	public Object getChildren(Integer regionid){
		//判断地区ID是否为空
		if(regionid==null){
			return JsonResultUtil.getErrorJson("缺少参数：regionid");
		}else{
			//获取地区子列表
			List list =regionsManager.listChildrenByid(regionid);
			return list;
		}
	}
	

}

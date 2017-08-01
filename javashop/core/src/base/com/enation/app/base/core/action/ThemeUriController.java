package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

@Controller 
@Scope("prototype")
@RequestMapping("/core/admin/themeUri")
/**
 * uri映射action
 * @author Kanon 2015-11-25 version 1.1 添加注释
 * @version 2.0   2016-2-24  6.0升级改造   wangxin
 */
public class ThemeUriController extends GridController {
	@Autowired
	private IThemeUriManager themeUriManager;
	
	/**
	 * 跳转至uri映射列表
	 * @return uri映射列表
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/core/admin/uri/list";
	}
	
	/**
	 * 获取uri映射列表JSON
	 * @param keyword 关键字
	 * @param uriList uri 列表
	 * @return uri映射列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GridJsonResult listJson(String keyword){
		Map map = new HashMap();
		map.put("keyword", keyword);
		List uriList  = themeUriManager.list(map);
		return JsonResultUtil.getGridJson(uriList);
	}
	
	/**
	 * 跳转至uri映射添加页面
	 * @return uri映射添加页面
	 */
	@RequestMapping(value="/add")
	public String add(){
		return "/core/admin/uri/add";
	}
	
	/**
	 * 跳转至uri映射修改页面
	 * @param id uri映射Id
	 * @param themeUri uri映射
	 * @return uri映射修改页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(int id){
		ModelAndView view = new ModelAndView();
		view.addObject("themeUri",this.themeUriManager.get(id));
		view.setViewName("/core/admin/uri/edit");
		return view;
	}
	
	/**
	 * 新增uri映射
	 * @param themeUri uri映射
	 * @return 新增状态
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(ThemeUri themeUri){
		try{
			this.themeUriManager.add(themeUri);
			return JsonResultUtil.getSuccessJson("添加成功");
		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("失败:"+e.getMessage());
		}
	}
	
	/**
	 * 保存修改uri映射
	 * @param themeUri uri映射
	 * @return 修改状态 
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(ThemeUri themeUri,int id){
		
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			if(id<=6){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			}
		}
		
		//保存修改uri映射
		try{
			this.themeUriManager.edit(themeUri);
			return JsonResultUtil.getSuccessJson("修改成功");
		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("修改失败:"+e.getMessage());
		}		
	}
	
	/**
	 * 批量修改
	 * @param uri uri映射数组
	 * @param ids uriId数组
	 * @param path  
	 * @param pagename 页面名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/batch-edit")
	public JsonResult batchEdit(String[] uri,int[] ids,String[] path,String[] pagename,int[] point,int[] httpcache){
		try{
			List<ThemeUri> uriList  = new ArrayList<ThemeUri>();
			if(uri!=null ){
				for(int i=0, len=uri.length;i<len;i++){
					ThemeUri themeUri  = new ThemeUri();
					themeUri.setUri( uri[i] );
					themeUri.setId(ids[i]);
					themeUri.setPath(path[i]);
					themeUri.setPagename(pagename[i]);
					if (point != null) {
						themeUri.setPoint(point[i]);
					}
					if (httpcache != null) {
						themeUri.setHttpcache(httpcache[i]);
					}
					uriList.add(themeUri);
				}
			}
			this.themeUriManager.edit(uriList);
			return JsonResultUtil.getSuccessJson("保存修改成功");
		}catch(RuntimeException e){
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("失败:"+e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(int id){
		
		if(EopSetting.IS_DEMO_SITE){
			if(id<=6){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能删除这些示例数据，请下载安装包在本地体验这些功能！");
			}
		}
		
		try{
			this.themeUriManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除成功");

		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());

		}
	}
	
	
	
}

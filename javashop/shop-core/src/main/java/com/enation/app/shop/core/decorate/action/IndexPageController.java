package com.enation.app.shop.core.decorate.action;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.decorate.service.IPageManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * 主页管理   已废弃
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/indexpage")
@SuppressWarnings(value={"rawtypes"})
@Deprecated
public class IndexPageController {
	@Autowired
	private IFloorManager floorManager;
	@Autowired
	private IPageManager pageManager;
	
	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 跳转主页管理
	 * @return url
	 */
	@RequestMapping(value="list")
	public String list(){
		return "/floor/admin/indexpage/list";
	}

	/**
	 * 添加楼层
	 * @param id 楼层id
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="add-floor")
	public ModelAndView addFloor(Integer id){
		ModelAndView view=new ModelAndView();
		view.addObject("pageid",id);
		view.addObject("actionName","save-add.do");
		view.setViewName("/floor/admin/indexpage/floor-input");
		return view;
	}

	/**
	 * 
	 * @param floor
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-add")
	public JsonResult saveAdd(Floor floor,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			if(file!=null){
				IUploader uploader=UploadFacatory.getUploaer();
				String logoPath=uploader.upload(file.getInputStream(), "floor", file.getOriginalFilename());
				floor.setLogo(logoPath);
			}
			this.floorManager.save(floor);
			return JsonResultUtil.getSuccessJson("楼层添加成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("楼层添加失败:"+e.getMessage());
		} catch (IOException e) {
			return JsonResultUtil.getErrorJson("楼层添加失败:"+e.getMessage());
		}
	}
	
	
	
	
	/**
	 * 加载第一个tab页
	 * @return spring ModelAndView
	 */ 
	@RequestMapping(value="indexpage-manager")
	public ModelAndView indexPageManager(){
		
		ModelAndView view=new ModelAndView();
		List indexPageList=this.pageManager.listPage(0);
//		freeMarkerPaser.putData("pageList",indexPageList);
		view.addObject("pageList",indexPageList);
		view.setViewName("/floor/admin/indexpage/indexpage-view");
		return view;
	}
	/**
	 * 加载第二个tab页
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="pc-manager")
	public String pcManager(){
		return "/floor/admin/indexpage/pcindex-list";
	}
	/**
	 * 加载第三个tab页
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="wap-manager")
	public String wapManager(){
		return "/floor/admin/indexpage/wapindex-list";
	}
}

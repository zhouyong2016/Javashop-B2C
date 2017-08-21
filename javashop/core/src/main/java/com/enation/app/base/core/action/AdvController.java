package com.enation.app.base.core.action;

import java.io.InputStream;
import java.util.List;

import org.apache.solr.client.solrj.impl.LBHttpSolrClient.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 后台广告管理
 * @author DMRain 2016年2月23日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/adv")
public class AdvController extends GridController{

	@Autowired
	private IAdColumnManager adColumnManager;

	@Autowired
	private IAdvManager advManager;

	/**
	 * 跳转至广告列表
	 * @return 广告列表页面
	 */
	@RequestMapping(value="/list")
	public ModelAndView list() {
		ModelAndView view = new ModelAndView();
		List adList=this.adColumnManager.listAllAdvPos();
		view.addObject("adList", adList);
		view.setViewName("/core/admin/adv/adv_list");
		return view;
	}

	/**
	 * 跳转至广告列表
	 * @return 广告列表页面
	 */
	@RequestMapping(value="/tlist")
	public ModelAndView tLlist() {
		String acid=ThreadContextHolder.getHttpRequest().getParameter("acid").toString();
		ModelAndView view = new ModelAndView();
		view.addObject("acid", acid);
		view.setViewName("/core/admin/adv/adv_tlist");
		return view;
	}

	/**
	 * 获取广告分页列表JSON
	 * @param advname 广告位名称
	 * @param order 排序
	 * @return 广告分页列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String advname, String order,Integer ac) {
		List<AdColumn> adColumnList = this.adColumnManager.listAllAdvPos();
		String str=ThreadContextHolder.getHttpRequest().getParameter("acid");
		Integer acid=0;
		if(!str.equals("")){
			acid=Integer.valueOf(str);
		}
		if(ac!=null && ac!=0){
			acid=ac;
		}
		this.webpage = this.advManager.search(advname,acid, this.getPage(), this.getPageSize(), order);
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 跳转至广告添加页面
	 * @param adColumnList 广告位列表
	 * @return 广告添加页面
	 */
	@RequestMapping(value="/add")
	public ModelAndView add() {
		ModelAndView view = new ModelAndView();
		view.addObject("adColumnList", this.adColumnManager.listAllAdvPos());
		view.setViewName("/core/admin/adv/adv_input");
		return view;
	}

	/**
	 * 保存新增广告
	 * @param pic 广告图片
	 * @param adv 广告
	 * @return 保存状态
	 */
	@ResponseBody
	@RequestMapping(value="/add-save")
	public JsonResult addSave(@RequestParam(value = "pic", required = false)MultipartFile pic,Adv adv) {
		if (pic != null) {
			if (FileUtil.isAllowUpImg(pic.getOriginalFilename())) {
				InputStream stream=null;
				try {
					stream=pic.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				}
				IUploader uploader=UploadFacatory.getUploaer();
				String path = uploader.upload(stream, "adv",pic.getOriginalFilename());
				adv.setAtturl(path);
			} else {
				return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
			}
		}

		adv.setDisabled("false");

		try {
			this.advManager.addAdv(adv);
			return JsonResultUtil.getSuccessJson("新增广告成功");
		} catch (RuntimeException e) {
			logger.error("新增广告失败", e);
			return JsonResultUtil.getErrorJson("新增广告失败");
		}
	}

	/**
	 * 跳转至广告详细页面
	 * @param adColumnList 广告位列表
	 * @param advid 广告ID
	 * @param adv 广告
	 * @param imgPath 广告图片地址
	 * @return 广告详细页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Adv adv, Long advid, String imgPath) {
		ModelAndView view = new ModelAndView();
		view.addObject("adColumnList", this.adColumnManager.listAllAdvPos());

		adv = this.advManager.getAdvDetail(advid);
		view.addObject("adv", adv);

		if (adv.getAtturl() != null && !StringUtil.isEmpty(adv.getAtturl())) {			
			imgPath = StaticResourcesUtil.convertToUrl(adv.getAtturl());
		}
		view.addObject("imgPath", imgPath);

		view.setViewName("/core/admin/adv/adv_edit");
		return view;
	}

	/**
	 * 保存修改广告
	 * @param pic 广告图片
	 * @param adv 广告
	 * @return 修改广告状态
	 */
	@ResponseBody
	@RequestMapping(value="/edit-save")
	public JsonResult editSave(@RequestParam(value = "pic", required = false)MultipartFile pic,Adv adv) {
		if (pic != null) {
			if (FileUtil.isAllowUpImg(pic.getOriginalFilename())) {
				InputStream stream=null;
				try {
					stream=pic.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				}
				IUploader uploader=UploadFacatory.getUploaer();
				String path = uploader.upload(stream, "adv",pic.getOriginalFilename());
				adv.setAtturl(path);
			} else {
				return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
			}
		}

		try {
			this.advManager.updateAdv(adv);
			return JsonResultUtil.getSuccessJson("修改广告成功");
		} catch (Exception e) {
			logger.error("修改广告失败", e);
			return JsonResultUtil.getErrorJson("修改广告失败");
		}
	}

	/**
	 * 停止广告
	 * @param advid 广告ID
	 * @param adv 广告
	 * @return 停止状态
	 */
	@ResponseBody
	@RequestMapping(value="/stop")
	public JsonResult stop(Adv adv, Long advid) {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(1);
		try {
			this.advManager.updateAdv(adv);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (RuntimeException e) {
			logger.error("停止广告失败", e);
			return JsonResultUtil.getErrorJson("操作失败");
		}
	}

	/**
	 * 开启广告
	 * @param advid 广告ID
	 * @param adv 广告
	 * @return 开启广告状态
	 */
	@ResponseBody
	@RequestMapping(value="/start")
	public JsonResult start(Adv adv, Long advid) {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(0);
		try {
			this.advManager.updateAdv(adv);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (RuntimeException e) {
			logger.error("开启广告失败", e);
			return JsonResultUtil.getErrorJson("操作失败");
		}
	}

	/**
	 * 删除广告
	 * @param aid 广告Id
	 * @return 删除状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] aid) {
		if (EopSetting.IS_DEMO_SITE) {
			for (Integer id : aid) {
				if (id <= 21) {
					return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				}
			}
		}

		try {
			this.advManager.delAdvs(aid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			logger.error("广告删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败");
		}

	}
}

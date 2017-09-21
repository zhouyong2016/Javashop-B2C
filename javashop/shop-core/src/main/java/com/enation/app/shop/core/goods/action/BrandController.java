package com.enation.app.shop.core.goods.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;


/**
 * 品牌action 负责品牌的添加和修改
 * 
 * @author apexking
 * @author LiFenLong 2014-4-1;4.0版本改造  
 * @author kingapex 2016.2.15;6.1版本改造
 */
@Controller 
@RequestMapping("/shop/admin/brand")
public class BrandController extends GridController {
	@Autowired
	private IBrandManager brandManager;

	/***
	 * 查看品牌是否被使用
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/check-used")
	public JsonResult checkUsed(Integer[] brand_id){
		 if(this.brandManager.checkUsed(brand_id) ){
			 return JsonResultUtil.getSuccessJson("已被使用");
		 }else{
			 return JsonResultUtil.getSuccessJson("未被使用");
		 }
	}

	/**
	 * 查看品牌名称是否重复
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkname")
	 public JsonResult checkname(Brand brand){
		 if(this.brandManager.checkname(brand.getName(),brand.getBrand_id())){
			 return JsonResultUtil.getSuccessJson("已存在");
		 }else{
			 return JsonResultUtil.getSuccessJson("不存在");
		 }
	 }
	
	/**
	 * 跳转至品牌添加页面
	 * @return
	 */
	@RequestMapping(value="/add")
	public String add() {
		return "/shop/admin/brand/brand_add";
	}
	
	
	/**
	 * 跳转至品牌列表页面
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list() {
		ModelAndView mv= this.getGridModelAndView();
		
		List<Map> brand_types= brandManager.queryAllTypeNameAndId();
		mv.addObject("brand_types", brand_types);
		mv.setViewName("/shop/admin/brand/brand_list");
		
		return mv;
	}
	
	/**
	 * 获取品牌JSON列表
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String keyword){
		Map brandMap= new HashMap();
		brandMap.put("keyword", keyword);
		Page webpage = brandManager.searchBrand(brandMap, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/***
	 * 后台品牌列表搜索
	 * @return
	 */
	@RequestMapping(value="/search")
	public String search() {
		return "/shop/admin/brand/brand_list";
	}

	
	/**
	 * 保存品牌添加
	 * @param brand
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save")
	public JsonResult save(Brand brand, @RequestParam(value = "file", required = false) MultipartFile file){
		try{
			if (file!=null) {
				
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					InputStream stream=null;
					try {
						stream=file.getInputStream();
					} catch (Exception e) {
						e.printStackTrace();
					}
					//上传分类图片
					IUploader uploader=UploadFacatory.getUploaer();
					//上传分类图片
					brand.setLogo(uploader.upload(stream, "brand",file.getOriginalFilename()));
				}
				
			}
			brand.setDisabled(0);
			this.brandManager.add(brand);
			return JsonResultUtil.getSuccessJson("品牌添加成功");
		}catch(Exception e){
			
			this.logger.error("品牌添加异常", e);
			return JsonResultUtil.getErrorJson("品牌修改失败");
		}
	}

	
	/**
	 * 跳转至品牌修改页面
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer brandId){
		ModelAndView view = new ModelAndView();
		Brand brand=brandManager.get(brandId);
		view.addObject("brand",brand);
		if(brand.getLogo()!=null&&!StringUtil.isEmpty(brand.getLogo())){			
			view.addObject("imgPath",StaticResourcesUtil.convertToUrl(brand.getLogo()));
			view.addObject("logo",new File(StaticResourcesUtil.convertToUrl(brand.getLogo())));
		}
		view.setViewName("/shop/admin/brand/brand_edit");
		return view;
	}
	

	/**
	 * 保存修改品牌
	 * @author xulipeng
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(@RequestParam(value = "file", required = false) MultipartFile file ,Brand brand) {
		if (file!=null) {
			if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
				return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
			}else{
				InputStream stream=null;
				try {
					stream=file.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				}
				IUploader uploader=UploadFacatory.getUploaer();
				//上传分类图片
				brand.setLogo(uploader.upload(stream, "brand",file.getOriginalFilename()));
			}
			
		}
		brandManager.update(brand);
		return JsonResultUtil.getSuccessJson("品牌修改成功");
	}

	/**
	 * 将品牌放入回收站
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] brand_id) {
		try {
			this.brandManager.delete(brand_id);
			
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.logger.error("删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());
		}
	 
	}

	
	/**
	 * 将品牌从回收站中还原
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/revert")
	public JsonResult revert(Integer[] brand_id) {
		try {
			brandManager.revert(brand_id);
			return JsonResultUtil.getSuccessJson("还原成功");
		} catch (RuntimeException e) {
			this.logger.error("还原失败", e);
			return JsonResultUtil.getErrorJson("还原失败:"+e.getMessage());
		}
	}

	/**
	 * 清空回收站中的品牌
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/clean")
	public JsonResult clean(Integer[] brand_id) {
		try{
			brandManager.clean(brand_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());
		 }
	}
	
}

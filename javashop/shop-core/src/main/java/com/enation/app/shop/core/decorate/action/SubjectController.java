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
import com.enation.app.shop.core.decorate.model.Subject;
import com.enation.app.shop.core.decorate.service.ISubjectManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;

/**
 * 专题管理controller
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/subject")
public class SubjectController extends GridController {
	
	@Autowired
	private ISubjectManager subjectManager;
	
	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 跳转专题列表
	 * @return
	 */
	@RequestMapping(value="list")
	public ModelAndView list(){
		ModelAndView view=getGridModelAndView();
		view.setViewName("/floor/admin/subject/list");
		return view;
	}
	
	/**
	 * 获取全部专题信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="list-json")
	public GridJsonResult listJson(){
		List list=this.subjectManager.listAll();
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 跳转专题添加
	 * @return
	 */
	@RequestMapping(value="add-subject")
	public ModelAndView addSubject(){
		ModelAndView view=new ModelAndView();
		view.addObject("actionName", "save-add.do");
		view.setViewName("/floor/admin/subject/add-subject");
		return view;
	}
	
	/**
	 * 保存专题
	 * @param subject 专题实体
	 * @param file 横幅文件
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-add")
	public JsonResult saveAdd(Subject subject,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			if(file!=null){
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					String bannerPath=uploader.upload(file.getInputStream(), "subject", file.getOriginalFilename());
					subject.setBanner(bannerPath);
				}
			}
			this.subjectManager.save(subject);
			return JsonResultUtil.getSuccessJson("专题添加成功");
		} catch (RuntimeException e) {
			this.logger.error("添加专题信息失败",e);
			return JsonResultUtil.getErrorJson("专题添加失败:"+e.getMessage());
		} catch (IOException e) {
			this.logger.error("添加专题信息失败",e);
			return JsonResultUtil.getErrorJson("专题添加失败:"+e.getMessage());
		}
	}

	/**
	 * 保存排序
	 * @param id 专题id
	 * @param sort 排序
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-sort")
	public JsonResult saveSort(Integer[] subject_ids,Integer[] subject_sorts){
		try {
			this.subjectManager.saveSort(subject_ids,subject_sorts);
			return JsonResultUtil.getSuccessJson("保存排序成功");
		} catch (RuntimeException e) {
			this.logger.error("保存专题排序失败",e);
			return JsonResultUtil.getErrorJson("保存排序失败");
		}
	}
	
	/**
	 * 保存显示状态
	 * @param id 专题id
	 * @param is_display 显示状态
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-display")
	public JsonResult saveDisplay(Integer id,Integer is_display){
		try {
			this.subjectManager.saveDisplay(id,is_display);
			return JsonResultUtil.getSuccessJson("保存显示状态成功");
		} catch (RuntimeException e) {
			this.logger.error("保存专题显示状态失败",e);
			return JsonResultUtil.getErrorJson("保存显示状态失败");
		}
	}
	
	/**
	 * 跳转编辑专题
	 * @param id 专题id
	 * @return
	 */
	@RequestMapping(value="edit-subject")
	public ModelAndView editSubject(Integer id){
		ModelAndView view=new ModelAndView();
		Subject subject=this.subjectManager.getSubjectByIdAboveAll(id);
		view.addObject("subject", subject);
		view.addObject("actionName","save-edit.do");
		view.setViewName("/floor/admin/subject/edit-subject");
		return view;
	}
	
	/**
	 * 保存编辑
	 * @param subject 专题实体
	 * @param file 横幅文件
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-edit")
	public JsonResult saveEdit(Subject subject,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			if(file!=null){
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					String bannerPath=uploader.upload(file.getInputStream(), "subject", file.getOriginalFilename());
					subject.setBanner(bannerPath);
				}
			}
			this.subjectManager.saveEdit(subject);
			return JsonResultUtil.getSuccessJson("保存专题成功");
		} catch (Exception e) {
			this.logger.error("修改专题失败",e);
			return JsonResultUtil.getErrorJson("保存专题失败");
		}
	}
	
	/**
	 * 删除专题
	 * @param id 专题id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete")
	public JsonResult delete(Integer id){
		try {
			this.subjectManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除专题成功");
		} catch (RuntimeException e) {
			this.logger.error("删除专题失败",e);
			return JsonResultUtil.getErrorJson("删除专题失败");
		}
	}
	
	/**
	 * 跳转专题设计页面
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="design")
	public ModelAndView design(Integer id){
		ModelAndView view=new ModelAndView();
		Subject subject=this.subjectManager.getSubjectByIdAboveAll(id);
		view.addObject("subject", subject);
		view.setViewName("/floor/admin/subject/design");
		return view;
	}
	
	/**
	 * 为专题添加商品
	 * @param subject_id 专题id
	 * @return
	 */
	@RequestMapping(value="add-goods")
	public ModelAndView addGoods(Integer subject_id){
		ModelAndView view=new ModelAndView();
		view.addObject("subject_id", subject_id);
		view.setViewName("/floor/admin/subject/add-goods");
		return view;
	}
	
	
	/**
	 * 保存商品信息
	 * @param subject_id  专题id
	 * @param goods_ids   商品id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-add-goods")
	public JsonResult saveAddGoods(Integer subject_id,Integer[] goods_ids){
		try {
			this.subjectManager.saveGoodsIds(subject_id,goods_ids);
			return JsonResultUtil.getSuccessJson("保存商品成功");
		} catch (Exception e) {
			this.logger.error("保存专题商品信息失败",e);
			return JsonResultUtil.getErrorJson("保存商品失败");
		}
	}
	
	/**
	 * 跳转商品编辑页面
	 * @param subject_id 专题id
	 * @param index 商品位置索引
	 * @return
	 */
	@RequestMapping(value="edit-goods")
	public ModelAndView editGoods(Integer subject_id,Integer index){
		ModelAndView view=new ModelAndView();
		view.addObject("subject_id", subject_id);
		view.addObject("index",index);
		view.setViewName("/floor/admin/subject/edit-goods");
		return view;
	}
	
	/**
	 * 获取已选择商品
	 * @param subject_id 专题id
	 * @param index 商品位置索引
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="get-selected-goods")
	public GridJsonResult getSelectedGoods(Integer subject_id,Integer index){
		Subject subject=this.subjectManager.getSubjectById(subject_id);
		String goods_ids=subject.getGoods_ids();
		List goodsList=this.subjectManager.getSelectedGoodsByGoodsIds(goods_ids,index);
		return JsonResultUtil.getGridJson(goodsList);
	}
	/**
	 * 保存商品编辑
	 * @param subject_id  专题id	
	 * @param goods_ids   商品id
	 * @param index 商品位置索引
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-edit-goods")
	public JsonResult saveEditGoods(Integer subject_id,Integer[] goods_ids,Integer index){
		try {
			this.subjectManager.saveEditGoods(subject_id,goods_ids,index);
			return JsonResultUtil.getSuccessJson("修改成功");
		} catch (RuntimeException e) {
			this.logger.error("修改专题商品信息失败",e);
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}
	
	/**
	 * 删除商品
	 * @param subject_id 专题id
	 * @param index 商品位置索引
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete-goods")
	public JsonResult deleteGoods(Integer subject_id,Integer index){
		try {
			this.subjectManager.deleteGoods(subject_id,index);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			this.logger.error("删除专题商品失败",e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 新增图片
	 * @param subject_id 专题id
	 * @return
	 */
	@RequestMapping(value="add-image")
	public ModelAndView addImage(Integer subject_id){
		ModelAndView view=new ModelAndView();
		view.addObject("subject_id", subject_id);
		view.setViewName("/floor/admin/subject/add-image");
		return view;
	}
	
	/**
	 * 保存图片
	 * @param id 专题id
	 * @param file 上传的文件
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-add-image")
	public JsonResult saveAddImage(Integer id,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			
			String imagePath=null;
			if(file!=null){
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					imagePath=uploader.upload(file.getInputStream(), "subject", file.getOriginalFilename());
				}
			}
			
			this.subjectManager.saveImage(id,imagePath);
			return JsonResultUtil.getSuccessJson("新增图片成功");
		} catch (Exception e) {
			this.logger.error("添加专题图片失败",e);
			return JsonResultUtil.getErrorJson("新增图片失败");
		}
	}
	
	/**
	 * 删除图片
	 * @param subject_id 专题id
	 * @param index 图片位置
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete-image")
	public JsonResult deleteImage(Integer subject_id,Integer index){
		//TODO 这是否需要把服务器上的图片删除
		try {
			this.subjectManager.deleteImage(subject_id,index);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			this.logger.error("删除专题图片失败",e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 保存图片编辑
	 * @param id 专题id
	 * @param file 上传的文件
	 * @param index 图片位置索引
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-edit-image")
	public JsonResult saveEditImage(Integer id,@RequestParam(value = "file", required = false) MultipartFile file,Integer index){
		try {
			
			String imagePath=null;
			if(file!=null){
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					imagePath=uploader.upload(file.getInputStream(), "subject", file.getOriginalFilename());
				}
			}
			
			this.subjectManager.saveEditImage(id,imagePath,index);
			return JsonResultUtil.getSuccessJson("编辑图片成功");
		} catch (Exception e) {
			this.logger.error("修改专题图片失败",e);
			return JsonResultUtil.getErrorJson("编辑图片失败");
		}
	}
	
}

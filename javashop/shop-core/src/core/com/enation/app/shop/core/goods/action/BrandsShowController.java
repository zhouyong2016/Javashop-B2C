package com.enation.app.shop.core.goods.action;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IBrandsTagManager;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/brands-show")
/**
 * 
 * 品牌标签关联
 * @author	Chopper
 * @version	v1.0, 2016-1-6 下午5:52:58
 * @since
 * @author Kanon 2016.2.15;6.1版本改造
 */
public class BrandsShowController extends GridController {
	 
	@Autowired
	private IBrandManager brandManager;
	
	@Autowired
	private ITagManager tagManager;  
	
	@Autowired
	private IBrandsTagManager brandsTagManager;
	
	
	/**
	 * 分类页跳转
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/shop/admin/brandsshow/taglist";
	}
	/**
	 * 品牌页跳转
	 * @return
	 */
	@RequestMapping(value="/brand-list")
	public ModelAndView brandlist(Integer tag_id){ 
		ModelAndView view=getGridModelAndView();
		view.addObject("tag_id",tag_id);
		view.setViewName("/shop/admin/brandsshow/brandlist");
		
		return view;
	}

	/**
	 * 返回json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){ 
		
		return JsonResultUtil.getGridJson(tagManager.list(this.getPage(),this.getPageSize(),1));
	}
	/**
	 * 返回json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json-brand")
	public GridJsonResult listJsonBrand(int tag_id){ 
		return JsonResultUtil.getGridJson(brandManager.listBrands(tag_id, this.getPage(), this.getPageSize()));
	}
	
	
	/**
	 * 删除关联品牌
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/del")
	public JsonResult del( int id,int tag_id){
		try {
			brandsTagManager.del(tag_id, id); 
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 添加品牌页面
	 * @return
	 */
	@RequestMapping(value="/search")
	public ModelAndView search(Integer tag_id){
		ModelAndView view=getGridModelAndView();
		view.addObject("tag_id",tag_id);
		view.setViewName("/shop/admin/brandsshow/search_list");
		return view;
	} 
	
	/**
	 * 添加品牌
	 */
	@ResponseBody
	@RequestMapping(value="/add")
	public JsonResult add(int tag_id,int[] brand_id){
		try {
			brandsTagManager.add(tag_id, brand_id);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (Exception e) {  
			return JsonResultUtil.getErrorJson("操作失败");
		}
	}
	
	/**
	 * 保存排序
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-order")
	public JsonResult saveOrder(int tag_id,int[] brand_id,int[] brand_num){
		try {
			brandsTagManager.saveOrder(tag_id,brand_id , brand_num);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("保存失败");
		}
	} 
	
}

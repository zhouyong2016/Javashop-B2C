package com.enation.app.shop.core.goods.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.service.IGoodsTagManager;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
/**
 * 首页显示和列表推荐的统一管理
 * @author kingapex
 * @author Kanon 2016-2-22 ;6.0版本改造
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/goods-show")
public class GoodsShowController extends GridController {

	@Autowired
	private ITagManager tagManager;
	
	@Autowired
	private IGoodsTagManager goodsTagManager;
	
	
	
	/**
	 * 商品标签列表
	 * @return 商品标签列表
	 */
	@RequestMapping(value="/tag-list")
	public String tagList(){
		return "/shop/admin/goodsshow/taglist";
	}
	
	/**
	 * 获取商品标签列表json
	 * @author LiFenLong
	 * @param taglist 标签列表,List
	 * @return 商品标签列表json
	 */
	@ResponseBody
	@RequestMapping(value="/tag-list-json")
	public GridJsonResult tagListJson(){
		Page page = tagManager.list(this.getPage(),this.getPageSize(),0);
		return JsonResultUtil.getGridJson(page);
	}
	
	/**
	 * 显示   商品标签    商品列表
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Integer tagid) {
		ModelAndView view=getGridModelAndView();
		view.addObject("tagid", tagid);
		view.setViewName("/shop/admin/goodsshow/list");
		return view;
	}
	
	/**
	 * 商品标签    商品列表json
	 * @param catid 商品分类Id,Integer
	 * @param tagid 标签Id,Integer
	 * @return 商品标签    商品列表json
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(@RequestParam(value = "catid", required = false) Integer catid,Integer tagid){
		
		if (catid == null || catid.intValue() == 0) {
			webpage = goodsTagManager.getGoodsList(tagid, this.getPage(), this.getPageSize());
		} else {
			webpage = goodsTagManager.getGoodsList(tagid, catid.intValue(), this.getPage(), this.getPageSize());
		}
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 跳转至标签商品选择列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/search")
	public ModelAndView search(Integer tagid) {
		ModelAndView view=getGridModelAndView();
		view.addObject("tagid", tagid);
		view.setViewName( "/shop/admin/goodsshow/search_list");
		return view;
	}

	/**
	 * 批量添加标签
	 * @param goods_id 商品Id,Integer[]
	 * @param tagid 标签Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/batch-add")
	public JsonResult batchAdd(Integer tagid,Integer[] goods_id) {
		try {
			if (goods_id != null && goods_id.length > 0) {
				for (Integer goodsId : goods_id) {
					goodsTagManager.addTag(tagid, goodsId);
				}
			}
			return JsonResultUtil.getSuccessJson("添加成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("添加失败");
		}
	}

	/**
	 * 删除一条记录
	 * @param tagid 标签Id,Integer
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer tagid,Integer goodsid) {
		try {
			goodsTagManager.removeTag(tagid, goodsid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}

	/**
	 * 批量更新排序数字
	 * @param goods_id 商品Id数组,Integer[]
	 * @param tagids 标签数组,Integer[]
	 * @param ordernum 排序数组,Integer[]
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-ordernum")
	public JsonResult saveOrdernum(Integer catid,Integer[] goods_id,Integer[] tagids,Integer[] ordernum) {
		try {
			goodsTagManager.updateOrderNum(goods_id, tagids, ordernum);
			int tempCatId = catid == null ? 0 : catid.intValue();
			return JsonResultUtil.getSuccessJson("保存排序成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("保存排序失败");
		}
	}
}

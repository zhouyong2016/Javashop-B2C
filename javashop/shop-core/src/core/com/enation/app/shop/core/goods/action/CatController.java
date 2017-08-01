package com.enation.app.shop.core.goods.action;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.PermssionRuntimeException;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
/**
 * 商品分类action
 * @author apexking
 * @author xulipeng—修改 2014年4月9日17:54:39
 * @author Kanon 2016.2.15;6.0版本改造
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/cat")
public class CatController {

	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IGoodsTypeManager goodsTypeManager;

	/**
	 * 分类列表
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list() {
		return "/shop/admin/cat/cat_list";
	}

	/**
	 * 全部分类列表json数据，
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public String listJson(Integer parentid) {
		List<Cat> catList = goodsCatManager.listAllChildren(parentid);
		String s = JSONArray.fromObject(catList).toString();
		return s.replace("name", "text").replace("cat_id", "id");
	}
	
	/**
	 * 检测类别是否重名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkname")
	public JsonResult checkname(Cat cat) {
		if (this.goodsCatManager.checkname(cat.getName(), cat.getCat_id())) {
			return JsonResultUtil.getSuccessJson("");
		} else {
			return JsonResultUtil.getErrorJson("");
		}
	}

	
	/**
	 * 异步加载分类列表json数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-list-by-parentid-json")
	public String getlistByParentidJson(Integer parentid){
		
		List<Map> catList = goodsCatManager.getListChildren(parentid);
		String s = JSONArray.fromObject(catList).toString();
		return s.replace("name", "text").replace("cat_id", "id");
	}
	
	/**
	 * 获取全部分类JSON列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/add-list-json")
	public String addlistJson() {
		List<Cat> addlist = goodsCatManager.listAllChildren(0);
		String s = JSONArray.fromObject(addlist).toString();
		
		return s.replace("name", "text").replace("cat_id", "id");
		
	}
	
	/**
	 * 获取商品类型JSON列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/type-list-json")
	public String typelistjson() {
		String s = JSONArray.fromObject(goodsTypeManager.listAll()).toString();
		return s.replace("name", "text").replace("type_id", "id");
	}

	/**
	 * 跳转至分类添加页面
	 * @return
	 */
	@RequestMapping(value="/add")
	public String add() {
		return "/shop/admin/cat/cat_add";
	}

	/**
	 * 跳转至子分类添加页面
	 * @return
	 */
	@RequestMapping(value="/add-children")
	public ModelAndView addChildren(Integer cat_id) {
		ModelAndView view = new ModelAndView();
		view.addObject("cat",goodsCatManager.getById(cat_id));
		view.setViewName("/shop/admin/cat/cat_add_children");
		return view;
	}
	
	/**
	 * 跳转至分类修改页面
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer cat_id) {
		ModelAndView view = new ModelAndView();
		
		Cat cat=goodsCatManager.getById(cat_id);
		
		if (cat.getImage() != null && !StringUtil.isEmpty(cat.getImage())) {
			view.addObject("imgPath",StaticResourcesUtil.convertToUrl(cat.getImage()));
		}
		view.addObject("typeList",goodsTypeManager.listAll());
		view.addObject("catList",goodsCatManager.listAllChildren(0));
		view.addObject("cat",cat);
		view.setViewName("/shop/admin/cat/cat_edit");
		return view;
	}

	/**
	 * 保存新增商品分类
	 * @author xulipeng
	 * 2014年4月9日17:53:13
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Integer cattype,Cat cat,@RequestParam(value = "file", required = false) MultipartFile file) {
		
		if(EopSetting.IS_DEMO_SITE ){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
		}
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
				cat.setImage(uploader.upload(stream, "goodscat",file.getOriginalFilename()));
			}
			
		}
		
		if(cattype==1){
			cat.setParent_id(0);
		}
		cat.setGoods_count(0);
		//得到父级对象   如果大于当前级别大于三级 则返回提示
		Cat parent_cat=goodsCatManager.getById(cat.getParent_id());
		if(parent_cat != null){
			//替换cat_path 根据cat_path规则来匹配级别
			String cat_path=parent_cat.getCat_path().replace("|", ",");	
			String [] str=cat_path.split(",");
			//如果当前的cat_path length 大于4 证明当前分类级别大于五级 提示
			if(str.length >= 4){
				return JsonResultUtil.getErrorJson("最多为三级分类,添加失败");
			}
		}
		goodsCatManager.saveAdd(cat);
		return JsonResultUtil.getSuccessJson("商品分类添加成功");
	}

	/**
	 * 保存修改商品分类
	 * @author xulipeng
	 * 2014年4月9日17:53:13
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Cat cat,Integer cattype,@RequestParam(value = "file", required = false) MultipartFile file) {
	
			if(EopSetting.IS_DEMO_SITE ){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
			}
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
					cat.setImage(uploader.upload(stream, "goodscat",file.getOriginalFilename()));
				}
				
			}
			
		try {
			//如果选择是顶级分类那么设置父类id为0
			if(cattype==1){
				cat.setParent_id(0);
			}
			if (cat.getParent_id().intValue() == 0) {
				this.goodsCatManager.update(cat);
				return JsonResultUtil.getSuccessJson("商品分类修改成功");
			}
			Cat targetCat = goodsCatManager.getById(cat.getParent_id());// 将要修改为父分类的对象
			//如果是子分类，且是显示状态
			if(cat.getList_show().equals("1")&&targetCat.getList_show().equals("0")){
				return JsonResultUtil.getErrorJson("保存失败：上级分类为未显示状态");
			}
			if (cat.getParent_id().intValue() == cat.getCat_id().intValue()
					|| targetCat.getParent_id().intValue() == cat.getCat_id()
							.intValue()) {
				return JsonResultUtil.getErrorJson("保存失败：上级分类不能选择当前分类或其子分类");
			} else {
				this.goodsCatManager.update(cat);
				return JsonResultUtil.getSuccessJson("商品分类修改成功");
			}
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("非法操作");
		}

	}

	/**
	 * 删除商品分类
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer cat_id) {

		try {
			if(EopSetting.IS_DEMO_SITE && cat_id <93){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			}
		
			int r = this.goodsCatManager.delete(cat_id);
			if (r == 0) {
				return JsonResultUtil.getSuccessJson("删除成功");
			} else if (r == 1) {
				return JsonResultUtil.getErrorJson("此类别下存在子类别不能删除");
			} else if (r == 2) {
				return JsonResultUtil.getErrorJson("此类别下存在商品不能删除!");
			}
		} catch (PermssionRuntimeException ex) {
			return JsonResultUtil.getErrorJson("非法操作!");
		}
		return null;
	}

	/**
	 * 获取子分类的Json
	 */
	@ResponseBody
	@RequestMapping(value="/get-child-json")
	public Object getChildJson(Integer cat_id,Integer cattype) {

		try {
			return JsonMessageUtil.getListJson(this.goodsCatManager.listChildren(cat_id));
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 保存商品分类排序
	 * @param cat_ids	分类id 数组,用于保存排序
	 * @param cat_sorts	分类排序值
	 */
	@ResponseBody
	@RequestMapping(value="/save-sort")
	public JsonResult saveSort(int[] cat_ids,int[] cat_sorts) {
		this.goodsCatManager.saveSort(cat_ids, cat_sorts);
		return JsonResultUtil.getSuccessJson("保存成功");
	}


}

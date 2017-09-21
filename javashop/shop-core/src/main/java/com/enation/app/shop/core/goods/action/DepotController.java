package com.enation.app.shop.core.goods.action;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
/**
 * 库房管理Action
 * @author LiFenLong 2014-4-2;4.0版本改造
 * @author Kanon 2016.2.15;6.0版本改造
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/depot")
public class DepotController extends GridController {


	@Autowired
	private IDepotManager depotManager;
	
	/**
	 * 跳转仓库列表
	 * @return 仓库列表页面
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/shop/admin/depot/list";
	}
	
	/**
	 * 获取仓库列表json
	 * @param depotList 仓库列表
	 * @return 仓库列表json
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List list=this.depotManager.list();
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 跳转添加仓库页面
	 * @return 添加仓库页面
	 */
	@RequestMapping(value="/add")
	public String add(){
		return "/shop/admin/depot/add";
	}
	
	/**
	 * 跳转修改仓库页面
	 * @param id 仓库Id,Integer
	 * @param room 仓库对象,Depot
	 * @return 修改仓库页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(int id){
		
		ModelAndView view = new ModelAndView();
		view.addObject("room",this.depotManager.get(id));
		view.setViewName("/shop/admin/depot/edit");
		return view;
	}
	
	/**
	 * 添加仓库
	 * @param room 仓库对象,Depot
	 * @return JsonResult
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Depot room){
		try {
			this.depotManager.add(room);
			return JsonResultUtil.getSuccessJson("仓库新增成功");
		} catch (Exception e) {
			logger.error("仓库新增失败", e);
			return JsonResultUtil.getErrorJson("仓库新增失败"+e.getMessage());
		}
	}
	
	/**
	 * 修改仓库
	 * @param room 仓库对象,Depot
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Depot room){
		try {
			Integer identy = 0;
			if(room.getChoose() == 0){
				List<Depot> list=this.depotManager.list();
				//标识仓库中是否存在默认仓库，如果要将仓库是否默认修改成否，检测仓库中是否存在默认仓库，否则无法修改
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getId() != room.getId()){
						if(list.get(i).getChoose() == 1){
							identy = 1;
						}
					}
				}
				if(identy == 0){
					return JsonResultUtil.getErrorJson("必需存在一个默认仓库");
				}
			}
			
			this.depotManager.update(room);
			return JsonResultUtil.getSuccessJson("修改仓库成功");
		} catch (Exception e) {
			logger.error("修改仓库失败", e);
			return JsonResultUtil.getErrorJson("修改仓库失败"+e.getMessage());
		}
	}
	
	/**
	 * 删除仓库
	 * @param id 仓库Id
	 * @param message 判断仓库是否可以删除： 如果仓库为默认仓库，不能删除
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(int id){
		try {
			String message= this.depotManager.delete(id);
			if(message.equals("删除成功")){
				return JsonResultUtil.getSuccessJson("删除成功");
			}else{
				return JsonResultUtil.getErrorJson(message);
			}
		} catch (Exception e) {
			logger.error("仓库删除失败", e);
			return JsonResultUtil.getErrorJson("仓库删除失败");
		}
	}

}

package com.enation.app.shop.core.other.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.other.model.ReceiptContent;
import com.enation.app.shop.core.other.service.IReceiptContentManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * 后台发票信息管理
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年7月1日 下午5:35:46
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/receipt")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReceiptController extends GridController {

	@Autowired
	private IReceiptContentManager receiptContengManager;
	
	/**
	 * 跳转到发票信息页
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list() {
		ModelAndView view =this.getGridModelAndView();
		view.addObject("receiptContentList", this.receiptContengManager.listReceiptContent());
		view.setViewName("/shop/admin/receipt/receipt_content_list");
		return view;
	}
	
	/**
	 * 查询发票内容json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson() {
		
		webpage = receiptContengManager.getAllReceiptContent(this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);

	}
	
	/**
	 * 删除发票内容
	 * @param contentid 发票内容Id
	 * @return 修改状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer contentid){
		
		try {
			this.receiptContengManager.delete(contentid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			logger.error("删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败["+ e.getMessage() + "]");
		}
	}
	/**
	 * 跳转到发票添加页
	 * @return
	 */
	@RequestMapping(value="/add")
	public ModelAndView add() throws Exception {
		ModelAndView view = new ModelAndView();
		view.addObject("receiptContentList", this.receiptContengManager.listReceiptContent());
		view.setViewName("/shop/admin/receipt/receipt_content_add");
		return view;
	}
	/**
	 * 新增发票内容
	 * @param receiptContent 发票内容
	 * @return 新增状态
	 */
	@ResponseBody
	@RequestMapping(value="/add-save")
	public JsonResult addSave(ReceiptContent receiptContent) throws Exception {
		Integer count = receiptContengManager.checkLast();
		if(count >= 5){
			return JsonResultUtil.getErrorJson("最多可添加5个发票内容!");
		}
		try{
			/**判断是否发票内容重名*/
			boolean flag = this.receiptContengManager.is_exist(receiptContent);
			if(flag){
				return JsonResultUtil.getErrorJson("发票内容已存在,添加失败!");
			}else{
				receiptContengManager.saveAdd(receiptContent);
				return JsonResultUtil.getSuccessJson("新增发票内容成功");
			}
		 } catch (RuntimeException e) {
			 e.printStackTrace();
			    logger.error("新增发票内容失败", e);
				return JsonResultUtil.getErrorJson("新增发票内容失败["+ e.getMessage() + "]");
		 }	

	}
	
	/**
	 * 跳转至发票内容修改页面
	 * @param contentid 发票内容ID
	 * @param receiptContent 发票内容
	 * @return 
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer contentid,ReceiptContent receiptContent) throws Exception {
		ModelAndView view = new ModelAndView();
		view.addObject("receiptContent",receiptContengManager.get(contentid));
		view.setViewName("/shop/admin/receipt/receipt_content_edit");
		return view;
		
	}
	
	/**
	 * 发票内容修改
	 * @param receiptContent 发票内容
	 * @return 修改状态
	 */
	@ResponseBody
	@RequestMapping(value="/edit-save")
	public JsonResult editSave(ReceiptContent receiptContent) throws Exception {
		
		try {
			this.receiptContengManager.saveEdit(receiptContent);
			return JsonResultUtil.getSuccessJson("修改发票内容成功");
			} catch (RuntimeException e) {
				e.printStackTrace();
				logger.error(e,e.fillInStackTrace());
				return JsonResultUtil.getErrorJson("修改发票内容失败["+ e.getMessage() + "]");
				}
		}
	
	
	
	
	/**
	 * 跳转到历史发票页
	 * 
	 */
	@RequestMapping(value="/history-list")
	public ModelAndView hostoryList() {
		ModelAndView view = new ModelAndView();
		view.setViewName("/shop/admin/receipt/receipt_history_list");
		return view;
	}
	/**
	 * 获取历史发票的json
	 * @return 历史发票的json
	 */
	@ResponseBody
	@RequestMapping(value="/history-list-json")
	public GridJsonResult historyListJson() {
		webpage = receiptContengManager.getHistoryReceipt(this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);

	}
	
	
	/**
	 * 跳转至历史发票详情页面
	 * @param sn 订单编号
	 * @return 
	 */
	@RequestMapping(value="/view-history")
	public ModelAndView viewHistory() throws Exception {
		ModelAndView view = new ModelAndView();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String order_id = request.getParameter("order_id");
		view.addObject("orderReceipt",receiptContengManager.getHistory(order_id));
		view.setViewName("/shop/admin/receipt/receipt_history_view");
		return view;
		
	}
	
	
	
	}

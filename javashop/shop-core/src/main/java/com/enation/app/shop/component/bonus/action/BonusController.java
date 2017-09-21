package com.enation.app.shop.component.bonus.action;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 红包管理
 * @author DMRain 2016年3月14日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/bonus")
public class BonusController extends GridController{

	@Autowired
	private IBonusManager bonusManager;
	
	@Autowired
	private IBonusTypeManager bonusTypeManager;
	
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@RequestMapping(value = "/send")
	public ModelAndView send(Integer typeid){
		Long use_end_date = bonusTypeManager.get(typeid).getUse_end_date();
		long now  = DateUtil.getDateline();
		if (use_end_date < now) {
			throw new RuntimeException("抱歉，优惠券使用结束时间不能小于当前时间");
		}
		ModelAndView view = new ModelAndView();
		view.addObject("typeid", typeid);
		
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		view.addObject("bonusType", bonusType);
		
		Integer send_type = bonusType.getSend_type();
		view.addObject("send_type", send_type);
		
		if (send_type == 0) {
			List lvList = this.memberLvManager.list();
			view.addObject("lvList", lvList);
			view.setViewName("/shop/admin/bonus/send_for_member");
		} else if (send_type == 1) {
			view.setViewName("/shop/admin/bonus/send_for_goods");
		} else if (send_type == 2) {
			view.setViewName("/shop/admin/bonus/send_for_order");
		} else if (send_type == 3) {
			view.setViewName("/shop/admin/bonus/send_for_offline");
		} else {
			view.setViewName("/shop/admin/bonus/send_for_member");
		}
		
		return view;
	}
	
	/**
	 * 读取某类型的红包列表
	 * @return
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Integer typeid, Integer send_type){
		ModelAndView view = new ModelAndView();
		view.addObject("typeid", typeid);
		view.addObject("send_type", send_type);
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/shop/admin/bonus/bonus_list");
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "/list-json")
	public GridJsonResult listJson(Integer typeid){
		this.webpage = this.bonusManager.list(this.getPage(), this.getPageSize(), typeid);
		return JsonResultUtil.getGridJson(webpage);
	}
	/**
	 * 按照会员等级发送红包
	 * 发送成功返回已发送红包个数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/send-for-member-lv")
	public JsonResult sendForMemberLv(Integer typeid){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int lvid  = StringUtil.toInt(request.getParameter("lvid"),0);
		//判断是否选择会员等级
		if(lvid==0){
			return JsonResultUtil.getErrorJson("必须选择会员级别");
		}
		
		
		int onlyEmailChecked = StringUtil.toInt(request.getParameter("onlyEmailChecked"),0);
		
		try {
			int count =bonusManager.sendForMemberLv(typeid, lvid, onlyEmailChecked);
			//将发放成功的红包  改为不可修改 
			this.bonusManager.changeToCantEdit(typeid);
			
			return JsonResultUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			return JsonResultUtil.getErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		
	}
	//按人发送红包
	@ResponseBody
	@RequestMapping(value = "/send-for-member")
	public JsonResult sendForMember(Integer typeid, Integer[] memberids){
		try {
			int count = this.bonusManager.sendForMember(typeid, memberids);
			//将发放成功的红包  改为不可修改 
			this.bonusManager.changeToCantEdit(typeid);
			return JsonResultUtil.getNumberJson("count",count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			return JsonResultUtil.getErrorJson("发放红包出错["+e.getMessage()+"]");
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/send-for-goods")
	public Object sendForGoods(Integer typeid, Integer[] goodsids){
		try {
			int count = this.bonusManager.sendForGoods(typeid, goodsids);
			//将发放成功的红包  改为不可修改 
			this.bonusManager.changeToCantEdit(typeid);
			return JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			return JsonResultUtil.getErrorJson("发放红包出错["+e.getMessage()+"]");
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/send-for-off-line")
	public Object sendForOffLine(Integer typeid){
		try {
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			int createnum = StringUtil.toInt(request.getParameter("createnum"),0); 
			int count =this.bonusManager.sendForOffLine(typeid, createnum);
			//将发放成功的红包  改为不可修改 
			this.bonusManager.changeToCantEdit(typeid);
			return JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			return JsonResultUtil.getErrorJson("发放红包出错["+e.getMessage()+"]");
		}
	}
	
	/**
	 * 移除一个红包
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete")
	public JsonResult delete(Integer bonusid){
		try {
			this.bonusManager.delete(bonusid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("删除失败【"+e.getMessage()+"】");
		}
	}
	
	/**
	 * 重新发送邮件
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/re-send-mail")
	public String reSendMail(){
		
		return "";
	}
	
	@ResponseBody
	@RequestMapping(value = "/get-goods-list")
	public Object getGoodsList(Integer typeid){
		try {
			List<Map> goodsList = this.bonusManager.getGoodsList(typeid);
			return JsonMessageUtil.getListJson(goodsList);
			
		} catch (Exception e) {
			this.logger.error("获取已绑定商品出错",e);
			 return JsonResultUtil.getErrorJson("获取已绑定商品出错");
		}
	}
	
	@RequestMapping(value = "/export-excel")
	public ModelAndView exportExcel(Integer typeid){
		ModelAndView view = new ModelAndView();
		
		BonusType bonusType = bonusTypeManager.get(typeid);
		view.addObject("bonusType", bonusType);
		view.addObject("filename", bonusType.getType_name() + "红包列表.xls");
		view.addObject("excelPath", this.bonusManager.exportToExcel(typeid));
		view.setViewName("download");
		return view;
	}
	
	public InputStream getInputStream(String excelPath) {
    	
    	if(StringUtil.isEmpty(excelPath)) return null;
    	
    	InputStream in =null;
    	try {
			in = new java.io.FileInputStream(excelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	 return in;
    }
	
	public String getFileName(String filename) {   
  	  
        String downFileName = filename;
  
        try {   
  
            downFileName = new String(downFileName.getBytes(), "ISO8859-1");   
  
        } catch (UnsupportedEncodingException e) {   
  
            e.printStackTrace();   
  
        }   
        return downFileName;   
    }
	
	public String execute(){
        return "success";
	}
}

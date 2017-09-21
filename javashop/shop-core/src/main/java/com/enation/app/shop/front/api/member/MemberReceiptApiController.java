package com.enation.app.shop.front.api.member;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 会员发票api 
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:15:48
 */
@Controller
@RequestMapping("/api/shop/member-receipt")
@Scope("prototype")
public class MemberReceiptApiController extends GridController{

	@Autowired
	private IMemberReceiptManager memberReceiptManager;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 获取会员发票
	 * @param 无
	 * @return json字串
	 * result  为1表示调用正确，0表示失败 ，int型
	 * data: 发票列表
	 * 如果没有登录返回空数组
	 */
	@ResponseBody
	@RequestMapping(value="/list",produces = MediaType.APPLICATION_JSON_VALUE)
	public String list() {
		List<Receipt> receiptList = null;
		Member member = UserConext.getCurrentMember();
		if (member != null) {
			/** 读取此会员的发票列表 */
			receiptList = memberReceiptManager.listReceipt();
		} else {
			receiptList = new ArrayList();
		}
		Map data = new HashMap();
		data.put("receiptList", receiptList);
		String json = JsonMessageUtil.getObjectJson(data);
		return json;
	}


	/**
	 * 添加发票
	 * @param title：发票抬头,String型，必填
	 * @param content:发票类别,String型，必填
	 * @param type：发票类型,如果传递"1"则为个人，如果传递"2"为单位
	 * @param duty： 税号
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/add",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult add() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		/** 获取当前的会员 */
		Member member = UserConext.getCurrentMember();
		/** 判断会员是否登录 */
		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		/** 如果当前发票数量超过10个 则自动删除最早发票 */
		if (memberReceiptManager.receiptCount(member.getMember_id()) >= 10) {
			int receiptMin = this.memberReceiptManager.receiptMin(member.getMember_id());		
			this.memberReceiptManager.delete(receiptMin);
		}
		try {
			/**保存发票信息 */
			String title = request.getParameter("title");
			if(StringUtil.isEmpty(title)){
				return JsonResultUtil.getErrorJson("请选择发票抬头");
			}
			/**修改会员默认发票*/
			this.memberReceiptManager.updateReceiptDefult();
			Receipt receipt= new Receipt();
			/**单位 */
			String content = request.getParameter("content");
			String receiptType=request.getParameter("type");
			Integer type = Integer.parseInt(receiptType);
			String duty = request.getParameter("duty");
			receipt.setTitle(title);
			receipt.setContent(content);
			receipt.setMember_id(member.getMember_id());
			receipt.setIs_default(1);
			receipt.setType(type);
			if(type==2){
				receipt.setDuty(duty);
			}
			receipt = this.memberReceiptManager.addReceipt(receipt);
			return JsonResultUtil.getObjectMessageJson(receipt, "添加成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("添加失败["+ e.getMessage() + "]");				

		}
	}

	/**
	 * 修改发票
	 * @param receipt_id：要修改的发票ID
	 * @param title：发票抬头,String型，必填
	 * @param content:发票类别,String型，必填
	 * @param type：发票类型,如果传递"1"则为个人，如果传递"2"为单位
	 * @param duty： 税号
	 * result  为1表示添加成功，0表示失败 ，int型
	 */
	@ResponseBody
	@RequestMapping(value="/edit",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult updateReceipt(Integer id){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		/** 获取当前会员信息 */
		Member member = UserConext.getCurrentMember();
		/** 判断当前会员是否登录 */
		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		Receipt receipt = memberReceiptManager.getReceipt(id);
		/**查看是否是会员自己的发票，权限控制 */
		if( receipt==null || member.getMember_id() != receipt.getMember_id()){
			return JsonResultUtil.getErrorJson("您没有操作权限");	
		}
		try {
			/**保存发票信息*/
			String title = request.getParameter("title");
			if(StringUtil.isEmpty(title)){
				return JsonResultUtil.getErrorJson("请选择发票抬头");
			}
			String receiptType=request.getParameter("type");
			String content = request.getParameter("content");
			String duty = request.getParameter("duty");
			/**修改会员默认发票*/
			this.memberReceiptManager.updateReceiptDefult();
			/**将修改的发票设为默认发票*/
			Integer type = Integer.parseInt(receiptType);
			receipt.setIs_default(1);
			receipt.setTitle(title);
			receipt.setContent(content);
			receipt.setType(type);
			/** 如果是单位发票则添加税号 */
			if(type == 2){
				receipt.setDuty(duty);
			}
			this.memberReceiptManager.updateReceipt(receipt);
			return JsonResultUtil.getSuccessJson("修改成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("修改失败[" + e.getMessage() + "]");
		}
	}


	/**
	 * 删除发票
	 * @param id ：要删除的发票id
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/delete",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(Integer id) {

		/** 查看是否是会员自己的发票，权限控制*/
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		Receipt receipt = memberReceiptManager.getReceipt(id);
		if( receipt==null || member.getMember_id() != receipt.getMember_id()){
			return JsonResultUtil.getErrorJson("您没有操作权限");	
		}
		try {
			memberReceiptManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			return JsonResultUtil.getErrorJson("删除失败[" + e.getMessage() + "]");				

		}
	}

}

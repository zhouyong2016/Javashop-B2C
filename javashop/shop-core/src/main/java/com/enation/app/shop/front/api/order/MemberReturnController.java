package com.enation.app.shop.front.api.order;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.ReturnsOrder;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderItemStatus;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;


/**
 *  会员退换货申请，whj
 * (这个版本图片和文本信息不能同时提交，需要再实施)
 * @author Sylow
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Controller 
@RequestMapping("/api/shop/returnorder")
public class MemberReturnController{
	
	@Autowired
	private IReturnsOrderManager returnsOrderManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IGoodsManager goodsManager;
	
	/**
	 * (这个版本暂时不支持上传图片，需要再实施)  whj
	 * @param file 文件
	 * @param returntype 【必填】 退货类型
	 * @param ordersn 【必填】 订单编号
	 * @param applyreason 【选填】 申请理由
	 * @param goodsns 【必填】 订单货号 如果多个，使用“,”号隔开
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/return-add", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult returnAdd(
			@RequestParam(value = "file", required = false) MultipartFile file,
			int returntype,
			String ordersn,
			@RequestParam(value = "applyreason", required = false) String applyreason,
			String goodsns) {
		ReturnsOrder returnOrder=new ReturnsOrder();
		//先上传图片
		String subFolder = "order";
		if(file!=null){
			String fileFileName = file.getName();
			//判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
				if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
					return JsonResultUtil.getErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
				}
			}
			//判断文件大小
			if(file.getSize() > 200 * 1024){
				return JsonResultUtil.getErrorJson("对不起,图片不能大于200K！");
			}
			InputStream stream=null;
			try {
				stream=file.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			IUploader uploader=UploadFacatory.getUploaer();
			String imgPath=	uploader.upload(stream, subFolder,file.getOriginalFilename());
			returnOrder.setPhoto(imgPath);
		}
		int type = returntype; //获得退货还是换货类型，type类型.
                //获得订单号;
		Order order = this.orderManager.get(ordersn);                     //
		Member member = UserConext.getCurrentMember();
		Integer member_id;
		if(orderManager.get(ordersn)==null){
			return JsonResultUtil.getErrorJson("此订单不存在！");
		}else{
			if(order.getStatus().intValue()!=OrderStatus.ORDER_SHIP&&order.getStatus().intValue()!=OrderStatus.ORDER_ROG){
				return JsonResultUtil.getErrorJson("您的订单还没有发货！");
			}
			
			member_id = order.getMember_id();
			if (!member_id.equals(member.getMember_id())){
				return JsonResultUtil.getErrorJson("此订单号不是您的订单号！");
			}else{
				Integer memberid = member_id;
			}
		}
		ReturnsOrder tempReturnsOrder = this.returnsOrderManager.getByOrderSn(ordersn);
		if(tempReturnsOrder != null){
			return JsonResultUtil.getErrorJson("此订单已经申请过退货或换货，不能再申请！");
		}
         //获得goodsns; String goods = request.getParameter("goodsns");是正常获得form提交的值，但是这里提交了图片，就不能这么用了，不清楚为啥。
		String goods = goodsns;
			//goodsns是个数组		
		String[] goodsnArray;
		if (goods != null&&!goods.equals("")){
			goodsnArray = StringUtils.split(goods,",");
		}else{
			return JsonResultUtil.getErrorJson("您填写的货号为空！");
		}
		List<Map> items = orderManager.getItemsByOrderid(order.getOrder_id());
		if(items==null){
			return JsonResultUtil.getErrorJson("您的订单下没有货物！");
		}
		List<String> goodSnUnderOrder=new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			goodSnUnderOrder.add(goodsManager.getGoods((Integer)items.get(i).get("goods_id")).getSn());
		}
		for (int j = 0; j < goodsnArray.length; j++) {
			if(goodsnArray[j].indexOf("-") != -1){
				goodsnArray[j]=goodsnArray[j].substring(0, goodsnArray[j].indexOf("-"));
			}
		}
		for (int j = 0; j < goodsnArray.length; j++) {
			if(!goodSnUnderOrder.contains(goodsnArray[j])){
				return JsonResultUtil.getErrorJson("您所填写的所有货物号必须属于一个订单中！");
			}else{
				continue;
			}
		}
		int[] goodids=new int[goodsnArray.length];;
		
		for (int i = 0; i < goodsnArray.length; i++) {
			goodids[i]=this.goodsManager.getGoodBySn(goodsnArray[i]).getGoods_id();
		}

		returnOrder.setGoodsns(goods);
		returnOrder.setMemberid(member_id);
		returnOrder.setOrdersn(ordersn);
		returnOrder.setApply_reason(applyreason);
		returnOrder.setType(type);
		int orderid=orderManager.get(ordersn).getOrder_id();
		//写订单退换货日志
		if(type==1){
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_RETURN,goodids);	
			orderManager.frontAddLog(order.getOrder_id(), "用户"+member.getUname()+"申请退货");
			return JsonResultUtil.getSuccessJson("退货，我们会在2个工作日内处理您的请求！");
		}
		
		if(type==2){
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_CHANGE,goodids);			
			orderManager.frontAddLog(order.getOrder_id(), "用户"+member.getUname()+"申请退货");
			return JsonResultUtil.getSuccessJson("换货申请已提交，我们会在2个工作日内处理您的请求！");
		}
		return null;
	}

}

package com.enation.app.shop.front.api.order;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.order.model.Cart;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.front.api.order.publicmethod.CartPublicMethod;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

/**
 * 购物车api
 * @author kingapex
 *2013-7-19下午12:58:43
 * @version 2.0,2016-02-16 Sylow v60版本改造
 * @since v6.0
 */
@Controller 
@RequestMapping("/api/shop/cart")
public class CartApiController extends GridController {
	
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IProductManager productManager;
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private ISettingService settingService;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private CartPluginBundle cartPluginBundle;
	@Autowired
	private CartPublicMethod cartPublicMethod;
	
	/**
	 * 将一个货品添加至购物车。
	 * 需要传递productid和num参数
	 * 
	 * @param productid 货品id，int型
	 * @num 数量，int 型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody  
	@RequestMapping(value="/add-product", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult addProduct(Integer productid, Integer num, Integer showCartData, Integer activity_id){
		// 无效参数判定
		if (showCartData == null) {
			showCartData = 0;
		}
		Product product = productManager.get(productid);
		return cartPublicMethod.addCart(product, num, showCartData, activity_id);
	}
	
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	@ResponseBody  
	@RequestMapping(value="/add-goods", produces = MediaType.APPLICATION_JSON_VALUE)

	public JsonResult addGoods(Integer goodsid, Integer num,@RequestParam(value = "showCartData", required = false)  Integer showCartData, Integer activity_id){

		Product product = productManager.getByGoodsId(goodsid);
		showCartData=showCartData==null?0:showCartData;
		return cartPublicMethod.addCart(product, num, showCartData, activity_id);
	}
	
	/**
	 * 获取购物车数据
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	@ResponseBody  
	@RequestMapping(value="/get-cart-data", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getCartData(){
		
		try{
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			
			Double goodsTotal = cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			
			java.util.Map<String, Object> data =new HashMap();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			return JsonResultUtil.getObjectJson(data);
		}catch(RuntimeException e ){
			TestUtil.print(e);
			this.logger.error("获取购物车数据出错",e);
			return JsonResultUtil.getErrorJson("获取购物车数据出错");
		}
	}
	
	
	
	/**
	 * 删除购物车一项
	 * @param cartid:要删除的购物车id,int型,即 CartItem.item_id
	 * 
	 * @return 返回json字串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 * 
	 * {@link CartItem }
	 */
	@ResponseBody  
	@RequestMapping(value="/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(){
		
		//查看是否为本会员的购物车商品
		Member member = UserConext.getCurrentMember();

		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String cartid= request.getParameter("cartid");
		//会员登录后再做权限判断
		if(member!=null){
			Cart cart = cartManager.get(StringUtil.toInt(cartid,0));
			
			if( cart==null || !member.getMember_id().equals(cart.getMember_id())){
				return JsonResultUtil.getErrorJson("没有操作权限");
			}
		}
		
		try{
			cartManager.delete(request.getSession().getId(), Integer.valueOf(cartid));
			return JsonResultUtil.getSuccessJson("删除购物项成功");
		}catch(RuntimeException e){
			TestUtil.print(e);
			this.logger.error("删除购物项失败",e);
			return JsonResultUtil.getErrorJson("删除购物项失败");
		}
	}
	
	/**
	 * 更新购物车的数量
	 * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num:要更新数量,int型
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * store: 此商品的库存 int型
	 */
	@ResponseBody  
	@RequestMapping(value="/update-num", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult updateNum(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			String num= request.getParameter("num");
			num = StringUtil.isEmpty(num)?"1":num;//lzf add 20110113
			String productid= request.getParameter("productid");
			Product product=productManager.get(Integer.valueOf(productid));
			Integer store=product.getEnable_store();
			if(store==null) {
				store=0;
			}
			if(store >=Integer.valueOf(num)){
				cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  Integer.valueOf(num));
			}
			return JsonResultUtil.getNumberJson("store", store);
		}catch(RuntimeException e){
//			TestUtil.print(e);
			this.logger.error("更新购物车数量出现意外错误", e);
			return JsonResultUtil.getErrorJson("更新购物车数量出现意外错误:"+e.getMessage());
		}
	}
	
	/**
	 * 购物车的价格总计信息
	 * @param 无
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * orderprice: 订单价格，OrderPrice型
	 * {@link OrderPrice}  
	 */
	@ResponseBody  
	@RequestMapping(value="/get-total", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getTotal(){
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		OrderPrice orderprice  =this.cartManager.countPrice(cartManager.listGoods(sessionid), null, null);
		return JsonResultUtil.getObjectJson(orderprice);
	}
	
	/**
	 * 清空购物车
	 */
	@ResponseBody  
	@RequestMapping(value="/clean", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult clean(){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			cartManager.clean(request.getSession().getId());
			return JsonResultUtil.getSuccessJson("清空购物车成功");
		}catch(RuntimeException e){
			this.logger.error("清空购物车出错",e);
			return JsonResultUtil.getErrorJson("清空购物车出错");
		}
	}
	
	/**
	 * 选择货品进行下单
	 */
	@ResponseBody  
	@RequestMapping(value="/check-product", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkProduct(Integer product_id,boolean checked){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			String sessionid  = request.getSession().getId();
			cartManager.checkProduct(sessionid, product_id, checked);
			return JsonResultUtil.getSuccessJson("选择购物车商品成功");
		}catch(RuntimeException e){
			this.logger.error("选择购物车商品出错",e);
			return JsonResultUtil.getErrorJson("选择购物车商品出错");
		}
	}
	/**
	 * 选择货品进行下单
	 */
	@ResponseBody  
	@RequestMapping(value="/check-all", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkAll(boolean checked){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			String sessionid  = request.getSession().getId();
			cartManager.checkAll(sessionid,checked);
			return JsonResultUtil.getSuccessJson("选择购物车商品成功");
		}catch(RuntimeException e){
			this.logger.error("选择购物车商品出错",e);
			return JsonResultUtil.getErrorJson("选择购物车商品出错");
		}
	}
	

}

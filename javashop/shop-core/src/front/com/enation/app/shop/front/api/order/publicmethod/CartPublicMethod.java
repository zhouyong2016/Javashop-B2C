package com.enation.app.shop.front.api.order.publicmethod;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.order.model.Cart;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.other.model.Activity;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.TestUtil;



/**
 * 将商品加入购物车 方法 抽出
 * @author Chopper
 * @version v1.0
 * @since v6.1
 * 2016年10月19日 下午3:34:04 
 *
 */
@Component
public class CartPublicMethod {

	protected final Logger logger = Logger.getLogger(getClass());
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
	private IActivityManager activityManager;
	
	
	/**
	 * 加入购物车
	 * @param product 产品
	 * @param num 数量
	 * @param showCartData 是否显示数量
	 * @param activity_id 活动id
	 * @return
	 */
	public JsonResult addCart(Product product, int num, int showCartData, Integer activity_id){

		
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		if(product!=null){
			try{
				//判断商品是否下架 商品是否删除
				Goods good=goodsManager.getGoods(product.getGoods_id());
				if(good.getMarket_enable()!=1){
					return JsonResultUtil.getErrorJson("抱歉！您所选择的货品已经下架");
				}
				
				
				int enableStore = product.getEnable_store();
				if (enableStore < num) {
					throw new RuntimeException("抱歉！您所选择的货品库存不足。");
				}
				//查询已经存在购物车里的商品
				
				Cart tempCart = cartManager.getCartByProductId(product.getProduct_id(), sessionid);
				if(tempCart != null){
					int tempNum = tempCart.getNum();
					if (enableStore < num + tempNum) {
						throw new RuntimeException("抱歉！您所选择的货品库存不足。");
					}
				}
				HttpServletRequest request=ThreadContextHolder.getHttpRequest();//获取当前请求
				String exchange=request.getParameter("exchange");//获取exchange参数
				if("true".equals(exchange)){//如果是积分商品 这走下面的插件桩的方法
					this.cartPluginBundle.onAddProductToCart(product,num);
				}
				Cart cart = new Cart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice( product.getPrice() );
				cart.setName(product.getName());
				
				//默认商品添加购物车选中 
				//add by Kanon 2016-7-12
				cart.setIs_check(1);
				//如果商品参加了促销活动，就将促销活动ID添加至购物车表
				//add by DMRain 2016-1-15

				//如果活动为空，则获取当前活动 查看是否参与活动
				Activity  act = null ;
				if(activity_id==null){
					act = activityManager.getCurrentAct();
					if(act!=null){
						activity_id=act.getActivity_id();
					}
				}
				//是否参与活动
				if (activityManager.checkGoodsAct(product.getGoods_id(),
						activity_id) == 1) {
					//活动有效，则说明是当前活动，不需要查询，否则去查询指定活动
					if(act==null){
						act = activityManager.get(activity_id);
					}
					// 如果传了一个活动过来，这个商品就参与活动了 如果活动超时，也取消
					if (act != null && act.getEnd_time() > DateUtil.getDateline()) {
						cart.setActivity_id(act.getActivity_id());
						// 活动结束时间写入购物车 满足活动结束，商品价格回归正常 ADD FROM Chopper
						// 2016-10-19
						cart.setActivity_end_time(act.getEnd_time());
					}
				}

				this.cartManager.add(cart);
				

				//需要同时显示购物车信息
				if(showCartData==1){
					return this.getCartData();
				}
				
				
				return JsonResultUtil.getSuccessJson("添加成功");
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				TestUtil.print(e);
				return JsonResultUtil.getErrorJson("添加失败: " + e.getMessage());
			}
			
		}else{
			return JsonResultUtil.getErrorJson("该货品不存在，未能添加到购物车");
		}
	
	}
	
	/**
	 * 获取购物车数量
	 * @return 前台显示结果集
	 */
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
	
	
	
}

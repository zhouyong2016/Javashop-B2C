package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.core.goods.model.mapper.CartItemMapper;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.order.model.Cart;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.IPromotionManager;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.app.shop.front.api.order.publicmethod.CartPublicMethod;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 购物车业务实现
 * 
 * @author kingapex 2010-3-23下午03:30:50
 * edited by lzf 2011-10-08
 */
@Service("cartManager")
public class CartManager implements ICartManager {
	
	/**
	 * 购物车消息
	 */
	public static String CART_MESSAGE="cart_message";
	/**
	 * 购物车被更新 默认消息
	 */
	public static String CART_MESSAGE_DEFAULT="购物车由于商品价格或活动信息修改，导致有细微调整，带来的不便请谅解。";
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	@Autowired
	private CartPluginBundle cartPluginBundle;
	@Autowired
	private IMemberLvManager memberLvManager;
	@Autowired
	private IPromotionManager promotionManager;
	@Autowired
	private IProductManager productManager;
	@Autowired
	private CartPublicMethod cartPublicMethod;
	@Autowired
	private IDaoSupport daoSupport;

	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 促销活动管理接口 
	 * add_by DMRain 2016-7-12
	 */
	@Autowired
	private IActivityManager activityManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#add(com.enation.app.shop.core.order.model.Cart)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(Cart cart) {
	 
		/**
		 * 读取当前会员
		 */
		Member member = UserConext.getCurrentMember();
		Integer memberid = null;
		
		/**
		 * 如果会员已经登陆，则设置购物车的会员id
		 */
		if(member!=null){
			cart.setMember_id(member.getMember_id());
			memberid =member.getMember_id();
		}
		
		/**
		 * 触发购物车添加事件
		 */
		try {
			this.cartPluginBundle.onAdd(cart);
		} catch (RuntimeException e) {
			throw e;
		}
		
		/**
		 * 查看购物车中是否有此商品，如果有则更新数据，如果没有则添加到购物车
		 */
		String sql ="select count(0) from es_cart where  product_id=?   and itemtype=? and ( session_id=? or member_id=? )";
		
		/**
		 * 打入cartPluginBundle桩中的filterGetCountSql方法  add by jianghongyan 2016-06-15
		 */
		sql=this.cartPluginBundle.filterGetCountSql(sql);
		
		int count = this.daoSupport.queryForInt(sql, cart.getProduct_id(),cart.getItemtype(),cart.getSession_id(),memberid);
		
		if(count>0){
			/**
			 * 打入cartPluginBundle桩中的filterUpdateSql方法  add by jianghongyan 2016-06-15
			 */
			String updateSql="update es_cart set num=num+? where product_id=? and itemtype=?  and ( session_id=? or member_id=? )";
			updateSql=this.cartPluginBundle.filterUpdateSql(updateSql);
			this.daoSupport.execute(updateSql, cart.getNum(),cart.getProduct_id(),cart.getItemtype(),cart.getSession_id(),memberid);
			this.cartPluginBundle.onAfterAdd(cart);
			return 0;
		}else{
			
			this.daoSupport.insert("es_cart", cart);

			Integer cartid  = this.daoSupport.getLastId("es_cart");
			cart.setCart_id(cartid);
			
			this.cartPluginBundle.onAfterAdd(cart);
			return cartid;
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#get(int)
	 */
	@Override
	public Cart get(int cart_id){
		return (Cart) this.daoSupport.queryForObject("SELECT * FROM es_cart WHERE cart_id=?", Cart.class, cart_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#getCartByProductId(int, java.lang.String)
	 */
	@Override
	public Cart getCartByProductId(int productId, String sessionid){
		
		/**
		 * 读取当前会员
		 */
		Member member = UserConext.getCurrentMember();
		Integer memberid = null;
		
		
		/**
		 * 如果会员已经登陆，则设置购物车的会员id
		 */
		if(member!=null){
			memberid =member.getMember_id();
		}
		
		return (Cart)this.daoSupport.queryForObject("SELECT * FROM es_cart WHERE product_id=? AND (session_id=? or member_id=?)", Cart.class, productId,sessionid,memberid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#getCartByProductId(int, java.lang.String, java.lang.String)
	 */
	@Override
	public Cart getCartByProductId(int productId, String sessionid, String addon){
		return (Cart)this.daoSupport.queryForObject("SELECT * FROM es_cart WHERE product_id=? AND session_id=? AND addon=?", Cart.class, productId, sessionid, addon);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#countItemNum(java.lang.String)
	 */
	@Override
	public Integer countItemNum(String sessionid) {
		Member member  =UserConext.getCurrentMember();
		if(member==null){
			String sql = "select count(0) from es_cart where session_id =?";
			return daoSupport.queryForInt(sql.toString(), sessionid);
		}else{
			String sql = "select count(0) from es_cart where member_id =?";
			return daoSupport.queryForInt(sql.toString(), member.getMember_id());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#listGoods(java.lang.String)
	 */
	@Override
	public List<CartItem> listGoods(String sessionid) {
		//更新需要同步的商品
		this.reReorganization();
 
		StringBuffer sql = new StringBuffer();
		/**
		 * 打入插件桩 cartPluginBundle 的filterListGoodsSql方法  add by jianghongyan 2016-06-15
		 */
		sql.append("select ");
		this.cartPluginBundle.filterListGoodsSql(sql);
		sql.append(" c.is_check,c.cart_id,g.cat_id as catid,g.goods_id,g.thumbnail,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight,c.activity_id from es_cart c, es_product p,es_goods g ");
		
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id");
		
		List<CartItem>  list =null;
		
		/**
		 * 获取当前登陆会员
		 * 如果已经登陆过则通过会员id来读取
		 */
		Member member  =UserConext.getCurrentMember();
		if(member==null){
			sql.append(" and c.session_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), sessionid);
		}else{
			sql.append(" and c.member_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), member.getMember_id());
		}
	
		//循环判断购物车中的货品是否参加了促销活动，如果参加了促销活动，判断促销活动是否已结束或已失效，如果已结束或已失效，就将此购物项中的促销活动id置为空
		//add_by DMRain 2016-7-12
		for (CartItem cart : list) {
			Integer act_id = cart.getActivity_id();
			if (act_id != null && act_id != 0) {
				int result = this.activityManager.checkActivity(act_id);
				if (result == 1) {
					this.daoSupport.execute("update es_cart set activity_id = null where cart_id = ?", cart.getId());
					cart.setActivity_id(0);
				}
			}
		}
		
		cartPluginBundle.filterList(list, sessionid);
		
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#checkListGoods(java.lang.String)
	 */
	@Override
	public List<CartItem> selectListGoods(String sessionid) {
		//刷新购物车列表
		this.reReorganization();
		
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("select  ");
		this.cartPluginBundle.filterSelectListGoods(sql);
		sql.append(" c.is_check,g.cat_id as catid,g.goods_id,g.thumbnail,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight,c.activity_id  from es_cart c, es_product p,es_goods g ");
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.is_check=1 ");
		
		List<CartItem>  list =null;
		
		/**
		 * 获取当前登陆会员
		 * 如果已经登陆过则通过会员id来读取
		 */
		Member member  =UserConext.getCurrentMember();
		if(member==null){
			sql.append(" and c.session_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), sessionid);
		}else{
			sql.append(" and c.member_id=?");
			list = this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), member.getMember_id());
		}
	
		//循环判断购物车中的货品是否参加了促销活动，如果参加了促销活动，判断促销活动是否已结束或已失效，如果已结束或已失效，就将此购物项中的促销活动id置为空
		//add_by DMRain 2016-7-12
		for (CartItem cart : list) {
			Integer act_id = cart.getActivity_id();
			if (act_id != null && act_id != 0) {
				int result = this.activityManager.checkActivity(act_id);
				if (result == 1) {
					this.daoSupport.execute("update es_cart set activity_id = null where cart_id = ?", cart.getId());
					cart.setActivity_id(0);
				}
			}
		}
		
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ICartManager#clean(java.lang.String)
	 */
	public void clean(String sessionid){
		
		/**
		 * 获取当前登陆会员
		 * 清除当前会员或按sessionid来清空
		 */
		Member member  =UserConext.getCurrentMember();
		Integer memberid = null;
		
		if(member!=null){
			memberid = member.getMember_id();
		}
		
		String sql ="delete from es_cart where is_check=1 and (session_id=? or member_id=?)  ";
		this.daoSupport.execute(sql, sessionid,memberid);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#delete(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void delete(String sessionid, Integer cartid) {
		String sql = "";
		Member member = UserConext.getCurrentMember();
		
		//判断当前是否有会员登陆，如果有就按照member_id和cart_id删除，没有就按照sessionid和cart_id删除
		//add by DMRain 2016-3-22
		if (member != null) {
			sql = "delete from es_cart where member_id = " + member.getMember_id() + " and cart_id = ?";
		} else {
			sql = "delete from es_cart where session_id = '" + sessionid + "' and cart_id = ?";
		}

		this.daoSupport.execute(sql, cartid);
		
		//以下代码需要注意：如果当前登陆会员不为空，那么sessionid就和购物车中商品的sessionid不同了，所以使用以下插件时需要注意sessionid的变化
		//add by DMRain 2016-3-22
		this.cartPluginBundle.onDelete(sessionid, cartid);
//		HttpCacheManager.sessionChange();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#updateNum(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateNum(String sessionid, Integer cartid, Integer num) {
		try {
			//以下代码需要注意：如果当前登陆会员不为空，那么sessionid就和购物车中商品的sessionid不同了，所以使用以下插件时需要注意sessionid的变化
			//add by DMRain 2016-3-22
			//执行购物车数量修改前事件
			cartPluginBundle.onBeforeUpdate(sessionid, cartid, num);
		
			String sql = "";
			Member member = UserConext.getCurrentMember();
			
			//判断当前是否有会员登陆，如果有就按照member_id和cart_id修改，没有就按照sessionid和cart_id修改
			//add by DMRain 2016-3-22
			if (member != null) {
				//修改购物车数量
				sql = "update es_cart set num = ? where member_id = " + member.getMember_id() + " and cart_id = ?";
			} else {
				//修改购物车数量
				sql = "update es_cart set num = ? where session_id = '" + sessionid + "' and cart_id = ?";
			}
			
			this.daoSupport.execute(sql, num, cartid);
			
			//以下代码需要注意：如果当前登陆会员不为空，那么sessionid就和购物车中商品的sessionid不同了，所以使用以下插件时需要注意sessionid的变化
			//add by DMRain 2016-3-22
			//执行修改购物车修改后事件
			this.cartPluginBundle.onUpdate(sessionid, cartid);
		}catch (RuntimeException e) {
			throw e;
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#countGoodsTotal(java.lang.String)
	 */
	@Override
	public Double countGoodsTotal(String sessionid) {
		StringBuffer sql = new StringBuffer();
		
		sql.append("select sum( c.price * c.num ) as num from es_cart c ");
		sql.append("where  c.itemtype=0 ");
		
		Double price = 0D;
				
		/**
		 * 获取当前登陆会员
		 * 如果已经登陆过则通过会员id来读取
		 */
		Member member  =UserConext.getCurrentMember();
		
		if(member==null){
			sql.append(" and c.session_id=?");
			price = this.daoSupport.queryForDouble(sql.toString(),  sessionid);
		}else{
			sql.append(" and c.member_id=?");
			price =   this.daoSupport.queryForDouble(sql.toString()  ,member.getMember_id());
		}
		
		
		return price;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#countPoint(java.lang.String)
	 */
	@Override
	public Integer countPoint(String sessionid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select  sum(g.point * c.num) from "
				+ " es_cart c,"
				+ " es_product p,"
				+ " es_goods g ");
		sql.append("where (c.itemtype=0  or c.itemtype=1)  and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");

		return this.daoSupport.queryForInt(sql.toString(), sessionid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#countGoodsWeight(java.lang.String)
	 */
	@Override
	public Double countGoodsWeight(String sessionid) {
		StringBuffer sql = new StringBuffer(
				"select sum( c.weight * c.num )  from es_cart c where c.session_id=?");
		Double weight =   this.daoSupport.queryForDouble(sql.toString(),   sessionid);
		return weight;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ICartManager#countPrice(java.util.List, java.lang.Integer, java.lang.String)
	 */
	@Override
	public OrderPrice countPrice(List<CartItem> cartItemList, Integer shippingid,String regionid) {
		
		OrderPrice orderPrice = new OrderPrice();
		
		//计算商品重量
		Double weight=0.0;
 
		//订单总价格
		Double  orderTotal = 0d;
		
		//配送费用
		Double dlyPrice = 0d;  
		
		//优惠后的订单价格,默认为商品原始价格
		Double goodsPrice =0.0; 
	 
		if(cartItemList!=null && !cartItemList.isEmpty()){
			//计算商品重量及商品价格
			for (CartItem cartItem : cartItemList) {
				
				// 计算商品重量
				weight = CurrencyUtil.add(weight, CurrencyUtil.mul(cartItem.getWeight(), cartItem.getNum()));
				
			 
				//计算商品优惠后的价格小计
				Double itemTotal = CurrencyUtil.mul(cartItem.getCoupPrice(), cartItem.getNum());
				goodsPrice=CurrencyUtil.add(goodsPrice, itemTotal);
				
			}
		}
		
		 
		//如果传递了配送信息，计算配送费用
		if(regionid!=null &&shippingid!=null ){
			if(shippingid!=0){
				//计算原始配置送费用
				Double[] priceArray = this.dlyTypeManager.countPrice(shippingid, weight, goodsPrice, regionid);
				
				//费送费用
				dlyPrice = priceArray[0];
			}
			
		}

		
		//商品金额 
		orderPrice.setGoodsPrice(goodsPrice); 
		
		//配送费用
		orderPrice.setShippingPrice(dlyPrice);
				
		//订单总金额:商品金额+运费
		orderTotal = CurrencyUtil.add(goodsPrice, dlyPrice); 
		
		//订单总金额
		orderPrice.setOrderPrice(orderTotal);
		
		
		//应付金额为订单总金额
		orderPrice.setNeedPayMoney(orderTotal); 
		
		//订单总的优惠金额
		orderPrice.setDiscountPrice( CurrencyUtil.add(0d,BonusSession.getUseMoney())); 
		
		orderPrice.setNeedPayMoney(CurrencyUtil.add(orderTotal,-orderPrice.getDiscountPrice())); 
		
		//如果优惠金额后订单价格小于0
		if(orderPrice.getNeedPayMoney()<=0){
			orderPrice.setNeedPayMoney(0d);
		}
		
		//订单可获得积分
		orderPrice.setPoint(0); 
		
		//商品总重量
		orderPrice.setWeight(weight);
		
		//订单可获得的赠品ID add by DMRain 2016-1-15
		orderPrice.setGift_id(0);
		
		//订单可获得的优惠券ID add by DMRain 2016-1-19
		orderPrice.setBonus_id(0);
		
		return orderPrice;
		 
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#updatePriceByProductid(java.lang.Integer, java.lang.Double)
	 */
	@Override
	public void updatePriceByProductid(Integer product_id,Double price) {
		String sql = "update es_cart set price=? where product_id=?";
		this.daoSupport.execute(sql, price,product_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#checkGoodsInCart(java.lang.Integer)
	 */
	@Override
	public boolean checkGoodsInCart(Integer goodsid) {
		String sql ="select count(0) from es_cart where goods_id=?";
		return this.daoSupport.queryForInt(sql, goodsid)>0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#checkProduct(java.lang.String, java.lang.Integer, boolean)
	 */
	@Override
	public void checkProduct(String sessionid,Integer product_id, boolean check) {
		
		Member member  =UserConext.getCurrentMember();
		
		StringBuffer sql=new StringBuffer("update es_cart set is_check=? where product_id=?");

		/**
		 * 打入cartPluginBundle桩 的filterCheckProductSql方法 add by jianghongyan 2016-06-15
		 */
		this.cartPluginBundle.filterCheckProductSql(sql);
		if(member==null){
			sql.append(" and session_id=?");
			this.daoSupport.execute(sql.toString(),check==true?1:0,product_id, sessionid);
		}else{
			sql.append(" and member_id=?");
			this.daoSupport.execute(sql.toString(), check==true?1:0,product_id, member.getMember_id());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#checkAll(java.lang.String)
	 */
	@Override
	public void checkAll(String sessionid,boolean check) {
		Member member  =UserConext.getCurrentMember();
		
		StringBuffer sql=new StringBuffer("update es_cart set is_check=? ");
				
		if(member==null){
			sql.append(" where session_id=?");
			this.daoSupport.execute(sql.toString(), check==true?1:0,sessionid);
		}else{
			sql.append(" where member_id=?");
			this.daoSupport.execute(sql.toString(), check==true?1:0, member.getMember_id());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#changeProduct(java.lang.Integer)
	 */
	@Override
	public void changeProduct(Integer goods_id) { 
		this.daoSupport.execute("update es_cart set is_change = 1 where goods_id = ?",goods_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#changeProduct(java.lang.Integer)
	 */
	@Override
	public void changeAll() { 
		this.daoSupport.execute("update es_cart set is_change = 1");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#reReorganization()
	 */
	@Override
	public void reReorganization() {
		
		String sessionid = ThreadContextHolder.getHttpRequest().getSession().getId();
		Member member  =UserConext.getCurrentMember();
		List<Cart> carts;
		StringBuffer sql = new StringBuffer("select * from es_cart where ");
		if(member==null){
			sql.append(" session_id=?");
			carts = this.daoSupport.queryForList(sql.toString(), Cart.class, sessionid);
		}else{
			sql.append(" member_id=?");
			carts = this.daoSupport.queryForList(sql.toString(), Cart.class, member.getMember_id());
		}
		if(carts.size()==0){
			return;
		}
		for (Cart cart : carts) {
			//判断是否过期
			boolean isExpired = false;
			if(cart.getActivity_end_time()!=null){
				if(cart.getActivity_end_time()<DateUtil.getDateline()){
					isExpired=true;
				}
			}
			
			
			//如果需要更新
			if(cart.getIs_change()==1||isExpired){
				//如果有规格
				if(!StringUtil.isEmpty(cart.getAddon())){
					ThreadContextHolder.getHttpRequest().setAttribute("havespec", "1");
					ThreadContextHolder.getHttpRequest().setAttribute("productid",cart.getProduct_id());
				}  else{
					ThreadContextHolder.getHttpRequest().removeAttribute("havespec");
					ThreadContextHolder.getHttpRequest().removeAttribute("productid");
				}
				try {
					//购物车消息
					ThreadContextHolder.getSession().setAttribute(CART_MESSAGE, CART_MESSAGE_DEFAULT);
					//删除重新加入购物车
					this.delete(sessionid, cart.getCart_id());
					cartPublicMethod.addCart(productManager.get(cart.getProduct_id()), cart.getNum(), 0, cart.getActivity_id());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("重新加入购物车失败",e);
				}
			}
		}
		
	}

	@Override
	public void cleanHaveNoneMember(String sessionid) {
		
		/**
		 * 获取当前登陆会员
		 * 清除当前会员或按sessionid来清空
		 */

		String sql ="delete from es_cart where session_id=? and member_id is null  ";
		this.daoSupport.execute(sql, sessionid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ICartManager#countItemTotalNum(java.lang.String)
	 */
	@Override
	public Integer countItemTotalNum(String sessionid) {
		Member member  =UserConext.getCurrentMember();
		String sql ="";
		List<Cart> list = null;
		if(member==null){
			//获取购物车信息
			 sql = "select * from es_cart where session_id =?";
			 list = daoSupport.queryForList(sql, Cart.class, sessionid);
		}else{
			//获取购物车信息
			 sql = "select * from es_cart where member_id =?";
			 list = daoSupport.queryForList(sql, Cart.class, member.getMember_id());
		}
		//将每种商品数量相加
		Integer total = 0;
		for (Cart cart : list) {
			total+=cart.getNum();
		}
		return total;
	}

}

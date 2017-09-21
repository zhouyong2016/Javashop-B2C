package com.enation.app.shop.core.order.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.goods.plugin.GoodsStorePluginBundle;
import com.enation.app.shop.core.goods.service.IGoodsStoreManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.model.SellBackChild;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBackLog;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.app.shop.core.order.service.OrderItemStatus;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 退货manager
 * @author Sylow
 * @version v2.0,2016年2月20日 版本改造
 * @since v6.0
 * @version v2.1 2016年7月15日 去除无用代码
 */
@Service("sellBackManager")
public class SellBackManager implements ISellBackManager {

	@Autowired
	private IDaoSupport daoSupport;	
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IRefundManager refundManager;
	@Autowired
	private IGoodsStoreManager goodsStoreManager;
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	@Autowired
	private GoodsStorePluginBundle goodsStorePluginBundle; 

	/**
	 * 订单赠品管理 add_by DMRain 2016-7-19
	 */
	@Autowired
	private IOrderGiftManager orderGiftManager;

	/**
	 * 订单积分管理 add_by DMRain 2016-7-25
	 */
	@Autowired
	private IMemberPointManger memberPointManger;
	
	/**
	 * 会员管理 add_by DMRain 2016-7-25
	 */
	@Autowired
	private IMemberManager memberManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#list(int, int, java.lang.Integer)
	 */
	@Override
	public Page list(Integer page, Integer pageSize,Integer status,Integer type) {
		Page webpage;
		if(EopSetting.PRODUCT.equals("b2b2c")){
			String sql = "select * from es_sellback_list where tradestatus=? and type=? and store_id=? order by id desc ";
			webpage = this.daoSupport.queryForPage(sql, page, pageSize,status,type,ShopApp.self_storeid);
		}else{
			String sql = "select * from es_sellback_list where tradestatus=? and type=? order by id desc ";
			webpage = this.daoSupport.queryForPage(sql, page, pageSize,status,type);
		}

		return webpage;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#list(java.lang.Integer, int, int)
	 */
	@Override
	public Page list(Integer member_id,Integer page, Integer pageSize) {
		String sql = "select * from es_sellback_list where member_id=? order by id desc ";
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize,member_id);
		return webpage;
	}



	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#editStatus(java.lang.Integer, java.lang.Integer, int, double, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="退货单ID为${id}的退货审核")
	public void editStatus(Integer status, Integer id,int depotid,double  alltotal_pay,String seller_remark) {

		/**
		 * 检查退货仓库
		 */
		if(depotid==0 && status==1){
			throw new RuntimeException("必须选择退货仓库");
		}

		/**
		 * 更新退货单状态
		 */
		String sql = "update es_sellback_list set tradestatus=?,seller_remark=?,depotid=?,alltotal_pay=?,confirm_time=? where id=?";
		this.daoSupport.execute(sql, status,seller_remark,depotid,alltotal_pay,DateUtil.getDateline(),id);

		/**
		 * 获取退货单信息
		 */
		SellBack sellBackList = this.get(id);
		
		/**
		 * 如果拒绝则更新订单为完成状态
		 * 更新订单状态
		 */
		if  ( status.intValue() == SellBackStatus.in_storage.getValue()  ||status.intValue() == SellBackStatus.cancel.getValue()  ){
			
			/**
			 * 判断退货单中的赠品id是否为空,如果不为空，就改变订单赠品状态为已完成退货
			 * add_by DMRain 2016-7-19
			 */
			if (sellBackList.getGift_id() != null) {
				this.orderGiftManager.updateGiftStatus(sellBackList.getGift_id(), sellBackList.getOrderid(), 2);
				
			}
			
		}
		/**
		 * 如果拒绝，将订单设置为可以再次申请
		 */
		if(status.intValue()==SellBackStatus.cancel.getValue()){
			
			//设置订单可以再次申请
			sql="update es_order set status=? where order_id=?";
			this.daoSupport.execute(sql, OrderStatus.ORDER_COMPLETE,sellBackList.getOrderid());
			
			//循环修改状态 让订单可以继续提交退货申请
			List<SellBackGoodsList> sgs = this.daoSupport.queryForList( "select * from es_sellback_goodslist where recid = ?",SellBackGoodsList.class, id);
			for (SellBackGoodsList sellBackGoodsList : sgs) { 
				daoSupport.execute("update es_order_items set state = 0 where item_id = ? ",sellBackGoodsList.getItem_id() );
			}
			
			/**
			 * 判断退货单中的赠品id是否为空,如果不为空，就改变订单赠品状态为正常
			 * add_by DMRain 2016-7-19
			 */
			if (sellBackList.getGift_id() != null) {
				this.orderGiftManager.updateGiftStatus(sellBackList.getGift_id(), sellBackList.getOrderid(), 0);
			}
			
			//纪录订单日志
			this.orderManager.addLog(sellBackList.getOrderid(),"拒绝退货申请");
		}else{
			
			//纪录订单日志
			this.orderManager.addLog(sellBackList.getOrderid(),"通过退货申请");
		}

		/**
		 * 记录日志
		 */
		this.saveLog(id, "审核退货申请");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#search(java.lang.String, int, int)
	 */
	@Override
	public Page search(String keyword, int page, int pageSize) {
		String sql = "select * from es_sellback_list";
		String where = "";
		if (!StringUtil.isEmpty(keyword)) {
			where = " where tradeno like '%" + keyword
					+ "%' or ordersn like '%" + keyword + "%' order by id desc";
		}
		sql = sql + where;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#get(java.lang.String)
	 */
	@Override
	public SellBack get(String tradeno) {
		String sql = "select * from es_sellback_list where tradeno=?";
		SellBack result = (SellBack) this.daoSupport
				.queryForObject(sql, SellBack.class, tradeno);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#get(java.lang.Integer)
	 */
	@Override
	public SellBack get(Integer id) {
		String sql = "select * from es_sellback_list where id=?";
		SellBack result = (SellBack) this.daoSupport.queryForObject(sql, SellBack.class, id);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#addSellBack(com.enation.app.shop.core.order.model.SellBackList, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="订单号为${sellBack.ordersn}由${sellBack.regoperator}退货申请，申请退款金额：${sellBack.apply_alltotal}")
	public Integer addSellBack(SellBack sellBack,List<SellBackGoodsList> goodsList ){

		/**
		 * 进行权限及参数检查
		 */
		Integer memberid = sellBack.getMember_id();
		if(memberid==null || memberid==0){
			throw new RuntimeException("参数错误：会员id必须输入");
		}

		/**
		 * 校验申请会员是否是购买会员
		 */
		int orderid = sellBack.getOrderid();
		Order order  = this.orderManager.get(orderid);
		sellBack.setOrdersn(order.getSn());
		
		/**
		 * 在退货单中添加赠品id并改变订单赠品的状态 add_by DMRain 2016-7-19
		 */
		if (order.getGift_id() != 0) {
			int count = this.daoSupport.queryForInt("select count(0) from es_sellback_list where orderid = ? and gift_id = ?", orderid, order.getGift_id());
			
			//判断订单中的赠品是否已经退货，count等于0代表还没有退货
			if (count == 0) {
				sellBack.setGift_id(order.getGift_id());
				this.orderGiftManager.updateGiftStatus(order.getGift_id(), orderid, 1);
			}
		}

		/**
		 * 查出此订单的商品列表备用
		 */
		List<Map> itemList=this.orderManager.getItemsByOrderidAndGoodsList(orderid,goodsList);

		/**
		 * 检测订单商品项的状态是否可以退货
		 */
		this.checkItemStateForApply(itemList);


		/**
		 * 填充退货商品的各种信息
		 */
		this.fillGoodsItem(itemList, goodsList);


		/**
		 * 插入退货单
		 */
		this.daoSupport.insert("es_sellback_list", sellBack);
		int sellBackId = this.daoSupport.getLastId("es_sellback_list");




		/**
		 * 保存退货单明细
		 */
		for (SellBackGoodsList sellBackGoods : goodsList) {

			//设置退货商品的退货单id
			sellBackGoods.setRecid(sellBackId);

			//更新订单商品表为申请退货状态
			this.daoSupport.execute("update es_order_items set state=? where item_id=?", OrderItemStatus.APPLY_RETURN,sellBackGoods.getItem_id());
			this.daoSupport.insert("es_sellback_goodslist", sellBackGoods);

		}
		//修改订单状态为已申请退款
		this.daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_MAINTENANCE,orderid);
		//退货之后事件
		orderPluginBundle.afterReturnOrder(goodsList, order);

		orderManager.addLog(sellBack.getOrderid(), "申请退货");
		return sellBackId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#addSellBackAdmin(com.enation.app.shop.core.model.SellBackList, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="订单号为${sellBack.ordersn}由${sellBack.regoperator}退货申请，申请退款金额：${sellBack.apply_alltotal}")
	public Integer addSellBackAdmin(SellBack sellBack,List<SellBackGoodsList> goodsList ){


		int orderid = sellBack.getOrderid();
		Order order  = this.orderManager.get(orderid);
		sellBack.setOrdersn(order.getSn());

		/**
		 * 在退货单中添加赠品id并改变订单赠品的状态 add_by DMRain 2016-7-19
		 */
		if (order.getGift_id() != 0) {
			int count = this.daoSupport.queryForInt("select count(0) from es_sellback_list where orderid = ? and gift_id = ?", orderid, order.getGift_id());
			
			//判断订单中的赠品是否已经退货，count等于0代表还没有退货
			if (count == 0) {
				sellBack.setGift_id(order.getGift_id());
				this.orderGiftManager.updateGiftStatus(order.getGift_id(), orderid, 1);
			}
		}
		
		/**
		 * 查出此订单的商品列表备用
		 */
		List<Map> itemList  = this.orderManager.getItemsByOrderid(orderid);


		/**
		 * 检测订单商品项的状态是否可以退货
		 */
		this.checkItemStateForApply(itemList);


		/**
		 * 填充退货商品的各种信息
		 */
		this.fillGoodsItem(itemList, goodsList);
		sellBack.setSndto("管理员");

		/**
		 * 插入退货单
		 */
		this.daoSupport.insert("es_sellback_list", sellBack);
		int sellBackId = this.daoSupport.getLastId("es_sellback_list");

		/**
		 * 保存退货单明细
		 */
		for (SellBackGoodsList sellBackGoods : goodsList) {

			//设置退货商品的退货单id
			sellBackGoods.setRecid(sellBackId);

			//更新订单商品表为申请退货状态
			this.daoSupport.execute("update es_order_items set state=? where item_id=?", OrderItemStatus.APPLY_RETURN,sellBackGoods.getItem_id());
			this.daoSupport.insert("es_sellback_goodslist", sellBackGoods);
		}
		//更新订单表状态为交易成功已申请退货申请
		this.daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.ORDER_MAINTENANCE,orderid);
		
		//纪录订单日志
		this.orderManager.addLog(orderid, "订单申请退货");
		
		//退货之后事件
		orderPluginBundle.afterReturnOrder(goodsList, order);
		return sellBackId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#apply(com.enation.app.shop.core.order.model.SellBackList)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="订单号为${data.ordersn}申请退货,金额：${data.apply_alltotal}")
	public void apply(SellBack data) {
		//更新状态
		this.daoSupport.update("es_sellback_list", data,"id=" + data.getId()); 
		
		this.examination(data.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#cancle(com.enation.app.shop.core.order.model.SellBackList)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancle(SellBack data) {

		Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();

		daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_COMPLETE, orderid);

		daoSupport.execute("update es_sellback_list set tradestatus=? where id=?",SellBackStatus.cancel.getValue(),data.getId());

		orderManager.addLog(orderid, "取消退货"); 
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#saveLog(java.lang.Integer, com.enation.app.shop.core.order.model.SellBackStatus, java.lang.String)
	 */
	@Override
	public void saveLog(Integer recid,  String logdetail) {

		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDateline());
		sellBackLog.setOperator(UserConext.getCurrentAdminUser().getUsername());
		this.daoSupport.insert("es_sellback_log", sellBackLog);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#saveLog(java.lang.Integer, com.enation.app.shop.core.order.model.SellBackStatus, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveLog(Integer recid, String logdetail,String memeberName) {

		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDateline());
		sellBackLog.setOperator(memeberName);
		this.daoSupport.insert("es_sellback_log", sellBackLog);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#saveGoodsList(com.enation.app.shop.core.order.model.SellBackGoodsList)
	 */
	@Override
	public Integer saveGoodsList(SellBackGoodsList data) {
		if (data.getId() == null) {
			this.daoSupport.insert("es_sellback_goodslist", data);
		} else {
			this.daoSupport.update("es_sellback_goodslist", data,"id=" + data.getId());
		}

		Integer id = this.daoSupport.getLastId("es_sellback_goodslist");

		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getSellBackGoods(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public SellBackGoodsList getSellBackGoods(Integer recid, Integer goodsid) {
		String sql = "select * from es_sellback_goodslist where recid=? and goods_id=?";
		SellBackGoodsList result = (SellBackGoodsList) this.daoSupport.queryForObject(sql, SellBackGoodsList.class, recid, goodsid);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getGoodsList(java.lang.Integer)
	 */
	@Override
	public List getGoodsList(Integer recid) {
		String sql="select * from es_sellback_goodslist where recid=?";

		List<Map> sellBackGoodsList= this.daoSupport.queryForList(sql, recid);

		for (Map map : sellBackGoodsList) {
			String specJson = (String)map.get("spec_json");
			if(!StringUtil.isEmpty(specJson)){
				List<Map<String,Object>> specList = JsonUtil.toList(specJson);

				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
				freeMarkerPaser.setClz(this.getClass());
				freeMarkerPaser.putData("specList",specList);
				freeMarkerPaser.setPageName("order_item_spec");
				String html = freeMarkerPaser.proessPageContent(); 
				map.put("spec", html);
			}
		}

		return sellBackGoodsList ;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#editGoodsNum(java.util.Map)
	 */
	@Override
	public void editGoodsNum(Map data) {
		Integer recid = (Integer) data.get("recid");
		Integer goods_id = (Integer) data.get("goods_id");
		this.daoSupport.update("es_sellback_goodslist", data, "recid=" + recid+" and goods_id=" + goods_id);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#editStorageNum(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void editStorageNum(Integer recid, Integer product_id, Integer num,Integer item_id) {
		this.daoSupport.execute("update es_sellback_goodslist set storage_num=storage_num+? where recid=? and product_id=? and item_id=?", num,recid, product_id,item_id);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#sellBackLogList(java.lang.Integer)
	 */
	@Override
	public List sellBackLogList(Integer recid) {
		return this.daoSupport.queryForList("select * from es_sellback_log where recid=? order by id desc",recid);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#searchSn(java.lang.String)
	 */
	@Override
	public int searchSn(String sn) {
		String sql = "select id from es_sellback_list where ordersn=? and (tradestatus=0 or tradestatus=1)";
		List<Map> list = this.daoSupport.queryForList(sql,sn);
		int num = 0;
		if(list.size() > 0){
			num = Integer.parseInt(list.get(0).get("id").toString());
		}
		return num;
	} 

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISellBackManager#getSellbackChilds(int, int)
	 */
	@Override
	public List getSellbackChilds(int orderId ,int parentGoodsId){
		String sql = "SELECT c.*,g.`name`,g.price,p.product_id,i.num FROM es_sellback_child c LEFT JOIN es_goods g ON c.goods_id = g.goods_id LEFT JOIN es_product p ON c.goods_id = p.goods_id LEFT JOIN es_order_item_child i ON c.goods_id = i.goodsid AND c.order_id = i.orderid WHERE c.order_id = ? AND c.parent_id = ?  ";
		return this.daoSupport.queryForList(sql, orderId,parentGoodsId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#saveSellbackChild(int, int, int, int)
	 */
	@Override
	public void saveSellbackChild(int orderId,int goodsId,int parentId,int returnNum){
		SellBackChild sellBackChild = new SellBackChild();
		sellBackChild.setGoods_id(goodsId);
		sellBackChild.setOrder_id(orderId);
		sellBackChild.setParent_id(parentId);
		sellBackChild.setReturn_num(returnNum);
		sellBackChild.setStorage_num(0);
		this.daoSupport.insert("es_sellback_child", sellBackChild);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#updateSellbackChild(int, int, int, int)
	 */
	@Override
	public void updateSellbackChild(int orderId,int goodsId,int returnNum,int storageNum){
		SellBackChild sellBackChild = new SellBackChild();
		sellBackChild.setGoods_id(goodsId);
		sellBackChild.setOrder_id(orderId);
		sellBackChild.setStorage_num(storageNum);
		sellBackChild.setReturn_num(returnNum);
		this.daoSupport.update("es_sellback_child", sellBackChild,
				" es_sellback_child.order_id = " + orderId
				+ " AND es_sellback_child.goods_id = " + goodsId);

		Map goods = this.getGoods(goodsId);
		//响应库存退货入库事件 2015-07-23  冯兴隆
		this.goodsStorePluginBundle.onStockReturn(goods);
		//响应库存变更事件 2015-07-23  冯兴隆
		this.goodsStorePluginBundle.onStockChange(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getSellbackChild(int, int)
	 */
	@Override
	public SellBackChild getSellbackChild(int orderId,int goodsId){
		String sql = "SELECT * FROM es_sellback_child c WHERE c.order_id = ? AND c.goods_id = ?";
		return (SellBackChild) this.daoSupport.queryForObject(sql,SellBackChild.class, orderId,goodsId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#editChildStorageNum(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void editChildStorageNum(Integer orderId,Integer parentId,Integer goods_id, Integer num) {
		this.daoSupport.execute("update es_sellback_child set storage_num=? where order_id=? and goods_id=? and parent_id = ?", num,orderId, goods_id,parentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#list(int)
	 */
	@Override
	public List<Map> list(int goods_id) {
		String sql = "select pp.*, g.name as name ,g.sn as sn ,g.p11 as spec from "
				+ "es_package_product"
				+ " pp inner join "
				+ "es_goods"
				+ " g on g.goods_id = pp.rel_goods_id";
		sql += " where pp.goods_id = " + goods_id;
		List<Map> list = this.daoSupport.queryForList(sql);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getPackInfo(int, int)
	 */
	@Override
	public Map getPackInfo(int goodsId,int relGoodsId){
		String sql = "SELECT * FROM es_package_product WHERE goods_id = ? AND rel_goods_id = ?";
		Map map = this.daoSupport.queryForMap(sql, goodsId,relGoodsId);
		return map;
	} 

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#isPack(int)
	 */
	@Override
	public int isPack(int productid) {
		int is_pack = this.daoSupport
				.queryForInt(
						"select is_pack from es_product where product_id =?",
						productid); // 是否是整箱
		return is_pack;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#delSellerBackChilds(int)
	 */
	@Override
	public void delSellerBackChilds(int orderId){
		String sql = "delete from es_sellback_child WHERE order_id = ? ";
		this.daoSupport.execute(sql, orderId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#addRetund(com.enation.app.shop.core.order.model.SellBackList)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="订单ID为${sellBackList.ordersn}由${sellBackList.regoperator}申请退款,金额：${sellBackList.apply_alltotal}")
	public void addSellBack(SellBack sellBackList) {
		Order order  = this.orderManager.get(sellBackList.getOrderid());
		sellBackList.setOrdersn(order.getSn());
		
		/**
		 * 在退货单中添加赠品id并改变订单赠品的状态 add_by DMRain 2016-7-21
		 */
		if (order.getGift_id() != 0) {
			int count = this.daoSupport.queryForInt("select count(0) from es_sellback_list where orderid = ? and gift_id = ?", sellBackList.getOrderid(), order.getGift_id());
			
			//判断订单中的赠品是否已经退货，count等于0代表还没有退货
			if (count == 0) {
				sellBackList.setGift_id(order.getGift_id());
				this.orderGiftManager.updateGiftStatus(order.getGift_id(), sellBackList.getOrderid(), 1);
			}
		}
		
		this.daoSupport.insert("es_sellback_list", sellBackList);

		int sellBackId = this.daoSupport.getLastId("es_sellback_list");

		//循环订单货物表将所有的货物添加至退款货品列表
		List<OrderItem> itemList=orderManager.listGoodsItems(sellBackList.getOrderid());
		for (OrderItem orderItem : itemList) {
			SellBackGoodsList sellBackGoods = new SellBackGoodsList();
			sellBackGoods.setPrice(orderItem.getPrice());
			sellBackGoods.setReturn_num(orderItem.getNum());
			sellBackGoods.setShip_num(orderItem.getNum());
			sellBackGoods.setGoods_id(orderItem.getGoods_id());	
			sellBackGoods.setGoods_remark(sellBackList.getReason());
			sellBackGoods.setProduct_id(orderItem.getProduct_id());
			sellBackGoods.setItem_id(orderItem.getItem_id());
			sellBackGoods.setGoods_name(orderItem.getName());
			sellBackGoods.setGoods_image(orderItem.getImage());
			sellBackGoods.setSpec_json(orderItem.getAddon());
			//设置退货商品的退货单id
			sellBackGoods.setRecid(sellBackId);

			//更新订单商品表为申请退货状态
			//this.daoSupport.execute("update es_order_items set state=? where item_id=?", OrderItemStatus.APPLY_RETURN,sellBackGoods.getItem_id());

			this.daoSupport.insert("es_sellback_goodslist", sellBackGoods);
		}
		//修改订单状态为已申请退款
		this.daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_MAINTENANCE,sellBackList.getOrderid());

		//订单退还事件
		orderPluginBundle.afterReturnOrder(null, order);

		orderManager.addLog(sellBackList.getOrderid(), "申请退款,金额:"+sellBackList.getApply_alltotal());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#authRetund(java.lang.Integer, java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.ORDER,detail="平台售后已审核")
	public void authRetund(Integer id, Integer status,String seller_remark) {
		
		//更改售后申请状态记录操作备注
		this.daoSupport.execute("update es_sellback_list set tradestatus=?,seller_remark=? where id=?",status,seller_remark, id);

		SellBack sellBackList = this.get(id);
		
		//获取退款金额如果为空，则把申请金额填入退款金额
		Double alltotal_pay = sellBackList.getAlltotal_pay();
		if(alltotal_pay==null){
			sellBackList.setAlltotal_pay(sellBackList.getApply_alltotal());
			this.daoSupport.update("es_sellback_list", sellBackList, " id = "+sellBackList.getId());
		}
		
		//获取单据的类型 1：退款单，2：退货单 add_by DMRain 2016-7-21
		Integer type = sellBackList.getType();
		
		
		//定义一个日志字段 add_by DMRain 2016-7-21
		String log = "";
		
		//获取退货单中的赠品id add_by DMRain 2016-7-21
		Integer gift_id = sellBackList.getGift_id();
		
		//审核通过创建退款单
		if(status==1){
			if (type == 1) {
				log = "申请退款，通过，申请退款金额："+sellBackList.getApply_alltotal();
			} else if (type == 2) {
				log = "申请退货，通过，申请退款金额："+sellBackList.getApply_alltotal();
			}
			
			//后台审核后，如果是单店系统，就创建退款单，多店系统不创建退货单，add_by DMRain 2016-7-21
			if (EopSetting.PRODUCT.equals("b2c")) {
				this.addRefund(id);
			}
			
			orderManager.addLog(sellBackList.getOrderid(), log, UserConext.getCurrentAdminUser().getUsername());
			
			/**
			 * 判断退货单中的赠品id是否为空,如果不为空，就改变订单赠品状态为已完成退货
			 * add_by DMRain 2016-7-21
			 */
			if (gift_id != null) {
				this.orderGiftManager.updateGiftStatus(gift_id, sellBackList.getOrderid(), 2);
			}
			//退款分为发货前退款和发货后退款，所以发货前退款，可用库存回归
			if(type == 1){
				Order order = orderManager.get(sellBackList.getOrderid());
				if(order.getSigning_time()==null||order.getSigning_time()==0){//发货前退款
					List<Map<String, Object>> goodsList = order.getItemList();
					if(goodsList!=null){
						for(int i=0;i<goodsList.size();i++){
							OrderItem item = (OrderItem) goodsList.get(i);
							int num = item.getNum();
							int goods_id = item.getGoods_id();
							int product_id = item.getProduct_id();
							int depot_id = order.getDepotid();
							
							String sql = "update es_product_store set enable_store=enable_store+"+num+" where productid=? and goodsid=? and depotid=?";
							this.daoSupport.execute(sql, product_id,goods_id,depot_id);
						}
					}
				}
			}
		}else{
			if (type == 1) {
				log = "申请退款，拒绝通过";
			} else if (type == 2) {
				log = "申请退货，拒绝通过";
			}
			
			//退款分为发货前退款和发货后退款，所以退款拒绝后，应变为相应退款前的状态，使用签收时间区分两者
			Order order = orderManager.get(sellBackList.getOrderid());
			int orderstatus = 0 ;
			if(order.getSigning_time()==null||order.getSigning_time()==0){//未发货，则变为已支付状态
				orderstatus = OrderStatus.ORDER_PAY;
			}else{//变为已完成状态
				orderstatus = OrderStatus.ORDER_COMPLETE;
			}
			//修改订单状态为可申请退款
			this.daoSupport.execute("update es_order set status=? where order_id=?",orderstatus,sellBackList.getOrderid());
			
			//根据订单id获取订单商品货物集合 add_by DMRain 2016-7-26
			List<OrderItem> itemList = this.orderManager.listGoodsItems(sellBackList.getOrderid());
			
			/** 遍历订单商品货物集合，在审核退货拒绝时，将已经申请退货的货物状态改为正常，以便再次申请退货  add_by DMRain 2016-7-26*/
			for (OrderItem item : itemList) {
				if (item.getState() == 1) {
					this.daoSupport.execute("update es_order_items set state = ? where item_id = ?", OrderItemStatus.NORMAL, item.getItem_id());
				}
			}
			
			orderManager.addLog(sellBackList.getOrderid(), log, UserConext.getCurrentAdminUser().getUsername() );
			
			/**
			 * 判断退货单中的赠品id是否为空,如果不为空，就改变订单赠品状态为正常
			 * add_by DMRain 2016-7-21
			 */
			if (gift_id != null) {
				this.orderGiftManager.updateGiftStatus(gift_id, sellBackList.getOrderid(), 0);
			}
		}
		this.saveLog(id, log);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#inStorage(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void inStorage(Integer depotid, Integer sid, Integer goods_id,
			Integer num, Integer product_id,Integer itemId) {

		this.editStorageNum(sid, product_id, num,itemId);// 修改入库数量
		goodsStoreManager.increaseStroe(goods_id, product_id, depotid, num);
		this.saveLog(sid, "商品入库");

	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getSellBack(java.lang.Integer)
	 */
	@Override
	public SellBack getSellBack(Integer order_id) {
		String sql = "select * from es_sellback_list where orderid=? and ( (type=1 and tradestatus!=?) or (type=2 and tradestatus!=?))";
		SellBack result = (SellBack) this.daoSupport.queryForObject(sql, SellBack.class, order_id,SellBackStatus.refuse.getValue(),SellBackStatus.cancel.getValue());
		return result;

	}
	
	/**
	 * 为退货商品填充各种信息
	 * @param itemList 订单商品列表
	 * @param returnItemList 退货商品列表
	 */
	private void fillGoodsItem(List<Map> itemList,List<SellBackGoodsList> returnItemList){
		for (SellBackGoodsList sellBackGoods : returnItemList) {
			for (Map item  : itemList) {
				if(item.get("product_id").equals(sellBackGoods.getProduct_id())){
					sellBackGoods.setGoods_image((String)item.get("image"));
					sellBackGoods.setGoods_sn((String)item.get("sn"));
					sellBackGoods.setGoods_name((String)item.get("name"));
					String specJson =(String) item.get("addon");
					sellBackGoods.setSpec_json(specJson);
				}
			}
		}
	}


	/**
	 * 检测货物状态，是否可以申请退货
	 * @param orderid 订单id
	 * @throws RuntimeException 如果存在不能退货的状态抛出此异常
	 */
	private void checkItemStateForApply(List<Map> itemList){

		for (Map item : itemList) {
			int state =(Integer) item.get("state");
			if(state!=OrderItemStatus.NORMAL){
				throw new RuntimeException("商品["+item.get("name")+"]已经申请过退货了");
			}
		}
	}

	/**
	 * 
	 * @param goodsid
	 * @return
	 */
	private Map getGoods(int goodsid) {
		String sql = "select * from es_goods  where goods_id=?";
		Map goods = this.daoSupport.queryForMap(sql, goodsid);
		return goods;
	}

	/**
	 * 检查入库状态
	 * 判断退货单是否已经全部入库
	 * 循环退货商品列表判断退货数量是否等于入库数量
	 * @param id退货单id
	 */
	private void examination(Integer id){

		boolean is_all=true;
		List<SellBackGoodsList> sellBackGooodsList=daoSupport.queryForList("select * from  es_sellback_goodslist where recid=?",SellBackGoodsList.class, id);
		for (SellBackGoodsList goods : sellBackGooodsList) {
			if(!goods.getReturn_num().equals(goods.getStorage_num())){
				is_all= false;
			}

		}

		//判断是否全部入库
		if(is_all){
			this.confirmStorage(id);
		}else{
			this.confirmPartStorage(id);
		}
	}

	/**
	 * 修改退货单状态为部分入库
	 * @param id 退货单Id
	 */
	private void confirmPartStorage(Integer id) {
		this.daoSupport.execute("update es_sellback_list set tradestatus= ?  where id=?",SellBackStatus.some_storage.getValue(), id);
	}
	
	/**
	 * 修改退货单状态为全部入库
	 * 创建退款单
	 * @param id
	 */
	private void confirmStorage(Integer id) {
		//修改退货单状态为 全部入库
		this.daoSupport.execute("update es_sellback_list set tradestatus= ? where id=?",SellBackStatus.all_storage.getValue(), id);

		//添加退款单
		//this.getStoreRefund(id);
		addRefund(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#getSellBackGoodsByItemId(int, java.lang.Integer)
	 */
	@Override
	public SellBackGoodsList getSellBackGoodsByItemId(int id, Integer itemId) {
		String sql="select * from es_sellback_goodslist where recid=? and item_id=?";
		SellBackGoodsList result=(SellBackGoodsList) this.daoSupport.queryForObject(sql, SellBackGoodsList.class, id,itemId);
		return result;
	}
	
	/**
	 * 添加退款单
	 * @param sellBackList
	 */
	private void addRefund(Integer id){
		//如果审核通过，创建退款单
		SellBack sellBackList=this.get(id);
		Refund refund=new Refund();
		refund.setSn(DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));
		refund.setMember_id(sellBackList.getMember_id());
		refund.setReturn_account(sellBackList.getReturn_account());
		refund.setRefund_money(sellBackList.getApply_alltotal());
		refund.setSellback_id(sellBackList.getId());
		refund.setSndto(sellBackList.getSndto());
		refund.setCreate_time(DateUtil.getDateline());
		refund.setOrder_id(sellBackList.getOrderid());
		refund.setStatus(0);
		Order order=this.orderManager.get(sellBackList.getOrdersn());
		refund.setRefund_type(order.getPayment_type());
		refund.setRefund_way(sellBackList.getRefund_way());
		refundManager.addRefund(refund);
	}
	
	@Override
	@Log(type=LogType.ORDER,detail="订单ID为${order_id}退货商品入库")
	public void editSellBackDepotId(Integer id, Integer order_id) {
		String sql = "update es_sellback_list set depotid=? where orderid =?";
		this.daoSupport.execute(sql, id, order_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ISellBackManager#saveLogBySystem(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void saveLogBySystem(Integer recid, String logdetail) {
		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDateline());
		sellBackLog.setOperator("系统检测");
		this.daoSupport.insert("es_sellback_log", sellBackLog);
		
	}
}

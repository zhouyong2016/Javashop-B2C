package com.enation.app.shop.core.order.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.goods.service.IGoodsStoreManager;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.impl.MemberAddressManager;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.DlyCenter;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderPrintManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @author Kanon 2015-2-26;版本改造
 *
 */
@Service("orderPrintManager")
public class OrderPrintManager implements IOrderPrintManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IDepotManager depotManager;
	
	@Autowired
	private MemberAddressManager memberAddressManager;
	
	@Autowired
	private IOrderFlowManager orderFlowManager;
	
	@Autowired
	private IGoodsStoreManager goodsStoreManager;
	@Override
	public String getShipScript(Integer[] orderid) {
		if(orderid==null || orderid.length==0){return "";}
		
		String sql="select * from es_order where order_id in("+StringUtil.arrayToString(orderid, ",")+")";
		List<Order> orderList  = this.daoSupport.queryForList(sql, Order.class);
		
		StringBuffer str = new StringBuffer();
		for (Order order : orderList) {
			str.append(this.getShipTemplate(order));
		}
		return str.toString();
	}
	

	@Override
	public String getExpressScript(Integer[] orderid) {
		
			String sql="select * from es_order where order_id in("+StringUtil.arrayToString(orderid, ",")+")";
			List<Order> orderList  = this.daoSupport.queryForList(sql, Order.class);
			
			StringBuffer str = new StringBuffer();
			String code="";
			int size=0;
			String app_apth = StringUtil.getRootPath();

			for (Order order : orderList) {
				if(disdlycenter()){
					return "请选择默认发货点";
				}else if(!code.equals(order.getShipping_type())&&size!=0){
					return "快递单选择配送方式不同";
				}else if(this.getDlyType(order.getShipping_type()).equals("null")){
					return "请添加配送方式";
				}else if(order.getPay_status() == 2){
					if(!FileUtil.exist(app_apth+"/shop/admin/printtpl/express/"+this.getcode(order.getLogi_name())+".html")){
						return "没有此快递单模板请添加";
					}else{
						str.append(this.getExpressTemplate(order,this.getcode(order.getLogi_name())));
						code=order.getLogi_name();
					}
				}
				size+=1;
			}
			return str.toString();
	}
	/**
	 * 检验是否有默认发货点
	 * @return
	 */
	private boolean disdlycenter(){
		String sql="select count(name) from es_dly_center where choose='true' and disabled='false'";
		return this.daoSupport.queryForInt(sql)>0?false:true;
	}
	
	private String getcode(String logiName){
		List<Map> list=this.daoSupport.queryForList("select code from es_logi_company where name=?",logiName);
		if(list.size()!=0){
			return list.get(0).get("code").toString();
		}else{
			return "null";
		}
	}
	private String getDlyType(String logiName){
			List<Map> list= this.daoSupport.queryForList("select name from es_dly_type where name=?", logiName);
			if(list.size()!=0){
				return list.get(0).get("name").toString();
			}else{
				return "null";
			}
	}
	
	
	@SuppressWarnings("unchecked")
	private String getExpressTemplate(Order order,String code){
		@SuppressWarnings("rawtypes")
		List<Map> itemList  = listItem(order.getOrder_id());
		
		int  itemCount = 0;
		for (Map item : itemList) {
			int num = (Integer)item.get("num");
			itemCount+=num;
		}
		DlyCenter center  = this.getdlycenter();
		Calendar cal = Calendar.getInstance();
		FreeMarkerPaser freeMarkerPaser =  new FreeMarkerPaser();
		
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName(code);
		freeMarkerPaser.setPageFolder("/shop/admin/printtpl/express");
		
		freeMarkerPaser.putData("order",order);
		freeMarkerPaser.putData("cod_order",order.getShip_no());
		freeMarkerPaser.putData("dlycenter",center);
		//定义发货公司名称
		freeMarkerPaser.putData("shop_name","javashop");
		
		freeMarkerPaser.putData("o_ship_province",this.getregions(order.getShip_provinceid()));
		freeMarkerPaser.putData("o_ship_city",this.getregions(order.getShip_cityid()));
		freeMarkerPaser.putData("o_ship_region", this.getregions(order.getShip_regionid()));
		freeMarkerPaser.putData("d_dly_province",this.getregions(center.getProvince_id()));
		freeMarkerPaser.putData("d_dly_city", this.getregions(center.getCity_id()));
		freeMarkerPaser.putData("d_dly_region", this.getregions(center.getRegion_id()));
		freeMarkerPaser.putData("year",cal.get(Calendar.YEAR));
		freeMarkerPaser.putData("month",cal.get(Calendar.MONTH )+1);
		freeMarkerPaser.putData("day", cal.get(Calendar.DATE));
		freeMarkerPaser.putData("itemCount", itemCount);
		freeMarkerPaser.putData("ship_time","ship_time");
		freeMarkerPaser.putData("text","自定义内容");
		String script = freeMarkerPaser.proessPageContent();
		return script;
		
	}
	
	/**
	 * 过滤商品名称中的影响json解析的特殊字符
	 * @param str
	 * @return
	 */
	public static String StringFilter(String str) {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	} 
	
	private DlyCenter getdlycenter(){
		String sql="select * from es_dly_center where choose='true' and disabled='false'";
		return (DlyCenter)this.daoSupport.queryForObject(sql, DlyCenter.class);
	}
	private String getShipTemplate(Order order){
		
		List<Map> itemList  = listItem(order.getOrder_id()); 
		try {
			for (int i = 0; i < itemList.size(); i++) {
				itemList.get(i).put("name",
						StringFilter(itemList.get(i).get("name").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		int  itemCount = 0;
		for (Map item : itemList) {
			int num = (Integer)item.get("num");
			itemCount+=num;
			//获取订单商品规格
			if(item.get("addon")!=null){
				String addon=item.get("addon").toString();
				if(!StringUtil.isEmpty(addon)){
					item.put("specList", (List) JSONArray.toCollection(JSONArray.fromObject(addon),Map.class));
				}
			}
		}
		String depotname="";
		//获取发货信息
		DlyCenter dlycenter = this.getdlycenter();
		//获取仓库名称
		depotname= dlycenter!=null?dlycenter.getName():"";
		
		
		//发货人
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		String adminname = adminUser.getRealname();
		if(StringUtil.isEmpty(adminname)){
			adminname = adminUser.getUsername();
		}
		
		FreeMarkerPaser freeMarkerPaser =  new FreeMarkerPaser();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName("user");
		freeMarkerPaser.setPageFolder("/shop/admin/printtpl/ship");
		freeMarkerPaser.putData("order",order);
		
		freeMarkerPaser.putData("itemCount",itemCount);
		freeMarkerPaser.putData("depotname",depotname);
		freeMarkerPaser.putData("adminname",adminname);
		
		String userHtml = freeMarkerPaser.proessPageContent();
		String itemHtml = this.createItemHtml(freeMarkerPaser, itemList);
							
		
		freeMarkerPaser.setPageName("footer");
		String footerHtml = freeMarkerPaser.proessPageContent();
		
		
		userHtml=userHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
		itemHtml=itemHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
		footerHtml=footerHtml.replaceAll("(\r\n|\r|\n|\n\r)", "");
	
		freeMarkerPaser.setPageName("script");
		freeMarkerPaser.putData("userHtml",userHtml);
		freeMarkerPaser.putData("itemHtml",itemHtml);
		freeMarkerPaser.putData("footerHtml",footerHtml);
		String script = freeMarkerPaser.proessPageContent();
		
		return script;
	}
	
	private String createItemHtml(FreeMarkerPaser freeMarkerPaser,List itemList){
		
		StringBuffer itemHtml = new StringBuffer();
		
		int totalCount = itemList.size();//总条数
		int pageSize=15; //每页记录数
		int firstPageSize=10; //首页记录数
		 
		
		//生成第一页的商品列表
		int firstMax=totalCount> firstPageSize?firstPageSize:totalCount;
		
		List firstList  = itemList.subList(0, firstMax);
		
		
		freeMarkerPaser.setPageName("item");
		freeMarkerPaser.putData("itemList",firstList);
		freeMarkerPaser.putData("start",0);
		String firstHtml = freeMarkerPaser.proessPageContent();
		firstHtml="LODOP.ADD_PRINT_TABLE(\"60px\",\"-1\",\"200mm\",\"100%\",'"+firstHtml+"');";
		firstHtml+="LODOP.SET_PRINT_STYLEA(0,\"LinkedItem\",1);";
		itemHtml.append(firstHtml);
		
		//剩余数量
		int expessCount =totalCount-firstList.size();
		
		//计算剩下的分几页 
		int pageCount =(expessCount/pageSize)+(expessCount%pageSize>0?1:0);
		
		for(int pageNo=1;pageNo<=pageCount;pageNo++){
			itemHtml.append("LODOP.NEWPAGEA();");
			int start  =  firstMax+ (pageNo-1) *pageSize;
			int end = start+pageSize;
			if(pageNo==pageCount){
				end=totalCount;
			}
				
			List subList =itemList.subList( start, end);
			freeMarkerPaser.putData("start",start);
			freeMarkerPaser.putData("itemList",subList);
			String subHtml = freeMarkerPaser.proessPageContent();
			
			subHtml="LODOP.ADD_PRINT_TABLE(\"0\",\"-0\",\"200mm\",\"100%\",'"+subHtml+"');"+"LODOP.SET_PRINT_STYLEA(0,\"LinkedItem\",1);"; 
			itemHtml.append(subHtml); 
		} 
		return itemHtml.toString().replace("'", "\'");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderPrintManager#saveShopNos(java.lang.Integer[], java.lang.String[], java.lang.String[], java.lang.String[])
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.ORDER,detail="保存发货单号")
	public void saveShopNos(Integer[] orderids, String[] shipNos,String[] logi_id,String[] logi_name) {
		int i=0;
		for (Integer orderid : orderids) {
			String shipno= shipNos[i];
			this.daoSupport.execute("update es_order set ship_no=?,logi_id=?,logi_name=? where order_id=?", shipno,logi_id[i],logi_name[i],orderid);
			orderManager.addLog(orderid, "添加快递单号："+shipno+"，物流公司："+logi_name[i]);
			i++;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderPrintManager#ship(java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="订单发货")
	public String ship(Integer[] orderids) {
		StringBuffer sql = new StringBuffer("select * from es_order where disabled=0 ");
		
		if(orderids!=null && orderids.length>0)
			sql.append(" and order_id in ("+StringUtil.arrayToString(orderids, ",")+")");
		 
		List<Order> orderList  =  this.daoSupport.queryForList(sql.toString(), Order.class);
		String is_ship="true";
		for (Order order : orderList) {
			if(order.getStatus()==OrderStatus.ORDER_CANCELLATION|| order.getIs_cancel()==1){
				throw new RuntimeException("订单取消或订单申请取消订单无法发货!");
			}
			/**
			 * 如果为单店系统才检测仓库库存
			 */
			if("b2c".equals(EopSetting.PRODUCT)){
				is_ship=is_stock(order);
			}
			
			if(is_ship.equals("true")){
				
				//发货并判断配送方式的快递公司是否选择
				if(this.ship(order))
					return "发货失败";
			}
			
		}
		return is_ship;
	}
	
	
	
	
	/**
	 * 检测库存是否充足
	 * @param order
	 * @return
	 */
	private String is_stock(Order order){
		
		List<OrderItem> items=this.daoSupport.queryForList("select * from es_order_items where order_id=?", OrderItem.class,order.getOrder_id());
		
		String is_ship="true";
		for (OrderItem item:items) {
			int goods_num= goodsStoreManager.getbStoreByProId(item.getProduct_id(), order.getDepotid());
			if(goods_num<item.getNum()){
				is_ship="商品["+item.getName()+"]在仓库["+depotManager.get(order.getDepotid()).getName()+"]中库存不足，库存量为["+goods_num+"]发货量为["+item.getNum()+"],请进行调拨或更换发货仓库后再发货。";
			}
		}
		return is_ship;
	}
	
	
	private boolean ship(Order order){
		/*---------------------------
		 *发货
		 ------------------------------*/
		//创建发货单
		Delivery delivery = new Delivery();
		delivery.setOrder_id(order.getOrder_id());
		delivery.setMoney(order.getShipping_amount());
		delivery.setIs_protect(0);
		delivery.setProtect_price(0D);
		delivery.setMember_id(order.getMember_id());
		
		delivery.setLogi_id(order.getLogi_id());
		delivery.setLogi_name(order.getLogi_name());
		delivery.setShip_type(order.getLogi_name());
		
		Integer addressid  = order.getAddress_id();
		if(addressid!=null&&addressid!=0){
			MemberAddress address=  memberAddressManager.getAddress(order.getAddress_id());
			delivery.setProvince(address.getProvince());
			delivery.setCity(address.getCity());
			delivery.setRegion(address.getRegion());
		}else{
			delivery.setProvince("");
			delivery.setCity("");
			delivery.setRegion("");
		}
		
		delivery.setShip_name(order.getShip_name());
		delivery.setShip_addr(order.getShip_addr());
		delivery.setShip_email(order.getShip_email());
		delivery.setShip_mobile(order.getShip_mobile());
		delivery.setShip_zip(order.getShip_zip());
		

		
		//发货单号为订单中填写的发货单号
		delivery.setLogi_no(order.getShip_no());
		
		//创建发货明细
		List<DeliveryItem> itemList  = new ArrayList<DeliveryItem>();
		List<OrderItem> orderItemList  =  orderManager.listGoodsItems(order.getOrder_id());
		for (OrderItem orderItem : orderItemList) {
			DeliveryItem item1= new DeliveryItem();
			item1.setGoods_id(orderItem.getGoods_id());
			item1.setName(orderItem.getName());
			item1.setNum(orderItem.getNum());
			item1.setProduct_id(orderItem.getProduct_id());
			item1.setOrder_itemid(orderItem.getItem_id());
			item1.setDepotId(order.getDepotid());
			itemList.add(item1);
		}
		this.orderFlowManager.shipping(delivery, itemList);
		return false;
	}

	
	 
	 
	private List listItem(int orderid){
		String sql ="select i.num,i.price,i.addon,g.sn,g.name,g.type_id,g.p11 p11,g.p8 p8 from es_order_items i inner join es_goods g on i.goods_id = g.goods_id where i.order_id=?";
		return this.daoSupport.queryForList(sql, orderid);
	}
	private String getregions(Integer regionsId){
		String sql="select local_name from es_regions where region_id = "+regionsId;
		return this.daoSupport.queryForString(sql);
	}

	@Override
	public int checkShipNo(Integer[] order_id) {
		String id_str = StringUtil.arrayToString(order_id, ",");
		String sql = "select count(ship_no) from es_order where order_id in (" + id_str + ")";
		int result = this.daoSupport.queryForInt(sql);
		return result;
	}
	

}

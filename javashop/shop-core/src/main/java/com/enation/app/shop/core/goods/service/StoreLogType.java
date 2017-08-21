package com.enation.app.shop.core.goods.service;


/**
 * 库存日志类型
 * @author kingapex
 *2014-5-6下午3:21:26
 */
public enum StoreLogType {
	unknown("未知的",-1),purchase("采购入库",0),create_order("创建订单",1),ship("发货出库",2),cancel_order("取消订单",3),return_goods("退货入库",4),
	inventory_in("盘点入库",5),inventory_out("盘点出库",6),movements_in("调拨入库",7), movements_out("调拨出库",8),change_depot("切换发货仓库",9);
	
	private int type;
	private String name;
	
	StoreLogType(String name,int type){
		this.type=type;
		this.name= name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	
}

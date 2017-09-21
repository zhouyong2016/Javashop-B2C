package com.enation.app.shop.core.order.model;

/**
 * 退货单状态
 * @author kingapex
 *2014-3-2下午1:00:27
 */
public enum SellBackStatus  {
	apply("申请退货",0),in_storage("退货入库",1),all_storage("全部入库",4),cancel("取消退货",2),some_storage("部分入库",5),refund("已退款",6),wait("等待审核",0),pass("审核通过",1),refuse("审核拒绝",2),application("等待退款",3),payment("退款中",7),refundfail("退款失败",8);
			
	private String name;
	private int value;
	
	private SellBackStatus(String _name,int _value){
		this.name=_name;
		this.value= _value;
		
	}
	
	
	public static SellBackStatus valueOf(int status){
		SellBackStatus[] statusList  = values();
		for (SellBackStatus sellBackStatus : statusList) {
			if(sellBackStatus.getValue() == status){
				return sellBackStatus;
			}
		}
		return null;
	}
	public String getName() {
		return name;
	}
	public int getValue() {
		return value;
	}
}


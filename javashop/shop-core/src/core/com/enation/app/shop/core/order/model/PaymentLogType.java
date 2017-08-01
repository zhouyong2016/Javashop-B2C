package com.enation.app.shop.core.order.model;


/**
 * 应收应付
 * @author kingapex
 *2014-3-2下午9:35:56
 */
public enum PaymentLogType {
	
	  	receivable("收款单",1),payable("退款款单",2);
		
		private String name;
		private int value;
		
		private PaymentLogType(String _name,int _value){
			this.name=_name;
			this.value= _value;
			
		}
		public String getName() {
			return name;
		}
		public int getValue() {
			return value;
		}
		
		public static PaymentLogType valueOf(int status){
			PaymentLogType[]  list  = values();
			for (PaymentLogType log : list) {
				if(log.getValue() == status){
					return log;
				}
			}
			return null;
		}
}

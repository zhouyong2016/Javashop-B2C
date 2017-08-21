package com.enation.app.shop.component.orderreturns.service;

/**
 * 退换货订单状态
 * 
 * @author dable
 * 
 */
public abstract class ReturnsOrderStatus {

	/**
	 * ------------------------------------------------------------- 退换货订单状态
	 * -------------------------------------------------------------
退换货表状态：
0.申请已提交 
1.已拒绝 
2.已通过审核    
3.已收货（并发货）
4.完成



	 */
	public static final int APPLY_SUB =0;
	public static final int APPLY_REFUSE =1;
	public static final int APPLY_PASSED = 2;
	public static final int GOODS_REC = 3;
	public static final int APPLY_END =4;

	/**
	 * 获取退换货订单状态
	 * 
	 * @param status
	 * @return
	 */
	public static String getOrderStatusText(int status) {
		String text = "";
		switch (status) {
		case APPLY_SUB:
			text = "申请已提交";
			break;
		case APPLY_REFUSE:
			text = "已拒绝 ";
			break;
		case APPLY_PASSED:
			text = "已通过审核";
			break;
		case GOODS_REC:
			text = "已收货(换货已发货)";
			break;
		case APPLY_END:
			text = "完成";
			break;

		}
		return text;
	}
}

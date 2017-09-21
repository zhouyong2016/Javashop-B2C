package com.enation.app.shop.core.member.service;

import java.util.List;

import com.enation.app.shop.component.receipt.Receipt;

/**
 * 
 * 会员发票
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:31:15
 */

public interface IMemberReceiptManager {
	/**
	 * 查询我的发票
	 * @return List<Receipt>
	 */
	public List<Receipt> listReceipt();
	/**
	 * 查询指定发票
	 * @param id
	 * @return Receipt
	 */
	public Receipt getReceipt(Integer id);
	/**
	 * 修改我的发票
	 * @param receipt
	 * @return Receipt
	 */
	public Receipt updateReceipt(Receipt receipt);

	/**
	 * 增加我的发票
	 * @param receipt
	 * @return Receipt
	 */
	public Receipt addReceipt(Receipt receipt);

	/**
	 * 删除发票
	 * @param id
	 * @return boolean
	 */
	public void delete(Integer id);
	
	/**
	 * 获取发票数量
	 * @param 
	 * @return int
	 */
	public int receiptCount(int member_id);
	/**
	 * 获取会员最早的一条发票ID
	 * @param 
	 * @return int
	 */
	public int receiptMin(int member_id);
	/**
	 * 修改会员默认发票
	 */
	public void updateReceiptDefult();
	/**
	 * 修改会员默认发票
	 */
	public void receiptDefult(Integer id);
	/**
	 * 获取会员默认发票
	 * @return 
	 */
	public Receipt getDefultReceipt(Integer member_id);
	
}

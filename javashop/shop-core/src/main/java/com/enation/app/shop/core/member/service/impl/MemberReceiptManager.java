package com.enation.app.shop.core.member.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;

/**
 * 
 * 会员发票管理 
 * @author wanglu	
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:33:46
 */

@Service
public class MemberReceiptManager implements IMemberReceiptManager {

	@Autowired
	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	public Receipt getReceipt(Integer id) {
		Receipt receipt = this.daoSupport.queryForObject(
				"select * from es_receipt where id = ?",
				Receipt.class, id);
		return receipt;
	}/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Receipt updateReceipt(Receipt receipt) {
		
		this.daoSupport.update("es_receipt", receipt, "id =" + receipt.getId());
		
		return receipt;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Receipt addReceipt(Receipt receipt) {

		Member member = UserConext.getCurrentMember();
		receipt.setMember_id(member.getMember_id());

		receipt.setAdd_time(DateUtil.getDateline());

		this.daoSupport.insert("es_receipt", receipt);
		Integer queryForInt = this.daoSupport.queryForInt("Select Max(id)  From es_receipt ");
		Receipt queryForObject = this.daoSupport.queryForObject("Select *  From es_receipt where id = ? ", Receipt.class, queryForInt);
		return queryForObject;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer id) {

		this.daoSupport.execute("delete from es_receipt where id = ? ",id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	public List<Receipt> listReceipt() {
		Member member = UserConext.getCurrentMember();
		List<Receipt> list = this.daoSupport.queryForList(
				"SELECT * FROM es_receipt WHERE member_id = ?", Receipt.class,  member.getMember_id());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	public int receiptCount(int member_id) {
		return daoSupport.queryForInt("select count(*) from es_receipt where member_id=?", member_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#receiptCount(int)
	 */
	@Override
	public int receiptMin(int member_id) {
		return  daoSupport.queryForInt("select min(id) from es_receipt where member_id=?", member_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#updateAddressDefult()
	 */
	@Override
	public void updateReceiptDefult() {
		Member member = UserConext.getCurrentMember();
		this.daoSupport.execute(
				"update es_receipt set is_default = 0 where member_id = ?", member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#addressDefult(java.lang.String)
	 */
	@Override
	public void receiptDefult(Integer id) {
		this.daoSupport.execute(
				"update es_member_address set is_default = 1 where id = ?",id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberReceiptManager#getDefultReceipt(java.lang.Integer)
	 */
	@Override
	public Receipt getDefultReceipt(Integer member_id) {
		return this.daoSupport.queryForObject("Select *  From es_receipt where member_id = ? and is_default=1 ", Receipt.class, member_id);
	}
	
	
}

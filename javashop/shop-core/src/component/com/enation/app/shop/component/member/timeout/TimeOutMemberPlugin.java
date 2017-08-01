package com.enation.app.shop.component.member.timeout;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryMonthExecuteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员等了次数每月清0
 * @author zh
 *
 */
@Component
public class TimeOutMemberPlugin extends AutoRegisterPlugin implements IEveryMonthExecuteEvent{
	private IDaoSupport daoSupport;

	@Override
	public void everyMonth() {
		String sql="UPDATE es_member SET logincount = 0";
		this.daoSupport.execute(sql);	
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}

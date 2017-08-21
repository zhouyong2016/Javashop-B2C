package com.enation.app.shop.core.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.AdvanceLogs;
import com.enation.app.shop.core.member.service.IAdvanceLogsManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

/**
 * 预存款日志
 * 
 * @author lzf<br/>
 *         2010-3-25 下午01:36:37<br/>
 *         version 1.0<br/>
 */
@Service("advanceLogsManager")
public class AdvanceLogsManager  implements IAdvanceLogsManager {
	@Autowired
	private IDaoSupport daoSupport;
	
	public Page pageAdvanceLogs(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();
		Page page = this.daoSupport.queryForPage("select * from es_advance_logs where member_id=? order by mtime DESC", pageNo, pageSize, member.getMember_id());
		return page;
	}

	public void add(AdvanceLogs advanceLogs) {
		this.daoSupport.insert("es_advance_logs", advanceLogs);
	}

	public List listAdvanceLogsByMemberId(int member_id) {
		return this.daoSupport.queryForList("select * from es_advance_logs where member_id=? order by mtime desc",	AdvanceLogs.class, member_id);
	}
	
}

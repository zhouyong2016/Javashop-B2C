package com.enation.app.shop.core.member.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Message;
import com.enation.app.shop.core.member.service.IMessageManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

public class MessageManager   implements IMessageManager {
	@Autowired
	private IDaoSupport  daoSupport;
	
	public Page pageMessage(int pageNo, int pageSize, String folder) {
		Member member = UserConext.getCurrentMember();
		String sql = "select * from es_message where folder = ? ";
		if(folder.equals("inbox")){//收件箱
			sql += " and to_id = ? and del_status != '1'";
		}else{//发件箱
			sql += " and from_id = ? and del_status != '2'";
		}
		sql += " order by date_line desc";
		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize, folder, member.getMember_id());
		List<Map> list = (List<Map>) (webpage.getResult());
//		for (Map order : list) {
//			Long time = (Long)order.get("date_line");
//			order.put("date", (new Date(time)));
//		}
		return webpage;
	}

	
	public void addMessage(Message message) {
		this.daoSupport.insert("es_message", message);
		
	}

	
	public void delinbox(String ids) {
		this.daoSupport.execute("delete from es_message where msg_id in (" + ids + ") and del_status = '2'");
		this.daoSupport.execute("update es_message set del_status = '1' where msg_id in (" + ids + ")");
	}

	
	public void deloutbox(String ids) {
		this.daoSupport.execute("delete from es_message where msg_id in (" + ids + ") and del_status = '1'");
		this.daoSupport.execute("update es_message set del_status = '2' where msg_id in (" + ids + ")");
	}

	
	public void setMessage_read(int msg_id) {
		this.daoSupport.execute("update es_message  set unread = '1' where msg_id = ?", msg_id);
	}

}

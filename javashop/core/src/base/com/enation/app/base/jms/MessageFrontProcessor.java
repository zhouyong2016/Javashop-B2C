package com.enation.app.base.jms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.IJmsProcessor;

/**
 * 会员站内消息获取
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午1:39:46
 */
@Service
public class MessageFrontProcessor   implements IJmsProcessor{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.jms.IJmsProcessor#process(java.lang.Object)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
		Map<String,Object> infoMap = (Map<String, Object>) data;
		String memberIds = "";
		Integer sendType = (Integer) infoMap.get("sendType");//发送类型    0  全站   1   部分
		List<String> memberIdsRes;
		if(sendType != null && sendType.equals(0)){
			String sql = " select member_id from es_member ";
		    memberIdsRes = daoSupport.queryForList(sql, new RowMapper() {
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					return rs.getString("member_id");
				}
			});
		}else{
			memberIds = (String) infoMap.get("memberIds");
			String[] memberIdsArray = memberIds.split(",");
			memberIdsRes = Arrays.asList(memberIdsArray);
		}
		if(memberIdsRes.size() > 0){
			String msgContent = (String) infoMap.get("msgContent");
			String msgTitle = (String) infoMap.get("msgTitle");
			Long sendTime = System.currentTimeMillis()/1000;
			Integer adminUserId = (Integer) infoMap.get("adminUserId");
			String adminName = (String) infoMap.get("adminUserName");
			for(String id : memberIdsRes){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("adminuser_id", adminUserId);
				map.put("is_delete", 1);
				map.put("is_read", 0);
				map.put("member_id", Integer.valueOf(id));
				map.put("msg_content", msgContent);
				map.put("send_time", sendTime);
				map.put("msg_title", msgTitle);
				map.put("adminuser_name", adminName);
				daoSupport.insert("es_message_front", map);
			}
		}
	}

}

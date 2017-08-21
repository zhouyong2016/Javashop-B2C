package com.enation.app.shop.core.other.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.other.service.ILogsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 日志
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月9日 下午3:15:31
 */
@Service
public class LogsManager  implements ILogsManager{

	@Autowired
	private IDaoSupport daoSupport;


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.ILogManager#getAllLogs(java.util.Map, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page getAllLogs(Map logsMap, int page, int pageSize, String sort, String order) {
		String sql = creatTempSql(logsMap);
		if(!StringUtil.isEmpty(sort)){
			sql += " order by "+sort+" ";
		}else{
			sql += " order by log_id ";
		}
		
		if(!StringUtil.isEmpty(order)){
			sql += order;
		}else{
			sql += "desc";
		}
		
		return this.daoSupport.queryForPage(sql, page, pageSize, null);
	}

	/**
	 * 拼装查询sql
	 * @param logsMap
	 * @return
	 */
	private String creatTempSql(Map logsMap) {
		
		String sql = "select log_type type,operator_name name,log_detail detail,log_time time from es_admin_logs where 1=1 ";
			
		String name = (String) logsMap.get("name");
		Integer userid = (Integer) logsMap.get("userid");
		String type = (String) logsMap.get("type");
		String start_time = (String) logsMap.get("start_time");
		String end_time = (String) logsMap.get("end_time");
		
		if(!StringUtil.isEmpty(name)){
			sql+= " and operator_name like '%"+ name +"%' ";
		}
		
		if(userid!=null && userid!=0){
			sql+= " and operator_id = "+userid;
		}
		
		if(!StringUtil.isEmpty(type) && !type.equals("0")){
			sql+= " and log_type = '"+ type +"'";
		}
		
		if(!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			sql+=" and log_time>"+stime;
		}
		
		if(!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and log_time<"+etime;
		}
			
			
		return sql;
	}

	
	
}

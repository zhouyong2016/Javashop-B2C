package com.enation.app.base.core.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.enation.app.base.core.service.ISqlServerManager;
import com.enation.framework.database.DBRuntimeException;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

/**
 * sql server 安装默认数据
 * @author xulipeng
 *
 */
@Service("sqlServerManager")
public class SqlServerManager implements ISqlServerManager {

 
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void installData(String table,Map fields) {

		if(!table.equals("es_type_brand") && !table.equals("es_tag_rel") && !table.equals("es_tag_relb")  && !table.equals("es_depot_user")  ){	//判断某些表没有主键
			if("es_auth_action".equals(table)){
				System.out.println(table);
			}
			if(table.equals("es_settings")){
				//this.daoSupport.execute("set identity_insert es_settings off");
			}else{
				this.daoSupport.execute("SET IDENTITY_INSERT " + table + " ON");
			}
		}
		
		
		table = StringUtil.addPrefix(table, "es_");
		String sql = "";

		try {
		 
			Assert.hasText(table, "表名不能为空");
			Assert.notEmpty(fields, "字段不能为空");
			table = quoteCol(table);

			Object[] cols = fields.keySet().toArray();
			Object[] values = new Object[cols.length];
			
			for (int i = 0; i < cols.length; i++) {
				if (fields.get(cols[i]) == null) {
					values[i] = null;
				} else {
					values[i] = fields.get(cols[i]).toString();
				}
				cols[i] = quoteCol(cols[i].toString());
			}

			sql = "INSERT INTO " + table + " (" + StringUtil.implode(", ", cols);
			sql = sql + ") VALUES (" + StringUtil.implodeValue(", ", values);
			sql = sql + ")";
			
			
			//jdbcTemplate.update(sql, values);
			this.daoSupport.execute(sql, values);
			
		} catch (Exception e) {
			// e.printStackTrace();
			throw new DBRuntimeException(e, sql);
		}
		
		if(!table.equals("es_type_brand") && !table.equals("es_tag_rel") && !table.equals("es_tag_relb")  && !table.equals("es_depot_user") ){	//判断某些表没有主键
			
			if(table.equals("es_settings")){
				
			}else{
				//this.daoSupport.execute("SET IDENTITY_INSERT " + table + " OFF");
			}
		}
		
	}
	

	/**
	 * 格式化列名 只适用于Mysql
	 * 
	 * @param col
	 * @return
	 */
	private String quoteCol(String col) {
		if (col == null || col.equals("")) {
			return "";
		} else {
//			if("2".equals(EopSetting.DBTYPE))//Oracle
//				return "\"" + col + "\"";
//			else if("1".equals(EopSetting.DBTYPE))//mysql
//				return "`" + col + "`";
//			else								//mssql
//				return "[" + col + "]";
			return col;
		}
	}

}

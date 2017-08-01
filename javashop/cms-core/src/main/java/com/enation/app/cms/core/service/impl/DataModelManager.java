package com.enation.app.cms.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.data.IDataOperation;
import com.enation.framework.log.LogType;

/**
 * 数据模型业务类
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service
public class DataModelManager implements IDataModelManager {
	@Autowired
	private IDataOperation dataOperation;
	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#add(com.enation.app.cms.core.model.DataModel)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.SETTING,detail="添加了一个${dataModel.china_name}数据模型")
	public void add(DataModel dataModel) {
		dataModel.setIf_audit(0);
		dataModel.setAdd_time(System.currentTimeMillis());
		this.daoSupport.insert("es_data_model", dataModel);
		
		StringBuffer createSQL = new StringBuffer();
		createSQL.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		createSQL.append("<dbsolution>\n");
		createSQL.append("<action>\n");
		createSQL.append("<command>create</command>\n");
		createSQL.append("<table>" + this.createTableName(dataModel.getEnglish_name()) + "</table>\n");
		createSQL.append("<field><name>id</name><type>int</type><size>8</size><option>11</option></field>\n");
		createSQL.append("<field><name>sort</name><type>int</type><size>8</size><option>00</option></field>\n");
		createSQL.append("<field><name>add_time</name><type>int</type><size>11</size><option>00</option></field>\n");
		createSQL.append("<field><name>lastmodified</name><type>int</type><size>11</size><option>00</option></field>\n");
		createSQL.append("<field><name>hit</name><type>long</type><size>20</size><option>00</option></field>\n");
		createSQL.append("<field><name>able_time</name><type>long</type><size>20</size><option>00</option></field>\n");
		createSQL.append("<field><name>state</name><type>int</type><size>8</size><option>00</option></field>\n");
		createSQL.append("<field><name>user_id</name><type>long</type><size>20</size><option>00</option></field>\n");
		createSQL.append("<field><name>cat_id</name><type>int</type><size>8</size><option>00</option></field>\n");
		createSQL.append("<field><name>is_commend</name><type>int</type><size>4</size><option>00</option></field>\n");
		createSQL.append("<field><name>sys_lock</name><type>int</type><size>4</size><option>00</option><default>0</default></field>\n");
		createSQL.append("<field><name>page_title</name><type>varchar</type><size>255</size><option>00</option></field>\n");
		createSQL.append("<field><name>page_keywords</name><type>varchar</type><size>255</size><option>00</option></field>\n");
		createSQL.append("<field><name>page_description</name><type>memo</type><size>21845</size><option>00</option></field>\n");
		createSQL.append("<field><name>site_code</name><type>int</type><size>11</size><option>00</option><default>100000</default></field>\n");
		createSQL.append("<field><name>siteidlist</name><type>varchar</type><size>255</size><option>00</option></field>\n");
		createSQL.append("</action>\n");
		createSQL.append("</dbsolution>");
		this.dataOperation.imported(createSQL.toString());
/*		StringBuffer createTbSql= new StringBuffer("create table ");
		createTbSql.append(this.createTableName(dataModel.getEnglish_name()));
		createTbSql.append("( id mediumint(8) not null");
		createTbSql.append(" auto_increment,sort smallint(1) default 0, add_time datetime, lastmodified datetime, hit int, able_time");
		createTbSql.append(" int, state mediumint(8) comment '1:未审核,2:已审核,3:被拒绝',");
		createTbSql.append(" user_id int, cat_id mediumint(8), is_commend mediumint(4)");
		createTbSql.append(" comment '0:普通,1:推荐', sys_lock mediumint(4) default 0 comment '0:正常,1:系统锁定',page_title varchar(255), page_keywords varchar(255), page_description text, site_code int default 100000, siteidlist varchar(255), primary key (id) )type = MYISAM;");
		this.daoSupport.execute(createTbSql.toString());*/
		
	}
	

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#delete(java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.SETTING,detail="删除一个${modelid}的数据模型")
	public void delete(Integer modelid) {
		DataModel dataModel  = this.get(modelid);
		
		//删除模型表
		this.daoSupport.execute("drop table "+this.createTableName(dataModel.getEnglish_name()));
		
		//删除模型字段数据记录
		this.daoSupport.execute("delete from es_data_field where model_id = ?",modelid);
		
		//删除模型记录
		this.daoSupport.execute("delete from es_data_model where model_id = ?", modelid);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#edit(com.enation.app.cms.core.model.DataModel)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.SETTING,detail="修改${dataModel.china_name}数据模型信息")
	public void edit(DataModel dataModel) {
		DataModel oldmodel = this.get(dataModel.getModel_id());
		this.daoSupport.update("es_data_model", dataModel, "model_id="+ dataModel.getModel_id());
		String tbname = this.createTableName(dataModel.getEnglish_name());
		if(!(tbname.equals("es_"+oldmodel.getEnglish_name()))){// 表名变了，更新表名
			//改表名三个数据库都不一样
			String db_type = EopSetting.DBTYPE;
			StringBuffer sql = new StringBuffer("");
			if (db_type.equals("1")) {//mysql
				sql.append("ALTER TABLE  es_" + oldmodel.getEnglish_name());
				sql.append(" RENAME TO ");
				sql.append(tbname);
			} else if (db_type.equals("2")) {//oracle
				sql.append("rename es_" + oldmodel.getEnglish_name() +" to "+tbname);
			} else if (db_type.equals("3")) {//sqlserver
				sql.append("EXEC sp_rename  es_" + oldmodel.getEnglish_name());
				sql.append(","+tbname);
			}
			this.daoSupport.execute(sql.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#get(java.lang.Integer)
	 */
	@Override
	public DataModel get(Integer modelid) {
		String sql ="select * from es_data_model where model_id = ?";
		return this.daoSupport.queryForObject(sql, DataModel.class, modelid);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#list()
	 */
	@Override
	public List<DataModel> list() {
		return this.daoSupport.queryForList("select * from es_data_model order by add_time asc", DataModel.class);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#checkIfModelInUse(java.lang.Integer)
	 */
	@Override
	public int checkIfModelInUse(Integer modelid) {
		DataModel dataModel  = this.get(modelid);
		return this.daoSupport.queryForInt("select count(0) from " +this.createTableName(dataModel.getEnglish_name()));
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataModelManager#checkIfModelInUse(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int checkIfModelInUse(String name, Integer modelid) {
		String sql = "select count(0) from es_data_model where english_name = ?";
		
		if (modelid != 0) {
			sql += " and model_id != "+modelid+"";
		}
		
		int count = this.daoSupport.queryForInt(sql, name);
		count = count > 0 ? 1 : 0;
		return count;
	}
	
	private String createTableName(String tbname){
		tbname = "es_" + tbname;
		return tbname;
	}

}

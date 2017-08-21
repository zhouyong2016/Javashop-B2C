package com.enation.app.base.core.service.dbsolution.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.jdbc.core.BatchUpdateUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;

import com.enation.app.base.core.service.ISqlServerManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据库导入类
 * 
 * @author liuzy
 * 
 */
public class DBImporter extends DBPorter {
	private Document xmlDoc;

	public DBImporter(DBSolution solution) {
		super(solution);
	}

	/**
	 * 加载xml文件
	 * 
	 * @param xmlFile xml
	 * @return
	 */
	private Document loadDocument(String xmlFile) throws DocumentException {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		File file = new File(xmlFile);
		if (file.exists())
			document = saxReader.read(new File(xmlFile));
		return document;
	}

	private Object parseValue(String value) {

		return solution.getFuncValue(solution.decodeValue(value.replaceAll("'", "")));
	}


	private boolean doInsert(Element action) {

		final String table = solution.getTableName(action.elementText("table"));
		String fields = action.elementText("fields");
		String values = action.elementText("values");


		final String[] field_ar= fields.split(",");
		final String[] value_ar= values.split(",");
		Map data = new HashMap();

		IDaoSupport daoSupport =  SpringContextHolder.getBean("daoSupport");

		try{
			for (int i = 0; i < field_ar.length; i++){
				data.put(field_ar[i], this.parseValue( value_ar[i]));

			}
			
 
			if(EopSetting.DBTYPE.equals("3")){	//如果使用sqlserver 数据库

				ISqlServerManager sqlServerManager = SpringContextHolder.getBean("sqlServerManager");
				sqlServerManager.installData(table, data);

			}else{
				if (solution.beforeInsert(table, fields, values)) {
					String sql =solution.getSqlExchange();
					if(!StringUtil.isEmpty(sql)){
						try {
							daoSupport.execute(sql);
						} catch (Exception e) {
							System.out.println("设置on失败");
						}
					}

					daoSupport.insert(table, data);
					solution.afterInsert(table, fields, values);

					sql =solution.getSqlExchange();
					if(!StringUtil.isEmpty(sql)){
						try {
							daoSupport.execute(sql);
						} catch (Exception e) {
							System.out.println("设置off失败");
						}
					}

				} else {
					return false;
				}
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private boolean doTruncate(Element action) {
		String table = solution.getTableName(action.elementText("table"));
		String sql="" ;
		sql+="truncate table "+table;
		return solution.executeSqls(sql);
	}
	private boolean doDrop(Element action) {
		String table = solution.getTableName(action.elementText("table"));
		String sql = solution.getDropSQL(table);
		return solution.executeSqls(sql);
	}

	private boolean doCreate(Element action) {
		String sql = solution.getCreateSQL(action);

		return solution.executeSqls(sql);
	}

	private boolean doIndex(Element action) {
		//FIXME 判断数据库类型,目前适配mysql和oracle
		if(EopSetting.DBTYPE.equals("2")){
			return doIndexOracle(action,0,0);
		}
		return doIndex(action,0,0);
	}
	/**
	 * 2016-4-27 新增方法 在创建oracle数据库时删除oracle自动生成的索引
	 * @param action
	 * @param i
	 * @param j
	 */
	private boolean doIndexOracle(Element action,  int userid, int siteid) {
		boolean result =false;

		/**
		 * 获取表名
		 */
		String table = solution.getTableName(action.elementText("table"));


		/**
		 * 读取索引节点表列
		 */
		List<Element> indexElList = action.elements("index");



		/**
		 * 生成每个索引的语句
		 */
		for (Element indexEl : indexElList) {

			
				String indexName =indexEl.attributeValue("name");
				String sql=" create index "+indexName+" on "+table;

				sql+="(";

				/**
				 * 索引的字段，可能有多个
				 */
				List<Element> fields = indexEl.elements("field");
				int i=0;
				for (Element fieldEl : fields) {
					String fieldName = fieldEl.elementText("name");
					if(i!=0){
						sql+=",";
					}
					sql+=fieldName;
					i++;
				}

				sql+=")";

				if(checkIndex(table,sql)){
					result = solution.executeSqls(sql);
				}
		}

		return result;
	}

	private boolean checkIndex(String table, String columns) {
//		List<Element> fields = indexEl.elements("field");
//		int i=0;
//		for (Element fieldEl : fields) {
//			String fieldName = fieldEl.elementText("name");
//			if(i!=0){
//				sql+=",";
//			}
//			sql+=fieldName;
//			i++;
//		}
		String sql="select * from user_ind_columns where table_name = '"+table.toUpperCase()+"' and index_name = 'PK_"+table.toUpperCase()+"'";
		JdbcTemplate jdbcTemplate =  SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String,Object>> list=jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			String column_name=map.get("COLUMN_NAME").toString();
			if(columns.contains(column_name.toLowerCase())){
				return false;
			}
		}
		return true;
	}

	private boolean doUnindex(Element action) {
		return doUnindex(action,0,0);
	}



	private boolean doTruncate(Element action, int userid, int siteid) {
		String table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		String sql="" ;
		sql+="truncate table "+table;
		return solution.executeSqls(sql);
	}

	private boolean doDrop(Element action, int userid, int siteid) {
		String table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		String sql = solution.getDropSQL(table);
		return solution.executeSqls(sql);
	}

	private boolean doCreate(Element action, int userid, int siteid) {
		String sql = solution.getSaasCreateSQL(action, userid, siteid);
		return solution.executeSqls(sql);
	}




	@SuppressWarnings("unchecked")
	private boolean doIndex(Element action, int userid, int siteid) {

		boolean result =false;

		/**
		 * 获取表名
		 */
		String table = solution.getTableName(action.elementText("table"));


		/**
		 * 读取索引节点表列
		 */
		List<Element> indexElList = action.elements("index");



		/**
		 * 生成每个索引的语句
		 */
		for (Element indexEl : indexElList) {

			String indexName =indexEl.attributeValue("name");
			String sql=" create index "+indexName+" on "+table;

			sql+="(";

			/**
			 * 索引的字段，可能有多个
			 */
			List<Element> fields = indexEl.elements("field");
			int i=0;
			for (Element fieldEl : fields) {
				String fieldName = fieldEl.elementText("name");
				if(i!=0){
					sql+=",";
				}
				sql+=fieldName;
				i++;
			}

			sql+=")";

			result = solution.executeSqls(sql);
		}

		return result;
	}


	private boolean doAlter(Element action,int userid,int siteid){

		try{
			String table;
			String sql="" ;
			if(userid==0 && siteid==0)
				table = solution.getTableName(action.elementText("table"));
			else
				table = solution.getSaasTableName(action.elementText("table"), userid, siteid);

			List<Element> fields = action.elements("field");
			for(int i=0,len=fields.size();i<len;i++) {
				Element element = fields.get(i);
				String type = element.attributeValue("type");
				String name = element.elementText("name") ;
				String size = element.elementText("size") ;

				if(i!=0){
					sql+=",";
				}
				if("add".equals(type)){
					String datatype = element.elementText("type") ;

					//oracle和sqlserver的都不写column关键字
					//区分oracle 和 sqlserver 和 mysql， alter语句的不同

					if( EopSetting.DBTYPE.equals("2") || EopSetting.DBTYPE.equals("3")){
						sql+=" "+ name +" " ;	
					}else{
						sql+=" add column "+ name +" " ;
					}

					sql+=solution.toLocalType(datatype, size);

					String def = element.elementText("default") ;
					if(!StringUtil.isEmpty(def)){
						sql+=" default "+def;
					}
				}
				if("drop".equals(type)){
					sql+=" drop column "+ name ;
				} 

			}

			if( EopSetting.DBTYPE.equals("2")){
				sql=" add ("+sql+")";
			}else if(EopSetting.DBTYPE.equals("3")){
				sql=" add "+sql+"";
			}

			sql ="alter table "+table +" "+sql;
			solution.executeSqls(sql);
			return true;
		}catch(RuntimeException e){
			e.printStackTrace();
			return false;
		}
	}





	private boolean doUnindex(Element action, int userid, int siteid) {
		/*	由于Oracle的drop index 不使用on table，存在差异，加上使用几率低，暂时放弃
		String sql = "drop index ";
		String table;
		if(userid==0 && siteid==0)
			table = solution.getTableName(action.elementText("table"));
		else
			table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		List<Element> fields = action.elements("field");
		String name = "_";
		for(int i=0;i<fields.size();i++) {
			Element element = fields.get(i);
			name = name + element.elementText("name") + "_";
		}
		name = name.substring(0,name.length()-1);
		sql = sql + "idx" + name + " on " + table;
		return solution.executeSqls(sql);
		 */
		return true;
	}
	/**
	 * 执行action内容
	 * 
	 * @param action
	 * @return
	 */
	private boolean doAction(Element action) {
		String command = action.elementText("command").toLowerCase();
		if ("create".equals(command)) {
			return doCreate(action);
		} else if ("insert".equals(command)) {
			return doInsert(action);
		} else if ("drop".equals(command)) {
			return doDrop(action);
		} else if ("index".equals(command)) {
			return doIndex(action);
		} else if ("unindex".equals(command)) {
			return doUnindex(action);
		} else if("alter".equals(command)){
			return doAlter(action, 0, 0);			
		} else if ("truncate".equals(command)) {
			return doTruncate(action);
		} 
		return true;
	}


	/**
	 * 导入一个xml文件到数据库中
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean doImport(String xml) {
		solution.beforeImport();
		try {
			if (xml.startsWith("file:")) {
				xml = FileUtil.readFile(xml.replaceAll("file:", ""));
				xmlDoc = DocumentHelper.parseText(xml);
			} else if (xml.startsWith("<?xml version")) {
				xmlDoc = DocumentHelper.parseText(xml);
			}
			else {
				xmlDoc = loadDocument(xml);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		List<Element> actions = xmlDoc.getRootElement().elements("action");
		for (Element action : actions) {
			if (!doAction(action))
				return false;
		}
		solution.afterImport();
		return true;
	}

 }
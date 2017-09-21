package com.enation.app.cms.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.plugin.ArticlePluginBundle;
import com.enation.app.cms.core.plugin.IDataSaveEvent;
import com.enation.app.cms.core.plugin.IFieldValueShowEvent;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service
public class DataManager implements IDataManager {

	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IDataModelManager dataModelManager;
	
	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@Autowired
	private ArticlePluginBundle articlePluginBundle;
	
	@Autowired
	private IDataCatManager dataCatManager;

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#add(java.lang.Integer, java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
//	@Log(type=LogType.SETTING,detail="添加了一个ID为${catid}所属数据类别，对应模型，模型ID为${modelid}")
	@Log(type=LogType.SETTING,detail="添加一个商城公告模型，模型ID为${modelid}")
	public void add(Integer modelid, Integer catid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		DataModel dataModel = this.dataModelManager.get(modelid);
		List<DataField> fieldList = dataFieldManager.list(modelid);
		Map<String, Object> article = new HashMap<String, Object>();

		// 激发字段保存事件
		for (DataField field : fieldList) {
			articlePluginBundle.onSave(article, field);
		}

		String sort = request.getParameter("sort");
		String page_title = request.getParameter("page_title");
		String page_keywords = request.getParameter("page_keywords");
		String page_description = request.getParameter("page_description");
		sort = StringUtil.isEmpty(sort) ? "0" : sort;
		// Date now = new Date()
		// 上面的写法有时与服务器的日期时间格式设置不匹配，会造成数据无法存入数据库
		// 改为如下写法
		// By lzf 2012-02-13
		Long now = DateUtil.getDateline();
		article.put("cat_id", String.valueOf(catid.intValue()));
		article.put("sort", sort);
		article.put("hit", 0);
		article.put("page_keywords", page_keywords);
		article.put("page_title", page_title);
		article.put("page_description", page_description);

		// if("2".equals(EopSetting.DBTYPE)) {
		article.put("add_time", now);
		article.put("lastmodified", now);
		// } else {
		// article.put("add_time",
		// DateUtil.toString(now,"yyyy-MM-dd HH:mm:ss"));
		// article.put("lastmodified",
		// DateUtil.toString(now,"yyyy-MM-dd HH:mm:ss"));
		// }

 
		this.daoSupport.insert("es_" + dataModel.getEnglish_name(), article);

		// 激发数据保存事件
		
		
	/** 由于商城中没有eop_data_log表，导致保存数据日志的时候，eclipse报错，暂时取消保存日志。  2015-05-25   whj
	 * 	String article_id = String.valueOf(this.baseDaoSupport.getLastId(dataModel.getEnglish_name()));
		article.put("id", article_id);
		this.articlePluginBundle.onSave(article, dataModel, IDataSaveEvent.DATASAVE_ADD);
	*/	
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#delete(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除一个商城公告模型")
	public void delete(Integer catid, Integer articleid) {
		DataModel dataModel = this.getModelByCatid(catid);
		// 激发数据删除事件 lzf add 2010-12-01
		this.articlePluginBundle.onDelete(catid, articleid);
		String sql = "delete from es_" + dataModel.getEnglish_name() + " where id=?";
		this.daoSupport.execute(sql, articleid);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#edit(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改一个商城公告模型，模型ID为${modelid}")
	public void edit(Integer modelid, Integer catid, Integer articleid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		DataModel dataModel = this.dataModelManager.get(modelid);
		 
		List<DataField> fieldList = dataFieldManager.list(modelid);
		Map article = new HashMap();
		for (DataField field : fieldList) {
			articlePluginBundle.onSave(article, field);
		}

		String page_title = request.getParameter("page_title");
		String page_keywords = request.getParameter("page_keywords");
		String page_description = request.getParameter("page_description");
		article.put("page_keywords", page_keywords);
		article.put("page_title", page_title);
		article.put("page_description", page_description);

		String sort = request.getParameter("sort");
		sort = StringUtil.isEmpty(sort) ? "0" : sort;
		article.put("cat_id", catid);
		article.put("sort", sort);
		article.put("lastmodified", DateUtil.getDateline());
		
		this.daoSupport.update("es_" + dataModel.getEnglish_name(), article, "id=" + articleid);
		this.articlePluginBundle.onSave(article, dataModel, IDataSaveEvent.DATASAVE_EDIT);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#list(java.lang.Integer, int, int)
	 */
	@Override
	public Page list(Integer catid, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select " + buildFieldStr(model.getModel_id())
				+ ",sort from es_" + model.getEnglish_name()
				+ " where cat_id = ? order by sort desc, add_time desc";
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize, catid);
		return webpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#list(java.lang.Integer, int, int, java.lang.Integer)
	 */
	@Override
	public Page list(Integer catid, int page, int pageSize, Integer site_code) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select "
				+ buildFieldStr(model.getModel_id())
				+ ",sort from es_"
				+ model.getEnglish_name()
				+ " where cat_id = ? and site_code between "
				+ site_code
				+ " and "
				+ StringUtil.getMaxLevelCode(site_code)
				+ " and (not siteidlist like '%,"
				+ EopContext.getContext().getCurrentChildSite().getSiteid()
				+ ",%' or siteidlist is null) order by sort desc, add_time desc";

		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize, catid);
		return webpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#importdata(java.lang.Integer, java.lang.Integer[])
	 */
	@Override
	public void importdata(Integer catid, Integer[] ids) {
		DataModel model = this.getModelByCatid(catid);
		String ids_str = StringUtil.arrayToString(ids, ",");
		int site_id = EopContext.getContext().getCurrentChildSite().getSiteid();
		String sql = "update es_" + model.getEnglish_name()
				+ " set siteidlist = CASE WHEN siteidlist is null THEN ',"
				+ site_id + ",' ELSE CONCAT(siteidlist,'" + site_id
				+ ",') END where id in (" + ids_str + ")";
		this.daoSupport.execute(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#list(java.lang.Integer)
	 */
	@Override
	public List list(Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select " + buildFieldStr(model.getModel_id())
				+ ",sort from es_" + model.getEnglish_name()
				+ " where cat_id = ? order by sort desc, add_time desc";
		List webpage = this.daoSupport.queryForList(sql, catid);
		return webpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#updateSort(java.lang.Integer[], java.lang.Integer[], java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.SETTING,detail="修改对应模型文章排序")
	public void updateSort(Integer[] ids, Integer[] sorts, Integer catid) {
		if (ids!= null && sorts != null && sorts.length == ids.length){
			DataModel model = this.getModelByCatid(catid);
		String sql = "update es_" + model.getEnglish_name() + " set sort = ? where id = ?";

		for (int i = 0; i < ids.length; i++) {
			this.daoSupport.execute(sql, sorts[i], ids[i]);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#listAll(java.lang.Integer, java.lang.String, int, int)
	 */
	@Override
	public Page listAll(Integer catid, String term, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		DataCat cat = this.dataCatManager.get(catid);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append("es_" + model.getEnglish_name());
		sql.append(" where cat_id in (select cat_id from ");
		sql.append("es_data_cat");
		sql.append(" where cat_path like '");
		sql.append(cat.getCat_path());
		sql.append("%'");
		sql.append(") ");

		if (!StringUtil.isEmpty(term)) {
			sql.append(term);
		}

		sql.append(" order by sort desc, add_time desc");
		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		return this.daoSupport.queryForPage(sql.toString(), page, pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map<String, Object> data = new HashMap<String, Object>();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());

					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));

				// 王峰注释下面语句，此种写法性能过于低下
				DataCat cat = dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", cat.getName());
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#listRelated(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	public List listRelated(Integer catid, Integer relcatid, Integer id, String fieldname) {

		Map article = this.get(id, catid, false);
		String ids = (String) article.get(fieldname);
		if (StringUtil.isEmpty(ids)) {
			return new ArrayList();
		}

		DataModel model = this.getModelByCatid(relcatid);

		StringBuffer sql = new StringBuffer("select * from ");
		sql.append("es_" + model.getEnglish_name());
		sql.append(" where id in (" + ids + ")");
		sql.append(" order by sort desc, add_time desc");

		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		return this.daoSupport.queryForList(sql.toString(), new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map<String, Object> data = new HashMap<String, Object>();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));

				// 王峰注释下面语句，此种写法性能过于低下
				// DataCat cat = dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", "");
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#get(java.lang.Integer, java.lang.Integer, boolean)
	 */
	@Override
	public Map get(Integer articleid, Integer catid, boolean filter) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select * from es_" + model.getEnglish_name() + " where id = ?";
		Map data = this.daoSupport.queryForMap(sql, articleid);

		if (filter) {
			List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());

			for (DataField field : fieldList) {
				String name = field.getEnglish_name();
				Object value = data.get(name);
				IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
				if (plugin != null) {
					if (plugin instanceof IFieldValueShowEvent) {
						value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						data.put(name, value);
					}
				}
			}
		}
		return data;
	}

	/**
	 * 更新浏览量
	 */
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#updateHit(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateHit(Integer id, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		this.daoSupport.execute("update es_" + model.getEnglish_name()	+ " set hit = hit + 1 where id = ?", id);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#search(int, java.lang.String)
	 */
	@Override
	public List search(int modelid, String connector) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append("es_" + model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {

			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!"utf-8".toLowerCase().equals(request.getCharacterEncoding())) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}

			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {

				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.daoSupport.queryForList(sql.toString(),	new RowMapper() {
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#search(int, java.lang.String, boolean)
	 */
	@Override
	public List search(int modelid, String connector, boolean showchild) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append("es_" + model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {

			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!"utf-8".toLowerCase().equals(request.getCharacterEncoding())) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}

			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}

			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

	 
		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.daoSupport.queryForList(sql.toString(),	new RowMapper() {
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();

					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#search(int, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page search(int pageNo, int pageSize, int modelid, String connector,String catid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append("es_" + model.getEnglish_name());

		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {
			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!"utf-8".toLowerCase().equals(request.getCharacterEncoding())) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}
			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (term.length() > 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					if (term.length() > 0)
						term.append(connector);

					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}
					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (term.length() > 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
		}

		/*
		 * Liuzy add 增加分类查询
		 */
		
		if (catid != null) {
			if (catid.startsWith("lt"))
				catid = catid.replaceAll("lt", "<");
			else if (catid.startsWith("gt"))
				catid = catid.replaceAll("gt", ">");
			else
				catid = "=" + catid;
			if (term.length() > 0)
				term.append(connector);
			term.append(" cat_id" + catid);
		}

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}
		sql.append(" order by sort desc,add_time desc");
		return this.daoSupport.queryForPage(sql.toString(), pageNo,	pageSize, new RowMapper() {

			@SuppressWarnings("unchecked")
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#search(int, int, int, java.lang.String, boolean)
	 */
	@Override
	public Page search(int pageNo, int pageSize, int modelid, String connector, boolean showchild) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append("es_" + model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {
			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (value != null)
				value = StringUtil.toUTF8(value);

			String name = field.getEnglish_name();

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					sql.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					sql.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

		 

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.daoSupport.queryForPage(sql.toString(), pageNo,	pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();

					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#census()
	 */
	@Override
	public Map census() {
		List<DataModel> modelList = this.dataModelManager.list();
		String sql;
		int count = 0;

		// 读取模型列表，并且读取模型相应的信息数，累加之后为当前站点的信息数量和。
		for (DataModel model : modelList) {
			String tbname = model.getEnglish_name();
			sql = "select count(0)  from es_" + tbname;
			count += this.daoSupport.queryForInt(sql);

		}
		// 读取栏目数
		sql = "select count(0) from es_data_cat";
		int catcount = this.daoSupport.queryForInt(sql);

		// 读取留言数
		sql = "select count(0)  from es_guestbook g where parentid = 0"; // and g.id not in(select parentid from
											// "+ this.getTableName("guestbook")+"
											// )";
		int msgcount = this.daoSupport.queryForInt(sql);

		Map<String, Integer> map = new HashMap<String, Integer>(3);
		map.put("count", count);
		map.put("catcount", catcount);
		map.put("msgcount", msgcount);
		return map;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#listAll(java.lang.Integer, java.lang.String, java.lang.String, boolean, int, int)
	 */
	@Override
	public Page listAll(Integer catid, String term, String orders, boolean showchild, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		DataCat cat = this.dataCatManager.get(catid);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append("es_" + model.getEnglish_name());
		sql.append(" where cat_id in (select cat_id from ");
		sql.append("es_data_cat");
		sql.append(" where cat_path like '");
		sql.append(cat.getCat_path());
		sql.append("%'");
		sql.append(") ");

		if (!StringUtil.isEmpty(term)) {
			sql.append(term);
		}
		//这个非空验证后改过
		if (!StringUtil.isEmpty(orders)&&orders!=null) {
			sql.append(" order by " + orders);
		} else {
			sql.append(" order by sort desc, add_time desc");
		}
		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		Page datalist = this.daoSupport.queryForPage(sql.toString(), page, pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));
				data.put("site_code", rs.getInt("site_code"));
				data.put("page_title", rs.getString("PAGE_TITLE"));
				//data.put("title", rs.getString("title"));

				// 经测试，有严重性能问题，暂时注释
				// DataCat cat =
				// dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", "");
				return data;
			}
		});
		this.logger.debug("查询sql[" + sql + "]完成");
		return datalist;
	}

	/**
	 * 获取某篇文章的当前类别下下一个文章id，如果是最后一篇则返回0
	 * 
	 * @param currentId
	 * @return
	 */
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#getNextId(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public int getNextId(Integer currentId, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select min(id) from es_" + model.getEnglish_name() + " where cat_id = ? and id > ?";
		List<Integer> list = this.daoSupport.queryForList(sql, new IntegerMapper(), catid, currentId);
		if (list == null || list.isEmpty())
			return 0;
		return list.get(0);
	}

	/**
	 * 获取某篇文章的当前类别下的上一篇文章id,如果是第一篇则为0
	 * 
	 * @param currentId
	 * @param catid
	 * @return
	 */
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#getPrevId(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public int getPrevId(Integer currentId, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select max(id) from es_" + model.getEnglish_name() + " where cat_id = ? and id < ?";
		List<Integer> list = this.daoSupport.queryForList(sql, new IntegerMapper(), catid, currentId);
		if (list == null || list.isEmpty())
			return 0;
		return list.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataManager#list(int, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page list(int pageNo, int pageSize, int modelid, String connector,String catid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append("es_" + model.getEnglish_name());

		for (DataField field : fieldList) {
			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!request.getCharacterEncoding().toLowerCase().equals("utf-8")) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}
			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);
		}

		sql.append(" order by sort desc,add_time desc");
		return this.daoSupport.queryForPage(sql.toString(), pageNo,	pageSize, new RowMapper() {

			@SuppressWarnings("unchecked")
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}
	
	
	private DataModel getModelByCatid(Integer catid) {
		String sql = "select dm.* from es_data_model dm , es_data_cat c where dm.model_id = c.model_id and c.cat_id = ?";
		List modelList = this.daoSupport.queryForList(sql, DataModel.class,	catid);
		if (modelList == null || modelList.isEmpty()) {
			throw new RuntimeException("此类别[" + catid + "]不存在模型");
		}
		DataModel model = (DataModel) modelList.get(0);

		return model;
	}

	private String buildFieldStr(Integer modelid) {
		StringBuffer sql = new StringBuffer("id");
		List<DataField> fieldList = this.dataFieldManager.listIsShow(modelid);
		for (DataField field : fieldList) {
			if (field.getIs_show() == 1) {
				sql.append(",");
				sql.append(field.getEnglish_name());
			}
		}
		return sql.toString();
	}

}

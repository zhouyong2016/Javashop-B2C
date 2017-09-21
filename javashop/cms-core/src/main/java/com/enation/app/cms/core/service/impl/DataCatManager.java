package com.enation.app.cms.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.service.ArticleCatRuntimeException;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;

/**
 * 文章类别管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service("dataCatDbManager")
public class DataCatManager implements IDataCatManager {

	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IDataModelManager dataMdelManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#add(com.enation.app.cms.core.model.DataCat)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.MEMBER,detail="文章类型中添加一个${cat.name}的文章分类")
	public void add(DataCat cat) {
//		判断文章分类的父类id不能同自己的id一样
        if(cat.getParent_id() == null)
        	cat.setParent_id(0);
        else{
        	if(cat.getCat_id() != null && cat.getParent_id().intValue() == cat.getCat_id().intValue() )
        		throw new ArticleCatRuntimeException(2);//文章分类的父类id不能同自己的id一样
        }
//      判断文章分类不能同名
        if(cat.getName() != null){
        	String sql = "select count(0) from es_data_cat where name = '"+ cat.getName()+"' and parent_id="+cat.getParent_id();
		    int count = this.daoSupport.queryForInt(sql);	
		    if(count > 0)
		    	throw new ArticleCatRuntimeException(1);//文章分类不能同名
        }
		this.daoSupport.insert("es_data_cat", cat);
		int cat_id = this.daoSupport.getLastId("es_data_cat");

		String sql = "";

		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
			sql = "select * from es_data_cat where cat_id=?";
			DataCat parent = (DataCat) this.daoSupport.queryForObject(sql, DataCat.class, cat.getParent_id());
			if(parent != null){
				cat.setCat_path(parent.getCat_path() + cat_id+"|");
			}
		} else {
			cat.setCat_path(cat.getParent_id()+"|" + cat_id+"|");
		}
		//更新类别path和url
		sql = "update es_data_cat set cat_path='" + cat.getCat_path()
			 +"',url='newslist-"+cat_id+"-1.html' where cat_id=" + cat_id;
		this.daoSupport.execute(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#delete(java.lang.Integer)
	 */
	@Override
	public int delete(Integer catid) {
		// 获取某类别下的子类别数
		String sql = "select count(0) from es_data_cat where parent_id = ?";
		int count = this.daoSupport.queryForInt(sql, catid);
		if (count > 0) {
			return 1;
		}
		sql = "delete from es_data_cat where cat_id = " + catid;
		this.daoSupport.execute(sql);
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#edit(com.enation.app.cms.core.model.DataCat)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.MEMBER,detail="修改${cat.name}的文章分类信息")
	public void edit(DataCat cat) {
		// 判断文章分类的父类id不能同自己的id一样
		if (cat.getParent_id() == null)
			cat.setParent_id(0);
		else {
			if (cat.getCat_id() != null	&& cat.getParent_id().intValue() == cat.getCat_id().intValue())
				throw new ArticleCatRuntimeException(2);// 文章分类的父类id不能同自己的id一样
		}
		//	      判断文章分类不能同名
		if(cat.getName() != null){
        	String sql = "select count(0) from es_data_cat where cat_id != "+cat.getCat_id()+" and name = '"+ cat.getName()+"' and parent_id = "+cat.getParent_id();
		    int count = this.daoSupport.queryForInt(sql);	
		    if(count > 0)
		    	throw new ArticleCatRuntimeException(1);//文章分类不能同名
        }

		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
			String sql = "select * from es_data_cat where cat_id = ?";
			DataCat parent = (DataCat) this.daoSupport.queryForObject(sql,
					DataCat.class, cat.getParent_id());
			if(parent != null){
			   cat.setCat_path(parent.getCat_path() + cat.getCat_id()+"|");
			}
		} else {
			cat.setCat_path(cat.getParent_id() +"|"+ cat.getCat_id()+"|");
		}

		HashMap map = new HashMap();
		map.put("name", cat.getName());
		map.put("parent_id", cat.getParent_id());
		map.put("cat_order", cat.getCat_order());
		map.put("cat_path", cat.getCat_path());
		map.put("url", cat.getUrl());
		map.put("model_id", cat.getModel_id());
		map.put("tositemap", cat.getTositemap());
		map.put("detail_url", cat.getDetail_url());
		map.put("descript", cat.getDescript());
		this.daoSupport.update("es_data_cat", map, "cat_id=" + cat.getCat_id());
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#saveSort(int[], int[])
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.MEMBER,detail="更新文章类别排序")
	public void saveSort(int[] cat_ids, int[] cat_sorts) {
		String sql = "";
		if (cat_ids != null && cat_sorts != null && cat_ids.length == cat_sorts.length) {
			for (int i = 0; i < cat_ids.length; i++) {
				if(cat_ids[i]==cat_ids[0]&&i!=0){
					break;
				}
				sql = "update es_data_cat set cat_order=" + cat_sorts[i]
						+ " where cat_id=" + cat_ids[i];
				this.daoSupport.execute(sql);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#get(java.lang.Integer)
	 */
	@Override
	public DataCat get(Integer catid) {
		return (DataCat) this.daoSupport.queryForObject(
				"select * from es_data_cat where cat_id=?",
				DataCat.class, catid);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#listLevelChildren(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<DataCat> listLevelChildren(Integer catid, Integer level) {
		DataCat dataCat = get(catid);
		String[] path = dataCat.getCat_path().split("\\|");
		try {
			int parentid = Integer.parseInt(path[level]);
			return listAllChildren(parentid);
		} catch(Exception e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#listAllChildren(java.lang.Integer)
	 */
	@Override
	public List<DataCat> listAllChildren(Integer parentid) {
		if(this.logger.isDebugEnabled()){
			this.logger.debug("查找"+parentid+"的子 ");
		}
		//根据parent_id读取分类数据
		String sql = "select cat_id,name,parent_id,if_audit,url,cat_order from es_data_cat where parent_id=?  order by parent_id,cat_order" ;
		List<DataCat> allCatList = this.daoSupport.queryForList(sql,DataCat.class,parentid);
		
		//读取所有的数据（除上面已读取的分类外）
		String total = "select parent_id from es_data_cat where parent_id!=?";
		List totalList = this.daoSupport.queryForList(total,parentid);
		
		List<DataCat> topCatList  = new ArrayList<DataCat>();
		for(DataCat cat :allCatList){
			Map map = new HashMap();
			map.put("parent_id", cat.getCat_id());
			//如果所有数据中的 cat_id 包含真实返回的分类数据，说明此分类有子。
			if(totalList.contains(map)){
				if(this.logger.isDebugEnabled()){
					this.logger.debug("发现子["+cat.getName()+"-"+cat.getCat_id() +"]");
				}
				cat.setHasChildren(true);
			}
			List<DataCat> children = this.getChildren(allCatList, cat.getCat_id());
			cat.setChildren(children);
			topCatList.add(cat);
		}
		return topCatList;			 
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#getParents(java.lang.Integer)
	 */
	@Override
	public List<DataCat> getParents(Integer catid) {
		DataCat cat  = this.get(catid);
		String path = cat.getCat_path();
		path  = path.replaceAll("\\|", ",")+"-1";
		return this.daoSupport.queryForList("select * from es_data_cat where cat_id in(" + path + ")", DataCat.class);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#del(int)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.MEMBER,detail="删除一个分类ID为${catid}的文章类别")
	public int del(int catid) {
		String sql = "";
		
		// 获取某类别下的子类别数
		sql = "select count(0) from es_data_cat where parent_id = ?";
		int count = this.daoSupport.queryForInt(sql, catid);
		if (count > 0) {
			return 1;
		}
		
		//删除文章是，将此文章下的子文章也删除！ add by DMRain 2016-3-22
		Integer model_id = this.get(catid).getModel_id();
		String table_name = "es_" + this.dataMdelManager.get(model_id).getEnglish_name();
		sql = "delete from " + table_name + " where cat_id = " + catid;
		this.daoSupport.execute(sql);
		
		sql = "delete from es_data_cat where cat_id = " + catid;
		this.daoSupport.execute(sql);
		
		return 0;
	}
	
	private List<DataCat> getChildren(List<DataCat> catList, Integer parentid){
		if(this.logger.isDebugEnabled()){
			this.logger.debug("查找["+parentid+"]的子");
		}
		List<DataCat> children =new ArrayList<DataCat>();
		for(DataCat cat :catList){
			if(cat.getParent_id().compareTo(parentid)==0){
				if(this.logger.isDebugEnabled()){
					this.logger.debug(cat.getName()+"-"+cat.getCat_id()+"是子");
				}
			 	cat.setChildren(this.getChildren(catList, cat.getCat_id()));
				children.add(cat);
			}
		}
		return children;
	}

	@Override
	public Integer getDataCat(String name) {
		String sql = "select count(*) from es_data_cat where name = ? ";
		return this.daoSupport.queryForInt(sql, name);
	}
}

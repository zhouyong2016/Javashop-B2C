package com.enation.app.shop.core.other.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.other.model.Article;
import com.enation.app.shop.core.other.model.ArticleCat;
import com.enation.app.shop.core.other.service.IArticleCatManager;
import com.enation.app.shop.core.other.service.IArticleManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

/**
 * 文章分类业务类
 * 
 * @author apexking
 * 
 */
@Service("articleCatDbManager")
@Deprecated
public class ArticleCatManager    implements IArticleCatManager {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private IDaoSupport  daoSupport;
	private IArticleManager articleManager;

	/**
	 * 根据id读取某个分类
	 * 
	 * @param cat_id
	 * @return
	 */
	public ArticleCat getById(int cat_id) {

		return (ArticleCat) this.daoSupport.queryForObject(
				"select * from article_cat where cat_id=?",
				ArticleCat.class, cat_id);

	}

	/**
	 * 保存添加分类
	 * 
	 * @param cat
	 */
	public void saveAdd(ArticleCat cat)  {
		
//		判断文章分类的父类id不能同自己的id一样
        if(cat.getParent_id() == null)
        	cat.setParent_id(0);
        else{
        	if(cat.getCat_id() != null && cat.getParent_id().intValue() == cat.getCat_id().intValue() )
        		throw new ArticleCatRuntimeException(2);//文章分类的父类id不能同自己的id一样
        		
        }
        
//      判断文章分类不能同名
        if(cat.getName() != null){
        	String sql = "select count(0) from article_cat where name = '"+ cat.getName()+"' and parent_id="+cat.getParent_id();
		    int count = this.daoSupport.queryForInt(sql);	
		    if(count > 0)
		    	throw new ArticleCatRuntimeException(1);//文章分类不能同名
        }
		this.daoSupport.insert("article_cat", cat);
		int cat_id = this.daoSupport.getLastId("article_cat");

		String sql = "";

		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
			sql = "select * from article_cat where cat_id=?";
			ArticleCat parent = (ArticleCat) this.daoSupport.queryForObject(sql, ArticleCat.class, cat.getParent_id());
			if(parent != null){
				cat.setCat_path(parent.getCat_path() + cat_id+ "|" );
			}
		} else {
			cat.setCat_path(cat.getParent_id() + "|" + cat_id+ "|" );
		}
       
		sql = "update article_cat set cat_path='" + cat.getCat_path()
				+ "' where cat_id=" + cat_id;
		this.daoSupport.execute(sql);

	}

	public void update(ArticleCat cat) {
		
		//判断文章分类的父类id不能同自己的id一样
		 if(cat.getParent_id() == null)
	        	cat.setParent_id(0);
	        else{
	        	if(cat.getCat_id() != null && cat.getParent_id().intValue() == cat.getCat_id().intValue() )
	        		throw new ArticleCatRuntimeException(2);//文章分类的父类id不能同自己的id一样
	        		
	        }
//	      判断文章分类不能同名
		if(cat.getName() != null){
        	String sql = "select count(0) from article_cat where cat_id != "+cat.getCat_id()+" and name = '"+ cat.getName()+"' and parent_id="+cat.getParent_id();
		    int count = this.daoSupport.queryForInt(sql);	
		    if(count > 0)
		    	throw new ArticleCatRuntimeException(1);//文章分类不能同名
        }

		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
			String sql = "select * from article_cat where cat_id=?";
			ArticleCat parent = (ArticleCat) this.daoSupport.queryForObject(sql,
					ArticleCat.class, cat.getParent_id());
			if(parent != null){
			   cat.setCat_path(parent.getCat_path()   + cat.getCat_id()+ "|" );
			}
		} else {
			cat.setCat_path(cat.getParent_id() + "|" + cat.getCat_id()+ "|" );
		}

		HashMap map = new HashMap();
		map.put("name", cat.getName());
		map.put("parent_id", cat.getParent_id());
		map.put("cat_order", cat.getCat_order());
		map.put("cat_path", cat.getCat_path());

		this.daoSupport.update("article_cat", map, "cat_id=" + cat.getCat_id());

	}

	/**
	 * 删除一个分类
	 * 
	 * @param cat_id
	 * @return 0删除成功 1删除失败 (存在子类别)
	 */

	public int delete(int cat_id) {

       //获取某类别下的子类别数
		String sql = "select count(0) from article_cat where parent_id = ?";
		int count = this.daoSupport.queryForInt(sql, cat_id);
		
		//获取某类别下的文章数
		String sqla = "select count(0) from article where cat_id = ?";
	    int counta = this.daoSupport.queryForInt(sqla, cat_id);

		if (count > 0 || counta > 0) {
			return 1; // 有子类别或者有文章
		}
	

		// sql = "select count(0) from js_goods_";

		sql = "delete from article_cat where cat_id=" + cat_id;

		this.daoSupport.execute(sql);

		return 0;
	}

	/**
	 * 保存分类排序
	 * 
	 * @param cat_ids
	 * @param cat_sorts
	 */
	public void saveSort(int[] cat_ids, int[] cat_sorts) {
		String sql = "";
		if (cat_ids != null && cat_sorts != null && cat_ids.length == cat_sorts.length) {
			for (int i = 0; i < cat_ids.length; i++) {
				sql = "update article_cat set cat_order=" + cat_sorts[i]
						+ " where cat_id=" + cat_ids[i];
				this.daoSupport.execute(sql);
			}
		}

	}

	/**
	 * 获取某个类别下的所有子类别 包含其子类别的
	 * 
	 * @param cat_id
	 *            如果为0则查询所有类别
	 * @return
	 */
	public List listChildById(Integer cat_id) {
		String sql = "select * from  article_cat  order by parent_id,cat_order" ;
		List<ArticleCat> allCatList = this.daoSupport.queryForList(sql, ArticleCat.class);
		List<ArticleCat> topCatList  = new ArrayList<ArticleCat>();
		for(ArticleCat cat :allCatList){
			if(cat.getParent_id().compareTo(cat_id)==0){
				if(this.logger.isDebugEnabled()){
					this.logger.debug("发现子["+cat.getName()+"-"+cat.getCat_id() +"]");
				}
				List<ArticleCat> children = this.getChildren(allCatList, cat.getCat_id());
				cat.setChildren(children);
				topCatList.add(cat);
			}
		}
		return topCatList;
	}

	private List<ArticleCat> getChildren(List<ArticleCat> catList ,Integer parentid){
		if(this.logger.isDebugEnabled()){
			this.logger.debug("查找["+parentid+"]的子");
		}
		List<ArticleCat> children =new ArrayList<ArticleCat>();
		for(ArticleCat cat :catList){
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
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#listHelp(int)
	 */
	@Override
	public List listHelp(int cat_id) {
		String sql = "select cat_id, name  from  article_cat c  where c.parent_id = " + cat_id;
	
		List<Map> list = this.daoSupport.queryForList(sql);
		for(Map map:list){
			Integer catid=StringUtil.toInt(map.get("cat_id").toString(), 0);
			List<Article> articleList = articleManager.listByCatId(catid);
			map.put("articleList", articleList);
		}
		return list;
	}


	

}

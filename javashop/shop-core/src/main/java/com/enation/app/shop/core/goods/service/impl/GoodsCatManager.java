package com.enation.app.shop.core.goods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.mapper.CatMapper;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

@Service("goodsCatDbManager")
public class GoodsCatManager  implements IGoodsCatManager {
 
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#checkname(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean checkname(String name,Integer catid){
		if(name!=null)name=name.trim();
		String sql ="select count(0) from es_goods_cat where name=? and cat_id!=?";
		if(catid==null){
			catid=0;
		}
		
		int count  = this.daoSupport.queryForInt(sql, name,catid);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#delete(int)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="删除ID为${catId}的商品类别")
	public int delete(int catId) {
		String sql =  "select count(0) from es_goods_cat where parent_id = ?";
		int count = this.daoSupport.queryForInt(sql,  catId );
		if (count > 0) {
			return 1; // 有子类别
		}

		sql =  "select count(0) from es_goods where cat_id = ?";
		count = this.daoSupport.queryForInt(sql,  catId );
		if (count > 0) {
			return 2; // 有子类别
		}
		sql =  "delete from  es_goods_cat   where cat_id=?";
		this.daoSupport.execute(sql,  catId );

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getById(int)
	 */
	@Override
	public Cat getById(int catId) {
		String sql = "select * from es_goods_cat  where cat_id=?";
		Cat cat =daoSupport.queryForObject(sql, Cat.class, catId);
		if(cat!=null){
			String image = cat.getImage();
			if(image!=null){
				image  =StaticResourcesUtil.convertToUrl(image); 
				cat.setImage(image);
			}
		}
		return cat;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#listChildren(java.lang.Integer)
	 */
	@Override
	public List<Cat> listChildren(Integer catId) {
		String sql  ="select c.*,'' type_name from es_goods_cat c where parent_id=?";
		return this.daoSupport.queryForList(sql,new CatMapper(), catId);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getListChildren(java.lang.Integer)
	 */
	@Override
	public List<Map> getListChildren(Integer cat_id) {
		String sql = "select c.*,tt.totleNum as totle_num,t.name as type_name from es_goods_cat c left join "
		 		+ " (select gcc.parent_id  gccat, count(gcc.cat_id) as totleNum from es_goods_cat gcc  "
		 		+ " where gcc.parent_id in (select cat_id from es_goods_cat gc where gc.parent_id=? ) GROUP BY gcc.parent_id )  tt on c.cat_id=tt.gccat "
		 		+ " left join es_goods_type t on c.type_id = t.type_id "
		 		+ " where c.parent_id=? order by c.cat_order asc";
		
		 List<Map> catlist = this.daoSupport.queryForList(sql, cat_id,cat_id);

		 for(Map map :catlist){
			if(map.get("totle_num")==null){		//判断某一个分类下的子分类数量 null赋值为0
				map.put("totle_num", 0);
			}
			int totle_num = Integer.parseInt(map.get("totle_num").toString());
			 if(totle_num!=0){		//判断某一个分类下的子分类数量 不为0 则是(easyui)文件夹并且是闭合状态。
				 map.put("state", "closed");
			 }
		 }
		 
		return catlist;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#listAllChildren(java.lang.Integer)
	 */
	@Override
	public List<Cat> listAllChildren(Integer catId) {

		String sql = "select c.*,t.name as type_name from  es_goods_cat c "
				+ " left join es_goods_type t on c.type_id = t.type_id where list_show=1 "
				+ " order by parent_id,cat_order";
		
		// this.findSql("all_cat_list");
		List<Cat> allCatList = daoSupport.queryForList(sql, new CatMapper());
		List<Cat> topCatList  = new ArrayList<Cat>();
		
		for(Cat cat :allCatList){
			if(cat.getParent_id().compareTo(catId)==0){
//				if(this.logger.isDebugEnabled()){
//					this.logger.debug("发现子["+cat.getName()+"-"+cat.getCat_id() +"]"+cat.getImage());
//				}
				List<Cat> children = this.getChildren(allCatList, cat.getCat_id());
				
				int i = this.daoSupport.queryForInt("select count(0) from es_goods_cat where parent_id="+cat.getCat_id());
				if(i!=0){
					cat.setState("closed");
				}
				cat.setChildren(children);
				topCatList.add(cat);
			}
		}
		return topCatList;
	}
 
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#saveAdd(com.enation.app.shop.core.goods.model.Cat)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="添加了一个商品类别名为${cat.name}的商品类别")
	public void saveAdd(Cat cat) {
		
		daoSupport.insert("es_goods_cat", cat);
		int cat_id = daoSupport.getLastId("es_goods_cat");
		String sql = "";
		//判断是否是顶级类似别，如果parentid为空或为0则为顶级类似别
		//注意末尾都要加|，以防止查询子孙时出错
		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) { //不是顶级类别，有父
			sql = "select * from es_goods_cat  where cat_id=?";
			Cat parent = daoSupport.queryForObject(sql, Cat.class, cat
					.getParent_id());
			cat.setCat_path(parent.getCat_path()  + cat_id+"|"); 
		} else {//是顶级类别
			cat.setCat_path(cat.getParent_id() + "|" + cat_id+"|");
			//2014-6-19 @author LiFenLong 如果为顶级类别则parent_id为0
			cat.setParent_id(0);
		}

		sql = "update es_goods_cat set  cat_path=?,parent_id=?  where  cat_id=?";
		daoSupport.execute(sql, new Object[] { cat.getCat_path(),cat.getParent_id(), cat_id });

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#update(com.enation.app.shop.core.goods.model.Cat)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="更新商品类别名为${cat.name}的商品类别")
	public void update(Cat cat) {
		Cat parent = null;
		
		if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
			
			String sql = "select * from es_goods_cat where cat_id=?";
			parent = daoSupport.queryForObject(sql, Cat.class, cat.getParent_id());
			cat.setCat_path(parent.getCat_path() + cat.getCat_id()+"|");
			
		} else {
			// 顶级类别，直接更新为parentid|catid
			cat.setCat_path(cat.getParent_id() + "|" + cat.getCat_id()+"|");
		}
		HashMap map = new HashMap();
		map.put("name", cat.getName());
		map.put("parent_id", cat.getParent_id());
		map.put("cat_order", cat.getCat_order());
		map.put("type_id", cat.getType_id());
		map.put("cat_path", cat.getCat_path());
		map.put("list_show", cat.getList_show());
		map.put("image", StringUtil.isEmpty(cat.getImage())?null:cat.getImage());
		daoSupport.update("es_goods_cat", map, "cat_id=" + cat.getCat_id());
		
		//修改子分类的cat_path
		List<Map> childList = this.daoSupport.queryForList("select * from es_goods_cat where parent_id=?", cat.getCat_id());
		if(childList!=null && childList.size()>0){
			for(Map maps : childList){
				Integer cat_id = (Integer) maps.get("cat_id");
				Map childmap = new HashMap();
				childmap.put("cat_path", cat.getCat_path()+cat_id+"|");
				daoSupport.update("es_goods_cat", childmap, " cat_id="+cat_id);
			}
		}
		
		//如果是父分类，且是关闭状态，则子分类变为关闭状态
		if(cat.getList_show().equals("0")){
			String sql = "update  es_goods_cat set list_show =? where cat_path like '"+cat.getCat_path()+"%'";
			daoSupport.execute(sql, cat.getList_show());
		}
	}
	

	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#saveSort(int[], int[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改商品类别的排序")
	public void saveSort(int[] cat_ids, int[] cat_sorts) {
		String sql = "";
		if (cat_ids != null) {
			for (int i = 0; i < cat_ids.length; i++) {
			    sql= "update  es_goods_cat  set cat_order=? where cat_id=?" ;
			    daoSupport.execute(sql,  cat_sorts[i], cat_ids[i] );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getParents(int)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<Cat> getParents(int catid) {
		Cat cat = this.getById(catid);
		String path=cat.getCat_path();
		path = path.substring(0,path.length()-1);
		path = path.replace("|", ",");
		List lists = new ArrayList();
		this.getParent(catid,lists);
		
		List list = new ArrayList();
		for(int i=(lists.size()-1);i>=0;i--){
			Cat c = (Cat) lists.get(i);
			list.add(c);
		}
		return list;
	}
	
	private List  getParent(Integer catid,List ls){
		if(catid!=null){
			String sql ="select cat_id,name,parent_id,type_id from es_goods_cat where cat_id="+catid;
			List<Cat> list =  this.daoSupport.queryForList(sql, Cat.class);
			if(!list.isEmpty()){
				for(Cat cat :list){
					ls.add(cat);
					this.getParent(cat.getParent_id(),ls);
				}
			}
		}
		return ls;
	}
	
	private List<Cat> getChildren(List<Cat> catList ,Integer parentid){
//		if(this.logger.isDebugEnabled()){
//			this.logger.debug("查找["+parentid+"]的子");
//		}
		List<Cat> children =new ArrayList<Cat>();
		for(Cat cat :catList){
			if(cat.getParent_id().compareTo(parentid)==0){
//				if(this.logger.isDebugEnabled()){
//					this.logger.debug(cat.getName()+"-"+cat.getCat_id()+"是子");
//				}
			 	cat.setChildren(this.getChildren(catList, cat.getCat_id()));
				children.add(cat);
			}
		}
		return children;
	}

	@Override
	public List getGoodsParentsType() {
		String sql = "select cat_id,name from es_goods_cat where parent_id=0";
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public List<Cat> getGoodsParentsType(Integer cat_id) {
		String sql  ="";
		Member m = UserConext.getCurrentMember();
		sql = "select goods_management_category from es_store where member_id="+ m.getMember_id();      //获取会员所有经营类目
		String goods_management_category = this.daoSupport.queryForString(sql);
		if(cat_id ==0 && goods_management_category!=null){
			sql = "select c.*,'' type_name from es_goods_cat c where cat_id in("+goods_management_category+")";
			return this.daoSupport.queryForList(sql, new CatMapper());
		}else{
			return listChildren(cat_id);
		}
	}

}

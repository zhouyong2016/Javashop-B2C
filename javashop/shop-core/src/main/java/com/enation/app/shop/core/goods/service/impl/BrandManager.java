package com.enation.app.shop.core.goods.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.model.mapper.BrandMapper;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.log.LogType;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造  clean delete checkUsed revert 方法参数String修改为Integer[]
 *
 */
@Service("brandManager")
public class BrandManager  implements IBrandManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#list(java.lang.String, int, int)
	 */
	@Override
	public Page list(String order, int page, int pageSize) {
		order = order == null ? " brand_id desc" : order;
		String sql = "select * from es_brand where disabled=0";
		sql += " order by  " + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#queryAllTypeNameAndId()
	 */
	@Override
	public List<Map> queryAllTypeNameAndId(){
//		String sql = "SELECT es_goods_type.type_id,es_goods_type.name FROM es_goods_type";
//		return this.daoSupport.queryForList(sql);
		String sql = "SELECT type_id,name FROM es_goods_type";
		return this.daoSupport.queryForList(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#search(int, int, java.lang.String, java.lang.Integer)
	 */
	@Override
	public Page search(int page, int pageSize,String brandname,Integer type_id) {
		Page webpage = null;
		String sql = "SELECT b.* FROM es_brand b left  join es_type_brand tb on b.brand_id=tb.brand_id left  join es_goods_type gt on tb.type_id = gt.type_id where b.name  like '%"+brandname+"%' ";
		if(type_id!=-100){
			sql+= "  and gt.type_id = ? ";
			webpage = this.daoSupport.queryForPage(sql, page, pageSize,type_id);
		}else{
			webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		}
		//System.out.println(sql);
		return webpage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#listTrash(java.lang.String, int, int)
	 */
	@Override
	public Page listTrash(String order, int page, int pageSize) {
		order = order == null ? " brand_id desc" : order;
		String sql = "select * from es_brand where disabled=1";
		sql += " order by  " + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#revert(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="还原回收站品牌")
	public void revert(Integer[] bid) {
		if (bid == null ){
			return;
		}
		
		String id_str = StringUtil.arrayToString(bid, ",");
		String sql = "update es_brand set disabled=0  where brand_id in (" + id_str
				+ ")";
		this.daoSupport.execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#checkUsed(java.lang.Integer[])
	 */
	@Override
	public boolean checkUsed(Integer[] ids){
		if (ids == null){
			return false;
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		
		String sql  ="select count(0) from es_goods where brand_id in (" + id_str + ")";;
		int count  = this.daoSupport.queryForInt(sql);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 将品牌放入回收站
	 * 
	 * @param bid
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="删除品牌")
	public void delete(Integer[] bid) {
		
		if (bid == null){
			return;
		}
		String id_str = StringUtil.arrayToString(bid, ",");
		
		//检测是否有商品关联
		String checksql="select count(0) from es_goods where brand_id in (" + id_str+ ")";
		int has_rel =this.daoSupport.queryForInt(checksql);
		
		if(has_rel>0){
			throw new RuntimeException("要删除的品牌已经关联商品，不能删除。");
		}
		String sql = "update es_brand set disabled=1  where brand_id in (" + id_str+ ")";
		
		this.daoSupport.execute(sql);
	}

	/**
	 * 品牌删除,真正的删除。
	 * 
	 * @param bid
	 * @param files
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#clean(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="清除回收站品牌")
	public void clean(Integer[] bid) {
		if (bid == null){
			return;
		}
		// 删除附件
		for (int i = 0; i < bid.length; i++) {
			int brand_id = bid[i];
			Brand brand = this.get(brand_id);
			if (brand != null) {
				String f = brand.getLogo();
				if (f != null && !f.trim().equals("")) {
					File file = new File(StringUtil.getRootPath() + "/" + f);
					file.delete();
				}
			}
		}
		String id_str = StringUtil.arrayToString(bid, ",");
		String sql = "delete  from  es_brand   where brand_id in (" + id_str + ")";
		this.daoSupport.execute(sql);
	}
	/**
	 * 读取所有品牌
	 * 
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#list()
	 */
	@Override
	public List<Brand> list() {
		String sql = "select * from es_brand where disabled=0";
		List list = this.daoSupport.queryForList(sql,new BrandMapper());
		return list;
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#listByTypeId(java.lang.Integer)
	 */
	@Override
	public List<Brand> listByTypeId(Integer typeid){
		String sql ="select b.* from es_type_brand tb inner join es_brand b  on    b.brand_id = tb.brand_id where tb.type_id=? and b.disabled=0";
		List list = this.daoSupport.queryForList(sql,new BrandMapper(),typeid);
		return list;
	}
	
	

	/**
	 * 读取品牌详细
	 * 会将本地文件存储的图片地址前缀替换为静态资源服务器地址。
	 * @param brand_id
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#get(java.lang.Integer)
	 */
	@Override
	public Brand get(Integer brand_id) {
		String sql = "select * from es_brand where brand_id=?";
		Brand brand = this.daoSupport.queryForObject(sql, Brand.class,
				brand_id);
 
		String logo = brand.getLogo();
		if(logo!=null){
			logo  =StaticResourcesUtil.convertToUrl(logo);
		}
		brand.setLogo(logo);
		return brand;
	}

	/**
	 * 分页读取某个品牌下的商品
	 * 
	 * @param brand_id
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandManager#getGoods(java.lang.Integer, int, int)
	 */
	public Page getGoods(Integer brand_id, int pageNo, int pageSize) {
		String sql = "select * from es_goods where brand_id=? and disabled=0";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,
				brand_id);
		return page;

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandManager#listBrands(java.lang.Integer, int, int)
	 */
	public Page listBrands(Integer tag_id,int pageNo,int pageSize){
		String sql = "select * from es_brand b inner join es_tag_relb r on b.brand_id = r.rel_id where r.tag_id = ? order by r.ordernum desc ";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,
				tag_id);
		return page; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandManager#listBrands(java.lang.Integer)
	 */
	public List<Brand> listBrands(Integer tag_id){
		String sql = "select b.* from es_brand b inner join es_tag_relb r on b.brand_id = r.rel_id where r.tag_id = ? order by r.ordernum desc ";
		List<Brand> brands = this.daoSupport.queryForList(sql,Brand.class, tag_id);
		return brands; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#add(com.enation.app.shop.core.goods.model.Brand)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="添加了一个品牌名为${brand.name}的品牌")
	public void add(Brand brand) {
		this.daoSupport.insert("es_brand", brand);

	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#update(com.enation.app.shop.core.goods.model.Brand)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改品牌名为${brand.name}的品牌信息")
	public void update(Brand brand) {
		
		if(brand.getLogo()!=null && "".equals(brand.getLogo())){
			this.deleteOldLogo(brand.getLogo());
		}
		this.daoSupport.update("es_brand", brand, "brand_id="
				+ brand.getBrand_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#listByCatId(java.lang.Integer)
	 */
	@Override
	public List<Brand> listByCatId(Integer catid) {
		String sql ="select b.* from es_brand b ,es_type_brand tb,es_goods_cat c where tb.brand_id=b.brand_id and c.type_id=tb.type_id and c.cat_id=? order by b.ordernum desc";
		return this.daoSupport.queryForList(sql, Brand.class, catid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#groupByCat()
	 */
	@Override
	public List groupByCat(){
		//取得商品分类的第一级列表
		List<Map> listCat = this.daoSupport.queryForList("select * from es_goods_cat where parent_id = 0 order by cat_order");
		for(Map map:listCat){
			List list = this.daoSupport.queryForList("select type_id from es_goods_cat where cat_path like '" + map.get("cat_path").toString() + "%'", new StringMapper());
			String types = StringUtil.listToString(list, ",");
			List listid = this.daoSupport.queryForList("select brand_id from es_type_brand where type_id in (" + types + ")", new StringMapper());
			String ids = StringUtil.listToString(listid, ",");
			List<Brand> listBrand = this.daoSupport.queryForList("select * from es_brand where brand_id in (" + ids + ")", Brand.class);
			map.put("listBrand", listBrand);
		}
		return listCat;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#checkname(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean checkname(String name,Integer brandid) {
		if(name!=null)name=name.trim();
		String sql ="select count(0) from es_brand where name=? and brand_id!=?";
		if(brandid==null) brandid=0;
		
		int count =this.daoSupport.queryForInt(sql, name,brandid);
		if(count>0)
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#list(int)
	 */
	@Override
	public List<Brand> list(int count) {
		String sql = "select * from es_brand where disabled=0";
		Page page = this.daoSupport.queryForPage(sql, 1, count, new BrandMapper());
		List list = (List<Brand>)page.getResult();
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IBrandManager#searchBrand(java.util.Map, int, int)
	 */
	@Override
	public Page searchBrand(Map brandMap, int page, int pageSize) {
		String keyword = (String) brandMap.get("keyword");
		
		String sql = "select * from es_brand where disabled=0";
		if(keyword!=null && !StringUtil.isEmpty(keyword)){
			sql+=" and name like '%"+keyword+"%'";
		}
		sql+=" order by brand_id desc";
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IBrandManager#saveOrder(int[], int[])
	 */
	@Override
	public void saveOrder(int[] ordernums, int[] ids) {
		
		for(int i = 0;i<ordernums.length;i++){
			this.daoSupport.execute("update es_brand set ordernum = ? where brand_id = ?", ordernums[i],ids[i]);
		}
	}
	
	private void deleteOldLogo(String logo){
		if(!logo.equals("http://static.enationsfot.com")){
			logo  =StaticResourcesUtil.convertToUrl(logo);
			FileUtil.delete(logo);
		}
	}  
}

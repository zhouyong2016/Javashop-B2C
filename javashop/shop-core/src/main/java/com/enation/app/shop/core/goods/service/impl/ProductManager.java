package com.enation.app.shop.core.goods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.Specification;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.StringUtil;

/**
 * 货品管理
 * @author kingapex
 *2010-3-9下午06:27:48
 */
@Service("ProductManager")
public class ProductManager implements IProductManager {
	
	@Autowired
	private IMemberPriceManager memberPriceManager;
	@Autowired
	private IMemberLvManager memberLvManager;
	@Autowired
	private IGoodsCatManager goodsCatManager;
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#add(java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(List<Product> productList) {
		 if(productList.size()>0){
			 Integer goodsid  =  productList.get(0).getGoods_id();
			 //清除规格信息
			 this.daoSupport.execute("delete from  es_goods_spec  where goods_id=?",goodsid);
			 this.daoSupport.execute("delete from  es_goods_lv_price  where goodsid=?", goodsid);
			 
			 String proidstr = this.getProductidStr(productList);
			 //清除删除的规格数据
			 String sql  ="delete from es_product where goods_id=? ";
			 if(!StringUtil.isEmpty( proidstr)){
				 sql+=" and  product_id  not in("+proidstr+")";
			 }
			 this.daoSupport.execute(sql,goodsid);
			 
			 
			 //清除删除掉的库存数据
			 sql="delete from es_product_store where goodsid=? ";
			 if(!StringUtil.isEmpty( proidstr)){
				 sql+=" and  productid  not in("+proidstr+")";
			 }
			 this.daoSupport.execute(sql, goodsid);
			 
		 }
		 
		 for(Product product:productList){
			 
			 //如果货号为空则插入新货品，如果货号存在则更新货品
			 Integer product_id  =  product.getProduct_id();
			 if( product_id==null ){
				 this.daoSupport.insert("es_product", product);
				 product_id= this.daoSupport.getLastId("es_product");
				 product.setProduct_id(product_id);
			 }else{
				 this.daoSupport.update("es_product", product, "product_id="+ product_id);
			 }
			 
			 //货品对应的规格组合
			 List<SpecValue> specList = product.getSpecList();
			 
			 for(SpecValue specvalue:specList){
				 this.daoSupport.execute(
						 "insert into es_goods_spec (spec_id,spec_value_id,goods_id,product_id)values(?,?,?,?)", 
						 specvalue.getSpec_id(),specvalue.getSpec_value_id(),product.getGoods_id(),product_id);
			 }
			 
			 //添加会员价格数据
			 List<GoodsLvPrice> lvPriceList  =  product.getGoodsLvPrices();
			 if(lvPriceList!=null){
				 for(GoodsLvPrice lvPrice  : lvPriceList){
					 lvPrice.setProductid(product_id);
					 this.daoSupport.insert("es_goods_lv_price", lvPrice);
				 }
				 
			 }
		 }
		 
		 if(productList.size()>0){
			 Integer goodsid  =  productList.get(0).getGoods_id();
			 //更新商品的specs字段
			 this.daoSupport.execute("update es_goods set specs=? where goods_id=?", JSONArray.fromObject(productList).toString(),goodsid);
		 } 
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#get(java.lang.Integer)
	 */
	@Override
	public Product get(Integer productid) {
		String sql ="select * from es_product where product_id=?";
		return this.daoSupport.queryForObject(sql, Product.class, productid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#listSpecName(int)
	 */
	@Override
	public List<String> listSpecName(int goodsid){
		

		StringBuffer sql = new StringBuffer();
		sql.append("select distinct s.spec_name ");
		sql.append(" from ");
		
		sql.append("es_specification");
		sql.append(" s,");
		

		
		sql.append("es_goods_spec");
		sql.append(" gs ");
		
		sql.append("where s.spec_id = gs.spec_id and gs.goods_id=?");
		List  list  =this.daoSupport.queryForList(sql.toString(),new StringMapper(), goodsid);
		return list;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#listSpecs(java.lang.Integer)
	 */
	@Override
	public List<Specification> listSpecs(Integer goodsId) {
 
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct s.spec_id,s.spec_type,s.spec_name,sv.spec_value_id,sv.spec_value,sv.spec_image ,gs.goods_id as goods_id ");
		sql.append(" from ");
		
		sql.append("es_specification");
		sql.append(" s,");
		
		sql.append("es_spec_values");
		sql.append(" sv,");
		
		sql.append("es_goods_spec");
		sql.append(" gs ");
		
		sql.append("where s.spec_id = sv.spec_id  and gs.spec_value_id = sv.spec_value_id and gs.goods_id=?  ORDER BY s.spec_id");
		List<Map> list  =this.daoSupport.queryForList(sql.toString(), goodsId);
			
		List<Specification> specList = new ArrayList<Specification>();
		Integer spec_id =0;
		Specification spec =null;
		for(Map map: list){
			Integer dbspecid =Integer.valueOf( map.get("spec_id").toString() );
			List<SpecValue> valueList ;
		
			if( spec_id.intValue() != dbspecid.intValue() ){
				spec_id = dbspecid;
				valueList  = new ArrayList<SpecValue>();
				 
				spec  = new Specification();
				spec.setSpec_id( dbspecid);
				spec.setSpec_name(map.get("spec_name").toString());
				spec.setSpec_type(Integer.parseInt(map.get("spec_type").toString()));
				
				specList.add(spec);
				
				spec.setValueList(valueList);
			}else{
				valueList = spec.getValueList();
			}
			
			SpecValue value  = new SpecValue();
			value.setSpec_value(map.get("spec_value").toString());
			value.setSpec_value_id(Integer.valueOf( map.get("spec_value_id").toString() ));
			String spec_img  = (String)map.get("spec_image");
			value.setSpec_type(Integer.parseInt(map.get("spec_type").toString()) );
			
			//将本地中径替换为静态资源服务器地址
			if( spec_img!=null ){
				spec_img  =StaticResourcesUtil.convertToUrl(spec_img); 
			}
			value.setSpec_image(spec_img);
			
			valueList.add(value);
		}
		
		return specList ;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#list(java.lang.Integer)
	 */
	@Override
	public List<Product> list(Integer goodsId) {
		String sql ="select * from es_product where goods_id=? order by product_id asc";
		List<Product> prolist = daoSupport.queryForList(sql,Product.class, goodsId);
		
		sql="select sv.*,gs.product_id product_id from  es_goods_spec   gs inner join  es_spec_values   sv on gs.spec_value_id = sv.spec_value_id where  gs.goods_id=? order by gs.id desc" ;
		 
		//sql="select * from "+ "es_goods_spec")+" gs,"+"es_spec_values")+" sv where gs.spec_value_id = sv.spec_value_id and  gs.goods_id=? ";
		//System.out.println(sql);
		List<Map> gsList  = this.daoSupport.queryForList(sql, goodsId);
		
		
		List<GoodsLvPrice> memPriceList = new ArrayList<GoodsLvPrice>();
		
		Member member = UserConext.getCurrentMember();
		double discount =1; //默认是原价,防止无会员级别时出错
		if(member!=null){
			memPriceList  = this.memberPriceManager.listPriceByGid(goodsId);
			MemberLv lv =this.memberLvManager.get(member.getLv_id());
			if(lv!=null)
			discount = lv.getDiscount()/100.00;
			////System.out.println(discount);
		}
		
		for(Product pro:prolist){
			if(member!=null){
				Double price  = pro.getPrice();
				if(memPriceList!=null && memPriceList.size()>0)
				price = this.getMemberPrice(pro.getProduct_id(), member.getLv_id(), price, memPriceList, discount);
				pro.setPrice(price);
			}
			int size = gsList.size()-1;
			for(int i=size;i>=0;i--){
				Map gs = gsList.get(i);
				Integer productid;
				productid = ((Integer)gs.get("product_id")).intValue();
				
				//是这个货品的规格
				//则压入到这个货品的规格中
				//用到了spec_value_id
				if(  pro.getProduct_id().intValue()  ==   productid  ){ 
					SpecValue spec = new SpecValue();
					spec.setSpec_value_id( (Integer)gs.get("spec_value_id")  );
					spec.setSpec_id( (Integer)gs.get("spec_id"));
					String spec_img  = (String)gs.get("spec_image");
					
					//将本地中径替换为静态资源服务器地址
					if( spec_img!=null ){
						spec_img  =StaticResourcesUtil.convertToUrl(spec_img); 
					}
					spec.setSpec_image(spec_img);
					spec.setSpec_value((String)gs.get("spec_value"));
					spec.setSpec_type(Integer.parseInt(gs.get("spec_type").toString()));
					pro.addSpec(spec);
				}
				
			}
		}
		return prolist;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#delete(java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] goodsid){
		String id_str = StringUtil.arrayToString(goodsid, ",");
		String sql ="delete from es_goods_spec where goods_id in ("+ id_str +")";
		this.daoSupport.execute(sql);
		
		sql ="delete from es_goods_lv_price where goodsid in ("+ id_str +")";
		this.daoSupport.execute(sql);
		
		sql ="delete from es_product where goods_id in ("+ id_str +")";
		this.daoSupport.execute(sql);

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#list(java.lang.String, java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public Page list(String name,String sn,int pageNo, int pageSize, String order) {
		order = order == null ? "product_id asc" : order;
		StringBuffer sql = new StringBuffer();
		sql.append("select p.* from es_product p left join " + "es_goods  g on g.goods_id = p.goods_id ");
		sql.append(" where g.disabled=0");
		if(!StringUtil.isEmpty(name)){
			sql.append(" and g.name like '%");
			sql.append(name);
			sql.append("%'");
		}
		if(!StringUtil.isEmpty(sn)){
			sql.append(" and g.sn = '");
			sql.append(sn);
			sql.append("'");
		}
		
		sql.append(" order by " + order);
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#list(java.lang.Integer[])
	 */
	@Override
	public List list(Integer[] productids) {
		if(productids==null || productids.length==0) return new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select p.* from " + "es_product  p left join " + "es_goods  g on g.goods_id = p.goods_id ");
		sql.append(" where g.disabled=0");
		sql.append(" and p.product_id in(");
		sql.append(StringUtil.arrayToString(productids, ","));
		sql.append(")");
		
		return  this.daoSupport.queryForList(sql.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#getByGoodsId(java.lang.Integer)
	 */
	@Override
	public Product getByGoodsId(Integer goodsid) {

		String sql ="select * from es_product where goods_id=?";
		List<Product> proList  =this.daoSupport.queryForList(sql, Product.class, goodsid);
		if(proList==null || proList.isEmpty()){
			return null;
		}
		return proList.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#listProductByCatId(java.lang.Integer)
	 */
	@Override
	public List listProductByCatId(Integer catid) {
		String sql = "select p.* from es_product p left join es_goods g on p.goods_id= g.goods_id left join es_goods_cat c on g.cat_id=c.cat_id";
		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " where  g.cat_id in(";
			sql += "select c.cat_id from es_goods_cat"
					+ " c where c.cat_path like '" + cat.getCat_path() + "%') ";
		}
		List list = this.daoSupport.queryForList(sql);
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductManager#listProductByStoreId(java.lang.Integer, int)
	 */
	@Override
	public List listProductByStoreId(Integer catid,int storeId) {
		String sql = "select p.*,g.store_id from es_product p left join es_goods g on p.goods_id= g.goods_id left join es_goods_cat c on g.cat_id=c.cat_id";
		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " where  g.cat_id in(";
			sql += "select c.cat_id from " + "es_goods_cat" 
					+ " c where c.cat_path like '" + cat.getCat_path() + "%') ";
		}
		sql += "AND g.store_id=" + storeId;
		List list = this.daoSupport.queryForList(sql);
		return list;
	}
	/**
	 * 由productList的id生成,号隔开的字串
	 * @param productList
	 * @return
	 */
	private String getProductidStr(List<Product> productList){
		StringBuffer str = new StringBuffer();
		for(Product pro:productList){
			
			Integer productid = pro.getProduct_id();
			if(productid!=null){
				if(str.length()!=0){
					str.append(",");	
				}
				str.append(pro.getProduct_id());
			}
		}
		
		return str.toString();
	}
	/**
	 * 获取某货品的会员价格
	 * @param price 销售价
	 * @param memPriceList 会员价列表
	 * @param discount 此会员级别的折扣
	 * @return
	 */
	private Double getMemberPrice(int productid,int lvid,Double price,List<GoodsLvPrice> memPriceList,double discount){
		Double memPrice  = price * discount; //默认是此会员级别的折扣价
		
		//然后由具体会员价格中寻找，看是否指定了具体的会员价格
		for( GoodsLvPrice  lvPrice  :memPriceList ){
			if(lvPrice.getProductid() == productid && lvPrice.getLvid() == lvid){ //找到此货品,此会员级别的价格
				memPrice = lvPrice.getPrice();
			}
		}
		return memPrice;
	}
}

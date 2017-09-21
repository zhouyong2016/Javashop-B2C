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
import com.enation.app.shop.core.goods.model.ProductSnapshot;
import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.Specification;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.goods.service.IProductSnapshotManager;
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
 * 
 * (货品快照管理) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2017年1月6日 上午12:10:36
 */
@Service("ProductSnapshotManager")
public class ProductSnapshotManager implements IProductSnapshotManager {
	
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
	 * @see com.enation.app.shop.core.goods.service.IProductManager#list(java.lang.Integer)
	 */
	@Override
	public List<ProductSnapshot> list(Integer goodsId,Integer snapshot_id,Integer product_id) {
		String sql ="select * from es_product_snapshot where snapshot_id=? and goods_id=? and product_id=?  order by product_id asc";
		List<ProductSnapshot> prolist = daoSupport.queryForList(sql,ProductSnapshot.class,snapshot_id, goodsId,product_id);
		
		sql="select sv.*,gs.product_id product_id from  es_goods_spec   gs inner join  es_spec_values   sv on gs.spec_value_id = sv.spec_value_id where  gs.goods_id=? and gs.product_id=? order by gs.id desc" ;
		 
		List<Map> gsList  = this.daoSupport.queryForList(sql, goodsId,product_id);
		
		
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
	 * @see com.enation.app.shop.core.goods.service.IProductManager#listSpecs(java.lang.Integer)
	 */
	@Override
	public List<Specification> listSpecs(Integer goodsId,Integer product_id) {
 
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct s.spec_id,s.spec_type,s.spec_name,sv.spec_value_id,sv.spec_value,sv.spec_image ,gs.goods_id as goods_id ");
		sql.append(" from ");
		
		sql.append("es_specification");
		sql.append(" s,");
		
		sql.append("es_spec_values");
		sql.append(" sv,");
		
		sql.append("es_goods_spec");
		sql.append(" gs ");
		
		sql.append("where s.spec_id = sv.spec_id  and gs.spec_value_id = sv.spec_value_id and gs.goods_id=? and gs.product_id=? ORDER BY s.spec_id");
		List<Map> list  =this.daoSupport.queryForList(sql.toString(), goodsId,product_id);
			
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

	@Override
	public void add(ProductSnapshot productSnapshot) {
		daoSupport.insert("es_product_snapshot", productSnapshot);
	}

}

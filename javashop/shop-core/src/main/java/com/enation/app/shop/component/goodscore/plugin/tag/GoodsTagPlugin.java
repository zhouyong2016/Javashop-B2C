package com.enation.app.shop.component.goodscore.plugin.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;

/**
 * 商品标签插件
 * @author kingapex
 * 2010-1-18下午02:56:43
 */
@Component
public class GoodsTagPlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent {
	
	@Autowired
	private ITagManager tagManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	/*为添加商品和修改商品页面填充必要的数据*/
	
	
	public String getAddHtml(HttpServletRequest request) {
		List<Map> taglist=new ArrayList<Map>();
		
		if(EopSetting.PRODUCT.equals("b2c")){
			taglist  = this.tagManager.listMap();
		}else{
			taglist=this.daoSupport.queryForList("select * from es_tags where store_id IS NULL");
		}
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		freeMarkerPaser.setPageName("tag");
		freeMarkerPaser.putData("tagList", taglist);
		return freeMarkerPaser.proessPageContent();
	}
	
	
	
	public String getEditHtml(Map goods, HttpServletRequest request) {
		List<Map> taglist=new ArrayList<Map>();
		
		if(EopSetting.PRODUCT.equals("b2c")){
			taglist  = this.tagManager.listMap();
		}else{
			taglist=this.daoSupport.queryForList("SELECT * FROM es_tags WHERE store_id IS NULL");
		}
	 
		Integer goods_id =  Integer.valueOf(goods.get("goods_id").toString());
		List<Integer> tagIds=this.tagManager.list(goods_id);
	 
		
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		freeMarkerPaser.setPageName("tag");
		freeMarkerPaser.putData("tagList", taglist);	
		freeMarkerPaser.putData("tagRelList", tagIds);
		return freeMarkerPaser.proessPageContent();
	}

	
	
	
	/*在保存添加和保存更新的时候，将tagid的数组和goodsid对应起来保存在库里*/
	
	
	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		if(goods.get("store_id")==null){
			this.save(goods, request);
		}

	}

	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request){
		if(goods.get("store_id")==null){
			this.save(goods, request);
		}
	}

	private void save(Map goods, HttpServletRequest request){
		Integer goods_id =  Integer.valueOf(goods.get("goods_id").toString());
		
		String[] tagstr=  request.getParameterValues("tag_id");
		Integer[] tagids = null;
		if(tagstr!=null){
			tagids = new Integer[tagstr.length];
			for(int i=0;i<tagstr.length;i++){
				tagids[i]=	Integer.valueOf(tagstr[i]) ;
			}
		}
		this.tagManager.saveRels(goods_id, tagids);
	}
	
	
	
	
	
	
	

	
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request){
		 

	}

	
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request){
		 
		 
	}

	
	
	public String getAuthor() {
		return "kingapex";
	}

	
	public String getId() {
		return "goodstag";
	}

	
	public String getName() {
		return "商品标签";
	}

	

	
	public String getVersion() {
		return "1.0";
	}

	


	@Override
	public String getTabName() {
		// TODO Auto-generated method stub
		return "标签";
	}

	@Override
	public int getOrder() {
		 
		return 13;
	}

}

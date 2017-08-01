package com.enation.app.shop.component.goodscore.plugin.seo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;


/**
 * 商品SEO优化插件
 * @author enation
 *
 */
@Component 
public class GoodsSeoPlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent{
	
 
	
	public void addTabs(){
	  
	}
	
	public void registerPages(){	 
		//this.registerPage("goods_add.tabcontent", "/plugin/seo/seo.jsp?ajax=yes");
	}

 

	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {

	}
 
	
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("seo");
		return freeMarkerPaser.proessPageContent();
	}

	public String getEditHtml(Map goods, HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		freeMarkerPaser.setPageName("seo");
		freeMarkerPaser.putData("goods", goods);
		return freeMarkerPaser.proessPageContent();
	}

	public void onAfterGoodsAdd(Goods goods, HttpServletRequest request) throws RuntimeException {
		
		
	}

	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)  {
		
		
	}

	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request)  {
		
		
	}
	
	

	public void onAfterGoodsEdit(Map goods, HttpServletRequest request)  {
	 
		
	}

	public String getAuthor() {
		
		return "kingapex";
	}

	public String getId() {
		
		return "goodsseo";
	}

	public String getName() {
		 
		return "商品SEO优化插件";
	}

	public String getType() {
		
		return "";
	}

	public String getVersion() {
		
		return "1.0";
	}

	public void perform(Object... params) {
		
		
	}

	@Override
	public String getTabName() {
		 
		return "SEO";
	}

	@Override
	public int getOrder() {
		 
		return 11;
	}
	
}

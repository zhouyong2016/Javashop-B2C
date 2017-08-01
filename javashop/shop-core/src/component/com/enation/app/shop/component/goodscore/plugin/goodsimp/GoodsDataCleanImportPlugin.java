package com.enation.app.shop.component.goodscore.plugin.goodsimp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.enation.app.shop.core.goods.plugin.goodsimp.IBeforeGoodsImportEvent;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商品导入前清除商品数据插件 
 * @author kingapex
 *
 */

@Component
public class GoodsDataCleanImportPlugin extends AutoRegisterPlugin implements
		IBeforeGoodsImportEvent {

	private IDaoSupport daoSupport;
	
	public void register() {
		

	}

	
	public void onBeforeImport(Document configDoc) {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String cleanall = request.getParameter("cleanall");
		String cleancat = request.getParameter("cleancat");
		String imptype = request.getParameter("imptype");
		
		if(!StringUtil.isEmpty(cleanall)){
			String static_server_path= SystemSetting.getStatic_server_path();

			//删除商品图片
			String imgfolder = static_server_path+"/attachment/goods";
			FileUtil.delete(imgfolder);
			
			String ckeditor =static_server_path+"/attachment/ckeditor";
			FileUtil.delete(imgfolder);
			
			this.daoSupport.execute("truncate table es_goods");
			this.daoSupport.execute("truncate table es_product");
			this.daoSupport.execute("truncate table es_goods_spec");
			this.daoSupport.execute("truncate table es_cart");
			this.daoSupport.execute("truncate table es_order");
			this.daoSupport.execute("truncate table es_order_items");
		}
		
		if( "2".equals(imptype)  && !StringUtil.isEmpty(cleancat) ){
			int catid  = Integer.valueOf( request.getParameter("catid") );
			this.daoSupport.execute("delete from product where es_goods_id in (select goods_id from es_goods where cat_id=?)", catid);
			this.daoSupport.execute("delete from goods where cat_id=?", catid);
			
		}
		
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "goodsImageDeletePlugin";
	}

	
	public String getName() {
		
		return "商品导入前删除商品图片插件 ";
	}

	
	public String getType() {
		
		return "goodsimp";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}


 
}

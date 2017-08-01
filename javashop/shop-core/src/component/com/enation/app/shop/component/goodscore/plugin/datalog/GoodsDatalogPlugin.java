package com.enation.app.shop.component.goodscore.plugin.datalog;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.DataLog;
import com.enation.app.shop.core.goods.plugin.IGoodsAfterAddEvent;
import com.enation.app.shop.core.goods.plugin.IGoodsAfterEditEvent;
import com.enation.eop.resource.IDataLogManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 商品数据日志记录插件 
 * @author kingapex
 * 2010-10-19下午05:03:29
 */
@Component
public class GoodsDatalogPlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent, IGoodsAfterEditEvent {
	
	@Autowired
	private IDataLogManager dataLogManager;
	
	public void register() {
		
	}

	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		
		DataLog datalog  = this.createDataLog(goods);
		datalog.setOptype("添加");
		this.dataLogManager.add(datalog);
		
	}

	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		DataLog datalog  = this.createDataLog(goods);
		datalog.setOptype("修改");
		this.dataLogManager.add(datalog);
	}
	
	
	private DataLog createDataLog(Map goods){
		
		DataLog datalog  = new DataLog();
		datalog.setContent("商品名:"+goods.get("name")+"<br>"+"描述:"+goods.get("intro"));
		String  image_file =(String) goods.get("image_file");
		
		StringBuffer pics  = new StringBuffer();
		if( !StringUtil.isEmpty(image_file)) {
			String[] files = StringUtils.split(image_file,",");
			for(String file:files){
				if(pics.length()>0)
					pics.append(",");
				pics.append( StaticResourcesUtil.getThumbPath(file, "_thumbnail") );
				pics.append("|");
				pics.append(file );
			}
		}
		
		datalog.setPics(pics.toString());
		datalog.setLogtype("商品");
		datalog.setOptype("添加");
		datalog.setUrl("/goods-"+goods.get("goods_id")+".html");
		
		return datalog;
		
		
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "goodsdatalog";
	}

	
	public String getName() {
		
		return "商品数据日志记录插件";
	}

	
	public String getType() {
		
		return "datalog";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}


}

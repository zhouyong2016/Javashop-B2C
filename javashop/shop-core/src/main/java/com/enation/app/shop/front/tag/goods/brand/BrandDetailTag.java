package com.enation.app.shop.front.tag.goods.brand;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 根据品牌ID获得品牌详细
 * brand_id
 * @author wanghongjun
 *
 */

@Component
@Scope("prototype")
public class BrandDetailTag extends BaseFreeMarkerTag{
	
	private IBrandManager brandManager;
	private Brand brand;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer brand_id=(Integer) params.get("brand_id");
		if(brand_id==0){
			return "" ;
		}else{
			brand = brandManager.get(brand_id);
			return brand;
		}
		
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	
}

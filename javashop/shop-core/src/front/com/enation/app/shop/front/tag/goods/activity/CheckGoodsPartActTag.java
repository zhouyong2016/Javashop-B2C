package com.enation.app.shop.front.tag.goods.activity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.other.model.Activity;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 检查商品是否参加了促销活动
 * @author DMRain
 * @date 2016-6-14
 * @version 1.0
 */
@Component
public class CheckGoodsPartActTag extends BaseFreeMarkerTag{

	@Autowired
	private IActivityManager activityManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id = (Integer) params.get("goods_id");
		
		//获取当前正在进行的促销活动信息
		Activity activity = this.activityManager.getCurrentAct();
		
		Map result = new HashMap();
		
		//如果促销活动信息为空
		if(activity != null){
			
			//如果促销活动为全部商品参加（1：全部商品参加，2：部分商品参加）
			if(activity.getRange_type() == 1){
				result.put("activity", activity);
			}else if(activity.getRange_type() == 2){
				int num = this.activityManager.checkGoodsAct(goods_id, activity.getActivity_id());
				
				//如果商品参加了促销活动
				if(num == 1){
					result.put("activity", activity);
				}
			}
		}
		
		return result;
	}

}

package com.enation.app.shop.front.tag.goods.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.service.IGoodsSearchManager;
import com.enation.app.shop.core.goods.service.SearchEngineFactory;
import com.enation.app.shop.core.goods.utils.SortContainer;
import com.enation.app.shop.core.other.model.Activity;
import com.enation.app.shop.core.other.model.ActivityDetail;
import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.app.shop.core.other.service.IActivityDetailManager;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 促销详情tag
 * @author zh
 * @version v1.0
 * @since v6.1
 * 2016年10月7日 下午9:16:51
 */
@Component
public class ActivitySalesDetailTag extends BaseFreeMarkerTag{

	@Autowired
	private IActivityManager activityManager;
	
	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Autowired
	private IActivityDetailManager activityDetailManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap();
		Integer pageSize = (Integer)params.get("pageSize");
		//判断pagesize是否为null
		if(pageSize == null){
			pageSize = this.getPageSize();
		}
		int page=this.getPage();//使支持？号传递
		//得到当前的促销信息
		Activity activity=activityManager.getCurrentAct();
		//商品排序
		SortContainer.getSortList();
		Page webPage=null;
		//判断活动是否存在
		if(activity != null){
			//获取促销商品
			if(activity.getRange_type() == 1){
				//当全部商品参加活动，查询全部商品
				IGoodsSearchManager goodsSearchManager = SearchEngineFactory.getSearchEngine();
				webPage  =  goodsSearchManager.search(page, pageSize);
			}else{
				//当部分商品参与，查询参与活动的商品
				webPage = activityManager.listGoods(null, activity.getActivity_id(),page, pageSize);
			}
			Long totalCount = webPage.getTotalCount();
			List goodsList = (List) webPage.getResult();
			goodsList = goodsList == null ? new ArrayList() : goodsList;
			result.put("totalCount", totalCount);//总数
			result.put("pageSize", pageSize);
			result.put("page", page);
			result.put("goodsList", goodsList);
		}
		//将得到的促销活动详情和商品信息存入map 输出到前台
		//促销信息
		result.put("activity", activity);   
		//促销详细信息
		ActivityDetail activityDetail=this.activityDetailManager.getDetail(activity.getActivity_id());
		result.put("detail",activityDetail);
		if(activityDetail.getIs_send_gift() == 1){
			//赠品详细信息
			ActivityGift activityGift=activityGiftManager.get(activityDetail.getGift_id());
			result.put("activityGift",activityGift);
		}
		return result;
	}

}

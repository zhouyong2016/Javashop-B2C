package com.enation.app.shop.front.tag.goods;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 成交记录标签
 * @author LiFenLong
 *
 */
public class RecordListTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IOrderManager orderManager;
	/**
	 * @param goods_id,商品Id
	 * @return
	 * create_time,购买时间
	 * order_item_num,购买数量
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Integer goods_id=Integer.parseInt(params.get("goods_id").toString());
		Integer pageNo=this.getPage();
		Integer pageSize= this.getPageSize();
		return orderManager.getRecordList(goods_id, pageNo, pageSize);
	}
	
}

package com.enation.app.shop.front.tag.order;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.goods.plugin.search.GoodsDataFilterBundle;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 购买记录标签
 * @author liuzy
 *
 */
@Component
@Scope("prototype")

public class OrderHistoryTag extends BaseFreeMarkerTag {
	private IDaoSupport daoSupport;
	private GoodsDataFilterBundle  goodsDataFilterBundle;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer num = (Integer)params.get("num");
		if(num==null)
			num = 10;
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[OrderHistoryTag]");
		}
		
		String sql = "select * from es_goods g where goods_id in (select goods_id " +
				"from es_order_items i,es_order o where i.order_id=o.order_id and o.member_id=?)";
		
		Page webPage = daoSupport.queryForPage(sql, 1, num, member.getMember_id());
		
		goodsDataFilterBundle.filterGoodsData((List)webPage.getResult());
		return webPage.getResult();
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public GoodsDataFilterBundle getGoodsDataFilterBundle() {
		return goodsDataFilterBundle;
	}

	public void setGoodsDataFilterBundle(GoodsDataFilterBundle goodsDataFilterBundle) {
		this.goodsDataFilterBundle = goodsDataFilterBundle;
	}

}

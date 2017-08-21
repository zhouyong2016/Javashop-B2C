package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员订单数标签
 * @author kingapex
 *2013-7-29下午12:07:21
 */
@Component
@Scope("prototype")
public class OrderNumberTag extends BaseFreeMarkerTag {
	private IDaoSupport daoSupport;
	
	/**
	 * 会员订单数量标签
	 * @param 无
	 * @return 订单数量Map,Map型
	 * 此Map中的key为订单状态，假设变量为orderNum，则通过${orderNumber["0"]}可以输出状态为0的订单。
	 * 因为此值可能为空，所以在页面中应该写为${orderNumber["0"]!'0'}防止出错。
	 * {@link OrderStatus} 宏俊注意：这个OrderStatus只要把订单状态列在文档中即可，付款状态及收货状态先不用列。
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[OrderNumberTag]");
		}
		String sql ="select count(0) num,status from es_order where member_id=? group by status";
		List<Map> list =(List)daoSupport.queryForList(sql, member.getMember_id());
		Map data = new HashMap();
		
		for(Map map :list){
			data.put("" + map.get("status"), map.get("num"));
		}
		
		//未付款按付款方式查询
		sql="select count(0) from es_order where status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO +" and member_id=?";
		List<Integer> noPayList= this.daoSupport.queryForList(sql,new IntegerMapper(), member.getMember_id());
		if(noPayList.isEmpty()){
			data.put("0", 0);
		}else{
			data.put("0",noPayList.get(0));
		}
		return data;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

}

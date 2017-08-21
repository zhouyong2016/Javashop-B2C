package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单提交切换支付方式和配送方式和送货日期
 * @author xulipeng
 * @version 1.0
 * @since v6.2
 * 2016年11月28日
 */
@Component
@Scope("prototype")
public class OrderPaymentDlytypeTag extends BaseFreeMarkerTag {

	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Autowired
	private ICartManager cartManager;
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpSession session =  ThreadContextHolder.getSession();
		Map paymentAssignMap = (Map) session.getAttribute("paymentDlytypeSession");
		
		Member member = UserConext.getCurrentMember();
		MemberAddress address = new MemberAddress();
		if(member == null){
			return address;
		}else{
			Integer memberid = member.getMember_id();
			address = this.memberAddressManager.getMemberDefault(memberid);	//查询默认地址
		}
		
		Map map = paymentAssignMap;
		if(map==null){
			map = new HashMap();

			Double orderPrice = cartManager.countGoodsTotal(session.getId());
			Double weight = cartManager.countGoodsWeight(session.getId());
			Integer regionid = 1;
			if(address != null){
				regionid = address.getRegion_id();
			}
			List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice,regionid.toString());
			DlyType dlytype = dlyTypeList.get(0);
			
			map.put("paymentId", 0);	//支付方式	//0为在线支付
			map.put("dlytype", dlytype); 	//配送方式
			map.put("shipDay", "任意日期"); 	//送货日期
			
		}else{
			
		}
		
		return map;
	}

}

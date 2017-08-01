package com.enation.app.shop.component.ordercore.plugin.statecheck;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.shop.component.ordercore.plugin.setting.OrderSetting;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 收货后7*24小时没有评价的订单自动好评
 * @author LiFenLong
 *
 */
@Component
public class CommentCheckPlugin extends AutoRegisterPlugin implements IEveryDayExecuteEvent {
	
	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IMemberCommentManager memberCommentManager;
	
	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;
	
	@Override
	public void everyDay() {
		Integer time=OrderSetting.getComment_order_day()*24*60*60;
		if(SystemSetting.getTest_mode()==1){//如果测试模式开启 将时间改为60秒
			time=60;
		}
		String sql="SELECT tr.member_id,tr.goods_id from es_member_order_item mo INNER JOIN es_transaction_record tr ON mo.order_id=tr.order_id WHERE mo.commented=0  and tr.rog_time+?<? GROUP BY tr.member_id,tr.goods_id ";
		List<Map> list= daoSupport.queryForList(sql,time,DateUtil.getDateline());
		this.commentOrder(list);
		
	}
	/**
	 * 评论订单
	 * @param list
	 */
	private void commentOrder(List<Map> list){
		MemberComment memberComment = new MemberComment();
		
		for(Map map :list){
			Integer goods_id=Integer.parseInt(map.get("goods_id").toString()) ;
			Integer member_id= Integer.parseInt(map.get("member_id").toString());
			memberComment.setGoods_id(goods_id);
			if("b2c".equals(EopSetting.PRODUCT)){
				memberComment.setGrade(3);
			}else{
				memberComment.setGrade(5);
			}
			memberComment.setImg(null);
			memberComment.setMember_id(member_id );
			memberComment.setDateline(DateUtil.getDateline());
			memberComment.setType(1);
			memberComment.setContent("东西不错！我很喜欢！店主耐心！");
			memberComment.setStatus(1);
			memberCommentManager.add(memberComment);
			//更新为已经评论过此商品
			MemberOrderItem memberOrderItem = memberOrderItemManager.get(member_id,goods_id,0);
			if(memberOrderItem != null){
				memberOrderItem.setCommented(1);
				memberOrderItem.setComment_time(DateUtil.getDateline());
				memberOrderItemManager.update(memberOrderItem);
			}
		}
	}
}

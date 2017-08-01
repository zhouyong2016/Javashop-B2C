package com.enation.app.shop.core.order.model;

import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.NotDbField;


/**
 * 退货
 * @author kingapex；modify by dable
 *
 */
public class ReturnsOrder {
	private Integer id;//退换货单ID号
	private String ordersn;//订单SN号
	private Integer memberid;//会员ID号
	private Integer state;//退换货单状态
	private String goodsns;//货物sn
	private Integer type;//1是退货2是换货
	private Long add_time;//添加时间
	private String photo;//货物签收图片,库中存入的是路径
	private String refuse_reason;//客服拒绝原因
	private String apply_reason;//用户请求原因
	private String membername;//会员名字
	


	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getApply_reason() {
		return apply_reason;
	}
	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrdersn() {
		return ordersn;
	}
	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}
	public Integer getMemberid() {
		return memberid;
	}
	public void setMemberid(Integer memberid) {
		this.memberid = memberid;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getGoodsns() {
		return goodsns;
	}
	public void setGoodsns(String goodsns) {
		this.goodsns = goodsns;
	}
	public String getPhoto() {
		if(photo!=null&&!photo.equals("")){
			this.photo=StaticResourcesUtil.convertToUrl(photo);
		}
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getRefuse_reason() {
		return refuse_reason;
	}
	public void setRefuse_reason(String refuse_reason) {
		this.refuse_reason = refuse_reason;
	}
	
	//不会作为表的字段在查询时转换成类的属性
	@NotDbField
	public String getMembername() {
		return membername;
	}
	public void setMembername(String membername) {
		this.membername = membername;
	}
	public Long getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Long add_time) {
		this.add_time = add_time;
	}
	
}
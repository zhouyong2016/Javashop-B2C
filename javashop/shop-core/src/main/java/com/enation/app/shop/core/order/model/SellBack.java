package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 退货单
 * @author lina
 *2013-11-10上午10:16:43
 */
public class SellBack {
	
	private Integer id;				//ID
	private String tradeno;			//退货单号
	private Integer tradestatus;	//状态 0待审核。1.审核成功代发货.2.已入库。3.已完成。4.已取消。5.部分入库  6拒绝申请7.退款中 8.退款失败
	private int orderid; 			//订单id 2015-11-19 kingapex新增，因为在多处需要此参数， 还需要通过ordersn去取，所以新增此字段
	private String ordersn;			//订单号
	private String regoperator;		//操作员
	private Long regtime;			//创建时间
	private Double alltotal_pay;	//退款金额
	private Double apply_alltotal;	//申请退款金额
	private String goodslist;		//商品列表
	private String seller_remark;	//客服备注
	private String warehouse_remark;//库管备注
	private String finance_remark;	//财务备注
	private Integer member_id;		//退货人会员id
	private String sndto;			//退货人
	private String tel;				//电话
	private String adr;				//地址
	private String zip;				//邮编
	private Double total;			//总数
	private Integer depotid;		//仓库Id
	private String refund_way;		//退款方式
	private String return_account;	//退款账户
	private String remark;			//备注
	private String reason;			//退货（退款）原因
	private Integer type;			//1.退款 。2.退货
	private Long confirm_time;		//确认退货单时间
	private Integer gift_id;		//赠品id add_by DMRain 2016-7-19
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Integer getTradestatus() {
		return tradestatus;
	}
	public void setTradestatus(Integer tradestatus) {
		this.tradestatus = tradestatus;
	}
	public String getOrdersn() {
		return ordersn;
	}
	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}
	public String getRegoperator() {
		return regoperator;
	}
	public void setRegoperator(String regoperator) {
		this.regoperator = regoperator;
	}
	public Long getRegtime() {
		return regtime;
	}
	public void setRegtime(Long regtime) {
		this.regtime = regtime;
	}
	public String getGoodslist() {
		return goodslist;
	}
	public void setGoodslist(String goodslist) {
		this.goodslist = goodslist;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSeller_remark() {
		return seller_remark;
	}
	public void setSeller_remark(String seller_remark) {
		this.seller_remark = seller_remark;
	}
	public String getWarehouse_remark() {
		return warehouse_remark;
	}
	public void setWarehouse_remark(String warehouse_remark) {
		this.warehouse_remark = warehouse_remark;
	}
	public String getFinance_remark() {
		return finance_remark;
	}
	public void setFinance_remark(String finance_remark) {
		this.finance_remark = finance_remark;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getSndto() {
		return sndto;
	}
	public void setSndto(String sndto) {
		this.sndto = sndto;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public String getRefund_way() {
		return refund_way;
	}
	public void setRefund_way(String refund_way) {
		this.refund_way = refund_way;
	}
	public String getReturn_account() {
		return return_account;
	}
	public void setReturn_account(String return_account) {
		this.return_account = return_account;
	}
	public Double getAlltotal_pay() {
		return alltotal_pay;
	}
	public void setAlltotal_pay(Double alltotal_pay) {
		this.alltotal_pay = alltotal_pay;
	}
	public int getOrderid() {
		return orderid;
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Long getConfirm_time() {
		return confirm_time;
	}
	public void setConfirm_time(Long confirm_time) {
		this.confirm_time = confirm_time;
	}
	public Integer getGift_id() {
		return gift_id;
	}
	public void setGift_id(Integer gift_id) {
		this.gift_id = gift_id;
	}
	public Double getApply_alltotal() {
		return apply_alltotal;
	}
	public void setApply_alltotal(Double apply_alltotal) {
		this.apply_alltotal = apply_alltotal;
	}
}

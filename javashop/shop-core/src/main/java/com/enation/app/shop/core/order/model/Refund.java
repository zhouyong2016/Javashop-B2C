package com.enation.app.shop.core.order.model;

/**
 * 退款单
 * @author Kanon
 * @version v1.0,2016-6-6
 * @since v6.1
 */
public class Refund {

	private Integer id;	//id
	private String sn;	//编号
	private Integer sellback_id;	//售后服务id
	private Integer order_id;	//订单Id
	private Long create_time;	//创建时间 
	private Long refund_time;	//退款时间
	private Double refund_money;	//退款金额
	private String refund_way;		//退款方式
	private String return_account;	//退款账户
	private Integer member_id;	//会员Id
	private String sndto;		//退货人
	private String member_name;	//会员名称
	private String refund_user;	//退款操作人(管理员名称)
	private Integer status;	//退款状态 0，新建 1，已退款 新增状态2.退款中3.退款失败
	/** 支付类型 */
	private String refund_type;
	/** 新增退款交易发起时间，供中国银联使用 */
	private String txn_time;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getRefund_time() {
		return refund_time;
	}
	public void setRefund_time(Long refund_time) {
		this.refund_time = refund_time;
	}
	public Double getRefund_money() {
		return refund_money;
	}
	public void setRefund_money(Double refund_money) {
		this.refund_money = refund_money;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getRefund_user() {
		return refund_user;
	}
	public void setRefund_user(String refund_user) {
		this.refund_user = refund_user;
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
	public String getSndto() {
		return sndto;
	}
	public void setSndto(String sndto) {
		this.sndto = sndto;
	}
	public Integer getSellback_id() {
		return sellback_id;
	}
	public void setSellback_id(Integer sellback_id) {
		this.sellback_id = sellback_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getRefund_type() {
		return refund_type;
	}
	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}
	public String getTxn_time() {
		return txn_time;
	}
	public void setTxn_time(String txn_time) {
		this.txn_time = txn_time;
	}
	
}

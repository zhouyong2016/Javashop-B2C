package com.enation.app.shop.core.other.model;

/**
 * 促销活动实体类
 * 2016-5-23
 * @author DMRain
 * @version 1.0
 */
public class Activity {

	/** 促销活动ID */
	private Integer activity_id;
	
	/** 促销活动名称 */
	private String activity_name;
	
	/** 促销活动开始时间 */
	private Long start_time;
	
	/** 促销活动结束时间 */
	private Long end_time;
	
	/** 促销活动详细 */
	private String description;
	
	/** 参加促销活动商品形式 1：全部参加，2：部分参加 */
	private Integer range_type;
	
	/** 促销活动类型 1：满优惠(一级优惠)，2：多级优惠，3：单品立减优惠，4：第二件半价优惠 */
	private Integer activity_type;

	/** 是否禁用 0：否，1：是 */
	private Integer disabled;
	
	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public Long getStart_time() {
		return start_time;
	}

	public void setStart_time(Long start_time) {
		this.start_time = start_time;
	}

	public Long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRange_type() {
		return range_type;
	}

	public void setRange_type(Integer range_type) {
		this.range_type = range_type;
	}

	public Integer getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(Integer activity_type) {
		this.activity_type = activity_type;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

}

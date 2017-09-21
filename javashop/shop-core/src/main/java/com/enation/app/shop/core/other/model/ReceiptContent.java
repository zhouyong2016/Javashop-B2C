package com.enation.app.shop.core.other.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 
 * 发票内容实体类
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月30日 下午10:55:22
 */
public class ReceiptContent implements java.io.Serializable {
	/** 发票内容ID*/
	private Integer contentid;
	/** 发票内容名字 */
	private String receipt_content;

	public Integer getContentid() {
		return contentid;
	}

	public void setContentid(Integer contentid) {
		this.contentid = contentid;
	}

	public String getReceipt_content() {
		return receipt_content;
	}

	public void setReceipt_content(String receipt_content) {
		this.receipt_content = receipt_content;
	}

	


}
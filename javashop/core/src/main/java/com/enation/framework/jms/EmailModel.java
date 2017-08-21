package com.enation.framework.jms;

import java.util.HashMap;
import java.util.Map;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class EmailModel {

	//设置到模板中的变量，非数据库字段
	private Map data = new HashMap();
	
	private String title = "";
	
	private String email = "";
	
	//模板路径，非数据库字段
	private String template = "";

	private int email_id; //数据库主键
	private String email_type; //邮件类型
	private int is_success; //是否成功
	private int error_num;//错误次数
	private long last_send;//最后发送时间
	private String content;
	
	public EmailModel() {
	}

	public EmailModel(Map data, String title, String to, String template,String type) {
		super();
		this.data = data;
		this.title = title;
		this.email = to;
		this.template = template;
		this.email_type=type;
	}

	@NotDbField
	public Map getData() {
		return data;
	}

	public void setData(Map data) {
		this.data = data;
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotDbField
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}




	public String getEmail_type() {
		return email_type;
	}

	public void setEmail_type(String email_type) {
		this.email_type = email_type;
	}

	public int getError_num() {
		return error_num;
	}

	public void setError_num(int error_num) {
		this.error_num = error_num;
	}

	

	public long getLast_send() {
		return last_send;
	}

	public void setLast_send(long last_send) {
		this.last_send = last_send;
	}

	public void setLast_send(int last_send) {
		this.last_send = last_send;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@PrimaryKeyField
	public int getEmail_id() {
		return email_id;
	}

	public void setEmail_id(int email_id) {
		this.email_id = email_id;
	}

	public int getIs_success() {
		return is_success;
	}

	public void setIs_success(int is_success) {
		this.is_success = is_success;
	}

	
}

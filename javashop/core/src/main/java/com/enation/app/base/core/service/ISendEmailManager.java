package com.enation.app.base.core.service;

import com.enation.app.base.core.model.Smtp;
import com.enation.framework.jms.EmailModel;
/**
 * 
 * 发送email接口
 * @author jianghongyan
 * @version v1.0
 * @since v6.2
 * 2016年12月5日 上午11:06:01
 */
public interface ISendEmailManager {

	/**
	 * 通过java Transport发送邮件  支持ssl
	 * @param smtp  smtp配置
	 * @param emailModel 邮件内容的模型
	 * @return emailid 
	 * @throws Exception
	 */
	public int sendMailByTransport(Smtp smtp, EmailModel emailModel) throws Exception;
	/**
	 * 通过javamail 发送邮件 暂不支持ssl
	 * @param smtp  smtp配置
	 * @param emailModel 邮件内容的模型
	 * @return emailid 
	 * @throws Exception
	 */
	public int sendMailByMailSender(Smtp smtp, EmailModel emailModel) throws Exception;

}

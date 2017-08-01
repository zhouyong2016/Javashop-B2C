package com.enation.app.base.core.service.impl;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISendEmailManager;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 
 * 发送email实现类
 * @author jianghongyan
 * @version v1.0
 * @since v6.2
 * 2016年12月5日 上午11:06:31
 */
@Service
public class SendEmailManager implements ISendEmailManager{
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private IDaoSupport daoSupport;
	/**
	 * 通过java Transport 发送邮件
	 * 支持ssl发送
	 * @param smtp smtp配置实体
	 * @param emailModel 邮件数据模型
	 * @return emailid
	 * @throws Exception
	 */
	public int sendMailByTransport(final Smtp smtp, EmailModel emailModel) throws Exception {
		int emailid =0;
		
		Properties props = new Properties();
		props.put("mail.smtp.host", smtp.getHost());
		props.put("mail.smtp.port", String.valueOf(smtp.getPort()));
		props.put("mail.smtp.auth", "true");
		props.put("mail.transport.protocol", "smtp");
		
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.socketFactory.port", String.valueOf(smtp.getPort()));
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtps.ssl.protocols", "TSLv1 TSLv1.1 TLSv1.2");
		props.put("mail.smtp.ssl.ciphersuites","SSL_RSA_WITH_RC4_128_SHA SSL_RSA_WITH_RC4_128_MD5 TLS_RSA_WITH_AES_128_CBC_SHA TLS_DHE_RSA_WITH_AES_128_CBC_SHA TLS_DHE_DSS_WITH_AES_128_CBC_SHA SSL_RSA_WITH_3DES_EDE_CBC_SHA SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA SSL_RSA_WITH_DES_CBC_SHA SSL_DHE_RSA_WITH_DES_CBC_SHA SSL_DHE_DSS_WITH_DES_CBC_SHA SSL_RSA_EXPORT_WITH_RC4_40_MD5 SSL_RSA_EXPORT_WITH_DES40_CBC_SHA SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA TLS_EMPTY_RENEGOTIATION_INFO_SCSV");		

		
		
		Session sendMailSession = Session.getDefaultInstance(props,
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtp.getUsername(),
								smtp.getPassword());
					}
				});
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(smtp.getUsername(),smtp.getMail_from());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address to = new InternetAddress(emailModel.getEmail());
		mailMessage.setRecipient(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(emailModel.getTitle());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// 设置邮件消息的主要内容
		String mailContent = this.getEmailHtml(emailModel);
//		mailMessage.setText(mailContent);
		mailMessage.setContent(mailContent, "text/html;charset=utf-8");
		//向库中插入
		emailid =this.addEmailList(emailModel);
		// 发送邮件
		Transport.send(mailMessage);
		
	
		return emailid;
	}

	/**
	 * 通过spring提供的javaMailSender发送邮件
	 * 在支持ssl有些问题
	 * @param smtp  smtp配置实体
	 * @param emailModel 邮件数据模型
	 * @return emailid
	 * @throws Exception
	 */
	public int sendMailByMailSender(Smtp smtp,EmailModel emailModel) throws Exception {
		int emailid =0;
		//否则使用javaMailSender
		JavaMailSenderImpl javaMailSender=(JavaMailSenderImpl)mailSender;
		javaMailSender.setHost(smtp.getHost());
		javaMailSender.setUsername(smtp.getUsername());
		javaMailSender.setPassword(smtp.getPassword());
		
		
		MimeMessage message = mailSender.createMimeMessage();	
		MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
		
		//设置邮件标题
		helper.setSubject(emailModel.getTitle());
		
		//设置邮件 收件人
		helper.setTo(emailModel.getEmail());
		
		//设置发送者
		helper.setFrom( smtp.getMail_from());
	 	
		String html= this.getEmailHtml(emailModel);
		emailModel.setContent(html);
		
		helper.setText(html, true);
		
	
		//向库中插入
		emailid =this.addEmailList(emailModel);
		
		//发送邮件
		javaMailSender.send(message);
		
		return emailid;
	}


	private String getEmailHtml(EmailModel emailModel) throws Exception{
		Configuration cfg =FreeMarkerUtil.getCfg();
		
		String app_path= StringUtil.getRootPath();
		String pageFolder = app_path+"/themes/";
		cfg.setDirectoryForTemplateLoading(new File(pageFolder));
		
		Template temp = cfg.getTemplate(emailModel.getTemplate());
		ByteOutputStream stream = new ByteOutputStream();

		Writer out = new OutputStreamWriter(stream);
		
		//解析模板
		temp.process(emailModel.getData(), out);

		out.flush();
		String html = stream.toString();
		return html;
	}
	/**
	 * 向数据库中插入邮件队列，并返回邮件id
	 * @param emailModel
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private int addEmailList(EmailModel emailModel){
		emailModel.setIs_success(1); //默认假设成功
		emailModel.setLast_send(DateUtil.getDateline());
		this.daoSupport.insert("es_email_list", emailModel);
		return daoSupport.getLastId("es_email_list");
	}
	
 
}

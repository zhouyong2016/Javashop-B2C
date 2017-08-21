package com.enation.framework.jms;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISendEmailManager;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.framework.database.IDaoSupport;


/**
 * 
 * @author kingapex
 *
 */
@Service
public class EmailProcessor  implements IJmsProcessor {
	protected final Logger logger = Logger.getLogger(getClass());

	
	@Autowired
	private ISmtpManager smtpManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private ISendEmailManager sendEmailManager;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
		
		EmailModel emailModel = (EmailModel)data;
		int emailid =0;
		
		try {
			//获取当前的SMTP信息
			Smtp smtp = smtpManager.getCurrentSmtp();
			//如果启用ssl，或者使用qq邮箱,需要使用java提供的Transport发送邮件
			if(smtp.getOpen_ssl()==1||"smtp.qq.com".equals(smtp.getHost())){
				emailid=this.sendEmailManager.sendMailByTransport(smtp,emailModel);
			}else{
				emailid=this.sendEmailManager.sendMailByMailSender(smtp,emailModel);
			}
			this.smtpManager.sendOneMail(smtp);
		} catch (Exception e) {
			
			//如果发送失败，则记录
			if(emailid!=0){
				this.daoSupport.execute("update es_email_list set is_success=0,error_num=error_num+1 where email_id=?", emailid);
				
			}
			this.logger.error("发送邮件出错",e);
		}
	}

	
}

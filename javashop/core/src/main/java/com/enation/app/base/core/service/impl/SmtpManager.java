package com.enation.app.base.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISendEmailManager;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProcessor;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * smtp管理
 * @author kingapex
 * @date 2011-11-1 下午12:10:30 
 * @version V1.0
 */
@SuppressWarnings("unchecked")
@Service("smtpDbManager")
public class SmtpManager implements ISmtpManager {

	
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private ISendEmailManager sendEmailManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#add(com.enation.app.base.core.model.Smtp)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="添加了一个smtp，host为${smtp.host}")
	public void add(Smtp smtp) {
		this.daoSupport.insert("es_smtp", smtp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#edit(com.enation.app.base.core.model.Smtp)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改一个smtp信息，host为${smtp.host}")
	public void edit(Smtp smtp) {
		this.daoSupport.update("es_smtp", smtp,"id="+smtp.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除一个smtp信息")
	public void delete(Integer[] idAr) {
		
		if(idAr==null || idAr.length==0) return;
		String idstr = StringUtil.arrayToString(idAr, ",");
		
		this.daoSupport.execute("delete from es_smtp where id in("+idstr+")");
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#list()
	 */
	
	@Override
	public List<Smtp> list() {
		return this.daoSupport.queryForList("select * from es_smtp", Smtp.class);
	}

	@Override
	public void sendOneMail(Smtp currSmtp) {
		this.daoSupport.update("es_smtp", currSmtp, "id="+currSmtp.getId());
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#get(int)
	 */
	@Override
	public Smtp get(int id){
		return (Smtp) this.daoSupport.queryForObject("select * from es_smtp where id=?", Smtp.class, id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmtpManager#getCurrentSmtp()
	 */
	@Override
	public Smtp getCurrentSmtp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Log(type=LogType.SETTING,detail="发送测试邮件，邮件为${smtp.mail_from}")
	public void testSend(Smtp smtp,String send_to) throws Exception{
		EmailModel emailModel=new EmailModel();
		EopSite  site  =EopSite.getInstance();
		
		emailModel.setTitle("测试邮件--"+site.getSitename());
		emailModel.setEmail(send_to);
		emailModel.setTemplate("test-email-template.html");
		emailModel.setEmail_type("测试邮件");
		if(smtp.getId()!=null&&smtp.getId()!=0){
			if(StringUtil.isEmpty(smtp.getPassword())){
				Smtp smtp2=this.get(smtp.getId());
				smtp.setPassword(smtp2.getPassword());
			}
		}
		if(smtp.getOpen_ssl()==1||"smtp.qq.com".equals(smtp.getHost())){
			this.sendEmailManager.sendMailByTransport(smtp, emailModel);
		}else{
			this.sendEmailManager.sendMailByMailSender(smtp, emailModel);
		}
	}
}

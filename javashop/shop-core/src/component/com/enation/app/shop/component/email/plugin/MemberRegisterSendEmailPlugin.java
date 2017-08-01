package com.enation.app.shop.component.email.plugin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.member.plugin.IMemberRegisterEvent;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.RequestUtil;

/**
 * 会员注册发送邮件插件
 * @author kingapex
 *
 */
@Component
public class MemberRegisterSendEmailPlugin extends AutoRegisterPlugin implements
		IMemberRegisterEvent {
	
	@Autowired
	private EmailProducer mailMessageProducer;
	
	@Autowired
	private IMemberPointManger memberPointManger;

	@Override
	public void onRegister(Member member) {
		if (member != null && member.getEmail() != null) {
			this.sendRegEmail(member);
		}

	}
	/**
	 * 发送邮件
	 * @param member 会员
	 * checkurl 返回URL
	 * member.name 会员名称
	 * member.email 会员邮箱
	 * site.name 站点名称
	 * site.logo 站点LOGO
	 * 
	 */
	private void sendRegEmail(Member member){
		int sms_isopen = SystemSetting.getSms_reg_open();
		if(sms_isopen==0){
			String domain =RequestUtil.getDomain();
			EopSite site  =EopSite.getInstance();
			String checkurl  = domain+"/memberemailcheck.html?s="+ EncryptionUtil1.authcode(member.getMember_id()+","+member.getRegtime(), "ENCODE","",0);
			EmailModel emailModel = new EmailModel();
			emailModel.getData().put("username", member.getUname());
			emailModel.getData().put("checkurl", checkurl);
			emailModel.getData().put("sitename", site.getSitename());
			emailModel.getData().put("logo", site.getLogofile());
			emailModel.getData().put("domain", domain);
			
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_EMIAL_CHECK) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num");
				int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num_mp");
				emailModel.getData().put("point", point);
				emailModel.getData().put("mp", mp);
			}
			
			emailModel.setTitle(member.getUname()+"您好，"+site.getSitename()+"会员注册成功!");
			emailModel.setEmail(member.getEmail());
			emailModel.setTemplate("reg_email_template.html");
			emailModel.setEmail_type("注册成功提示");
			mailMessageProducer.send(emailModel);	
		}
	}
	
	

}

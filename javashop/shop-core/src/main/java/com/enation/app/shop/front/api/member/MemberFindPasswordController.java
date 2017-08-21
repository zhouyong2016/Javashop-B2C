package com.enation.app.shop.front.api.member;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.action.JsonResult;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * @author LiFenLong
 * 会员找回密码
 * @version v2.0,2016-2-20 6.0版本升级 wangxin  
 */

@Controller
@RequestMapping("/api/shop/findPasswordbyEmail")
public class MemberFindPasswordController {
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private EmailProducer mailMessageProducer;
	
	/**
	 * 找回密码
	 * @param choose 找回方式.0为邮箱1.用户名
	 * @param email 邮箱或者用户名
	 * @param email
	 */
	@ResponseBody
	@RequestMapping(value="/find",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult find(Integer choose,String email){
		//获取站点信息
		EopSite  site  =EopSite.getInstance();
		//验证邮箱
		
		//获取用户信息By Email
		Member member =	new Member();
		if(choose==0){
			if(email==null || !StringUtil.validEmail(email)){
				return JsonResultUtil.getErrorJson("请输入正确的邮箱地址");				

			}
			member=this.memberManager.getMemberByEmail(email);
		}else{
			if(email == null || StringUtil.isEmpty(email)){
				return JsonResultUtil.getErrorJson("请输入正确的用户名");				

			}
			member=this.memberManager.getMemberByUname(email);
		}
		if(member==null){
			return JsonResultUtil.getErrorJson("["+ email +"]不存在!");	
		}
		String domain =RequestUtil.getDomain();
		String initCode = member.getMember_id()+","+member.getRegtime();
		String edit_url  =domain+ "/findPassword.html?s="+ EncryptionUtil1.authcode(initCode, "ENCODE","",0);
		if(member.getEmail() == null){
			return JsonResultUtil.getErrorJson("用户邮箱地址为空!请完善个人用户信息");
		}
		//编辑邮件信息发送邮件
		EmailModel emailModel = new EmailModel();
		emailModel.getData().put("logo", site.getLogofile());
		emailModel.getData().put("sitename", site.getSitename());
		emailModel.getData().put("username", member.getUname());
		emailModel.getData().put("checkurl", edit_url);
		emailModel.setTitle("找回您的登录密码--"+site.getSitename());
		emailModel.setEmail(member.getEmail());
		emailModel.setTemplate("findpass_email_template.html");
		emailModel.setEmail_type("找回密码");
		mailMessageProducer.send(emailModel);
		
		this.memberManager.updateFindCode(member.getMember_id(),DateUtil.getDateline()+"");
		return JsonResultUtil.getSuccessJson("请登录"+ member.getEmail()+"查收邮件并完成密码修改。");

	}
	/**
	 * 修改密码
	 * @param password
	 * @param conpasswd
	 * @param s
	 */
	@ResponseBody
	@RequestMapping(value="/modify",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult modify(String email,String password,String conpasswd,String s){
		if(email==null || !StringUtil.validEmail(email)){
			return JsonResultUtil.getErrorJson("邮箱错误,请重试");				
		}
		if(s==null){
			return JsonResultUtil.getErrorJson("非法链接地址,请重新找回");				
		}
		String str = EncryptionUtil1.authcode(s, "DECODE","",0);
		String[] array = StringUtils.split(str,",");
		if(array.length!=2){
			return JsonResultUtil.getErrorJson("验证字串不正确,请重新找回");				
		}
		int memberid  = Integer.valueOf(array[0]);
		long regtime = Long.valueOf(array[1]);
		
		Member member = this.memberManager.get(memberid);
		if(member==null || member.getRegtime() != regtime){
			return JsonResultUtil.getErrorJson("验证字串不正确,请重新找回");				
		}
		if(member.getFind_code()==null||"".equals(member.getFind_code())||member.getFind_code().length()!=10){
			return JsonResultUtil.getErrorJson("地址已经过期,请重新找回");				
		}
		int time = Integer.parseInt(member.getFind_code())+60*60;
		if(DateUtil.getDateline()>time){
			return JsonResultUtil.getErrorJson("地址已经过期,请重新找回");				
		}
		if(!password.equals(conpasswd)){
			return JsonResultUtil.getErrorJson("密码不同");				
		}
		this.memberManager.updatePassword(memberid, password);
		this.memberManager.updateFindCode(memberid, "");
		this.memberManager.login(member.getUname(), password);
		return JsonResultUtil.getSuccessJson("修改密码成功");

	}
	
	
	
	
}

package com.enation.app.shop.front.api.member;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.base.core.util.SmsTypeKeyEnum;
import com.enation.app.base.core.util.SmsUtil;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;
/**
 * 会员API
 * @author Sylow
 * @version 2.1,2016-07-20
 */
@Controller
@RequestMapping("/api/shop/member")
@Scope("prototype")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MemberApiController  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MemberApiController.class);
	
	@Autowired
	private IMemberManager memberManager;
	@Autowired	
	private EmailProducer mailMessageProducer;
	@Autowired
	private IMemberPointManger memberPointManger;
	@Autowired
	private ISmsManager smsManager;
	
	

	/**
	 * 判断当前用户是否登录
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/is-login",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult isLogin() {
		Member member = UserConext.getCurrentMember();
		Map<String, Object> result = new HashMap<String, Object>();
		if (member != null) {
			
			result.put("state", 1);
			result.put("msg", "已经登录");
		} else {
			result.put("state", 0);
			result.put("msg", "未登录");
		}
		
		return JsonResultUtil.getObjectJson(result);
	}
	
	/**
	 * 新版登录逻辑，验证手机号成功以后若已注册则登录，若没登录跳转到填充资料页面
	 * @param mobile 手机号
	 * @param code 验证码
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="/sms-login",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult smsLogin(String mobile, String validcode) {
		try {
			
			boolean checkResult = SmsUtil.validSmsCode(validcode, mobile, SmsTypeKeyEnum.CHECK.toString());
			
			// 验证结果
			if (checkResult) {
				
				boolean isRegister = SmsUtil.validMobileIsRegister(mobile);
				
				Map<String, Object> result = new HashMap<String, Object>();
				
				// 如果注册了
				if (isRegister) {
					Member member = this.memberManager.loginByMobile(mobile);
					//手机登录时没有密码
					shiroLogin(member.getUname(), validcode);
					
					result.put("check_type", "login");
					
				// 如果没注册,加密账户信息，返回给前端跳转到填充信息页面
				} else {
					
					// 把注册信息 加密  放到session当中
					String ciphertext = EncryptionUtil1.authcode("{\"account_type\" : \"mobile\",\"account\" : \"" + mobile + "\"}", "ENCODE", "", 0);
					HttpServletRequest request = ThreadContextHolder.getHttpRequest();
					request.getSession().setAttribute("account_info", ciphertext);
					
					result.put("check_type", "register");
				}
				return JsonResultUtil.getObjectJson(result);
			} else {
				return JsonResultUtil.getErrorJson("验证码错误");
			}
		} catch(RuntimeException e) {
			TestUtil.print(e);
			LOGGER.debug(e.getMessage());
			return JsonResultUtil.getErrorJson(e.getMessage());
		}

	}
	
	
	/**
	 * 验证注册短信验证码
	 * @param mobile 手机号
	 * @param smsCode 验证码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/vali-register-sms-code",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult valiRegisterSmsCode(String mobile, String smsCode){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			if (SmsUtil.validSmsCode(smsCode, mobile, SmsTypeKeyEnum.REGISTER.toString())) {
				
				// 把注册信息 加密  放到session当中
				String ciphertext = EncryptionUtil1.authcode("{\"account_type\" : \"mobile\",\"account\" : \"" + mobile + "\"}", "ENCODE", "", 0);
				request.getSession().setAttribute("account_info", ciphertext);
				
				return JsonResultUtil.getSuccessJson("验证成功");
			} else {
				return JsonResultUtil.getErrorJson("验证失败：验证码错误");
			}
		} catch(RuntimeException e) {
			TestUtil.print(e);
			LOGGER.debug(e.getMessage());
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 会员登录
	 * @param username:用户名,String型
	 * @param password:密码,String型
	 * @param validcode:验证码,String型
	 * @param remember:两周内免登录,String型
	 * @return json字串
	 * result  为1表示登录成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/login",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult login(String validcode, String username, String password, String remember,Integer type,String mobile) {
		/**
		 * 由江宏岩修改
		 * 为支持手机号发送验证码登录
		 */
		if(type==null||type!=0){
			if (this.validcode(validcode,"memberlogin") == 1) {
				int result = this.memberManager.login(username, password);
				if (result == 1) {

					remember = "1"; //add_by Sylow 默认支持两周免登录

					// 两周内免登录
					if (remember != null && remember.equals("1")) {
						String cookieValue = EncryptionUtil1.authcode(
								"{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
								"ENCODE", "", 0);
						HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 24 * 14);
					}
					shiroLogin(username, password);
					return JsonResultUtil.getSuccessJson("登录成功");
				}else{
					return JsonResultUtil.getErrorJson("账号密码错误");				
				}
			} 
			return JsonResultUtil.getErrorJson("验证码错误！");	
		}else{
			try {
				if (SmsUtil.validSmsCode(validcode, mobile, SmsTypeKeyEnum.LOGIN.toString())) {
					Member member = this.memberManager.loginByMobile(mobile);
					if (member!=null) {
						
						remember = "1"; //add_by Sylow 默认支持两周免登录
						
						// 两周内免登录
						if (remember != null && remember.equals("1")) {
							String cookieValue = EncryptionUtil1.authcode(
									"{username:\"" + member.getUname() + "\",password:\"" + StringUtil.md5(member.getPassword()) + "\"}",
									"ENCODE", "", 0);
							HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 60 * 24 * 14);
						}
						//手机登录时没有密码
						shiroLogin(member.getUname(), validcode);
						return JsonResultUtil.getSuccessJson("登录成功");
					}else{
						return JsonResultUtil.getErrorJson("账号密码错误");				
					}
				} 
			} catch (Exception e) {
				return JsonResultUtil.getErrorJson(e.getMessage());	
			}
			return JsonResultUtil.getErrorJson("验证码错误！");	
		}
	}

	/**
	 * 通过Shiro登录，Shiro权限过滤器才能生效。
	 * @param username 用户名
	 * @param password 密码
	 */
	private void shiroLogin(String username, String password) {
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			Subject subject = SecurityUtils.getSubject();
			subject.login(token);
		} catch (AuthenticationException e) {
			LOGGER.error("Shiro subject login failed", e);
		}
	}

	/**
	 * 注销会员登录
	 * @param 无
	 * @return json字串
	 * result  为1表示注销成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/logout",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult logout() {
		/*this.showSuccessJson("注销成功");*/
		//如果用户注销异常 不返回首页问题
		try {
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", null, 0);
			this.memberManager.logout();
			//then shiro subject logout
			shiroLogout();
		} catch (Exception e) {
			LOGGER.error("会员注销时发生异常", e);
		}
		return JsonResultUtil.getSuccessJson("注销成功");
		/*return WWAction.JSON_MESSAGE;*/
	}

	/**
	 * 从Shiro中注销
	 */
	private void shiroLogout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			subject.logout();
		}
	}

	/**
	 * 修改会员密码
	 * @param oldpassword:原密码,String类型
	 * @param newpassword:新密码,String类型
	 * @return json字串
	 * result  为1表示修改成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/change-password",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult changePassword(String oldpassword, String newpassword, String re_passwd) {
		Member member = UserConext.getCurrentMember();
		if(member==null){
			return JsonResultUtil.getErrorJson("尚未登录，无权使用此api");				

		}
		String oldPassword = oldpassword;
		oldPassword = oldPassword == null ? "" : StringUtil.md5(oldPassword);
		if (oldPassword.equals(member.getPassword())) {
			String password = newpassword;
			String passwd_re = re_passwd;
			if(StringUtil.isEmpty(password)){
				return JsonResultUtil.getErrorJson("新密码不能为空");
			}
			if(oldpassword.equals(password)){ 				
				return JsonResultUtil.getErrorJson("您输入新旧密相同，请重新输入"); 			
				}
			if (passwd_re.equals(password)) {
				try {
					memberManager.updatePassword(password);
					return JsonResultUtil.getSuccessJson("修改密码成功");

				} catch (Exception e) {
					LOGGER.error("修改密码失败", e);
					return JsonResultUtil.getErrorJson("修改密码失败");				
				}
			} else {
				return JsonResultUtil.getErrorJson("修改失败！两次输入的密码不一致");				

			}
		} else {
			return JsonResultUtil.getErrorJson("修改失败！原始密码不符");							
		}
	}

	/**
	 * 修改会员登陆密码
	 * @author add by DMRain 2016-7-7
	 * @param newpassword 新密码
	 * @param re_passwd 再次输入新密码
	 * @param authCode 验证码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/update-password",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult updatePassword(String newpassword, String re_passwd, String authCode) {
		Member member = UserConext.getCurrentMember();
		//会员信息不能为空
		if (member == null) {
			return JsonResultUtil.getErrorJson("尚未登录，无权使用此api");				
		}
		
		String password = newpassword;
		String passwd_re = re_passwd;
		
		//新密码不能为空
		if(StringUtil.isEmpty(password)){
			return JsonResultUtil.getErrorJson("新密码不能为空");
		}
		
		//输入的新密码与原密码相同
		if (member.getPassword().equals(StringUtil.md5(password))) {
			return JsonResultUtil.getErrorJson("输入的新密码与原密码相同");
		}
		
		//再一次输入密码不能为空
		if(StringUtil.isEmpty(re_passwd)){
			return JsonResultUtil.getErrorJson("请再一次输入密码");
		}
		
		//两次输入的密码必须一致
		if (!passwd_re.equals(password)) {
			return JsonResultUtil.getErrorJson("两次输入的密码不一致");	
		}
		
		//验证码不能为空
		if(StringUtil.isEmpty(authCode)){
			return JsonResultUtil.getErrorJson("请输入验证码");
		}
		
		//验证验证码输入的是否正确
		if (this.validcode(authCode, "membervalid") == 0) {
			return JsonResultUtil.getErrorJson("验证码输入错误");
		}
		
		try {
			memberManager.updatePassword(password);
			return JsonResultUtil.getSuccessJson("修改密码成功");

		} catch (Exception e) {
			LOGGER.error("修改密码失败", e);
			return JsonResultUtil.getErrorJson("修改密码失败");				
		}
	}

	/**
	 * 会员手机验证
	 * @author add_by DMRain 2016-7-7
	 * @param mobileCode 手机校验码
	 * @param validcode 验证码
	 * @param mobile 手机号
	 * @param key 手机校验码key值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member-mobile-validate", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult memberMobileValidate(String mobileCode, String validcode, String mobile, String key){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Member member = UserConext.getCurrentMember();
		try {
			
			//会员信息不能为空
			if (member == null) {
				return JsonResultUtil.getErrorJson("尚未登录，无权使用此api");				
			}
			
			//手机校验码不能为空
			if(StringUtil.isEmpty(mobileCode)){
				return JsonResultUtil.getErrorJson("手机校验码不能为空");
			}
			
			//手机号不能为空
			if (StringUtil.isEmpty(mobile)) {
				return JsonResultUtil.getErrorJson("手机号不能为空");	
			}
			
			//验证手机校验码的key值不能为空
			if(StringUtil.isEmpty(key)){
				return JsonResultUtil.getErrorJson("出现错误，请重试！");
			}
			
			//验证码不能为空
			if(StringUtil.isEmpty(validcode)){
				return JsonResultUtil.getErrorJson("验证码不能为空");
			}
			
			if (this.validcode(validcode, "membervalid") == 0) {
				return JsonResultUtil.getErrorJson("验证码输入错误");
			}
			
			boolean result = SmsUtil.validSmsCode(mobileCode, mobile, key);
			
			//如果手机校验码错误
			if (!result) {
				return JsonResultUtil.getErrorJson("短信验证码错误");
			} else {
				
				//如果验证码输入错误
	//			if (this.validcode(validcode, "membervalid") == 0) {
	//				return JsonResultUtil.getErrorJson("验证码输入错误");
	//			} else {
	//				
					// 把注册信息 加密  放到session当中
					String ciphertext = EncryptionUtil1.authcode("{\"account_type\" : \"mobile\",\"account\" : \"" + mobile + "\"}", "ENCODE", "", 0);
					request.getSession().setAttribute("account_detail", ciphertext);
					
					//如果手机校验码的key值为binding
					if (key.equals("binding")) {
						this.memberManager.changeMobile(member.getMember_id(), mobile);
					}
					
					return JsonResultUtil.getSuccessJson("验证成功");
	//			}
			}
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 验证原密码输入是否正确
	 * @param oldpassword:密码，String型
	 * @return json字串
	 * result  为1表示原密码正确，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/password",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult password(String oldpassword){
		Member member = UserConext.getCurrentMember();
		String old = oldpassword;
		String oldPassword = StringUtil.md5(old);
		if (oldPassword.equals(member.getPassword())){
			return JsonResultUtil.getSuccessJson("正确");
		}else{
			return JsonResultUtil.getErrorJson("输入原始密码错误");				
		}
	}



	/**
	 * 搜索会员(要求管理员身份)
	 * @param lvid:会员级别id，如果为空则搜索全部会员级别，int型
	 * @param keyword:搜索关键字,可为空，String型
	 * @return json字串
	 * result  为1表示搜索成功，0表示失败 ，int型
	 * data: 会员列表
	 * {@link Member}
	 */
	@ResponseBody
	@RequestMapping(value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult search(Map memberMap, Integer lvid , String keyword){
		try{
			if(UserConext.getCurrentAdminUser()==null){
				return JsonResultUtil.getErrorJson("无权访问此api");				

			}
			memberMap = new HashMap();
			memberMap.put("lvId", lvid);
			memberMap.put("keyword", keyword);
			memberMap.put("stype", 0);
			List memberList  =this.memberManager.search(memberMap);
			if (memberList.size() == 0) {
				return JsonResultUtil.getErrorJson("未搜索到相关会员");
			} else {
				return JsonResultUtil.getObjectJson(memberList);
			}
		}catch(Throwable e){
			LOGGER.error("搜索会员出错", e);
			return JsonResultUtil.getErrorJson("搜索会员出错");						
		}
	}

	/**
	 * 检测username是否存在，并生成json返回给客户端
	 */
	@ResponseBody
	@RequestMapping(value="/checkname",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkname(String username) {
		int result = this.memberManager.checkname(username);
		if(result==0){
			return JsonResultUtil.getSuccessJson("会员名称可以使用！");
		}else{
			return JsonResultUtil.getErrorJson("该会员名称已经存在！");						
		}
	}

	/**
	 * 检测email是否存在，并生成json返回给客户端
	 */
	@ResponseBody
	@RequestMapping(value="/checkemail",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkemail(String email) {
		int result = this.memberManager.checkemail(email);
		if(result==0){
			return JsonResultUtil.getSuccessJson("邮箱不存在，可以使用");
		}else{
			return JsonResultUtil.getErrorJson("该邮箱已经存在！");	
		}

	}

	/**
	 * 检测mobile是否存在，并生成json返回给客户端
	 * @author add_by DMRain 2016-7-6
	 * @param mobile 手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkmobile",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkmobile(String mobile) {
		int result = this.memberManager.checkMobile(mobile);
		if(result == 0){
			return JsonResultUtil.getSuccessJson("手机号不存在，可以使用");
		}else{
			return JsonResultUtil.getErrorJson("该手机号已经存在！");	
		}

	}
	
	/**
	 * 用户登录修改信息检测邮箱是否存在
	 * 检测email是否存在，并生成json返回给客户端
	 * @param email
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkemailInEdit",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkemailInEdit(String email) {
		Member member = UserConext.getCurrentMember();
		boolean	flag = this.memberManager.checkemailInEdit(email, member.getMember_id());//true为可用，false不可用
		if(flag){
			return JsonResultUtil.getSuccessJson("邮箱不存在，可以使用");
		}else{
			return JsonResultUtil.getErrorJson("该邮箱已经存在！");	
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value="/reg-mobile",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult regMobile(String validcode, String license, String email, String username, String password, String mobile,String type){
		if(type!=null&&type.equals("1")){
			if(this.validcode(validcode,"memberreg")==0){
				return JsonResultUtil.getErrorJson("验证码输入错误!");				
			}
			
			try {
				// 适配b2c 的注册/ V62 PC注册流程改版
				HttpServletRequest request = ThreadContextHolder.getHttpRequest();
				String smsCode = request.getParameter("sms_code").toString();
				
				// 校验失败
				if (!SmsUtil.validSmsCode(smsCode, mobile, SmsTypeKeyEnum.REGISTER.toString())) {
					return JsonResultUtil.getErrorJson("短信验证码错误");
				}
			} catch(RuntimeException e) {
				return JsonResultUtil.getErrorJson(e.getMessage());
			}
		}
		

		if(this.memberManager.checkMobile(mobile)==1){
			return JsonResultUtil.getErrorJson("此手机号已注册，请更换手机号!");				

		}

		if (!"agree".equals(license)) {
			return JsonResultUtil.getErrorJson("同意注册协议才可以注册!");							
		}

		if (StringUtil.isEmpty(password)) {
			return JsonResultUtil.getErrorJson("密码不能为空！");							
		}

		Member member = new Member();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String registerip = request.getRemoteAddr();


		member.setMobile(mobile);
		member.setUname(username);
		member.setName(username);       //会员的uname及name分不清楚，暂时这2个字段在注册的时候使用同一个值
		member.setPassword(password);
		member.setEmail(email);
		member.setRegisterip(registerip);


		int result = memberManager.register(member);
		if (result == 1) { // 添加成功
			this.memberManager.login(username, password);
			ThreadContextHolder.getSession().removeAttribute("mobileCode");
			ThreadContextHolder.getSession().removeAttribute("account_info");
			return JsonResultUtil.getSuccessJson("注册成功");
		} else {
			return JsonResultUtil.getErrorJson("用户名[" + member.getUname() + "]已存在!");				
		}
	}

	/**
	 * 会员注册
	 * @version v1.2,2015-12-17 whj   会员的uname及name分不清楚，暂时这2个字段在注册的时候使用同一个值
	 */
	@ResponseBody
	@RequestMapping(value="/register",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult register(String validcode, String license, String email, String username, String password, String mobile, String friendid) {
		if (this.validcode(validcode,"memberreg") == 0) {
			return JsonResultUtil.getErrorJson("验证码输入错误!");				
		}
		if (!"agree".equals(license)) {
			return JsonResultUtil.getErrorJson("同意注册协议才可以注册!");				
		}

		Member member = new Member();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String registerip = request.getRemoteAddr();

		if (StringUtil.isEmpty(username)) {
			return JsonResultUtil.getErrorJson("用户名不能为空！");				
		}
//		if (StringUtil.isEmpty(mobile)) {
//			return JsonResultUtil.getErrorJson("手机号码不能为空！");				
//		}
		if (username.length() < 4 || username.length() > 20) {
			return JsonResultUtil.getErrorJson("用户名的长度为4-20个字符！");				
		}
		if (username.contains("@")) {
			return JsonResultUtil.getErrorJson("用户名中不能包含@等特殊字符！");				
		}
		if (StringUtil.isEmpty(email)) {
			return JsonResultUtil.getErrorJson("注册邮箱不能为空！");				
		}
		if (!StringUtil.validEmail(email)) {
			return JsonResultUtil.getErrorJson("注册邮箱格式不正确！");
		}
		if (StringUtil.isEmpty(password)) {
			return JsonResultUtil.getErrorJson("密码不能为空！");
		}
		if (memberManager.checkname(username) > 0) {
			return JsonResultUtil.getErrorJson("此用户名已经存在，请您选择另外的用户名!");
		}
		if (memberManager.checkemail(email) > 0) {
			return JsonResultUtil.getErrorJson("此邮箱已经注册过，请您选择另外的邮箱!");
		}

		member.setMobile(mobile);
		member.setUname(username);
		member.setName(username);       //会员的uname及name分不清楚，暂时这2个字段在注册的时候使用同一个值
		member.setPassword(password);
		member.setEmail(email);
		member.setRegisterip(registerip);
		if (!StringUtil.isEmpty(friendid)) {
			Member parentMember = this.memberManager.get(Integer.parseInt(friendid));
			if (parentMember != null) {
				member.setParentid(parentMember.getMember_id());
			}
		} else {
			// 不是推荐链接 检测是否有填写推荐人
			String reg_Recomm = request.getParameter("reg_Recomm");
			if (!StringUtil.isEmpty(reg_Recomm)	&& reg_Recomm.trim().equals(email.trim())) {
				return JsonResultUtil.getErrorJson("推荐人的邮箱请不要填写自己的邮箱!");
			}
			if (!StringUtil.isEmpty(reg_Recomm)	&& StringUtil.validEmail(reg_Recomm)) {
				Member parentMember = this.memberManager.getMemberByEmail(reg_Recomm);
				if (parentMember == null) {
					return JsonResultUtil.getErrorJson("您填写的推荐人不存在!");
				} else {
					member.setParentid(parentMember.getMember_id());
				}
			}
		}

		int result = memberManager.register(member);
		if (result == 1) { // 添加成功
			this.memberManager.login(username, password);
			return JsonResultUtil.getObjectJson("注册成功");

		} else {
			return JsonResultUtil.getErrorJson("用户名[" + member.getUname() + "]已存在!");				
		}
	}
	/**
	 * 重新发送激活邮件
	 */
	@ResponseBody
	@RequestMapping(value="/re-send-reg-mail",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult reSendRegMail(){
		try{
			//重新发送激活邮件
			Member member = UserConext.getCurrentMember();
			if(member == null){
				return JsonResultUtil.getErrorJson("请您先登录再重新发送激活邮件!");				
			}
			member = memberManager.get(member.getMember_id());
			if(member == null){
				return JsonResultUtil.getErrorJson("用户不存在,请您先登录再重新发送激活邮件!");				

			}
			if(member.getLast_send_email() != null && System.currentTimeMillis() / 1000 - member.getLast_send_email().intValue() < 2 * 60 * 60){
				return JsonResultUtil.getErrorJson("对不起，两小时之内只能重新发送一次激活邮件!");				

			}

			EopSite site  = EopSite.getInstance();
			String domain =RequestUtil.getDomain();
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
			emailModel.setEmail_type("邮箱激活");
			mailMessageProducer.send(emailModel);
			member.setLast_send_email(DateUtil.getDateline());
			memberManager.edit(member);
			return JsonResultUtil.getSuccessJson("激活邮件发送成功，请登录您的邮箱 " + member.getEmail() + " 进行查收！");

		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson(e.getMessage());				

		}
	}

	/**
	 * @param tel         // 固定电话
	 * @param file        // 头像
	 * @param truename    // 真实姓名
	 * @param zip         // 邮编
	 * @param mobile      // 电话
	 * @param sex         // 性别
	 * @param city        // 城市
	 * @param region      // 区
	 * @param town        //城镇
	 * @param email       // 邮件
	 * @param address     // 详细地址
	 * @param mybirthday  // 生日
	 * @param province_id // 省ID
	 * @param city_id     // 市ID
	 * @param region_id   // 区ID
	 * @param town_id     //城镇
	 * @param province    // 省份
	 * @return
	 * 修改人：whj 
	 * 修改时间：2016-09-18 
	 * 修改内容：对（mobile）字段为空的校验
	 */
	
	@ResponseBody
	@RequestMapping(value="/save-info",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult saveInfo(
			@RequestParam(value = "tel",  required = false) String tel,
			@RequestParam(value = "file", required = false) MultipartFile file, String truename, 
			@RequestParam(value = "zip",  required = false) String zip, String mobile, String sex, 
			String city, String region,String town, String email, String address, String mybirthday, Integer province_id, 
			Integer city_id, Integer region_id, Integer town_id,String province){
		Member member = UserConext.getCurrentMember();

		member = memberManager.get(member.getMember_id());


		//先上传图片
		String faceField = "faceFile";

		if(file!=null){
			//判断文件类型
			if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
				return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
			}

			//判断文件大小

			if(file.getSize() > 200 * 1024){
				return JsonResultUtil.getErrorJson("'对不起,图片不能大于200K！");				

			}
			InputStream stream=null;
			try {
				stream=file.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			IUploader uploader=UploadFacatory.getUploaer();
			String imgPath=	uploader.upload(stream, faceField,file.getOriginalFilename());
			member.setFace(imgPath);
		}

		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();

		if(StringUtil.isEmpty(mybirthday)){
			member.setBirthday(0L);
		}else{
			member.setBirthday(DateUtil.getDateline(mybirthday));
		}


		member.setProvince_id(province_id);
		member.setCity_id(city_id);
		member.setRegion_id(region_id);
		member.setTown_id(town_id);
		member.setProvince(province);
		member.setCity(city);
		member.setRegion(region);
        member.setTown(town);
		member.setEmail(email);
		member.setAddress(address);
		member.setZip(zip);
		
		/** 修改个人信息时，不允许直接修改手机号了 add_by DMRain 2016-7-21 */
//		if(mobile!=null){
//			member.setMobile(mobile);
//		}
		
		//判断会员电话字段是否为空
		if(StringUtil.isEmpty(member.getMobile())){
			if(!StringUtil.isEmpty(mobile)){
				member.setMobile(mobile);
			}
		}
		
		member.setTel(tel);

		//如果会员真实姓名不为空
		if(truename != null){
			member.setName(truename);
		}

		member.setSex(Integer.valueOf(sex));


		// 身份
		String midentity = request.getParameter("member.midentity");
		if (!StringUtil.isEmpty(midentity)) {
			member.setMidentity(StringUtil.toInt(midentity));
		} else {
			member.setMidentity(0);
		}
		
		try {
			// 判断否需要增加积分
			boolean addPoint = false;
			if (member.getInfo_full() == 0 && !StringUtil.isEmpty(member.getName())&&!StringUtil.isEmpty(member.getNickname()) && !StringUtil.isEmpty(member.getProvince())&& !StringUtil.isEmpty(member.getCity()) && !StringUtil.isEmpty(member.getRegion()) && !StringUtil.isEmpty(member.getTel())) {
				addPoint = true;
			}
			// 增加积分
			if (addPoint) {
				member.setInfo_full(1);
				memberManager.edit(member);
				if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_FINISH_PROFILE)) {
					int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num");
					int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num_mp");
					//添加会员的等级积分和消费积分 dongxin改
					memberPointManger.add(member, point,	"完善个人资料", null, 0,0);
					memberPointManger.add(member, 0,	"完善个人资料", null, mp,1);
				}
			} else {
				memberManager.edit(member);
			}
			return JsonResultUtil.getSuccessJson("编辑个人资料成功！");

		} catch (Exception e) {
			LOGGER.error("编辑个人资料失败！", e);
			return JsonResultUtil.getErrorJson("编辑个人资料失败！");				
		}
	} 




	/**
	 * 保存从Flash编辑后返回的头像，保存二次，一大一小两个头像
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-avatar",produces = MediaType.APPLICATION_JSON_VALUE)
	public String saveAvatar(String photoServer, String photoId, String type) {
		String targetFile = makeFilename("avatar",photoServer,photoId,type);

		int potPos = targetFile.lastIndexOf('/') + 1;
		String folderPath = targetFile.substring(0, potPos);
		FileUtil.createFolder(folderPath);

		try {
			File file = new File(targetFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			FileOutputStream dos = new FileOutputStream(file);
			int x = request.getInputStream().read();
			while (x > -1) {
				dos.write(x);
				x = request.getInputStream().read();
			}
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ("big".equals(type)) {
			Member member = UserConext.getCurrentMember();
			member.setFace("fs:/attachment/avatar/" + photoId + "_big."
					+ FileUtil.getFileExt(photoServer));
			memberManager.edit(member);
		}

		String json = "{\"data\":{\"urls\":[\"" + targetFile
				+ "\"]},\"status\":1,\"statusText\":\"保存存成功\"}";

		return json;
	}

	/**
	 * 上传头像文件
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upload-avatar",produces = MediaType.APPLICATION_JSON_VALUE)
	public String uploadAvatar(File faceFile, String faceFileName, @RequestParam(value = "face", required = false) MultipartFile face) {
		JSONObject jsonObject = new JSONObject();
		try {
			if (faceFile != null) {
				IUploader uploader=UploadFacatory.getUploaer();
				String file = uploader.upload(face.getInputStream(), "avatar",face.getOriginalFilename());
				Member member = UserConext.getCurrentMember();
				jsonObject.put("result", 1);
				jsonObject.put("member_id", member.getMember_id());
				jsonObject.put("url", toUrl(file));
				jsonObject.put("message", "操作成功！");
			}
		} catch (Exception e) {
			jsonObject.put("result", 0);
			jsonObject.put("message", "操作失败！");
		}

		/*this.json = jsonObject.toString();

		return WWAction.JSON_MESSAGE;*/
		String json =jsonObject.toString();
		return json;
	}

	//************to宏俊：以api先不用书写文档****************/
	protected String toUrl(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();
		String urlBase = static_server_domain;
		return path.replaceAll("fs:", urlBase);
	}

	protected String makeFilename(String subFolder, String photoServer, String photoId, String type ) {
		String ext = FileUtil.getFileExt(photoServer);
		String fileName = photoId + "_" + type + "." + ext;
		String static_server_path= SystemSetting.getStatic_server_path();

		String filePath = static_server_path + "/attachment/";
		if (subFolder != null) {
			filePath += subFolder + "/";
		}

		filePath += fileName;
		return filePath;
	}

	/**
	 * 校验验证码
	 * 
	 * @param validcode
	 * @param name (1、memberlogin:会员登录  2、memberreg:会员注册 3、membervalid:会员手机验证)
	 * @return 1成功 0失败
	 */
	private int validcode(String validcode,String name) {
		if (validcode == null) {
			return 0;
		}

		String code = (String) ThreadContextHolder.getSession().getAttribute(ValidCodeServlet.SESSION_VALID_CODE + name);

		if (code == null) {
			return 0;
		} else {
			if (!code.equalsIgnoreCase(validcode)) {
				return 0;
			}
		}
		return 1;
	}

	
	// 
	
}

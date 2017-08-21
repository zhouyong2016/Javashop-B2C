package com.enation.app.base.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.TestUtil;
import com.enation.framework.util.Validator;

/**
 * 短信相关通用方法
 * @author Sylow
 * @version v1.0,2016年7月6日
 * @since v6.1
 */
public class SmsUtil {

	// 短信验证码session前缀
	private static final String SMS_CODE_PREFIX = "es_sms_";
	
	// 短信验证间隔时间session前缀
	private static final String INTERVAL_TIME_PREFIX = "es_interval_";
	
	// 发送时间间隔
	private static final double SEND_INTERVAL = 60d;
	
	// 商标前缀
	private static final String BRAND_PREFIX = "Javashop";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);
	
	//短信超时时间前缀
	private static final String SENDTIME_PREFIX = "es_sendtime";
	//短信过期时间
	private static final Long SMS_CODE_TIMEOUT = 120l;
	
	/**
	 * 发送短信验证码
	 * @param mobile 手机号
	 * @param key 类型key枚举 {@link SmsTypeKeyEnum}
	 * @param isCheckRegister 是否判断已经注册  check用的
	 * @exception RuntimeException 发送短信程序出错异常
	 * @return 发送结果 Map<String, Object> 其中key=state_code值{0=发送失败，1=发送成功,2=发送限制(操作过快等等限制)},key=msg 值为{提示消息}
	 */
	public static Map<String, Object> sendMobileSms(String mobile, String key, int isCheckRegister) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			
			//防止 空值
			if (key == null || "".equals(key)) {
				
				// 默认为登录
				key = SmsTypeKeyEnum.LOGIN.toString();	
			}
			//如果手机号格式不对
			if (!Validator.isMobile(mobile) ) {
				result.put("state_code", 2);
				result.put("msg", "手机号码格式错误");
				return result;
			}
			
			// 判断是否允许可以发送
			if (!validIsCanSendSms(key)) {
				result.put("state_code", 2);
				result.put("msg", "您的操作过快，请休息一下");
				return result;
			}
			
			ISmsManager smsManager = SpringContextHolder.getBean("smsManager");
			
			//随机生成的动态码
			String dynamicCode = "" + (int)((Math.random() * 9 + 1) * 100000);
			
			//如果是测试模式，验证码为1111
			if(SystemSetting.getTest_mode()==1){
				dynamicCode="1111";
			}
			
			//动态码短信内容
			String smsContent = "" +  dynamicCode;
			
			// 1如果是登录
			if (key.equals(SmsTypeKeyEnum.LOGIN.toString())) {
				smsContent = "您的登录验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
				
				// 校验手机是否注册过  
				if (!validMobileIsRegister(mobile)) {
					result.put("state_code", 2);
					result.put("msg", "当前手机号没有绑定相关帐号");
					return result;
				}
				
			// 2如果是注册
			} else if (key.equals(SmsTypeKeyEnum.REGISTER.toString())) {
				smsContent = "您的注册验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
				
				// 校验手机是否注册过  
				if (validMobileIsRegister(mobile)) {
					result.put("state_code", 2);
					result.put("msg", "当前输入手机号码已绑定有帐号，可直接登录");
					return result;
				}
				
			// 3如果是找回密码
			} else if (key.equals(SmsTypeKeyEnum.BACKPASSWORD.toString())) {
				smsContent = "您正在尝试找回密码，验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
				
				// 校验手机是否注册过  
				if (!validMobileIsRegister(mobile)) {
					result.put("state_code", 2);
					result.put("msg", "当前手机号没有绑定相关帐号");
					return result;
				}
			
			// 4是绑定帐号
			} else if (key.equals(SmsTypeKeyEnum.BINDING.toString())) {
				smsContent = "您正在绑定手机号，验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
				
				// 校验手机是否注册过
				if (validMobileIsRegister(mobile)) {
					result.put("state_code", 2);
					result.put("msg", "当前输入手机号码已绑定有帐号，请解绑后再绑定");
					return result;
				}
				
			// 5是修改密码
			} else if (key.equals(SmsTypeKeyEnum.UPDATE_PASSWORD.toString())) {
				smsContent = "您正在修改密码，验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
				
				// 校验手机是否注册过
				if (!validMobileIsRegister(mobile)) {
					result.put("state_code", 2);
					result.put("msg", "没有找到该手机号绑定的账户");
					return result;
				}
			// 6是普通校验
			} else if (key.equals(SmsTypeKeyEnum.CHECK.toString())) {
				
				// 如果需要验证用户是否注册
				if (isCheckRegister == 1) {
					
					// 校验手机是否注册过
					if (!validMobileIsRegister(mobile)) {
						result.put("state_code", 2);
						result.put("msg", "没有找到该手机号绑定的账户");
						return result;
					}
				}
				
				smsContent = "您好，您的验证码为：" +  dynamicCode + ", 如非本人操作，请忽略本短信 【" + BRAND_PREFIX + "】";
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("code", dynamicCode);
			
			smsManager.send(mobile, smsContent, param);
			HttpSession session = ThreadContextHolder.getSession();
			
			// session中的格式是  前缀+key+手机号  例子:  es_sms_login_13123456789
			String codeSessionKey = SMS_CODE_PREFIX + key + mobile;
			session.setAttribute(codeSessionKey, dynamicCode);
			session.setAttribute(INTERVAL_TIME_PREFIX + key, DateUtil.getDateline());
			session.setAttribute(SENDTIME_PREFIX+key+mobile, DateUtil.getDateline());
			System.out.println(smsContent);
			
			String ip = ThreadContextHolder.getHttpRequest().getServerName();
			LOGGER.debug("已发送短信:内容:" + smsContent + ",手机号:" + mobile + ",ip:" + ip);
			System.out.println("已发送短信:内容:" + smsContent + ",手机号:" + mobile + ",ip:" + ip);
			
			result.put("state_code", 1);
			result.put("msg", "发送成功");
		} catch(RuntimeException e) {
			TestUtil.print(e);
			result.put("state_code", 0);
			result.put("msg", "发送失败,短信系统出现异常");
		}
		return result;
	}
	
	/**
	 * 验证手机验证码是否正确
	 * @param validCode 验证码
	 * @param mobile 手机号
	 * @param key key 类型key枚举 {@link SmsTypeKeyEnum}
	 * @exception RuntimeException 手机号格式错误出错
	 * @return
	 */
	public static boolean validSmsCode(String validCode, String mobile, String key) {
		// 如果手机号格式不对
		if ( !Validator.isMobile(mobile) ) {
			throw new RuntimeException("手机号码格式错误");
		}
		
		//防止 空值
		if (key == null || "".equals(key)) {
			
			// 默认为登录
			key = SmsTypeKeyEnum.LOGIN.toString();	
		}
		
		// 如果验证码为空
		if (validCode == null || "".equals(validCode)) {
			return false;
		}
		String code = (String) ThreadContextHolder.getSession().getAttribute(SMS_CODE_PREFIX + key + mobile);
		
		// 验证码为空
		if (code == null) {
			return false;
		} else {
			
			// 忽略大小写 判断  不正确
			if (!code.equalsIgnoreCase(validCode)) {
				return false;
			}
		}
		
		//新增优化  auth zjp 2016-12-13
		//验证短信是否超时
		Long sendtime = (Long) ThreadContextHolder.getSession().getAttribute(SENDTIME_PREFIX + key + mobile);
		Long checktime = DateUtil.getDateline();
		//验证session但中是否存在当前注册用户的验证码
		if(sendtime==null){
			return false;
		};
		if((checktime-sendtime >= SMS_CODE_TIMEOUT)){
			throw new RuntimeException("验证码超时");
		}	
		//验证通过后  去除session信息
		ThreadContextHolder.getSession().removeAttribute(SMS_CODE_PREFIX + key + mobile);
		return true;
	}
	
	/**
	 * 验证手机号有没有注册
	 * @param mobile 手机号
	 * @exception RuntimeException 手机号格式错误出错
	 * @return boolean false=没有注册 true=注册了
	 */
	public static boolean validMobileIsRegister(String mobile){
		
		// 如果手机号格式不对
		if ( !Validator.isMobile(mobile) ) {
			throw new RuntimeException("手机号码格式错误");
		}
		
		IMemberManager memberManager = SpringContextHolder.getBean("memberManager");
		boolean isExists = memberManager.checkMobile(mobile) != 0;
		return isExists;
	}
	
	/**
	 * 验证是否可以发送信息(做倒计时判断，同一种类型加以校验)
	 * @param key 类型key枚举 {@link SmsTypeKeyEnum}
	 * @return true=允许发送 false=不允许
	 */
	private static boolean validIsCanSendSms(String key){
		
		HttpSession session = ThreadContextHolder.getSession();
		
		// 当前时间
		Long now = DateUtil.getDateline();
		
		// session加上指定前缀
		Long lastGenTime = (Long) session.getAttribute(INTERVAL_TIME_PREFIX + key);	
		
		//如果lastGenTime不存在，即是第一次发送，允许发送；
		//如果发送间隔已超出限定间隔时间，允许发送；
		if (lastGenTime == null || CurrencyUtil.sub(now, lastGenTime) >= SEND_INTERVAL) {
			return true;
		} else {
			return false;
		}
		
	}
	
}

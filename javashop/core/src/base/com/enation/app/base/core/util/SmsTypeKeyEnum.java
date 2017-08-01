package com.enation.app.base.core.util;

/**
 * 发送短信类型枚举
 * @author Sylow
 * @version v1.0,2016年7月6日
 * @since v6.1
 */
public enum SmsTypeKeyEnum {
	
	//普通校验
	CHECK("check"),	
	//登录
	LOGIN("login"),		
	//绑定
	BINDING("binding"),
	//注册
	REGISTER("register"),
	//找回密码
	BACKPASSWORD("back_password"),
	//修改密码
	UPDATE_PASSWORD("update_password");
	
	// 构造方法
    private SmsTypeKeyEnum(String key) {
        this.key = key;
    }
	
    
    @Override
    public String toString(){
		return this.key;
	}
    
    private String key;
	
}

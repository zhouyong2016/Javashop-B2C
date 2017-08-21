package com.enation.app.base.core.plugin.sms;

import org.springframework.stereotype.Service;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;

@Service
public class SmsPluginBundle extends AutoRegisterPluginsBundle {
	
//	/**
//	 * 发送短信验证码事件
//	 */
//	public boolean onSendSms(String phone,String content,String code,Map map){
//		try{
//			List<IPlugin> plugins = this.getPlugins();
//			
//			if (plugins != null) {
//				for (IPlugin plugin : plugins) {
//					if (plugin instanceof ISmsSendEvent) {
//						if (loger.isDebugEnabled()) {
//							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 开始...");
//						}
//						ISmsSendEvent event = (ISmsSendEvent) plugin;
//						//event.onSend(phone, content,code, map);
//						if (loger.isDebugEnabled()) {
//							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 结束.");
//						}
//					} 
//				}
//			}
//		}catch(RuntimeException  e){
//			if(this.loger.isErrorEnabled())
//			this.loger.error("调用会员插件发送短信事件错误", e);
//			throw e;
//		}
//		return false;
//	}

	@Override
	public String getName() {
		return "短信插件桩";
	}
}

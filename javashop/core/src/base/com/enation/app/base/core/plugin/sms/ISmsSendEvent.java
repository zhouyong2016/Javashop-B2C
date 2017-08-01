package com.enation.app.base.core.plugin.sms;

import java.util.Map;

/**
 * 短信发送事件
 * @author xulipeng
 *
 */
public interface ISmsSendEvent {

	/**
	 * 发送短信事件
	 * @param phone		手机号
	 * @param content	发送内容
	 * @param param		其它参数
	 * @return
	 */
	public boolean onSend(String phone,String content,Map param);
}

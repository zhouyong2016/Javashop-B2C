package com.enation.app.base.jms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.framework.jms.EopJmsMessage;
import com.enation.framework.jms.EopProducer;


/**
 * 会员站内消息
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午1:35:40
 */
@Service
public class MessageFrontProducer {
	
	@Autowired
	private EopProducer eopProducer;
	
	
	public void send(Map<String,Object> infoMap) {
		EopJmsMessage jmsMessage = new EopJmsMessage();
		jmsMessage.setData(infoMap);
		jmsMessage.setProcessorBeanId("messageFrontProcessor");
		eopProducer.send(jmsMessage);
		
	}	
}

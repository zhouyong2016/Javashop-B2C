package com.enation.framework.jms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 日志记录生产者
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午10:12:58
 */
@Service
public class LogProducer {
	
	@Autowired
	private EopProducer eopProducer;
	
	/**
	 * 生产者生产数据	
	 * @param infoMap
	 * @throws Exception
	 */
	public void send(Map<String,Object> infoMap) throws Exception{
		EopJmsMessage jmsMessage = new EopJmsMessage();
		jmsMessage.setData(infoMap);
		jmsMessage.setProcessorBeanId("logProcessor");
		eopProducer.send(jmsMessage);
		
	}	
}

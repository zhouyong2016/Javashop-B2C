package com.enation.framework.jms;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * eop 
 * @author Kanon
 *
 */
@Service("eopProducer")
public class EopProducer {

	/**
	 * jms模板
	 */
	private JmsTemplate template;

	/**
	 * 队列
	 */
	private Queue destination;

	/**
	 * 发送
	 * @param eopJmsMessage jms消息
	 */
	public void send(IEopJmsMessage eopJmsMessage) {
		
		if(eopJmsMessage instanceof ITaskView){
			TaskContainer.pushTask((ITaskView) eopJmsMessage);
		}
		//解析，并且发送消息
		template.convertAndSend(this.destination, eopJmsMessage);
		
	}
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}
	
}

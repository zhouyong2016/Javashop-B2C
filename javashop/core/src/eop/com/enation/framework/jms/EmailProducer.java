package com.enation.framework.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mailMessageProducer")
public class EmailProducer {
	
	@Autowired
	private EopProducer eopProducer;
	
	
	public void send(EmailModel emailModel) {
		
		EopJmsMessage jmsMessage = new EopJmsMessage();
		jmsMessage.setData(emailModel);
		jmsMessage.setProcessorBeanId("emailProcessor");
		eopProducer.send(jmsMessage);
		
	}	
}

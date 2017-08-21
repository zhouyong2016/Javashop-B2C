package com.enation.framework.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;



/**
 * Eop消息转换器
 * @author kingapex
 *
 */
public class EopMessageConverter implements MessageConverter {
	
	@Override
	public Object fromMessage(Message msg) throws JMSException,MessageConversionException {
		if (msg instanceof ObjectMessage) {
			return ((Map) ((ObjectMessage) msg).getObjectProperty("Map")).get("eop_message");
		} else {
			throw new JMSException("Msg:[" + msg + "] is not Map");
		}
	}

	@Override
	public Message toMessage(Object obj, Session session) throws JMSException,MessageConversionException {
		if (obj instanceof IEopJmsMessage) {
			ActiveMQObjectMessage objMsg = (ActiveMQObjectMessage) session.createObjectMessage();
			Map<String, IEopJmsMessage> map = new HashMap<String, IEopJmsMessage>();
			map.put("eop_message", (IEopJmsMessage) obj);
			objMsg.setObjectProperty("Map", map);
			return objMsg;
		} else {
			throw new JMSException("Object:[" + obj + "] is not Member");
		}
	}

}

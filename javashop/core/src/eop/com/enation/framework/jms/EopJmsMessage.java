package com.enation.framework.jms;

import com.enation.eop.sdk.context.EopContext;


/**
 * jsm信息处理器
 * @author kingapex
 *
 */  
public class EopJmsMessage implements IEopJmsMessage {

	private Object data;
	private String beanid;
	private EopContext context;
	
	
	public EopJmsMessage(){
		context= EopContext.getContext();
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public String getProcessorBeanId() {
		return beanid;
	}
	
	public void setData(Object _data){
		data = _data;
	}
	
	public void setProcessorBeanId(String _beanid){
		this.beanid= _beanid;
	}

	@Override
	public EopContext getEopContext() {
		return context;
	}
}

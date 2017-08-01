package com.enation.framework.jms;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.ILogCreator;
import com.enation.framework.log.LogCreatorFactory;
import com.enation.framework.model.Log;
import com.enation.framework.model.LogStore;
import com.enation.framework.util.DateUtil;

/**
 * 日志记录消费者
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午10:14:25
 */
@Service
public class LogProcessor   implements IJmsProcessor{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.jms.IJmsProcessor#process(java.lang.Object)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
		Map map = (Map)data;
		String type = (String) map.get("type");//类型标识
		String detail = (String) map.get("detail");//操作描述
		JoinPoint point = (JoinPoint) map.get("point");
		AdminUser adminUser = (AdminUser) map.get("admin_user");
		String memberName = (String) map.get("member_name");
		Integer memberId = (Integer) map.get("member_id");
		Object obj = map.get("store_id");
		Integer storeId ;
		Map  valuesMap = new HashMap ();
		//找出所有表达式
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(detail);
		
		while (matcher.find()) {
			 String ex = matcher.group(1);
			 Object value;
			try {
				value = this.getValue(point, ex);
				valuesMap.put(ex, value);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			 
		}
		detail = this.resolvedDetail(detail, valuesMap);
		if(adminUser != null){
			Log log = new Log();
			log.setLog_detail(detail);
			log.setLog_time(DateUtil.getDateline());
			log.setLog_type(type);
			log.setOperator_id(adminUser.getUserid());
			log.setOperator_name(adminUser.getUsername());
			daoSupport.insert("es_admin_logs", log);
		}
		
		if(obj != null){
			storeId = (Integer)obj;
			LogStore logStore = new LogStore();
			logStore.setLog_detail(detail);
			logStore.setLog_time(DateUtil.getDateline());
			logStore.setLog_type(type);	
			logStore.setOperator_id(memberId);
			logStore.setOperator_name(memberName);
			logStore.setStore_id(storeId);
			daoSupport.insert("es_store_logs", logStore);
		}
	}
	
	/**
	 * 得到参数中ex参数名的值
	 * @param point
	 * @param ex
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private Object getValue(JoinPoint point,String ex) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String resultStr = ex ;
		CodeSignature signature = (CodeSignature)point.getSignature();
		String[] ar = ex.split("\\.");
		String[] paramsName = signature.getParameterNames();
		String objectName =  ar[0];
		int argpos = this.findArgPos(paramsName, objectName); //参数位置
		if(argpos != -1){//参数中没有找到
			if(ar.length==2){//对象.属性格式
				String propertyName = ar[1];
				Class clz = signature.getParameterTypes()[argpos]; //参数class
				//判断是否找到这个对象类
				if(clz == null){
					return resultStr;
				}
				Object obj = point.getArgs()[argpos];
				PropertyDescriptor sourcePd  =BeanUtils.getPropertyDescriptor(clz,  propertyName);
				//判断此类中是否有这个属性
				if (sourcePd == null) {
					return resultStr;
				}
				Method readMethod = sourcePd.getReadMethod();
				//判断此类中该属性是否有get方法
				if (readMethod == null || !Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())){
					return resultStr;
				}
				Object value = readMethod.invoke(obj);
				return value == null ? "" : value;
			}else{
				Object value = point.getArgs()[argpos];
				return value;
			}
		}
		return resultStr;
	}
	
	/**
	 * 将${}里面的内容替换相应的值
	 * @param templateString 需要替换${}的字符串
	 * @param valuesMap 对应${}里的map
	 * @return
	 */
	private String resolvedDetail(String templateString,Map valuesMap){
		 StrSubstitutor sub = new StrSubstitutor(valuesMap);
		 String resolvedString = sub.replace(templateString);
		 return resolvedString;
	}
	
	/**
	 * 根据参数名，查找参数位置
	 * @param paramsName
	 * @param objectName
	 * @return
	 */
	private int findArgPos(String[] paramsName,String objectName){
		 for (int i = 0; i < paramsName.length; i++) {
			if(paramsName[i].equals(objectName)){
				return i;
			}
		}
		 return -1;
	}
	
}

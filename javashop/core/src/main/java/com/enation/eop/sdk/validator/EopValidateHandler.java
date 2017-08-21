package com.enation.eop.sdk.validator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
/**
 * 
 * Eop 参数校验处理类
 * @author jianghongyan
 * @version v1.0
 * @since v6.2
 * 2016年12月9日 上午12:00:53
 */
@ControllerAdvice
public class EopValidateHandler {
	/**
	 * 处理单个参数校验
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public JsonResult handleValidationException(ConstraintViolationException e){
		for(ConstraintViolation<?> s:e.getConstraintViolations()){
			return JsonResultUtil.getErrorJson(s.getInvalidValue()+": "+s.getMessage());
		}
		return JsonResultUtil.getErrorJson("请求参数不合法");
	}
	/**
	 * 处理实体类校验
	 * @param e
	 * @return
	 */
	@ExceptionHandler(BindException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public JsonResult handleValidationBeanException(BindException e){
		for(ObjectError s:e.getAllErrors()){
			return JsonResultUtil.getErrorJson(s.getObjectName()+": "+s.getDefaultMessage());
		}
		return JsonResultUtil.getErrorJson("请求参数不合法");
	}
	
}

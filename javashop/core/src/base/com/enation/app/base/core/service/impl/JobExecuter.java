package com.enation.app.base.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.base.core.plugin.job.JobExecutePluginsBundle;
import com.enation.app.base.core.service.IJobExecuter;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;

/**
 * 任务执行器
 * @author kingapex
 *
 */ 
public class JobExecuter implements IJobExecuter {

	@Autowired
	private JobExecutePluginsBundle jobExecutePluginsBundle;

	@Autowired
	private IComponentManager componentManager;


	@Override
	public void everyHour() {
		Integer isOpen=checkClusterIsOpen();
		if(isOpen == 0){
			jobExecutePluginsBundle.everyHourExcecute();
		}
	}

	@Override 
	public void everyDay() {
		try{
			Integer isOpen=checkClusterIsOpen();
			if(isOpen == 0){
				this.jobExecutePluginsBundle.everyDayExcecute();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void everyMonth() {
		Integer isOpen=checkClusterIsOpen();
		if(isOpen == 0){
			this.jobExecutePluginsBundle.everyMonthExcecute();
		}

	}

	private Integer checkClusterIsOpen(){
		ComponentView componentView= componentManager.getCmptView("集群组件");
		if(componentView==null||componentView.getEnable_state()!=1){
			return 0;
		}
		return 1;
	}


}

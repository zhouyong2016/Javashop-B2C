/**
 * 
 */
package com.enation.app.base.core.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.TaskProgress;
import com.enation.app.base.core.service.ProgressContainer;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 进度action
 * @author kingapex
 *2015-5-13
 */

@Controller
@RequestMapping("/core/admin/progress")
public class ProgressController {

	/**
	 * 检测是否有任务正在进行
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/has-task")
	public Object hasTask(String progressid){
		
		if( StringUtil.isEmpty(progressid) ){
			return JsonResultUtil.getErrorJson("progressid 不能为空"+this);
		}
		
		
		int hastask = ProgressContainer.getProgress(progressid)==null?0:1;
		return JsonResultUtil.getNumberJson("hastask", hastask);
	}
	
	
	/**
	 * 查看生成进度
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="view-progress")
	public Object viewProgress(String progressid){
		 
		if( StringUtil.isEmpty(progressid) ){
			return JsonResultUtil.getErrorJson("progressid 不能为空"+this);
		}
		
		TaskProgress taskProgress =  ProgressContainer.getProgress(progressid);
		
		if( taskProgress== null ){
			System.out.println("is null");
			taskProgress= new TaskProgress(100);
		}
		
		if(taskProgress.getTask_status()!=0){ //出错 或成功了，移除此次任务
			ProgressContainer.remove(progressid);
		}
		return JsonMessageUtil.getObjectJson(taskProgress);
		
	}
}

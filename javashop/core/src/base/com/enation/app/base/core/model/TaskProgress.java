/**
 * 
 */
package com.enation.app.base.core.model;

import java.math.BigDecimal;

import com.enation.app.base.core.service.ProgressContainer;


/**
 * 任务进度
 * @author kingapex
 *2015-5-13
 */
public class TaskProgress {
	
	private String id;
	private  double sum_per; //百分比
	private double step_per; //每步占比
	private  String text; //正在生成的内容
	private  int task_status=0;//生成状态 0:正在生成，1:生成完成，2：生成出错
	private  int task_total; //任务总数
	
	
	/**
	 * 构造时要告诉任务总数，以便计算每步占比
	 * @param total
	 */
	public  TaskProgress(int total){
		
		//计算每步的百分比
		this.task_total =total;
		BigDecimal b1 = new BigDecimal("100");
		BigDecimal b2 = new BigDecimal(""+task_total);
		step_per = b1.divide(b2,5,BigDecimal.ROUND_HALF_UP).doubleValue();
		
	}
	
	/**
	 * 完成一步
	 */
	public void step(String text){
		
		this.sum_per += this.step_per;
		this.text= text;
	}
 
	/**
	 * 成功
	 */
	public void success(){
		this.sum_per=100;
		this.text="完成";
		this.task_status=1;
	//	ProgressContainer.remove(this.id);
	}
	
	/**
	 * 失败
	 * @param text
	 */
	public void fail(String text){
		
		this.task_status=2;
		this.text= text;
	//	ProgressContainer.remove(this.id);
	}
 
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public  int getTask_status() {
		return task_status;
	}
	public  void setTask_status(int task_status) {
		this.task_status = task_status;
	}
	public int getTask_total() {
		return task_total;
	}
	public void setTask_total(int task_total) {
		this.task_total = task_total;
	}

	public int getSum_per() {
		return Double.valueOf(sum_per).intValue();
	}

	public void setSum_per(int sum_per) {
		this.sum_per = sum_per;
	}

	public double getStep_per() {
		return step_per;
	}

	public void setStep_per(double step_per) {
		this.step_per = step_per;
	}

	public void setId(String id) {
		this.id = id;
	} 
	
	
	
}

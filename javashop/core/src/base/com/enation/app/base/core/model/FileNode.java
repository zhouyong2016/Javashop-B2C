package com.enation.app.base.core.model;

import com.enation.framework.util.StringUtil;

/**
 * 资源管理器的一个文件实体
 * @author kingapex
 * 2010-8-18下午12:06:40
 */
public class FileNode {
	private static final String[] IMAGE_EXT= {"jpg","gif","bmp","png"}; //声明图片的扩展名
	private String name;
	private Long size;
	private Long lastmodify;
	private Integer isfolder; //是否是文件夹,1是，0否
	private String content; //文件内容
	private String ext;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Long getLastmodify() {
		return lastmodify;
	}
	public void setLastmodify(Long lastmodify) {
		this.lastmodify = lastmodify;
	}
	public Integer getIsfolder() {
		return isfolder;
	}
	public void setIsfolder(Integer isfolder) {
		this.isfolder = isfolder;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public int getIsImage(){
		if( StringUtil.isEmpty(this.ext)){
			return 0;
		}
		
		for(  String imgExt :IMAGE_EXT){
			if(imgExt.equals(this.ext)){
				return 1;
			}
		}
		
		return 0;
		
	}
	
 
}

package com.enation.eop.resource.model;

import java.util.List;

import com.enation.framework.database.NotDbField;

/**
 * @author lzf
 *         <p>
 *         created_time 2009-11-13 下午02:42:25
 *         </p>
 * @version 1.0
 */
public class Menu extends Resource {

	private Integer pid;
	private String title;
	private String url;
	private String target;
	private Integer sorder;
	private Integer menutype;
	private String datatype;
	private String appid;
	
	/**针对演示站点的配置  是否限制演示站点的查看  0=不限制 1=限制*/
	private int is_display; //add _by Sylow 2016-04-30

	/* 是否可以导出，默认是不导出在profile.xml中
	 解决组件安装的菜单也会被导入在profile.xml中
	 add by kingapex 2012-08-03
	 */
	private int canexp = 0;

	/*  添加了菜单icon
	 add by kingapex 2013-06-07
	 */
	private String icon;
	private String icon_hover;
	
	private List<Menu> children;
	private boolean hasChildren;
	
	private String state;

	@NotDbField
	public boolean getHasChildren() {
		hasChildren = this.children == null || this.children.isEmpty() ? false
				: true;
		return hasChildren;
	}

	public static final int MENU_TYPE_SYS = 1;
	public static final int MENU_TYPE_APP = 2;
	public static final int MENU_TYPE_EXT = 3;

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	private Integer selected;

	public Integer getSelected() {
		return selected;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Integer getSorder() {
		return sorder;
	}

	public void setSorder(Integer sorder) {
		this.sorder = sorder;
	}

	public Integer getMenutype() {
		return menutype;
	}

	public void setMenutype(Integer menutype) {
		this.menutype = menutype;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public int getCanexp() {
		return canexp;
	}

	public void setCanexp(int canexp) {
		this.canexp = canexp;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon_hover() {
		return icon_hover;
	}

	public void setIcon_hover(String icon_hover) {
		this.icon_hover = icon_hover;
	}

	@NotDbField
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getIs_display() {
		return is_display;
	}

	public void setIs_display(int is_display) {
		this.is_display = is_display;
	}


}

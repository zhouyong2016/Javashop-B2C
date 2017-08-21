package com.enation.app.shop.core.member.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.PluginTab;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;


/**
 * 会员插件桩 
 * @author wangxin
 * @date 2016-2-18  
 * @version V2.0
 */
@Service("memberPluginBundle")
public class MemberPluginBundle extends AutoRegisterPluginsBundle {
	
	/**
	 * 激发退出事件
	 */
	public void onLogout(Member member){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberLogoutEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " onLogout 开始...");
						}
						IMemberLogoutEvent event = (IMemberLogoutEvent) plugin;
						event.onLogout(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onLogout 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用会员插件注销事件错误", e);
			throw e;
		}
	}
	
	
	
	/**
	 * 激发登录事件
	 */
	public void onLogin(Member member,Long upLogintime){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberLoginEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onLogin 开始...");
						}
						IMemberLoginEvent event = (IMemberLoginEvent) plugin;
						event.onLogin(member,upLogintime);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onLogin 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用会员插件登录事件错误", e);
			throw e;
		}
	}
	
	
	
	
	/**
	 * 激发注册事件
	 */
	public void onRegister(Member member){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberRegisterEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 开始...");
						}
						IMemberRegisterEvent event = (IMemberRegisterEvent) plugin;
						event.onRegister(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用会员插件注册事件错误", e);
			throw e;
		}
	}
	
	

	
	/**
	 * 激发邮件校验事件
	 */
	public void onEmailCheck(Member member){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberEmailCheckEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 开始...");
						}
						IMemberEmailCheckEvent event = (IMemberEmailCheckEvent) plugin;
						event.onEmailCheck(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onRegister 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用会员插件邮件验证事件错误", e);
			throw e;
		}
	}


	
	/**
	 * 激发更新密码事件
	 * @param password
	 * @param memberid
	 */
	public void onUpdatePassword(String password,int memberid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberUpdatePasswordEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onUpdatePassword 开始...");
						}
						IMemberUpdatePasswordEvent event = (IMemberUpdatePasswordEvent) plugin;
						event.updatePassword(password, memberid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onUpdatePassword 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
				this.loger.error("调用会员更新密码事件错误", e);
			throw e;
		}
	}
	
	
	

	
	

	/**
	 * 获取各个插件的html
	 * 
	 */
	public  List<PluginTab>   getDetailHtml(Member member) {
		
		List<PluginTab> list = new ArrayList<PluginTab>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("member",member);
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IMemberTabShowEvent) {
					IMemberTabShowEvent event = (IMemberTabShowEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
						
					/**
					 * 如果插件返回不执行，则跳过此插件
					 */
					if(!event.canBeExecute(member)){
						continue;
					}
					String tabName = event.getTabName(member);
					PluginTab tab = new PluginTab();
					
					tab.setTabTitle(tabName);
					tab.setOrder(event.getOrder());
					tab.setTabHtml(event.onShowMemberDetailHtml(member));
					
					list.add(tab);
					
				}
			}
		}
		
		//对tablist进行排序列
		PluginTab.sort(list);
		return list;
	}
	
	
	public  void onAddressAdd(MemberAddress address){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberAddressAddEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 开始...");
						}
						IMemberAddressAddEvent event = (IMemberAddressAddEvent) plugin;
						event.addressAdd(address);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
				this.loger.error("调用会员添加地址事件错误", e);
			throw e;
		}
	}		
	/**
	 * 激发会员恢复事件
	 */
	public  void onRecycle(Member member){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberRecycleEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 开始...");
						}
						IMemberRecycleEvent event = (IMemberRecycleEvent) plugin;
						event.recycleMember(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
				this.loger.error("调用会员添加地址事件错误", e);
			throw e;
		}
	}		
	/**
	 * 激发会员删除事件
	 */
	public  void onDeleteMember(Member member){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberDeleteEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 开始...");
						}
						IMemberDeleteEvent event = (IMemberDeleteEvent) plugin;
						event.delteMember(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAddressAdd 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
				this.loger.error("调用会员添加地址事件错误", e);
			throw e;
		}
	}		
	
	public void onComment(MemberComment comment) {
		try {
			List<IPlugin> plugins = this.getPlugins();

			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberCommentEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " onMemberComment 开始...");
						}
						IMemberCommentEvent event = (IMemberCommentEvent) plugin;
						event.onMemberComment(comment);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " onAddressAdd 结束.");
						}
					}
				}
			}
		} catch (RuntimeException e) {
			if (this.loger.isErrorEnabled())
				this.loger.error("调用会员添加地址事件错误", e);
			throw e;
		}
	}
	
	/**
	 * 激发会员修改事件
	 * @param comment
	 */
	public void onEdit(Member member) {
		try {
			List<IPlugin> plugins = this.getPlugins();

			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IMemberEditEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " onEditMember 开始...");
						}
						IMemberEditEvent event = (IMemberEditEvent) plugin;
						event.onEditMember(member);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " onEditMember 结束.");
						}
					}
				}
			}
		} catch (RuntimeException e) {
			if (this.loger.isErrorEnabled())
				this.loger.error("调用会员修改事件错误", e);
			throw e;
		}
	}
	
	@Override
	public String getName() {
		return "会员插件桩";
	}
	
}

package com.enation.app.shop.core.member.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.plugin.MemberPluginBundle;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员管理
 * 
 * @author kingapex 2010-4-30上午10:07:24
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Service("memberManager")
public class MemberManager  implements IMemberManager {
	@Autowired
	private IDaoSupport  daoSupport;

	@Autowired
	protected IMemberLvManager memberLvManager;   //改造完成

	@Autowired
	private MemberPluginBundle memberPluginBundle; //改造完成

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#logout()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void logout() {

		Member member = UserConext.getCurrentMember();
		member = this.get(member.getMember_id());
		ThreadContextHolder.getSession().removeAttribute(UserConext.CURRENT_MEMBER_KEY);
		this.memberPluginBundle.onLogout(member);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#register(com.enation.app.base.core.model.Member)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int register(Member member) {
		int result = add(member);
		try {
			if (result == 1) {
				this.memberPluginBundle.onRegister(member);
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#add(com.enation.app.base.core.model.Member)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.MEMBER,detail="添加一个${member.uname}会员")
	public int add(Member member) {
		if (member == null)
			throw new IllegalArgumentException("member is null");
		if (member.getUname() == null)
			throw new IllegalArgumentException("member' uname is null");
		if (member.getPassword() == null)
			throw new IllegalArgumentException("member' password is null");

		if (this.checkname(member.getUname()) == 1) {
			return 0;
		}
		if(member.getLv_id()==null||member.getLv_id()==0){
			Integer lvid = memberLvManager.getDefaultLv();
			member.setLv_id(lvid);
		}

		//如果会员昵称为空，就将会员登陆用户名设置为昵称	修改人:DMRain 2015-12-16
		if(member.getNickname() == null){
			member.setNickname(member.getUname());
		}


		member.setPoint(0);
		member.setAdvance(0D);

		if(member.getRegtime()==null){
			member.setRegtime(DateUtil.getDateline());
		}

		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(0);
		member.setPassword(StringUtil.md5(member.getPassword()));

		// Dawei Add
		member.setMp(0);
		member.setFace("");
		member.setMidentity(0);
		if(member.getSex()==null){
			member.setSex(1);	//新注册用户性别默认为'男'
		}


		this.daoSupport.insert("es_member", member);
		int memberid = this.daoSupport.getLastId("es_member");
		member.setMember_id(memberid);

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#checkEmailSuccess(com.enation.app.base.core.model.Member)
	 */
	@Override
	public void checkEmailSuccess(Member member) {

		int memberid = member.getMember_id();
		String sql = "update es_member set is_cheked = 1 where member_id =  " + memberid;
		this.daoSupport.execute(sql);
		this.memberPluginBundle.onEmailCheck(member);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#get(java.lang.Integer)
	 */
	@Override
	public Member get(Integer memberId) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where member_id=? AND m.disabled!=1";
		Member m = this.daoSupport.queryForObject(sql, Member.class, memberId);
		return m;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#getMember(java.lang.Integer)
	 */
	@Override
	public Map getMember(Integer memberId) {
		String sql="select * from es_member where member_id=?";
		List list = this.daoSupport.queryForList(sql, memberId);
		if(!list.isEmpty()){
			Map map = (Map) list.get(0);
			return map;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#getMemberByUname(java.lang.String)
	 */
	@Override
	public Member getMemberByUname(String uname) {
		String sql = "select * from es_member where uname=? AND disabled!=1";
		List list = this.daoSupport.queryForList(sql, Member.class, uname);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#getMemberByEmail(java.lang.String)
	 */
	@Override
	public Member getMemberByEmail(String email) {
		String sql = "select * from es_member where email=? AND disabled!=1";
		List list = this.daoSupport.queryForList(sql, Member.class, email);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#edit(com.enation.app.base.core.model.Member)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="修改了用户名为${member.uname}的会员信息")
	public Member edit(Member member) {
		// 前后台用到的是一个edit方法，请在action处理好
		this.daoSupport.update("es_member", member, "member_id=" + member.getMember_id());
		Integer memberpoint = member.getPoint();
		
		//xulipeng  增加修改插件
		this.memberPluginBundle.onEdit(member);

		//改变会员等级
		if(memberpoint!=null ){
			MemberLv lv =  this.memberLvManager.getByPoint(memberpoint);
			if(lv!=null ){
				if((member.getLv_id()==null ||lv.getLv_id().intValue()>member.getLv_id().intValue())){
					this.updateLv(member.getMember_id(), lv.getLv_id());
				} 
			}
		}
		Member currentMember = UserConext.getCurrentMember();
		//FIXME 如果当前Session中，从前台登录了该用户，应从Session中移除，给出提示告知需重新登录。
		if ( currentMember != null && currentMember.getMember_id().equals(member.getMember_id()) ) {
			//ThreadContextHolder.getSession().removeAttribute(UserConext.CURRENT_MEMBER_KEY);
			//这里不能直接将member直接放入session  因为这个member不是从数据库查出的 有很多字段为null 而从sql中查出的Integer会封装成0
			String sql = "select m.*,l.name as lvname from "
					+ "es_member m left join "
					+ "es_member_lv"
					+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.member_id=?";
			
			Member newMember=this.daoSupport.queryForObject(sql, Member.class, member.getMember_id());
			
			ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, newMember);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#checkname(java.lang.String)
	 */
	@Override
	public int checkname(String name) {
		String sql = "select count(0) from es_member where uname=?";
		int count = this.daoSupport.queryForInt(sql, name);
		count = count > 0 ? 1 : 0;
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#checkemail(java.lang.String)
	 */
	@Override
	public int checkemail(String email) {
		String sql = "select count(0) from es_member where email=? AND disabled!=1";
		int count = this.daoSupport.queryForInt(sql, email);
		count = count > 0 ? 1 : 0;
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#checkMobile(java.lang.String)
	 */
	@Override
	public int checkMobile(String mobile) {
		String sql = "select count(0) from es_member where mobile=? AND disabled!=1";
		int count = this.daoSupport.queryForInt(sql, mobile);
		count = count > 0 ? 1 : 0;
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="删除会员")
	public void delete(Integer[] id) {
		if (id == null){
			return;
		}
		String str="";
		for (int i = 0; i < id.length; i++) {
			str+="?,";
		}
		str=str.substring(0,str.length()-1);
		String sql = "update es_member set disabled=1 where member_id in (" + str + ")";
		this.daoSupport.execute(sql,id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#updatePassword(java.lang.String)
	 */
	@Override
	public void updatePassword(String password) {
		//获取当前登录的会员信息
		Member member = UserConext.getCurrentMember();

		//修改密码
		this.updatePassword(member.getMember_id(), password);

		//将修改后的密码以MD5方式加密set进会员信息中
		member.setPassword(StringUtil.md5(password));

		//将会员信息重新set进session中
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		//修改密码成功后，删除session中的加密信息 add_by DMRain 2016-7-11
		ThreadContextHolder.getSession().removeAttribute("account_detail");

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#updatePassword(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void updatePassword(Integer memberid, String password) {
		String md5password = password == null ? StringUtil.md5("") : StringUtil.md5(password);
		String sql = "update es_member set password = ? where member_id =? ";
		this.daoSupport.execute(sql, md5password, memberid);
		this.memberPluginBundle.onUpdatePassword(password, memberid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#updateFindCode(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void updateFindCode(Integer memberid, String code) {
		String sql = "update es_member set find_code = ? where member_id =? ";
		this.daoSupport.execute(sql, code, memberid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#login(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username, String password) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.uname=? and password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ "es_member m left join "
					+ "es_member_lv"
					+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.email=? and password=?";
		}

		String pwdmd5 = com.enation.framework.util.StringUtil.md5(password);
		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username, pwdmd5);
		if (list == null || list.isEmpty()) {
			//如果没有查到用户 可能是使用手机号码＋密码登录
			sql = "select m.*,l.name as lvname from "
					+ "es_member m left join "
					+ "es_member_lv"
					+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.mobile=? and password=?";
			list=this.daoSupport.queryForList(sql, Member.class, username, pwdmd5);
			if(list == null || list.isEmpty()){
				return 0;
			}
		}
		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		//		HttpCacheManager.sessionChange();
		this.memberPluginBundle.onLogin(member, upLogintime);
		

		return 1;
	}



	@Override
	public Member loginByMobile(String mobile) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.mobile=? ";
		// 用户名中包含@，说明是用邮箱登录的


		List<Member> list = this.daoSupport.queryForList(sql, Member.class, mobile);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		//		HttpCacheManager.sessionChange();
		this.memberPluginBundle.onLogin(member, upLogintime);

		return member;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#login(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.uname=? ";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ "es_member m left join "
					+ "es_member_lv"
					+ " l on m.lv_id = l.lv_id where m.email=?";
		}

		List<Member> list = this.daoSupport.queryForList(sql, Member.class, username);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		//		HttpCacheManager.sessionChange();
		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#loginWithCookie(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int loginWithCookie(String username, String password) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.uname=? and password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ "es_member m left join "
					+ "es_member_lv"
					+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.email=? and password=?";
		}
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username, password);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#loginbysys(java.lang.String)
	 */
	@Override
	public int loginbysys(String username) {

		if (UserConext.getCurrentAdminUser()==null) {
			throw new RuntimeException("您无权进行此操作，或者您的登录已经超时");
		}

		String sql = "select m.*,l.name as lvname from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where m.disabled!=1 and m.uname=?";
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		//		HttpCacheManager.sessionChange();
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#addMoney(java.lang.Integer, java.lang.Double)
	 */
	@Override
	public void addMoney(Integer memberid, Double num) {
		String sql = "update es_member set advance=advance+? where member_id=?";
		this.daoSupport.execute(sql, num, memberid);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#cutMoney(java.lang.Integer, java.lang.Double)
	 */
	@Override
	public void cutMoney(Integer memberid, Double num) {
		Member member = this.get(memberid);
		if (member.getAdvance() < num) {
			throw new RuntimeException("预存款不足:需要[" + num + "],剩余["
					+ member.getAdvance() + "]");
		}
		String sql = "update es_member set advance=advance-? where member_id=?";
		this.daoSupport.execute(sql, num, memberid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#searchMember(java.util.Map, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public Page searchMember(Map memberMap, Integer page, Integer pageSize,String other,String order) {
		String sql = createTemlSql(memberMap);
		if(other != null && order != null){
			sql+=" order by "+other+" "+order;
		}else{
			sql+=" order by m.member_id desc";
		}
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#searchMemberNoShop(java.util.Map, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public Page searchMemberNoShop(Map memberMap, Integer page,
			Integer pageSize, String other, String order) {
		String sql = createTemlSqlNoShop(memberMap);
		if(other != null && order != null){
			sql+=" order by "+other+" "+order;
		}
		//		System.out.println(sql);
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#search(java.util.Map)
	 */
	@Override
	public List<Member> search(Map memberMap) {
		String sql = createTemlSql(memberMap);
		return this.daoSupport.queryForList(sql, Member.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#updateLv(int, int)
	 */
	@Override
	public void updateLv(int memberid, int lvid) {
		String sql = "update es_member set lv_id=? where member_id=?";
		this.daoSupport.execute(sql, lvid, memberid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#getMemberByMobile(java.lang.String)
	 */
	@Override
	public Member getMemberByMobile(String mobile) {
		String sql = "select * from es_member where mobile=? and disabled!=1";
		List list = this.daoSupport.queryForList(sql, Member.class, mobile);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	/**
	 * 
	 * @param memberMap
	 * @return
	 */
	private String createTemlSql(Map memberMap){

		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String uname =(String) memberMap.get("uname");
		String mobile = (String) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");


		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");


		String sql = "select m.*,lv.name as lv_name from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " lv on m.lv_id = lv.lv_id where 1=1 and m.disabled!='1' ";

		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.uname like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%'";
				sql+=" or m.name like '%"+keyword+"%')";
			}
		}

		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}

		if (uname != null && !uname.equals("")) {
//			sql += " and m.name like '%" + uname + "%'";
			sql += " and m.uname like '%" + uname + "%'";
		}
		if(mobile!=null&&!mobile.equals("")){
			sql += " and m.mobile like '%" + mobile + "%'";
		}

		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}

		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}

		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		return sql;
	}

	/**
	 * 
	 * @param memberMap
	 * @return
	 */
	private String createTemlSqlNoShop(Map memberMap){

		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String uname =(String) memberMap.get("uname");
		String mobile = (String) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");


		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");


		String sql = "select m.*,lv.name as lv_name from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " lv on m.lv_id = lv.lv_id where 1=1 and m.disabled!='1' ";

		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.uname like '%"+keyword+"%'";
				sql+=" or m.name like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%')";
			}
		}

		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}

		if (uname != null && !uname.equals("")) {
			sql += " and m.name like '%" + uname + "%'";
			sql += " or m.uname like '%" + uname + "%'";
		}
		if(mobile!=null){
			sql += " and m.mobile like '%" + mobile + "%'";
		}

		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}

		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}

		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		sql+=" and m.member_id NOT IN(select member_id from es_store s)";
		return sql;
	}

	@Override
	public Member getDisabled(Integer member_id) {
		String sql = "select m.*,l.name as lvname from "
				+ "es_member" + " m left join "
				+ "es_member_lv"
				+ " l on m.lv_id = l.lv_id where member_id=?";
		Member m = this.daoSupport.queryForObject(sql, Member.class, member_id);
		return m;
	}

	@Override
	public boolean checkemailInEdit(String email, Integer member_id) {
		// TODO Auto-generated method stub
		String sql="select * from es_member m where m.email=? and m.member_id!=? and m.disabled!=1";
		List list=this.daoSupport.queryForList(sql, email,member_id);
		return list.isEmpty();
	}

	@Override
	public Member getByGoodsId(int goodsId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from es_member m ")
		.append("left join es_goods g on m.store_id = g.store_id ")
		.append("where g.goods_id = ?");
		return (Member) this.daoSupport.queryForObject(sql.toString(), Member.class, goodsId);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#changeMobile(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void changeMobile(Integer member_id, String mobile) {
		String sql = "update es_member set mobile = ? where member_id = ?";
		this.daoSupport.execute(sql, mobile, member_id);
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, this.get(member_id));

		//修改手机验证成功后，删除session中的加密信息
		ThreadContextHolder.getSession().removeAttribute("account_detail");
	}
	/**
	 * 查询所有会员回收站的会员信息
	 */
	@Override
	public Page searchMemberRecycle(Integer page, Integer pageSize) {
		String sql="select m.*,lv.name as lv_name from "
				+ "es_member m left join "
				+ "es_member_lv"
				+ " lv on m.lv_id = lv.lv_id where disabled=1 order by member_id desc";
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	/**
	 * 恢复会员信息
	 * member_id 会员id
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="恢复会员信息")
	public void regain(Integer[] member_id) {
		String str="";
		for (int i = 0; i < member_id.length; i++) {
			str+="?,";
		}
		str=str.substring(0,str.length()-1);
		String sql="  update es_member set disabled=0 where member_id in ("+ str +")";
		this.daoSupport.execute(sql,member_id);
	}
	/**
	 * 根据member_id查询会员信息
	 * member_id 会员id
	 */
	@Override
	public Member getMemberByMemberId(Integer member_id) {
		String sql="select * from es_member where member_id=?";
		Member member=this.daoSupport.queryForObject(sql, Member.class, member_id);
		return member;
	}

	@Override
	public int checkMobileExceptSelf(String mobile, Integer member_id) {
		String sql = "select count(0) from es_member where mobile=? AND disabled!=1 AND member_id!=?";
		int count = this.daoSupport.queryForInt(sql, mobile,member_id);
		count = count > 0 ? 1 : 0;
		return count;
	}

	@Override
	public void addPoint(int memberid, int value) {
		Member member = this.get(memberid);
		member.setPoint(member.getPoint()+value);
		member.setMp(member.getMp()+value);
		this.daoSupport.update("es_member", member, " member_id = "+member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IMemberManager#editMemberImg(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void editMemberImg(Integer member_id, String img) {
		String sql = "update es_member set face=? where member_id=?";
		this.daoSupport.execute(sql, img,member_id);
	}




}

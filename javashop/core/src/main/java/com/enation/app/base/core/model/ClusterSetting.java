package com.enation.app.base.core.model;

import java.io.Serializable;
import java.util.Map;

import com.enation.app.base.core.service.ISettingService;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.StringUtil;

public class ClusterSetting implements Serializable {

	private String scheme;

	private static String domain;				//服务器主域名
	private	static String order;				//订单
	private	static Integer orderEnabled = 0;	//订单服务器域名
	private static String goods;				//商品
	private	static Integer goodsEnabled = 0;	//商品服务器域名
	private static String member;				//会员
	private static Integer memberEnabled = 0;	//会员服务器域名

	private static String api;						//API服务器主域名
	private static String orderApi;					//订单api
	private static Integer orderApiEnabled = 0;		//订单API域名
	private static String goodsApi;					//商品api
	private static Integer goodsApiEnabled = 0;		//商品API域名
	private static String memberApi;				//会员api
	private static Integer memberApiEnabled = 0;	//会员API节点

	public static final String cluster_key="cluster"; //系统设置中的分组
	private static int cluster_cache =0; //缓存方案
	private static String solr;//solr配置
	private static int session_open=0;//是否开启session
	private static int fdfs_open=0; //是否开启文件分发
	private static int solr_open=0;//是否开启solr
	private static int static_page_open=0; //是否开启静态页生成




	/**
	 * 集群设置缓存更新
	 */
	public static void load(){
		ISettingService settingService= SpringContextHolder.getBean("settingDbService");
		Map<String,String> clusters = settingService.getSetting(cluster_key);
		if(clusters!=null){
			cluster_cache=Integer.parseInt(clusters.get("cache"));
			session_open=Integer.parseInt(clusters.get("session_open"));
			solr_open=Integer.parseInt(clusters.get("solr_open"));
			solr=clusters.get("solr");
			domain=clusters.get("domain");
			order=clusters.get("order");
			member=clusters.get("member");
			goods=clusters.get("goods");
			static_page_open=Integer.parseInt(clusters.get("static_page_open"));
			if(clusters.get("orderEnabled")!=null){
				orderEnabled=Integer.valueOf(clusters.get("orderEnabled"));
			}
			if(clusters.get("goodsEnabled")!=null){
				goodsEnabled=Integer.valueOf(clusters.get("goodsEnabled"));
			}
			if(clusters.get("memberEnabled")!=null){
				memberEnabled=Integer.valueOf(clusters.get("memberEnabled"));
			}

			api=clusters.get("api");
			orderApi=clusters.get("orderApi");
			goodsApi=clusters.get("goodsApi");
			memberApi=clusters.get("memberApi");
			if(clusters.get("orderApiEnabled")!=null){
				orderApiEnabled=Integer.parseInt(clusters.get("orderApiEnabled"));
			}
			if(clusters.get("goodsApiEnabled")!=null){
				goodsApiEnabled=Integer.parseInt(clusters.get("goodsApiEnabled"));
			}
			if(clusters.get("memberApiEnabled")!=null){
				memberApiEnabled=Integer.parseInt(clusters.get("memberApiEnabled"));
			}
			String  fdfs_open_str = clusters.get("fdfs_open");
			fdfs_open= StringUtil.toInt(fdfs_open_str,0);
		}


	}


	public static ClusterSetting defaultSetting(String defaultDomain){
		ClusterSetting clusterSetting = new ClusterSetting();
		clusterSetting.setDomain(defaultDomain);
		clusterSetting.setApi(clusterSetting.getDomain());
		clusterSetting.setOrder(clusterSetting.getDomain());
		clusterSetting.setGoods(clusterSetting.getDomain());
		clusterSetting.setMember(clusterSetting.getDomain());
		clusterSetting.setOrderApi(clusterSetting.getDomain());
		clusterSetting.setGoodsApi(clusterSetting.getDomain());
		clusterSetting.setMemberApi(clusterSetting.getDomain());
		clusterSetting.setSolr("");
		return clusterSetting;
	}

	
	
	public static int getStatic_page_open() {
		return static_page_open;
	}


	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}

	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}

	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}

	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getOrderApi() {
		return orderApi;
	}
	public void setOrderApi(String orderApi) {
		this.orderApi = orderApi;
	}

	public String getGoodsApi() {
		return goodsApi;
	}
	public void setGoodsApi(String goodsApi) {
		this.goodsApi = goodsApi;
	}

	public String getMemberApi() {
		return memberApi;
	}
	public void setMemberApi(String memberApi) {
		this.memberApi = memberApi;
	}
	public Integer getOrderEnabled() {
		return orderEnabled;
	}
	public void setOrderEnabled(Integer orderEnabled) {
		this.orderEnabled = orderEnabled;
	}
	public Integer getGoodsEnabled() {
		return goodsEnabled;
	}
	public void setGoodsEnabled(Integer goodsEnabled) {
		this.goodsEnabled = goodsEnabled;
	}
	public Integer getMemberEnabled() {
		return memberEnabled;
	}
	public void setMemberEnabled(Integer memberEnabled) {
		this.memberEnabled = memberEnabled;
	}
	public Integer getOrderApiEnabled() {
		return orderApiEnabled;
	}
	public void setOrderApiEnabled(Integer orderApiEnabled) {
		this.orderApiEnabled = orderApiEnabled;
	}
	public Integer getGoodsApiEnabled() {
		return goodsApiEnabled;
	}
	public void setGoodsApiEnabled(Integer goodsApiEnabled) {
		this.goodsApiEnabled = goodsApiEnabled;
	}
	public Integer getMemberApiEnabled() {
		return memberApiEnabled;
	}
	public void setMemberApiEnabled(Integer memberApiEnabled) {
		this.memberApiEnabled = memberApiEnabled;
	}

	public String getSolr() {
		return solr;
	}

	public void setSolr(String solr) {
		this.solr = solr;
	}
	public static int getCluster_cache() {

		return cluster_cache;
	}
	public static int getSession_open() {
		return session_open;
	}
	public static void setSession_open(int session_open) {
		ClusterSetting.session_open = session_open;
	}
	public static int getFdfs_open(){
		return fdfs_open;
	}
	public static int getSolr_open() {
		return solr_open;
	}



}

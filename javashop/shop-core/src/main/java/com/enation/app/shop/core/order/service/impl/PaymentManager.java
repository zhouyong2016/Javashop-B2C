package com.enation.app.shop.core.order.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentQrCodeEvent;
import com.enation.app.shop.core.order.plugin.payment.PaymentPluginBundle;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.log.LogType;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 支付方式管理
 * @author kingapex
 * 2010-4-4下午02:26:19
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Service("paymentManager")
public class PaymentManager implements IPaymentManager {
	
	
	@Autowired
	private PaymentPluginBundle paymentPluginBundle; //支付插件桩
	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#list()
	 */
	@Override
	public List list() {
		String sql = "select * from es_payment_cfg";
		return this.daoSupport.queryForList(sql, PayCfg.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getPayCfgByName(java.lang.String)
	 */
	@Override
	public PayCfg getPayCfgByName(String name) {
		String sql = "select * from es_payment_cfg where type = ?";
		return this.daoSupport.queryForObject(sql, PayCfg.class,name); 
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#get(java.lang.Integer)
	 */
	@Override
	public PayCfg get(Integer id) {
		String sql = "select * from es_payment_cfg where id=?";
		PayCfg payment =this.daoSupport.queryForObject(sql, PayCfg.class, id);
		return payment;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#get(java.lang.String)
	 */
	@Override
	public PayCfg get(String pluginid) {
		String sql = "select * from es_payment_cfg where type=?";
		PayCfg payment =this.daoSupport.queryForObject(sql, PayCfg.class, pluginid);
		return payment;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#countPayPrice(java.lang.Integer)
	 */
	@Override
	public Double countPayPrice(Integer id) {
		return 0D;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#add(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="新安装了一个${type}支付类型的${name}支付方式")
	public Integer add(String name, String type, String biref,String pay_img,Integer isOnline,
			Map<String, String> configParmas,Integer isRetrace) {
		if(StringUtil.isEmpty(name)) throw new IllegalArgumentException("payment name is  null");
		if(StringUtil.isEmpty(type)) throw new IllegalArgumentException("payment type is  null");
		if(configParmas == null) throw new IllegalArgumentException("configParmas  is  null");
		
		PayCfg payCfg = new PayCfg();
		payCfg.setName(name);
		payCfg.setType(type);
		payCfg.setBiref(biref);
		payCfg.setPay_img(transformPath(pay_img));
		payCfg.setIs_online(isOnline);
		payCfg.setConfig( JSONObject.fromObject(configParmas).toString());
		payCfg.setIs_retrace(isOnline);
		this.daoSupport.insert("es_payment_cfg", payCfg);
		Integer id = this.daoSupport.getLastId("es_payment_cfg");
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getConfigParams(java.lang.Integer)
	 */
	@Override
	public Map<String, String> getConfigParams(Integer paymentId) {
		PayCfg payment =this.get(paymentId);
		String config  = payment.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getConfigParams(java.lang.String)
	 */
	@Override
	public Map<String, String> getConfigParams(String pluginid) {
		PayCfg payment =this.get(pluginid);
		if (payment == null) {
			
			return null;
		} 
		String config  = payment.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#edit(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改了${type}支付类型的${name}支付方式信息")
	public void edit(Integer paymentId, String name,String type, String biref,String pay_img,Integer isOnline,
			Map<String, String> configParmas,Integer isRetrace) {
		
		if(StringUtil.isEmpty(name)) throw new IllegalArgumentException("payment name is  null");
		if(configParmas == null) throw new IllegalArgumentException("configParmas  is  null");
		
		PayCfg payCfg = new PayCfg();
		payCfg.setName(name);
		payCfg.setBiref(biref);
		payCfg.setType(type);
		payCfg.setPay_img(transformPath(pay_img));
		payCfg.setIs_online(isOnline);
		payCfg.setIs_retrace(isRetrace);
		payCfg.setConfig( JSONObject.fromObject(configParmas).toString());	
		this.daoSupport.update("es_payment_cfg", payCfg, "id="+ paymentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="批量删除支付方式")
	public void delete(Integer[] idArray) {
		if(idArray==null || idArray.length==0) return;
		
		String idStr = StringUtil.arrayToString(idArray, ",");
		String sql  ="delete from es_payment_cfg where id in("+idStr+")";
		this.daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#listAvailablePlugins()
	 */
	@Override
	public List<IPlugin> listAvailablePlugins() {
		
		return this.paymentPluginBundle.getPluginList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getPluginInstallHtml(java.lang.String, java.lang.Integer)
	 */
	@Override
	public String getPluginInstallHtml(String pluginId,Integer paymentId) {
		IPlugin installPlugin =null;
		List<IPlugin>  plguinList =  this.listAvailablePlugins();
		for(IPlugin plugin :plguinList){
			if(plugin instanceof AbstractPaymentPlugin){
				
				if( ((AbstractPaymentPlugin) plugin).getId().equals(pluginId)){
					installPlugin = plugin;
					break;
				}
			}
		}
		
		
		if(installPlugin==null) throw new ObjectNotFoundException("plugin["+pluginId+"] not found!"); 
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		fp.setClz(installPlugin.getClass());
		 
		if(paymentId!=null){
			Map<String,String> params = this.getConfigParams(paymentId);
			Iterator<String> keyIter  = params.keySet().iterator();
			
			while(keyIter.hasNext()) {
				 String key  = keyIter.next();
				 String value = params.get(key);
				 fp.putData(key, value);
			}
		}
		return fp.proessPageContent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getCount()
	 */
	@Override
	public Integer getCount() {
		return daoSupport.queryForInt("select count(0) from es_payment_cfg");
	}
	
	/**
	 * 页面中传递过来的图片地址为:http://<staticserver>/<image path>如:
	 * http://static.enationsoft.com/attachment/goods/1.jpg
	 * 存在库中的为fs:/attachment/goods/1.jpg 生成fs式路径
	 * @param path
	 * @return
	 */
	private String transformPath(String path) {
		if(path==null){
			return "";
		}
		String static_server_domain= SystemSetting.getStatic_server_domain();
		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getPayQrCdoe(com.enation.app.shop.core.order.model.Order, java.lang.String)
	 */
	@Override
	public String getPayQrCdoe(Order order, String pluginId) {

		IPaymentQrCodeEvent event =  SpringContextHolder.getBean(pluginId);
		PayCfg payCfg = this.get(pluginId);
		String  code_url = event.onPayQrCode(payCfg, order);
		return code_url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getPayStatus(com.enation.app.shop.core.order.model.Order, java.lang.String)
	 */
	@Override
	public String getPayStatus(Order order, String pluginId) {
		IPaymentQrCodeEvent event =  SpringContextHolder.getBean(pluginId);
		PayCfg payCfg = this.get(pluginId);
		String str = event.onPayStatus(payCfg, order);
		return str;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentManager#getListByIsOnline(int)
	 */
	@Override
	public List<PayCfg> getListByIsOnline(int is_online) {
		String sql = "select * from es_payment_cfg where is_online=?";
		return this.daoSupport.queryForList(sql, PayCfg.class,is_online);
	}

}

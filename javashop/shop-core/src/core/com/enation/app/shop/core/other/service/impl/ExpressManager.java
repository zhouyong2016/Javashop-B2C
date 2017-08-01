package com.enation.app.shop.core.other.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.ExpressPlatform;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.app.shop.core.order.service.IExpressManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.plugin.IPlugin;

/**
 * 快递manager
 * @author Sylow
 * @version v2.0,2016年2月20日 版本改造
 * @since v6.0
 */
@Service("expressManager")
public class ExpressManager implements IExpressManager {

	@Autowired
	private IDaoSupport daoSupport;
	private Logger logger = Logger.getLogger(getClass());
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#getList()
	 */
	@Override
	public List getList() {
		List list = this.daoSupport.queryForList("select * from es_express_platform");
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#add(com.enation.app.base.core.model.ExpressPlatform)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="添加一个快递平台，快递平台为${platform.platform_name}")
	public void add(ExpressPlatform platform) {
		this.daoSupport.insert("es_express_platform", platform);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#getPlateform(java.lang.Integer)
	 */
	@Override
	public ExpressPlatform getPlateform(Integer id) {
		String sql ="select * from es_express_platform where id=?";
		ExpressPlatform platform = (ExpressPlatform) this.daoSupport.queryForObject(sql, ExpressPlatform.class, id);
		return platform;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#getPlateformHtml(java.lang.String, java.lang.Integer)
	 */
	@Override
	public String getPlateformHtml(String code,Integer id) {
		
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		IPlugin installPlugin = null;
		installPlugin = SpringContextHolder.getBean(code);
		fp.setClz(installPlugin.getClass());
		
		Map<String,String> params = this.getConfigParams(id);
		fp.putData(params);
		
		return fp.proessPageContent();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#setParam(java.lang.Integer, java.util.Map)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="设置相应的快递接口参数，快递平台ID为${id}")
	public void setParam(Integer id, Map<String, String> param) {
		String sql ="update es_express_platform set config=? where id=?";
		this.daoSupport.execute(sql, JSONObject.fromObject(param).toString(),id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#open(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="ID为${id}快递平台设置为默认启用的快递平台")
	public void open(Integer id) {
		this.daoSupport.execute("update es_express_platform set is_open=0");
		this.daoSupport.execute("update es_express_platform set is_open=1 where id=?", id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#getDefPlatform(java.lang.String, java.lang.String)
	 */
	@Override
	public Map getDefPlatform(String com, String nu) {
		try {
			List<ExpressPlatform> list = this.daoSupport.queryForList("select * from es_express_platform where is_open=1",ExpressPlatform.class);
			if(list!=null && list.size()==1){
				ExpressPlatform platform = list.get(0);
				String config = platform.getConfig();
				JSONObject jsonObject = JSONObject.fromObject( config );  
				Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
				IExpressEvent expressEvent = SpringContextHolder.getBean(platform.getCode());
				Map kuaidiresult =  expressEvent.getExpressDetail(com, nu, itemMap);
				return kuaidiresult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("查询快递错误"+e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IExpressManager#getPlateform(java.lang.String)
	 */
	@Override
	public int getPlateform(String code) {
		String sql ="select id from es_express_platform where code=?";
		List list = this.daoSupport.queryForList(sql, code);
		if(!list.isEmpty()){
			return 1;
		}
		return 0;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private Map<String, String> getConfigParams(Integer id) {
		ExpressPlatform platform = this.getPlateform(id);
		String config  = platform.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}

}

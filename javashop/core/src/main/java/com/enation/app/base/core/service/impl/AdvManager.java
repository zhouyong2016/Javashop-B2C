package com.enation.app.base.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 后台广告管理接口实现类
 * @author DMRain 2016年2月23日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service
public class AdvManager implements IAdvManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#addAdv(com.enation.app.base.core.model.Adv)
	 */
	@Override
	@Log(type=LogType.ADV,detail="平台广告列表新添加一个广告名为${adv.aname}广告")
	public void addAdv(Adv adv) {
		this.daoSupport.insert("es_adv", adv);

	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#delAdvs(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.ADV,detail="删除广告列表中一条广告")
	public void delAdvs(Integer[] ids) {
		if (ids == null || ids.length==0){
			return;
		}
		
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_adv where aid in (" + id_str + ")";
		this.daoSupport.execute(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#getAdvDetail(java.lang.Long)
	 */
	@Override
	public Adv getAdvDetail(Long advid) {
		Adv adv = this.daoSupport.queryForObject("select * from es_adv where aid = ?", Adv.class, advid);
		String pic  = adv.getAtturl();
		
		if (pic != null) {
			pic = StaticResourcesUtil.convertToUrl(pic); 
			adv.setAtturl(pic);
		}
		
		return adv;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#pageAdv(java.lang.String, int, int)
	 */
	@Override
	public Page pageAdv(String order, int page, int pageSize) {
		order = order == null ? " aid desc" : order;
		String sql = "select v.*, c.cname cname from es_adv v left join es_adcolumn c on c.acid = v.acid";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize, new AdvMapper());
		return rpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#updateAdv(com.enation.app.base.core.model.Adv)
	 */
	@Override
	@Log(type=LogType.ADV,detail="修改广告列表中广告名为${adv.aname}广告的广告信息")
	public void updateAdv(Adv adv) {
		this.daoSupport.update("es_adv", adv, "aid = " + adv.getAid());

	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#listAdv(java.lang.Long)
	 */
	@Override
	public List listAdv(Long acid) {
		Long nowtime = (new Date()).getTime();
		
		List<Adv> list = this.daoSupport.queryForList("select a.*,'' cname from es_adv a where acid = ? and isclose = 0", new AdvMapper(), acid);
		return list;
	}


	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#search(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public Page search(String cname,Integer acid, int pageNo, int pageSize, String order) {
		StringBuffer term  = new StringBuffer();
		StringBuffer sql = new StringBuffer("select v.*, c.cname cname from es_adv v left join es_adcolumn c on c.acid = v.acid");
		
		if (!StringUtil.isEmpty(cname)) {
			if (term.length() > 0) {
				term.append(" and ");
			} else {
				term.append(" where ");
			}
			
			term.append(" aname like'%"+cname+"%'");
		}
		if(acid != null && acid != 0){
			if (term.length() > 0) {
				term.append(" and ");
			} else {
				term.append(" where ");
			}
			term.append(" c.acid = "+acid);
		}
		sql.append(term);
		
		order = order == null ? " aid desc" : order;
		sql.append(" order by " + order );
		
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize,Adv.class);
		return page;
	}
	
	 

}

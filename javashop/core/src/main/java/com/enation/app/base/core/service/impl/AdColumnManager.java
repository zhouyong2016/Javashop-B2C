package com.enation.app.base.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 后台广告位管理接口实现类
 * @author DMRain 2016年2月20日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service
public class AdColumnManager implements IAdColumnManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#addAdvc(com.enation.app.base.core.model.AdColumn)
	 */
	@Override
	@Log(type=LogType.ADV,detail="添加了一个广告名为${adColumn.cname}的新广告")
	public void addAdvc(AdColumn adColumn) {
		this.daoSupport.insert("es_adcolumn", adColumn);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#delAdcs(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.ADV,detail="删除一个广告")
	public void delAdcs(Integer[] ids) {
		if (ids == null || ids.length==0){
			return;
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_adcolumn where acid in (" + id_str + ")";
		this.daoSupport.execute(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#getADcolumnDetail(java.lang.Long)
	 */
	@Override
	public AdColumn getADcolumnDetail(Long acid) {
		AdColumn  adColumn = this.daoSupport.queryForObject("select * from es_adcolumn where acid = ?", AdColumn.class, acid);
		return adColumn;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#listAllAdvPos()
	 */
	@Override
	public List listAllAdvPos() {
		List<AdColumn> list = this.daoSupport.queryForList("select * from es_adcolumn", AdColumn.class);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#pageAdvPos(int, int)
	 */
	@Override
	public Page pageAdvPos(String cname,int page, int pageSize) {
		StringBuffer term  = new StringBuffer();
		StringBuffer sql = new StringBuffer("select * from es_adcolumn");
		if (!StringUtil.isEmpty(cname)) {
			if (term.length() > 0) {
				term.append(" and ");
			} else {
				term.append(" where ");
			}
			
			term.append(" cname like'%"+cname+"%'");
		}
		sql.append(term);
		sql.append(" order by acid desc");
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize);
		return rpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdColumnManager#updateAdvc(com.enation.app.base.core.model.AdColumn)
	 */
	@Override
	@Log(type=LogType.ADV,detail="修改了广告名为${adColumn.cname}的广告信息")
	public void updateAdvc(AdColumn adColumn) {
		this.daoSupport.update("es_adcolumn", adColumn, "acid = " + adColumn.getAcid());
	}

}

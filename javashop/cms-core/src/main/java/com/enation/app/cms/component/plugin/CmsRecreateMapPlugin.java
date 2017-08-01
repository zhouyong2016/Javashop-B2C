package com.enation.app.cms.component.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.SiteMapUrl;
import com.enation.app.base.core.plugin.IRecreateMapEvent;
import com.enation.app.base.core.service.ISitemapManager;
import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * SiteMap信息重新生成插件
 * @author kingapex
 *2012-3-29下午6:27:27
 */
@Component
public class CmsRecreateMapPlugin extends AutoRegisterPlugin implements	IRecreateMapEvent {
	
	private ISitemapManager sitemapManager;
	
	@Autowired
	private IDataCatManager dataCatManager;
	
	@Autowired
	private IDataManager dataManager;


	public void onRecreateMap() {
		List<DataCat> listCat = this.dataCatManager.listAllChildren(0);
		for (DataCat cat : listCat) {
			if (cat.getTositemap() == 1) {
				List<Map> list = this.dataManager.list(cat.getCat_id());
				for (Map map : list) {
					SiteMapUrl url = new SiteMapUrl();
					url.setLoc("/" + cat.getDetail_url() + "-" + cat.getCat_id() + "-" + map.get("id") + ".html");
					url.setLastmod(System.currentTimeMillis());
					this.sitemapManager.addUrl(url);
				}
			}
		}
	}

}

package com.enation.app.shop.component.express;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ExpressPlatform;
import com.enation.app.base.core.plugin.express.AbstractExpressComponent;
import com.enation.app.shop.core.order.service.IExpressManager;
import com.enation.framework.component.IComponent;
/***
 * 网店快递组件
 * @author Xulipeng
 *
 */
@Component
public class ShopExpressComponent extends AbstractExpressComponent implements IComponent {

	private IExpressManager expressManager;
	
	@Override
	public void install() {
		
		int i =this.expressManager.getPlateform("kuaidi100Plugin");
		if(i==0){
			ExpressPlatform platform = new ExpressPlatform();
			platform.setPlatform_name("快递100");
			platform.setCode("kuaidi100Plugin");
			platform.setConfig("{'keyid':'92a25441fc46fded','user':'0'}");
			platform.setIs_open(1);
			this.addExpress(platform);
			this.expressManager.add(platform);
		}
		
		int j =this.expressManager.getPlateform("showapiPlugin");
		if(j==0){
			ExpressPlatform platform2 = new ExpressPlatform();
			platform2.setPlatform_name("showapi快递");
			platform2.setCode("showapiPlugin");
			this.addExpress(platform2);
			this.expressManager.add(platform2);
		}
		
		
	}

	@Override
	public void unInstall() {

	}

	public IExpressManager getExpressManager() {
		return expressManager;
	}

	public void setExpressManager(IExpressManager expressManager) {
		this.expressManager = expressManager;
	}

	
}

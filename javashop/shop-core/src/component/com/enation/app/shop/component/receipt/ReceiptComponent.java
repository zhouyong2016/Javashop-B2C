package com.enation.app.shop.component.receipt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.data.IDataOperation;

/**
 * 发票组件
 * @author kingapex
 *2012-2-6下午9:23:48
 */
@Component
public class ReceiptComponent implements IComponent {
	@Autowired
	private IDataOperation dataOperation;
	@Override
	public void install() {
		this.dataOperation.imported("file:com/enation/app/shop/component/receipt/receipt_install.xml");
	}

	@Override
	public void unInstall() {
		this.dataOperation.imported("file:com/enation/app/shop/component/receipt/receipt_uninstall.xml");

	}
}

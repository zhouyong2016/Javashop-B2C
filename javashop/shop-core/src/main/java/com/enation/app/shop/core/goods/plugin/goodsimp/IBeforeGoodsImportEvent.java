package com.enation.app.shop.core.goods.plugin.goodsimp;

import org.w3c.dom.Document;

/**
 * 商品批量导入前事件
 * @author kingapex
 *
 */
public interface IBeforeGoodsImportEvent {
	
	/**
	 * 在商品批量导入前会激发此事件
	 * @param configDoc
	 */
	public void onBeforeImport(Document configDoc);
}

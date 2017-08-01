package com.enation.app.shop.core.order.plugin.cart;

/**
 * 过滤购物车查询sql
 * @author jianghongyan
 *
 */
public interface ICartFilterSqlEvent {
	/**
	 * 过滤查询条数sql
	 * @param sql
	 * @return
	 */
	public String filterGetCountSql(String sql);

	/**
	 * 过滤更新数据库sql
	 * @param sql
	 * @return
	 */
	public String filterUpdateSql(String sql);

	public void filterListGoodsSql(StringBuffer sql);

	public void filterSelectListGoods(StringBuffer sql);

	public void filterCheckProductSql(StringBuffer sql);
}

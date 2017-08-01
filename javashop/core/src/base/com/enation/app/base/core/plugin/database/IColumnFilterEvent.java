package com.enation.app.base.core.plugin.database;

import java.sql.ResultSet;
import java.util.Map;


/**
 * 结果集列过滤事件
 * @author kingapex
 *
 */
public interface IColumnFilterEvent {
	public void filter(Map colValue,ResultSet rs);
}

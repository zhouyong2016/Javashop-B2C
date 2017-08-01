package com.enation.app.base.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.ShortMsgPluginBundle;
import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.framework.database.IDaoSupport;

/**
 * 短消息管理
 * @author kingapex
 *
 */
@Service
public class ShortMsgManager   implements IShortMsgManager {

	@Autowired
	private ShortMsgPluginBundle shortMsgPluginBundle;
	
	@Autowired
	protected IDaoSupport daoSupport;
	
	
	@Override
	public List<ShortMsg> listNotReadMessage() {
		return shortMsgPluginBundle.getMessageList();
	}

	
}

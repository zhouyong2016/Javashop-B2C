package com.enation.eop.sdk.context;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.base.core.model.MultiSite;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.webcontext.ThreadContextHolder;

public class EopContext {
	private static ThreadLocal<EopContext> EopContextHolder = new ThreadLocal<EopContext>();
	
	public static void setContext(EopContext context) {
		EopContextHolder.set(context);
	}

	public static void remove() {
		EopContextHolder.remove();
	}

	public static EopContext getContext() {
		EopContext context = EopContextHolder.get();
		return context;
	}

 

	// 当前子站
	private MultiSite currentChildSite;
	

	public MultiSite getCurrentChildSite() {
		return currentChildSite;
	}

	public void setCurrentChildSite(MultiSite currentChildSite) {
		this.currentChildSite = currentChildSite;
	}

 

	 

	 
}

package com.enation.app.base.core.service.solution;

import java.util.ArrayList;
import java.util.List;

import com.enation.framework.context.webcontext.ThreadContextHolder;

public class InstallUtil {
	public static String installing = "installing";

	public static void putMessaage(String msg) {
		if (ThreadContextHolder.getSession() != null) {
			List msgList = (List) ThreadContextHolder.getSession().getAttribute("installMsg");
			if (msgList == null) {
				msgList = new ArrayList();
			}
			msgList.add(msg);
			ThreadContextHolder.getSession().setAttribute("installMsg", msgList);
		}
	}
}

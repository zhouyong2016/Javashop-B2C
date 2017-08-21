package com.enation.app.shop.component.payment.plugin.paypal.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResultPrinter {

	public static void addResult(HttpServletRequest req, HttpServletResponse resp, String message,
			String request, String response, String error) {

		addDataToAttributeList(req, "messages", message);
		addDataToAttributeList(req, "requests", request);
		response = (response != null) ? response : error;
		addDataToAttributeList(req, "responses", response);
		addDataToAttributeList(req, "errors", error);
		if (error != null) {
			try {
				req.getRequestDispatcher("response.jsp").forward(req, resp);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void addDataToAttributeList(HttpServletRequest req,
			String listName, String data) {
		// Add Messages
		List<String> list;
		if ((list = (List<String>) req.getAttribute(listName)) == null) {
			list = new ArrayList<String>();
		}
		list.add(data);
		req.setAttribute(listName, list);
	}
}

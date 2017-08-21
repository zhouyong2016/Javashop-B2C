package com.enation.eop.processor.session;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface RequestEventObserver {
    public void completed(ServletRequest servletRequest, ServletResponse response);
}

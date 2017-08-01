package com.enation.eop.processor.session;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.enation.eop.processor.session.RequestEventObserver;

public class RequestEventSubject {
    private RequestEventObserver listener;

    public void attach(RequestEventObserver eventObserver) {
        listener = eventObserver;
    }

    public void detach() {
        listener = null;
    }

    public void completed(ServletRequest servletRequest, ServletResponse response) {
        if(listener != null)
            listener.completed(servletRequest, response);
    }
}

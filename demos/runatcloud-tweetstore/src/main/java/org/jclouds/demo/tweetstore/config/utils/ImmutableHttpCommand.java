package org.jclouds.demo.tweetstore.config.utils;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;

class ImmutableHttpCommand implements HttpCommand {
    private final HttpRequest request;

    public ImmutableHttpCommand(HttpRequest request) {
        this.request = request;
    }
    
    @Override
    public void setException(Exception exception) {
    }

    @Override
    public void setCurrentRequest(HttpRequest request) {
    }

    @Override
    public boolean isReplayable() {
        return false;
    }

    @Override
    public int incrementRedirectCount() {
        return 0;
    }

    @Override
    public int incrementFailureCount() {
        return 0;
    }

    @Override
    public int getRedirectCount() {
        return 0;
    }

    @Override
    public int getFailureCount() {
        return 0;
    }

    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public HttpRequest getCurrentRequest() {
        return request;
    }
}
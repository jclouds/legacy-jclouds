package org.jclouds.gogrid.mock;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;

/**
 * @author Oleksiy Yarmula
 */
public class HttpCommandMock implements HttpCommand {

    @Override
    public int incrementRedirectCount() {
        return 0;
    }

    @Override
    public int getRedirectCount() {
        return 0;
    }

    @Override
    public boolean isReplayable() {
        return false;
    }

    @Override
    public void changeHostAndPortTo(String host, int port) {
    }

    @Override
    public void changeToGETRequest() {
    }

    @Override
    public void changePathTo(String newPath) {
    }

    @Override
    public int incrementFailureCount() {
        return 0;
    }

    @Override
    public int getFailureCount() {
        return 0;
    }

    @Override
    public HttpRequest getRequest() {
        return null;
    }

    @Override
    public void setException(Exception exception) {
    }

    @Override
    public Exception getException() {
        return null;
    }
}

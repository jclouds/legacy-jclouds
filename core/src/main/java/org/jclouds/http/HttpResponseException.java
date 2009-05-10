package org.jclouds.http;

/**
 * Represents an error obtained from an HttpResponse.
 * 
 * @author Adrian Cole
 * 
 */
public class HttpResponseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    protected final HttpFutureCommand<?> command;
    protected final HttpResponse response;
    private String content;

    public HttpResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response, Throwable cause) {
	super(message, cause);
	this.command = command;
	this.response = response;
    }

    public HttpResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response, String content, Throwable cause) {
	super(message, cause);
	this.command = command;
	this.response = response;
	this.content = content;
    }

    public HttpResponseException(HttpFutureCommand<?> command,
	    HttpResponse response, Throwable cause) {
	this(String.format("command: %1$s failed with response: %2$s", command,
		response), command, response, cause);
    }

    public HttpResponseException(HttpFutureCommand<?> command,
	    HttpResponse response, String content, Throwable cause) {
	this(String.format(
		"command: %1$s failed with response: %2$s; content: [%3$s]",
		command, response), command, response, content, cause);
    }

    public HttpResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response) {
	super(message);
	this.command = command;
	this.response = response;
    }

    public HttpResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response, String content) {
	super(message);
	this.command = command;
	this.response = response;
	this.content = content;
    }

    public HttpResponseException(HttpFutureCommand<?> command,
	    HttpResponse response) {
	this(String.format("command: %1$s failed with response: %2$s", command,
		response), command, response);
    }

    public HttpResponseException(HttpFutureCommand<?> command,
	    HttpResponse response, String content) {
	this(String.format(
		"command: %1$s failed with response: %2$s; content: [%3$s]",
		command, response, content), command, response, content);
    }

    public HttpFutureCommand<?> getCommand() {
	return command;
    }

    public HttpResponse getResponse() {
	return response;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getContent() {
	return content;
    }

}
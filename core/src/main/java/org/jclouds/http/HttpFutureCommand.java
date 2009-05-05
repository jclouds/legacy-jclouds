/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;

import org.jclouds.command.FutureCommand;
import org.jclouds.logging.Logger;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class HttpFutureCommand<T> extends
	FutureCommand<HttpRequest, HttpResponse, T> {

    public HttpFutureCommand(String method, String uri,
	    ResponseCallable<T> responseCallable) {
	super(new HttpRequest(checkNotNull(method, "method"), checkNotNull(uri,
		"uri")), responseCallable);
    }

    protected void addHostHeader(String host) {
	getRequest().getHeaders().put("Host", host);
    }

    @Override
    public String toString() {
	return this.getClass().getName() + "{" + "request=" + this.getRequest()
		+ "," + "responseFuture=" + this.getResponseFuture() + '}';
    }

    /**
     * // TODO: Adrian: Document this!
     * 
     * @author Adrian Cole
     */
    public abstract static class ResponseCallable<T> implements
	    FutureCommand.ResponseCallable<HttpResponse, T> {
	@Resource
	protected Logger logger = Logger.NULL;

	private HttpResponse response;

	public HttpResponse getResponse() {
	    return response;
	}

	public void setResponse(HttpResponse response) {
	    this.response = response;
	}
    }

}

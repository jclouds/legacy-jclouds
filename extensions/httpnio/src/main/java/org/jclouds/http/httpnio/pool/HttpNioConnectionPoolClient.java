/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.httpnio.pool;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.http.nio.NHttpConnection;
import org.jclouds.command.pool.FutureCommandConnectionPoolClient;
import org.jclouds.http.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Singleton
public class HttpNioConnectionPoolClient
        extends
        FutureCommandConnectionPoolClient<NHttpConnection, HttpFutureCommand<?>>
        implements HttpFutureCommandClient {
    private List<HttpRequestFilter> requestFilters = Collections.emptyList();

    public List<HttpRequestFilter> getRequestFilters() {
        return requestFilters;
    }

    @Inject(optional = true)
    public void setRequestFilters(List<HttpRequestFilter> requestFilters) {
        this.requestFilters = requestFilters;
    }

    @Override
    protected void invoke(HttpFutureCommand<?> command) {
        HttpRequest request = (HttpRequest) command.getRequest();
        try {
            for (HttpRequestFilter filter : getRequestFilters()) {
                filter.filter(request);
            }
            super.invoke(command);
        } catch (HttpException e) {
            command.setException(e);
        }
    }

    @Inject
    public HttpNioConnectionPoolClient(
            ExecutorService executor,
            HttpNioFutureCommandConnectionPool httpFutureCommandConnectionHandleNHttpConnectionNioFutureCommandConnectionPool,
            BlockingQueue<HttpFutureCommand<?>> commandQueue) {
        super(
                executor,
                httpFutureCommandConnectionHandleNHttpConnectionNioFutureCommandConnectionPool,
                commandQueue);
    }
}

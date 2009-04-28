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
package org.jclouds.http.httpnio.pool;

import com.google.inject.Inject;
import org.apache.http.nio.NHttpConnection;
import org.jclouds.command.FutureCommand;
import org.jclouds.Logger;
import org.jclouds.command.pool.FutureCommandConnectionRetry;
import org.jclouds.command.pool.FutureCommandConnectionHandle;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionHandle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpNioFutureCommandConnectionRetry extends FutureCommandConnectionRetry<NHttpConnection> {

    @Inject
    public HttpNioFutureCommandConnectionRetry(java.util.logging.Logger logger, BlockingQueue<FutureCommand> commandQueue, AtomicInteger errors) {
        super(new Logger(logger), commandQueue, errors);
    }

    @Override
    public void associateHandleWithConnection(FutureCommandConnectionHandle<NHttpConnection> handle, NHttpConnection connection) {
        connection.getContext().setAttribute("operation-handle", handle);
    }
    
    @Override
    public HttpNioFutureCommandConnectionHandle getHandleFromConnection(NHttpConnection connection) {
        return (HttpNioFutureCommandConnectionHandle) connection.getContext().getAttribute("operation-handle");
    }

//    @Override
//    public void incrementErrorCountAndRetry(FutureCommand operation) {
//        ((HttpEntityEnclosingRequest) operation.getRequest()).removeHeaders(HTTP.CONTENT_LEN);
//        ((HttpEntityEnclosingRequest) operation.getRequest()).removeHeaders(HTTP.DATE_HEADER);
//        super.incrementErrorCountAndRetry(operation);
//    }
}
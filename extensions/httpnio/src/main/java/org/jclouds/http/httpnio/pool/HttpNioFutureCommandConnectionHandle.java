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

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.http.nio.NHttpConnection;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.pool.FutureCommandConnectionHandle;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class HttpNioFutureCommandConnectionHandle extends
	FutureCommandConnectionHandle<NHttpConnection> {

    @Inject
    public HttpNioFutureCommandConnectionHandle(
	    BlockingQueue<NHttpConnection> available, Semaphore maxConnections,
	    @Assisted NHttpConnection conn, @Assisted FutureCommand operation)
	    throws InterruptedException {
	super(maxConnections, operation, conn, available);

    }

    public void startConnection() {
	conn.getContext().setAttribute("operation", operation);
	logger.trace("invoking %1s on connection %2s", operation, conn);
	conn.requestOutput();
    }

    public void shutdownConnection() throws IOException {
	conn.shutdown();
    }

}

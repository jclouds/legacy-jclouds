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
package org.jclouds.command.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Utils;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.FutureCommandClient;
import org.jclouds.lifecycle.BaseLifeCycle;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class FutureCommandConnectionPoolClient<C, O extends FutureCommand<?, ?, ?>>
	extends BaseLifeCycle implements FutureCommandClient<O> {
    private final FutureCommandConnectionPool<C, O> futureCommandConnectionPool;
    private final BlockingQueue<O> commandQueue;

    @Inject
    public FutureCommandConnectionPoolClient(ExecutorService executor,
	    FutureCommandConnectionPool<C, O> futureCommandConnectionPool,
	    BlockingQueue<O> commandQueue) {
	super(executor, futureCommandConnectionPool);
	this.futureCommandConnectionPool = futureCommandConnectionPool;
	this.commandQueue = commandQueue;
    }

    @Override
    protected boolean shouldDoWork() {
	return super.shouldDoWork()
		&& futureCommandConnectionPool.getStatus()
			.equals(Status.ACTIVE);
    }

    @Override
    protected void doShutdown() {
	exception.compareAndSet(null, futureCommandConnectionPool
		.getException());
	while (!commandQueue.isEmpty()) {
	    FutureCommand<?, ?, ?> command = (FutureCommand<?, ?, ?>) commandQueue
		    .remove();
	    if (command != null) {
		if (exception.get() != null)
		    command.setException(exception.get());
		else
		    command.cancel(true);
	    }
	}
    }

    @Override
    protected void doWork() throws InterruptedException {
	O command = commandQueue.poll(1, TimeUnit.SECONDS);
	if (command != null) {
	    try {
		invoke(command);
	    } catch (Exception e) {
		Utils.<InterruptedException> rethrowIfRuntimeOrSameType(e);
		logger.error(e, "Error processing command %s", command);
	    }
	}
    }

    public void submit(O command) {
	exceptionIfNotActive();
	commandQueue.add(command);
    }

    protected void invoke(O command) {
	exceptionIfNotActive();
	FutureCommandConnectionHandle<C, O> connectionHandle = null;
	try {
	    connectionHandle = futureCommandConnectionPool.getHandle(command);
	} catch (InterruptedException e) {
	    logger
		    .warn(
			    e,
			    "Interrupted getting a connection for command %1s; retrying",
			    command);
	    commandQueue.add(command);
	    return;
	} catch (TimeoutException e) {
	    logger.warn(e,
		    "Timeout getting a connection for command %1s; retrying",
		    command);
	    commandQueue.add(command);
	    return;
	}

	if (connectionHandle == null) {
	    logger.error(
		    "Failed to obtain connection for command %1s; retrying",
		    command);
	    commandQueue.add(command);
	    return;
	}
	connectionHandle.startConnection();
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("FutureCommandConnectionPoolClient");
	sb.append("{status=").append(status);
	sb.append(", commandQueue=").append(
		(commandQueue != null) ? commandQueue.size() : 0);
	sb.append(", futureCommandConnectionPool=").append(
		futureCommandConnectionPool);
	sb.append('}');
	return sb.toString();
    }

}

/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.command.FutureCommand;
import org.jclouds.lifecycle.BaseLifeCycle;

import com.google.inject.Provides;
import com.google.inject.name.Named;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class FutureCommandConnectionPool<C, O extends FutureCommand<?, ?, ?>>
	extends BaseLifeCycle {
    protected final Semaphore allConnections;
    protected final BlockingQueue<C> available;
    protected final BlockingQueue<O> commandQueue;
    protected final FutureCommandConnectionHandleFactory<C, O> futureCommandConnectionHandleFactory;
    protected final int maxConnectionReuse;
    protected final AtomicInteger currentSessionFailures = new AtomicInteger(0);
    protected volatile boolean hitBottom = false;

    public FutureCommandConnectionPool(
	    ExecutorService executor,
	    Semaphore allConnections,
	    BlockingQueue<O> commandQueue,
	    FutureCommandConnectionHandleFactory<C, O> futureCommandConnectionHandleFactory,
	    @Named("maxConnectionReuse") int maxConnectionReuse,
	    BlockingQueue<C> available, BaseLifeCycle... dependencies) {
	super(executor, dependencies);
	this.allConnections = allConnections;
	this.commandQueue = commandQueue;
	this.futureCommandConnectionHandleFactory = futureCommandConnectionHandleFactory;
	this.maxConnectionReuse = maxConnectionReuse;
	this.available = available;
    }

    protected void setResponseException(Exception ex, C conn) {
	O command = getHandleFromConnection(conn).getCommand();
	command.getResponseFuture().setException(ex);
    }

    protected void cancel(C conn) {
	O command = getHandleFromConnection(conn).getCommand();
	command.cancel(true);
    }

    @Provides
    public C getConnection() throws InterruptedException, TimeoutException {
	exceptionIfNotActive();
	if (!hitBottom) {
	    hitBottom = available.size() == 0
		    && allConnections.availablePermits() == 0;
	    if (hitBottom)
		logger.warn("%1s - saturated connection pool", this);
	}
	logger
		.debug(
			"%1s - attempting to acquire connection; %d currently available",
			this, available.size());
	C conn = available.poll(5, TimeUnit.SECONDS);
	if (conn == null)
	    throw new TimeoutException(
		    "could not obtain a pooled connection within 5 seconds");

	logger.trace("%1s - %2d - aquired", conn, conn.hashCode());
	if (connectionValid(conn)) {
	    logger.debug("%1s - %2d - reusing", conn, conn.hashCode());
	    return conn;
	} else {
	    logger.debug("%1s - %2d - unusable", conn, conn.hashCode());
	    shutdownConnection(conn);
	    allConnections.release();
	    return getConnection();
	}
    }

    protected void fatalException(Exception ex, C conn) {
	setResponseException(ex, conn);
	exception.set(ex);
	shutdown();
    }

    protected abstract void shutdownConnection(C conn);

    protected abstract boolean connectionValid(C conn);

    public FutureCommandConnectionHandle<C, O> getHandle(O command)
	    throws InterruptedException, TimeoutException {
	exceptionIfNotActive();
	C conn = getConnection();
	FutureCommandConnectionHandle<C, O> handle = futureCommandConnectionHandleFactory
		.create(command, conn);
	associateHandleWithConnection(handle, conn);
	return handle;
    }

    protected void resubmitIfRequestIsReplayable(C connection, Exception e) {
	O command = getCommandFromConnection(connection);
	if (command != null) {
	    if (isReplayable(command)) {
		logger.info("resubmitting command: %1s", command);
		commandQueue.add(command);
	    } else {
		command.setException(e);
	    }
	}
    }

    protected abstract boolean isReplayable(O command);

    O getCommandFromConnection(C connection) {
	FutureCommandConnectionHandle<C, O> handle = getHandleFromConnection(connection);
	if (handle != null && handle.getCommand() != null) {
	    return handle.getCommand();
	}
	return null;
    }

    protected void setExceptionOnCommand(C connection, Exception e) {
	FutureCommand<?, ?, ?> command = getCommandFromConnection(connection);
	if (command != null) {
	    logger.warn(e, "exception in command: %1s", command);
	    command.setException(e);
	}
    }

    protected abstract void associateHandleWithConnection(
	    FutureCommandConnectionHandle<C, O> handle, C connection);

    protected abstract FutureCommandConnectionHandle<C, O> getHandleFromConnection(
	    C connection);

    protected abstract void createNewConnection() throws InterruptedException;

    public interface FutureCommandConnectionHandleFactory<C, O extends FutureCommand<?, ?, ?>> {
	FutureCommandConnectionHandle<C, O> create(O command, C conn);
    }
}

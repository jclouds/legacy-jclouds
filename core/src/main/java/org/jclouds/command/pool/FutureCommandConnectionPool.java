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

import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.jclouds.Logger;
import org.jclouds.command.FutureCommand;
import org.jclouds.lifecycle.BaseLifeCycle;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public abstract class FutureCommandConnectionPool<C> extends BaseLifeCycle {
    protected final Semaphore allConnections;
    protected final BlockingQueue<C> available;
    protected final FutureCommandConnectionHandleFactory<C> futureCommandConnectionHandleFactory;
    protected final int maxConnectionReuse;
    protected final AtomicInteger currentSessionFailures = new AtomicInteger(0);
    protected final FutureCommandConnectionRetry<C> futureCommandConnectionRetry;
    protected volatile boolean hitBottom = false;

    public FutureCommandConnectionPool(Logger logger, ExecutorService executor, FutureCommandConnectionRetry<C> futureCommandConnectionRetry, Semaphore allConnections, FutureCommandConnectionHandleFactory<C> futureCommandConnectionHandleFactory, @Named("maxConnectionReuse") int maxConnectionReuse, BlockingQueue<C> available, BaseLifeCycle... dependencies) {
        super(logger, executor, dependencies);
        this.futureCommandConnectionRetry = futureCommandConnectionRetry;
        this.allConnections = allConnections;
        this.futureCommandConnectionHandleFactory = futureCommandConnectionHandleFactory;
        this.maxConnectionReuse = maxConnectionReuse;
        this.available = available;
    }

    @SuppressWarnings("unchecked")
    protected void setResponseException(Exception ex, C conn) {
        FutureCommand command = futureCommandConnectionRetry.getHandleFromConnection(conn).getOperation();
        command.getResponseFuture().setException(ex);
    }

    @SuppressWarnings("unchecked")
    protected void cancel(C conn) {
        FutureCommand command = futureCommandConnectionRetry.getHandleFromConnection(conn).getOperation();
        command.cancel(true);
    }


    @Provides
    public C getConnection() throws InterruptedException, TimeoutException {
        exceptionIfNotActive();
        if (!hitBottom) {
            hitBottom = available.size() == 0 && allConnections.availablePermits() == 0;
            if (hitBottom)
                logger.warn("%1s - saturated connection pool", this);
        }
        logger.debug("%1s - attempting to acquire connection; %d currently available", this, available.size());
        C conn = available.poll(1, TimeUnit.SECONDS);
        if (conn == null)
            throw new TimeoutException("could not obtain a pooled connection within 1 seconds");

        logger.trace("%1s - %2d - aquired", conn, conn.hashCode());
        if (connectionValid(conn)) {
            logger.debug("%1s - %2d - reusing", conn, conn.hashCode());
            return conn;
        } else {
            logger.debug("%1s - %2d - unusable", conn, conn.hashCode());
            allConnections.release();
            return getConnection();
        }
    }

    protected void fatalException(Exception ex, C conn) {
        setResponseException(ex, conn);
        this.exception = ex;
        allConnections.release();
        shutdown();
    }

    protected abstract boolean connectionValid(C conn);

    public FutureCommandConnectionHandle<C> getHandle(FutureCommand<?,?,?> command) throws InterruptedException, TimeoutException {
        exceptionIfNotActive();
        C conn = getConnection();
        FutureCommandConnectionHandle<C> handle = futureCommandConnectionHandleFactory.create(command, conn);
        futureCommandConnectionRetry.associateHandleWithConnection(handle, conn);
        return handle;
    }

    protected abstract void createNewConnection() throws InterruptedException;

    public interface FutureCommandConnectionHandleFactory<C> {
        @SuppressWarnings("unchecked")
	FutureCommandConnectionHandle<C> create(FutureCommand command, C conn);
    }
}

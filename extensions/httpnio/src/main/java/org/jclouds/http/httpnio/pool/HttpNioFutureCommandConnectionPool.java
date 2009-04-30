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
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.jclouds.Logger;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.pool.FutureCommandConnectionPool;
import org.jclouds.command.pool.FutureCommandConnectionRetry;
import org.jclouds.command.pool.PoolConstants;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Connection Pool for HTTP requests that utilizes Apache HTTPNio
 * 
 * @author Adrian Cole
 */
public class HttpNioFutureCommandConnectionPool extends
	FutureCommandConnectionPool<NHttpConnection> implements EventListener {

    private final NHttpClientConnectionPoolSessionRequestCallback sessionCallback;
    private final DefaultConnectingIOReactor ioReactor;
    private final IOEventDispatch dispatch;
    private final InetSocketAddress target;
    private final int maxSessionFailures;

    @Inject
    public HttpNioFutureCommandConnectionPool(
	    java.util.logging.Logger logger,
	    ExecutorService executor,
	    Semaphore allConnections,
	    BlockingQueue<NHttpConnection> available,
	    AsyncNHttpClientHandler clientHandler,
	    DefaultConnectingIOReactor ioReactor,
	    IOEventDispatch dispatch,
	    FutureCommandConnectionHandleFactory requestHandleFactory,
	    InetSocketAddress target,
	    FutureCommandConnectionRetry<NHttpConnection> futureCommandConnectionRetry,
	    @Named(PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE) int maxConnectionReuse,
	    @Named(PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES) int maxSessionFailures) {
	super(new Logger(logger), executor, futureCommandConnectionRetry,
		allConnections, requestHandleFactory, maxConnectionReuse,
		available);
	this.ioReactor = ioReactor;
	this.dispatch = dispatch;
	this.target = target;
	this.maxSessionFailures = maxSessionFailures;
	this.sessionCallback = new NHttpClientConnectionPoolSessionRequestCallback();
	clientHandler.setEventListener(this);
    }

    @Override
    public void start() {
	synchronized (this.statusLock) {
	    if (this.status.compareTo(Status.INACTIVE) == 0) {
		executor.execute(new Runnable() {
		    public void run() {
			try {
			    ioReactor.execute(dispatch);
			} catch (IOException e) {
			    exception = e;
			    logger.error(e, "Error dispatching %1s", dispatch);
			    status = Status.SHUTDOWN_REQUEST;
			}
		    }
		});
	    }
	    super.start();
	}
    }

    public void shutdownReactor(long waitMs) {
	try {
	    this.ioReactor.shutdown(waitMs);
	} catch (IOException e) {
	    logger.error(e, "Error shutting down reactor");
	}
    }

    public boolean connectionValid(NHttpConnection conn) {
	return conn.isOpen() && !conn.isStale()
		&& conn.getMetrics().getRequestCount() < maxConnectionReuse;
    }

    protected void doWork() throws Exception {
	createNewConnection();
    }

    @Override
    protected void doShutdown() {
	// Give the I/O reactor 10 sec to shut down
	shutdownReactor(10000);
    }

    protected void createNewConnection() throws InterruptedException {
	boolean acquired = allConnections.tryAcquire(1, TimeUnit.SECONDS);
	if (acquired) {
	    if (shouldDoWork()) {
		logger.debug("%1s - opening new connection", target);
		ioReactor.connect(target, null, null, sessionCallback);
	    } else {
		allConnections.release();
	    }
	}
    }

    @Override
    protected boolean shouldDoWork() {
	return super.shouldDoWork()
		&& ioReactor.getStatus().equals(IOReactorStatus.ACTIVE);
    }

    class NHttpClientConnectionPoolSessionRequestCallback implements
	    SessionRequestCallback {

	public void completed(SessionRequest request) {
	    logger.trace("%1s - %2s - operation complete", request, request
		    .getAttachment());
	}

	public void cancelled(SessionRequest request) {
	    logger.info("%1s - %2s - operation cancelled", request, request
		    .getAttachment());
	    releaseConnectionAndCancelResponse(request);
	}

	private void releaseConnectionAndCancelResponse(SessionRequest request) {
	    allConnections.release();
	    FutureCommand<?, ?, ?> frequest = (FutureCommand<?, ?, ?>) request
		    .getAttachment();
	    if (frequest != null) {
		frequest.cancel(true);
	    }
	}

	private void releaseConnectionAndSetResponseException(
		SessionRequest request, Exception e) {
	    allConnections.release();
	    FutureCommand<?, ?, ?> frequest = (FutureCommand<?, ?, ?>) request
		    .getAttachment();
	    if (frequest != null) {
		frequest.setException(e);
	    }
	}

	public void failed(SessionRequest request) {
	    int count = currentSessionFailures.getAndIncrement();
	    logger.error(request.getException(),
		    "%1s - %2s - operation failed", request, request
			    .getAttachment());
	    releaseConnectionAndSetResponseException(request, request
		    .getException());
	    if (count >= maxSessionFailures) {
		exception = request.getException();
	    }

	}

	public void timeout(SessionRequest request) {
	    logger.warn("%1s - %2s - operation timed out", request, request
		    .getAttachment());
	    releaseConnectionAndCancelResponse(request);
	}

    }

    public void connectionOpen(NHttpConnection conn) {
	conn.setSocketTimeout(0);
	available.offer(conn);
	logger.trace("%1s - %2d - open", conn, conn.hashCode());
    }

    public void connectionTimeout(NHttpConnection conn) {
	logger.warn("%1s - %2d - timeout  %2d", conn, conn.hashCode(), conn
		.getSocketTimeout());
	allConnections.release();
	futureCommandConnectionRetry.shutdownConnectionAndRetryOperation(conn);
    }

    public void connectionClosed(NHttpConnection conn) {
	allConnections.release();
	logger.trace("%1s - %2d - closed", conn, conn.hashCode());
    }

    public void fatalIOException(IOException ex, NHttpConnection conn) {
	exception = ex;
	logger.error(ex, "%1s - %2d - %3s - pool error", conn, conn.hashCode(),
		target);
	futureCommandConnectionRetry.shutdownConnectionAndRetryOperation(conn);
    }

    public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
	exception = ex;
	logger.error(ex, "%1s - %2d - %3s - http error", conn, conn.hashCode(),
		target);
	fatalException(ex, conn);
    }

    public static interface FutureCommandConnectionHandleFactory
	    extends
	    FutureCommandConnectionPool.FutureCommandConnectionHandleFactory<NHttpConnection> {
	HttpNioFutureCommandConnectionHandle create(FutureCommand command,
		NHttpConnection conn);
    }

}
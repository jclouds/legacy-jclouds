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
import com.google.inject.name.Named;
import org.apache.http.HttpException;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.pool.FutureCommandConnectionHandle;
import org.jclouds.command.pool.FutureCommandConnectionPool;
import org.jclouds.command.pool.PoolConstants;
import org.jclouds.http.HttpFutureCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Connection Pool for HTTP requests that utilizes Apache HTTPNio
 *
 * @author Adrian Cole
 */
public class HttpNioFutureCommandConnectionPool extends
        FutureCommandConnectionPool<NHttpConnection, HttpFutureCommand<?>>
        implements EventListener {

    private final NHttpClientConnectionPoolSessionRequestCallback sessionCallback;
    private final DefaultConnectingIOReactor ioReactor;
    private final IOEventDispatch dispatch;
    private final InetSocketAddress target;
    private final int maxSessionFailures;

    @Inject
    public HttpNioFutureCommandConnectionPool(
            ExecutorService executor,
            Semaphore allConnections,
            BlockingQueue<HttpFutureCommand<?>> commandQueue,
            BlockingQueue<NHttpConnection> available,
            AsyncNHttpClientHandler clientHandler,
            DefaultConnectingIOReactor ioReactor,
            IOEventDispatch dispatch,
            FutureCommandConnectionHandleFactory requestHandleFactory,
            InetSocketAddress target,
            @Named(PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE) int maxConnectionReuse,
            @Named(PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES) int maxSessionFailures) {
        super(executor, allConnections, commandQueue, requestHandleFactory,
                maxConnectionReuse, available);
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
                            exception.set(e);
                            logger.error(e, "Error dispatching %1$s", dispatch);
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

    @Override
    public boolean connectionValid(NHttpConnection conn) {
        return conn.isOpen() && !conn.isStale()
                && conn.getMetrics().getRequestCount() < maxConnectionReuse;
    }

    @Override
    public void shutdownConnection(NHttpConnection conn) {
        if (conn.getMetrics().getRequestCount() >= maxConnectionReuse)
            logger.debug(
                    "%1$s - %2$d - closing connection due to overuse %1$s/%2$s",
                    conn, conn.hashCode(), conn.getMetrics().getRequestCount(),
                    maxConnectionReuse);
        if (conn.getStatus() == NHttpConnection.ACTIVE) {
            try {
                conn.shutdown();
            } catch (IOException e) {
                logger.error(e, "Error shutting down connection");
            }
        }
    }

    @Override
    protected void doWork() throws Exception {
        createNewConnection();
    }

    @Override
    protected void doShutdown() {
        // Give the I/O reactor 1 sec to shut down
        shutdownReactor(1000);
        assert this.ioReactor.getStatus().equals(IOReactorStatus.SHUT_DOWN) : "incorrect status after io reactor shutdown :"
                + this.ioReactor.getStatus();
    }

    @Override
    protected void createNewConnection() throws InterruptedException {
        boolean acquired = allConnections.tryAcquire(1, TimeUnit.SECONDS);
        if (acquired) {
            if (shouldDoWork()) {
                logger.debug("%1$s - opening new connection", target);
                ioReactor.connect(target, null, null, sessionCallback);
            } else {
                allConnections.release();
            }
        }
    }

    @Override
    protected void associateHandleWithConnection(
            FutureCommandConnectionHandle<NHttpConnection, HttpFutureCommand<?>> handle,
            NHttpConnection connection) {
        connection.getContext().setAttribute("command-handle", handle);
    }

    @Override
    protected HttpNioFutureCommandConnectionHandle getHandleFromConnection(
            NHttpConnection connection) {
        return (HttpNioFutureCommandConnectionHandle) connection.getContext()
                .getAttribute("command-handle");
    }

    class NHttpClientConnectionPoolSessionRequestCallback implements
            SessionRequestCallback {

        public void completed(SessionRequest request) {
            logger.trace("%1$s->%2$s[%3$s] - SessionRequest complete", request
                    .getLocalAddress(), request.getRemoteAddress(), request
                    .getAttachment());
        }

        public void cancelled(SessionRequest request) {
            logger.trace("%1$s->%2$s[%3$s] - SessionRequest cancelled", request
                    .getLocalAddress(), request.getRemoteAddress(), request
                    .getAttachment());
            releaseConnectionAndCancelResponse(request);
        }

        private void releaseConnectionAndCancelResponse(SessionRequest request) {
            allConnections.release();
            FutureCommand<?, ?, ?> frequest = (FutureCommand<?, ?, ?>) request
                    .getAttachment();
            if (frequest != null) {
                logger.error("%1$s->%2$s[%3$s] - Cancelling FutureCommand",
                        request.getLocalAddress(), request.getRemoteAddress(),
                        frequest);
                frequest.cancel(true);
            }
        }

        private void releaseConnectionAndSetResponseException(
                SessionRequest request, Exception e) {
            allConnections.release();
            HttpFutureCommand<?> frequest = (HttpFutureCommand<?>) request
                    .getAttachment();
            if (frequest != null) {
                logger.error(e,
                        "%1$s->%2$s[%3$s] - Setting Exception on FutureCommand",
                        request.getLocalAddress(), request.getRemoteAddress(),
                        frequest);
                frequest.setException(e);
            }
        }

        public void failed(SessionRequest request) {
            int count = currentSessionFailures.getAndIncrement();
            logger.warn("%1$s->%2$s[%3$s] - SessionRequest failed", request
                    .getLocalAddress(), request.getRemoteAddress(), request
                    .getAttachment());
            releaseConnectionAndSetResponseException(request, request
                    .getException());
            if (count >= maxSessionFailures) {
                logger
                        .error(
                                request.getException(),
                                "%1$s->%2$s[%3$s] - SessionRequest failures: %4$s, Disabling pool for %5$s",
                                request.getLocalAddress(), request
                                        .getRemoteAddress(),
                                maxSessionFailures, target);
                exception.set(request.getException());
            }

        }

        public void timeout(SessionRequest request) {
            logger.warn("%1$s->%2$s[%3$s] - SessionRequest timeout", request
                    .getLocalAddress(), request.getRemoteAddress(), request
                    .getAttachment());
            releaseConnectionAndCancelResponse(request);
        }

    }

    public void connectionOpen(NHttpConnection conn) {
        conn.setSocketTimeout(0);
        available.offer(conn);
        logger.trace("%1$s - %2$d - open", conn, conn.hashCode());
    }

    public void connectionTimeout(NHttpConnection conn) {
        String message = String.format("%1$s - %2$d - timeout  %2$d", conn, conn
                .hashCode(), conn.getSocketTimeout());
        logger.warn(message);
        resubmitIfRequestIsReplayable(conn, new TimeoutException(message));
    }

    public void connectionClosed(NHttpConnection conn) {
        logger.trace("%1$s - %2$d - closed", conn, conn.hashCode());
    }

    public void fatalIOException(IOException ex, NHttpConnection conn) {
        logger.error(ex, "%3$s-%1$d{%2$s} - io error", conn, conn.hashCode(),
                target);
        resubmitIfRequestIsReplayable(conn, ex);
    }

    public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
        logger.error(ex, "%3$s-%1$d{%2$s} - http error", conn, conn.hashCode(),
                target);
        setExceptionOnCommand(conn, ex);
    }

    public static interface FutureCommandConnectionHandleFactory
            extends
            FutureCommandConnectionPool.FutureCommandConnectionHandleFactory<NHttpConnection, HttpFutureCommand<?>> {
        HttpNioFutureCommandConnectionHandle create(
                HttpFutureCommand<?> command, NHttpConnection conn);
    }

    @Override
    protected boolean isReplayable(HttpFutureCommand<?> command) {
        return command.getRequest().isReplayable();
    }

}
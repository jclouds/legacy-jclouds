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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.UnmappableCharacterException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpException;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.SSLClientIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.params.HttpParams;
import org.jclouds.http.HttpCommandRendezvous;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.pool.HttpCommandConnectionHandle;
import org.jclouds.http.pool.HttpCommandConnectionPool;
import org.jclouds.http.pool.PoolConstants;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Connection Pool for HTTP requests that utilizes Apache HTTPNio
 * 
 * @author Adrian Cole
 */
public class NioHttpCommandConnectionPool extends HttpCommandConnectionPool<NHttpConnection>
         implements EventListener {

   private final NHttpClientConnectionPoolSessionRequestCallback sessionCallback;
   private final DefaultConnectingIOReactor ioReactor;
   private final IOEventDispatch dispatch;
   private final InetSocketAddress target;
   private final int maxSessionFailures;

   public static interface Factory extends HttpCommandConnectionPool.Factory<NHttpConnection> {
      NioHttpCommandConnectionPool create(URI endPoint);
   }

   @Inject
   public NioHttpCommandConnectionPool(ExecutorService executor, Semaphore allConnections,
            BlockingQueue<HttpCommandRendezvous<?>> commandQueue,
            BlockingQueue<NHttpConnection> available, AsyncNHttpClientHandler clientHandler,
            DefaultConnectingIOReactor ioReactor, HttpParams params,
            @Named(PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE) int maxConnectionReuse,
            @Named(PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES) int maxSessionFailures,
            @Assisted URI endPoint) throws Exception {
      super(executor, allConnections, commandQueue, maxConnectionReuse, available, endPoint);
      String host = checkNotNull(checkNotNull(endPoint, "endPoint").getHost(), String.format(
               "Host null for endpoint %s", endPoint));
      int port = endPoint.getPort();
      if (endPoint.getScheme().equals("https")) {
         this.dispatch = provideSSLClientEventDispatch(clientHandler, params);
         if (port == -1)
            port = 443;
      } else {
         this.dispatch = provideClientEventDispatch(clientHandler, params);
         if (port == -1)
            port = 80;
      }
      checkArgument(port > 0, String.format("Port %d not in range for endpoint %s", endPoint
               .getPort(), endPoint));
      this.ioReactor = ioReactor;
      this.maxSessionFailures = maxSessionFailures;
      this.sessionCallback = new NHttpClientConnectionPoolSessionRequestCallback();
      this.target = new InetSocketAddress(host, port);
      clientHandler.setEventListener(this);
   }

   public static IOEventDispatch provideSSLClientEventDispatch(AsyncNHttpClientHandler handler,
            HttpParams params) throws Exception {
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, null, null);
      return new SSLClientIOEventDispatch(handler, context, params);
   }

   public static IOEventDispatch provideClientEventDispatch(AsyncNHttpClientHandler handler,
            HttpParams params) throws Exception {
      return new DefaultClientIOEventDispatch(handler, params);
   }

   @Override
   public void start() {
      synchronized (this.statusLock) {
         if (this.status.compareTo(Status.INACTIVE) == 0) {
            executorService.execute(new Runnable() {
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
         logger.debug("%1$s - %2$d - closing connection due to overuse %1$s/%2$s", conn, conn
                  .hashCode(), conn.getMetrics().getRequestCount(), maxConnectionReuse);
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
            logger.debug("%1$s - opening new connection", getTarget());
            ioReactor.connect(getTarget(), null, null, sessionCallback);
         } else {
            allConnections.release();
         }
      }
   }

   @Override
   protected void associateHandleWithConnection(
            HttpCommandConnectionHandle<NHttpConnection> handle, NHttpConnection connection) {
      connection.getContext().setAttribute("command-handle", handle);
   }

   @Override
   protected NioHttpCommandConnectionHandle getHandleFromConnection(NHttpConnection connection) {
      return (NioHttpCommandConnectionHandle) connection.getContext()
               .getAttribute("command-handle");
   }

   class NHttpClientConnectionPoolSessionRequestCallback implements SessionRequestCallback {

      public void completed(SessionRequest request) {
         logger.trace("%1$s->%2$s[%3$s] - SessionRequest complete", request.getLocalAddress(),
                  request.getRemoteAddress(), request.getAttachment());
      }

      public void cancelled(SessionRequest request) {
         logger.trace("%1$s->%2$s[%3$s] - SessionRequest cancelled", request.getLocalAddress(),
                  request.getRemoteAddress(), request.getAttachment());
         releaseConnectionAndCancelResponse(request);
      }

      private void releaseConnectionAndCancelResponse(SessionRequest request) {
         allConnections.release();
         TransformingHttpCommandExecutorService frequest = (TransformingHttpCommandExecutorService) request
                  .getAttachment();
         if (frequest != null) {
            logger.error("%1$s->%2$s[%3$s] - Cancelling FutureCommand", request.getLocalAddress(),
                     request.getRemoteAddress(), frequest);
            // TODO frequest.cancel(true);
         }
      }

      private void releaseConnectionAndSetResponseException(SessionRequest request, Exception e) {
         allConnections.release();
         TransformingHttpCommand<?> frequest = (TransformingHttpCommand<?>) request.getAttachment();
         if (frequest != null) {
            logger.error(e, "%1$s->%2$s[%3$s] - Setting Exception on FutureCommand", request
                     .getLocalAddress(), request.getRemoteAddress(), frequest);
            frequest.setException(e);
         }
      }

      public void failed(SessionRequest request) {
         int count = currentSessionFailures.getAndIncrement();
         logger.warn("%1$s->%2$s[%3$s] - SessionRequest failed", request.getLocalAddress(), request
                  .getRemoteAddress(), request.getAttachment());
         releaseConnectionAndSetResponseException(request, request.getException());
         if (count >= maxSessionFailures) {
            logger.error(request.getException(),
                     "%1$s->%2$s[%3$s] - SessionRequest failures: %4$s, Disabling pool for %5$s",
                     request.getLocalAddress(), request.getRemoteAddress(), maxSessionFailures,
                     getTarget());
            exception.set(request.getException());
         }

      }

      public void timeout(SessionRequest request) {
         logger.warn("%1$s->%2$s[%3$s] - SessionRequest timeout", request.getLocalAddress(),
                  request.getRemoteAddress(), request.getAttachment());
         releaseConnectionAndCancelResponse(request);
      }

   }

   public void connectionOpen(NHttpConnection conn) {
      conn.setSocketTimeout(0);
      available.offer(conn);
      logger.trace("%1$s - %2$d - open", conn, conn.hashCode());
   }

   public void connectionTimeout(NHttpConnection conn) {
      String message = String.format("%1$s - %2$d - timeout  %2$d", conn, conn.hashCode(), conn
               .getSocketTimeout());
      logger.warn(message);
      resubmitIfRequestIsReplayable(conn, new TimeoutException(message));
   }

   public void connectionClosed(NHttpConnection conn) {
      logger.trace("%1$s - %2$d - closed", conn, conn.hashCode());
   }

   public void fatalIOException(IOException ex, NHttpConnection conn) {
      logger.error(ex, "%3$s-%1$s{%2$d} - io error", conn, conn.hashCode(), getTarget());
      HttpCommandRendezvous<?> rendezvous = getCommandFromConnection(conn);
      if (rendezvous != null) {
         /**
          * these exceptions, while technically i/o are unresolvable. set the error on the command
          * itself so that it doesn't replay.
          */
         if (ex instanceof UnmappableCharacterException) {
            setExceptionOnCommand(ex, rendezvous);
         } else {
            resubmitIfRequestIsReplayable(conn, ex);
         }
      }
   }

   public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
      logger.error(ex, "%3$s-%1$s{%2$d} - http error", conn, conn.hashCode(), getTarget());
      setExceptionOnCommand(conn, ex);
   }

   @Override
   protected NioHttpCommandConnectionHandle createHandle(HttpCommandRendezvous<?> command,
            NHttpConnection conn) {
      try {
         return new NioHttpCommandConnectionHandle(allConnections, available, endPoint, command,
                  conn);
      } catch (InterruptedException e) {
         throw new RuntimeException("Interrupted creating a handle to " + conn, e);
      }
   }

   @Override
   protected boolean isReplayable(HttpCommandRendezvous<?> rendezvous) {
      return rendezvous.getCommand().isReplayable();
   }

   @VisibleForTesting
   InetSocketAddress getTarget() {
      return target;
   }

}
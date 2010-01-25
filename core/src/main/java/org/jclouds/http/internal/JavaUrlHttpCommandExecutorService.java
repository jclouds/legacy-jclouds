/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

/**
 * Basic implementation of a {@link HttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JavaUrlHttpCommandExecutorService extends
         BaseHttpCommandExecutorService<HttpURLConnection> {

   public static final String USER_AGENT = "jclouds/1.0 java/" + System.getProperty("java.version");

   @Inject(optional = true)
   @Named(Constants.PROPERTY_RELAX_HOSTNAME)
   private boolean relaxHostname = false;
   private final Map<String, String> sslMap;
   @Nullable
   private final Semaphore globalMaxConnections;

   @Nullable
   private final ConcurrentMap<String, Semaphore> connectionLimiterMap;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_SYSTEM)
   private boolean systemProxies = System.getProperty("java.net.useSystemProxies") != null ? Boolean
            .parseBoolean(System.getProperty("java.net.useSystemProxies"))
            : false;
   private static final AtomicInteger currentConnections = new AtomicInteger(0);

   @Inject
   public JavaUrlHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler,
            HttpWire wire,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT) final int globalMaxConnections,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST) final int globalMaxConnectionsPerHost) {
      super(ioWorkerExecutor, userExecutor, retryHandler, errorHandler, wire);
      sslMap = Maps.newHashMap();
      this.globalMaxConnections = globalMaxConnections > 0 ? new Semaphore(globalMaxConnections,
               true) : null;
      this.connectionLimiterMap = globalMaxConnectionsPerHost > 0 ? new MapMaker()
               .<String, Semaphore> makeComputingMap(new Function<String, Semaphore>() {
                  public Semaphore apply(String uri) {
                     logger
                              .info(
                                       "host(%s), globalMaxConnections(%d), maxConnectionsPerHost(%d)",
                                       uri, globalMaxConnections, globalMaxConnectionsPerHost);

                     return new Semaphore(globalMaxConnectionsPerHost);
                  }
               })
               : null;
   }

   /**
    * 
    * Used to get more information about HTTPS hostname wrong errors.
    * 
    * @author Adrian Cole
    */
   class LogToMapHostnameVerifier implements HostnameVerifier {

      public boolean verify(String hostname, SSLSession session) {
         logger.warn("hostname was %s while session was %s", hostname, session.getPeerHost());
         sslMap.put(hostname, session.getPeerHost());
         return true;
      }
   }

   private static class ReleaseSemaphoreOnCloseOutputStream extends FilterOutputStream {

      private final String host;
      @Nullable
      private final Semaphore globalMaxConnections;
      @Nullable
      private final Map<String, Semaphore> connectionLimiterMap;
      private volatile AtomicBoolean closed = new AtomicBoolean(false);

      protected ReleaseSemaphoreOnCloseOutputStream(final Semaphore globalMaxConnections,
               final Map<String, Semaphore> connectionLimiterMap, final HttpURLConnection connection)
               throws InterruptedException {
         super(aquireAndGetStream(globalMaxConnections, connectionLimiterMap, connection,
                  new Supplier<OutputStream>() {
                     public OutputStream get() {
                        try {
                           return checkNotNull(connection.getOutputStream(), "output stream");
                        } catch (IOException e) {
                           releaseConnection(globalMaxConnections, connectionLimiterMap, connection
                                    .getURL().getHost());
                           throw new RuntimeException(e);
                        }
                     }
                  }));
         this.globalMaxConnections = globalMaxConnections;
         this.connectionLimiterMap = connectionLimiterMap;
         this.host = connection.getURL().getHost();
      }

      @Override
      public void close() throws IOException {
         if (closed.compareAndSet(false, true)) {
            if (out != null)
               super.close();
            releaseConnection(globalMaxConnections, connectionLimiterMap, host);
         }
      }

   }

   private static class ReleaseSemaphoreOnCloseInputStream extends FilterInputStream {
      private final String host;
      @Nullable
      private final Semaphore globalMaxConnections;
      @Nullable
      private final Map<String, Semaphore> connectionLimiterMap;
      private volatile AtomicBoolean closed = new AtomicBoolean(false);

      protected ReleaseSemaphoreOnCloseInputStream(Semaphore globalMaxConnections,
               Map<String, Semaphore> connectionLimiterMap, final HttpURLConnection connection)
               throws InterruptedException {
         super(aquireAndGetStream(globalMaxConnections, connectionLimiterMap, connection,
                  new Supplier<InputStream>() {
                     public InputStream get() {
                        try {
                           return connection.getInputStream() != null ? connection.getInputStream()
                                    : checkNotNull(connection.getErrorStream(),
                                             "input and error streams");
                        } catch (IOException e) {
                           return checkNotNull(connection.getErrorStream(), "error streams");
                        }
                     }
                  }));
         this.globalMaxConnections = globalMaxConnections;
         this.connectionLimiterMap = connectionLimiterMap;
         this.host = connection.getURL().getHost();
      }

      @Override
      public void close() throws IOException {
         if (closed.compareAndSet(false, true)) {
            if (in != null)
               super.close();
            releaseConnection(globalMaxConnections, connectionLimiterMap, host);
         }
      }

   }

   private static <T> T aquireAndGetStream(Semaphore globalMaxConnections,
            Map<String, Semaphore> connectionLimiterMap, HttpURLConnection connection,
            Supplier<T> provider) throws InterruptedException {
      String host = checkNotNull(connection.getURL().getHost(), "host");
      try {
         if (globalMaxConnections != null) {
            globalMaxConnections.acquire();
         }
         if (connectionLimiterMap != null) {
            Semaphore semaphore = connectionLimiterMap.get(host);
            semaphore.acquire();
         }
         T returnVal = provider.get();
         currentConnections.incrementAndGet();
         return returnVal;
      } catch (InterruptedException e) {
         releaseConnection(globalMaxConnections, connectionLimiterMap, host);
         throw e;
      } catch (NullPointerException e) {
         releaseConnection(globalMaxConnections, connectionLimiterMap, host);
         throw e;
      }
   }

   private static void releaseConnection(Semaphore globalMaxConnections,
            Map<String, Semaphore> connectionLimiterMap, String host) {
      if (connectionLimiterMap != null)
         connectionLimiterMap.get(host).release();
      if (globalMaxConnections != null)
         globalMaxConnections.release();
      currentConnections.decrementAndGet();
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException,
            InterruptedException {
      HttpResponse response = new HttpResponse();
      InputStream in = null;
      boolean noContent = false;
      try {
         in = new ReleaseSemaphoreOnCloseInputStream(globalMaxConnections, connectionLimiterMap,
                  connection);
      } catch (NullPointerException e) {
         noContent = true;
      } catch (RuntimeException e) {
         Throwables.propagate(e);
         assert false : "should have propagated exception";
      }
      for (String header : connection.getHeaderFields().keySet()) {
         response.getHeaders().putAll(header, connection.getHeaderFields().get(header));
         // release the semaphore early, if there is no content.
         if (header != null && header.equals(HttpHeaders.CONTENT_LENGTH)
                  && connection.getHeaderFields().get(header).get(0).equals("0")) {
            noContent = true;
         }
      }
      if (noContent || connection.getResponseCode() == 204) {
         Closeables.closeQuietly(in);
         in = null;
      } else if (in != null) {
         response.setContent(in);
      }

      response.setStatusCode(connection.getResponseCode());
      response.setMessage(connection.getResponseMessage());
      return response;
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException,
            InterruptedException {
      URL url = request.getEndpoint().toURL();
      HttpURLConnection connection;
      if (systemProxies) {
         System.setProperty("java.net.useSystemProxies", "true");
         Iterable<Proxy> proxies = ProxySelector.getDefault().select(request.getEndpoint());
         Proxy proxy = Iterables.getLast(proxies);
         connection = (HttpURLConnection) url.openConnection(proxy);
      } else {
         connection = (HttpURLConnection) url.openConnection();
      }
      if (relaxHostname && connection instanceof HttpsURLConnection) {
         HttpsURLConnection sslCon = (HttpsURLConnection) connection;
         sslCon.setHostnameVerifier(new LogToMapHostnameVerifier());
      }
      connection.setDoOutput(true);
      connection.setAllowUserInteraction(false);
      // do not follow redirects since https redirects don't work properly
      // ex. Caused by: java.io.IOException: HTTPS hostname wrong: should be
      // <adriancole.s3int0.s3-external-3.amazonaws.com>
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod(request.getMethod().toString());
      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header)) {
            connection.setRequestProperty(header, value);
            if ("Transfer-Encoding".equals(header) && "chunked".equals(value)) {
               connection.setChunkedStreamingMode(8192);
            }
         }
      }
      connection.setRequestProperty(HttpHeaders.HOST, request.getEndpoint().getHost());
      connection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT);

      if (request.getPayload() != null) {
         try {
            OutputStream out = new ReleaseSemaphoreOnCloseOutputStream(globalMaxConnections,
                     connectionLimiterMap, connection);
            try {
               request.getPayload().writeTo(out);
            } finally {
               Closeables.closeQuietly(out);
            }
         } catch (RuntimeException e) {
            Throwables.propagate(e);
         }
      } else {
         connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, "0");
      }
      return connection;
   }

   /**
    * Only disconnect if there is no content, as disconnecting will throw away unconsumed content.
    */
   @Override
   protected void cleanup(HttpURLConnection connection) {
      if (connection != null && connection.getContentLength() == 0)
         connection.disconnect();
   }

}

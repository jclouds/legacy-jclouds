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

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
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
   @Resource
   protected Logger logger = Logger.NULL;
   private final HostnameVerifier verifier;
   private final HttpUtils utils;

   @Inject
   public JavaUrlHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler,
            HttpWire wire, HttpUtils utils, HostnameVerifier verifier) {
      super(ioWorkerExecutor, retryHandler, errorHandler, wire);
      if (utils.getMaxConnections() > 0)
         System.setProperty("http.maxConnections", String.valueOf(utils.getMaxConnections()));
      this.utils = utils;
      this.verifier = verifier;
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException,
            InterruptedException {
      HttpResponse response = new HttpResponse();
      InputStream in = null;
      try {
         in = consumeOnClose(connection.getInputStream());
      } catch (IOException e) {
         in = bufferAndCloseStream(connection.getErrorStream());
      } catch (RuntimeException e) {
         Closeables.closeQuietly(in);
         Throwables.propagate(e);
         assert false : "should have propagated exception";
      }
      for (String header : connection.getHeaderFields().keySet()) {
         response.getHeaders().putAll(header, connection.getHeaderFields().get(header));
      }
      if (connection.getResponseCode() == 204) {
         Closeables.closeQuietly(in);
         in = null;
      } else if (in != null) {
         response.setContent(in);
      }
      response.setStatusCode(connection.getResponseCode());
      response.setMessage(connection.getResponseMessage());
      return response;
   }

   public InputStream consumeOnClose(InputStream in) {
      return new ConsumeOnCloseInputStream(in);
   }

   class ConsumeOnCloseInputStream extends FilterInputStream {

      protected ConsumeOnCloseInputStream(InputStream in) {
         super(in);
      }

      boolean closed;

      @Override
      public void close() throws IOException {
         try {
            if (!closed) {
               int result = 0;
               while (result != -1) {
                  result = read();
               }
            }
         } catch (IOException e) {
            logger.warn(e, "error reading stream");
         } finally {
            closed = true;
            super.close();
         }
      }

   }

   private InputStream bufferAndCloseStream(InputStream inputStream) throws IOException {
      InputStream in = null;
      try {
         if (inputStream != null) {
            in = new ByteArrayInputStream(ByteStreams.toByteArray(inputStream));
         }
      } finally {
         Closeables.closeQuietly(inputStream);
      }
      return in;
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException,
            InterruptedException {
      URL url = request.getEndpoint().toURL();
      HttpURLConnection connection;
      if (utils.useSystemProxies()) {
         System.setProperty("java.net.useSystemProxies", "true");
         Iterable<Proxy> proxies = ProxySelector.getDefault().select(request.getEndpoint());
         Proxy proxy = Iterables.getLast(proxies);
         connection = (HttpURLConnection) url.openConnection(proxy);
      } else if (utils.getProxyHost() != null) {
         SocketAddress addr = new InetSocketAddress(utils.getProxyHost(), utils.getProxyPort());
         Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
         Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
               return (new PasswordAuthentication(utils.getProxyUser(), utils.getProxyPassword()
                        .toCharArray()));
            }
         };
         Authenticator.setDefault(authenticator);
         connection = (HttpURLConnection) url.openConnection(proxy);
      } else {
         connection = (HttpURLConnection) url.openConnection();
      }
      if (utils.relaxHostname() && connection instanceof HttpsURLConnection) {
         HttpsURLConnection sslCon = (HttpsURLConnection) connection;
         sslCon.setHostnameVerifier(verifier);
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
         OutputStream out = connection.getOutputStream();
         try {
            request.getPayload().writeTo(out);
         } finally {
            out.flush();
            Closeables.closeQuietly(out);
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

/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.io.CountingOutputStream;

/**
 * Basic implementation of a {@link HttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JavaUrlHttpCommandExecutorService extends BaseHttpCommandExecutorService<HttpURLConnection> {

   public static final String USER_AGENT = "jclouds/1.0 java/" + System.getProperty("java.version");
   @Resource
   protected Logger logger = Logger.NULL;
   private final Supplier<SSLContext> untrustedSSLContextProvider;
   private final HostnameVerifier verifier;
   private final Field methodField;

   @Inject
   public JavaUrlHttpCommandExecutorService(HttpUtils utils,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
            DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
            @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider) throws SecurityException,
            NoSuchFieldException {
      super(utils, ioWorkerExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
      if (utils.getMaxConnections() > 0)
         System.setProperty("http.maxConnections", String.valueOf(checkNotNull(utils, "utils").getMaxConnections()));
      this.untrustedSSLContextProvider = checkNotNull(untrustedSSLContextProvider, "untrustedSSLContextProvider");
      this.verifier = checkNotNull(verifier, "verifier");
      this.methodField = HttpURLConnection.class.getDeclaredField("method");
      methodField.setAccessible(true);
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException, InterruptedException {
      HttpResponse.Builder builder = HttpResponse.builder();
      InputStream in = null;
      try {
         in = consumeOnClose(connection.getInputStream());
      } catch (IOException e) {
         in = bufferAndCloseStream(connection.getErrorStream());
      } catch (RuntimeException e) {
         closeQuietly(in);
         propagate(e);
         assert false : "should have propagated exception";
      }

      int responseCode = connection.getResponseCode();
      if (responseCode == 204) {
         closeQuietly(in);
         in = null;
      }
      builder.statusCode(responseCode);
      builder.message(connection.getResponseMessage());

      Builder<String, String> headerBuilder = ImmutableMultimap.<String, String> builder();
      for (String header : connection.getHeaderFields().keySet()) {
         // HTTP message comes back as a header without a key
         if (header != null)
            headerBuilder.putAll(header, connection.getHeaderFields().get(header));
      }
      ImmutableMultimap<String, String> headers = headerBuilder.build();
      Payload payload = in != null ? Payloads.newInputStreamPayload(in) : null;
      if (payload != null) {
         payload.getContentMetadata().setPropertiesFromHttpHeaders(headers);
         builder.payload(payload);
      }
      builder.headers(RestAnnotationProcessor.filterOutContentHeaders(headers));
      return builder.build();
   }

   private InputStream bufferAndCloseStream(InputStream inputStream) throws IOException {
      InputStream in = null;
      try {
         if (inputStream != null) {
            in = new ByteArrayInputStream(toByteArray(inputStream));
         }
      } finally {
         closeQuietly(inputStream);
      }
      return in;
   }

   @Override
   protected HttpURLConnection convert(HttpRequest request) throws IOException, InterruptedException {
      boolean chunked = "chunked".equals(request.getFirstHeaderOrNull("Transfer-Encoding"));
      URL url = request.getEndpoint().toURL();

      HttpURLConnection connection;

      if (utils.useSystemProxies()) {
         System.setProperty("java.net.useSystemProxies", "true");
         Iterable<Proxy> proxies = ProxySelector.getDefault().select(request.getEndpoint());
         Proxy proxy = getLast(proxies);
         connection = (HttpURLConnection) url.openConnection(proxy);
      } else if (utils.getProxyHost() != null) {
         SocketAddress addr = new InetSocketAddress(utils.getProxyHost(), utils.getProxyPort());
         Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
         Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
               return (new PasswordAuthentication(utils.getProxyUser(), utils.getProxyPassword().toCharArray()));
            }
         };
         Authenticator.setDefault(authenticator);
         connection = (HttpURLConnection) url.openConnection(proxy);
      } else {
         connection = (HttpURLConnection) url.openConnection();
      }
      if (connection instanceof HttpsURLConnection) {
         HttpsURLConnection sslCon = (HttpsURLConnection) connection;
         if (utils.relaxHostname())
            sslCon.setHostnameVerifier(verifier);
         if (utils.trustAllCerts())
            sslCon.setSSLSocketFactory(untrustedSSLContextProvider.get().getSocketFactory());
      }
      if (utils.getConnectionTimeout() > 0) {
         connection.setConnectTimeout(utils.getConnectionTimeout());
      }
      if (utils.getSocketOpenTimeout() > 0) {
         connection.setReadTimeout(utils.getSocketOpenTimeout());
      }
      connection.setDoOutput(true);
      connection.setAllowUserInteraction(false);
      // do not follow redirects since https redirects don't work properly
      // ex. Caused by: java.io.IOException: HTTPS hostname wrong: should be
      // <adriancole.s3int0.s3-external-3.amazonaws.com>
      connection.setInstanceFollowRedirects(false);
      try {
         connection.setRequestMethod(request.getMethod());
      } catch (ProtocolException e) {
         try {
            methodField.set(connection, request.getMethod());
         } catch (Exception e1) {
            logger.error(e, "could not set request method: ", request.getMethod());
            Throwables.propagate(e1);
         }
      }

      for (String header : request.getHeaders().keys()) {
         for (String value : request.getHeaders().get(header)) {
            connection.setRequestProperty(header, value);
         }
      }
      connection.setRequestProperty(HttpHeaders.HOST, request.getEndpoint().getHost());
      connection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT);

      if (request.getPayload() != null) {
         MutableContentMetadata md = request.getPayload().getContentMetadata();
         if (md.getContentMD5() != null)
            connection.setRequestProperty("Content-MD5", CryptoStreams.base64(md.getContentMD5()));
         if (md.getContentType() != null)
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, md.getContentType());
         if (md.getContentDisposition() != null)
            connection.setRequestProperty("Content-Disposition", md.getContentDisposition());
         if (md.getContentEncoding() != null)
            connection.setRequestProperty("Content-Encoding", md.getContentEncoding());
         if (md.getContentLanguage() != null)
            connection.setRequestProperty("Content-Language", md.getContentLanguage());
         if (chunked) {
            connection.setChunkedStreamingMode(8196);
         } else {
            Long length = checkNotNull(md.getContentLength(), "payload.getContentLength");
            connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, length.toString());
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6755625
            checkArgument(length < Integer.MAX_VALUE,
                     "JDK 1.6 does not support >2GB chunks. Use chunked encoding, if possible.");
            connection.setFixedLengthStreamingMode(length.intValue());
            if (length.intValue() > 0) {
              connection.setRequestProperty("Expect", "100-continue");
            }
         }
         CountingOutputStream out = new CountingOutputStream(connection.getOutputStream());
         try {
            request.getPayload().writeTo(out);
         } catch (IOException e) {
            throw new RuntimeException(String.format("error after writing %d/%s bytes to %s", out.getCount(), md
                     .getContentLength(), request.getRequestLine()), e);
         }
      } else {
         connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, "0");
         // for some reason POST/PUT undoes the content length header above.
         if (connection.getRequestMethod().equals("POST") || connection.getRequestMethod().equals("PUT"))
            connection.setFixedLengthStreamingMode(0);
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

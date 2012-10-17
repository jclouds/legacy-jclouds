/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static javax.ws.rs.core.HttpHeaders.CONTENT_ENCODING;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LANGUAGE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.EXPIRES;
import static org.jclouds.util.Patterns.PATTERN_THAT_BREAKS_URI;
import static org.jclouds.util.Patterns.URI_PATTERN;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.logging.internal.Wire;
import org.jclouds.util.Strings2;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class HttpUtils {

   @Inject(optional = true)
   @Named(Constants.PROPERTY_RELAX_HOSTNAME)
   private boolean relaxHostname = false;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_SYSTEM)
   private boolean systemProxies = System.getProperty("java.net.useSystemProxies") != null ? Boolean
         .parseBoolean(System.getProperty("java.net.useSystemProxies")) : false;

   private final int globalMaxConnections;
   private final int globalMaxConnectionsPerHost;
   private final int connectionTimeout;
   private final int soTimeout;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_HOST)
   private String proxyHost;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_PORT)
   private Integer proxyPort;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_USER)
   private String proxyUser;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_PROXY_PASSWORD)
   private String proxyPassword;
   @Inject(optional = true)
   @Named(Constants.PROPERTY_TRUST_ALL_CERTS)
   private boolean trustAllCerts;

   @Inject
   public HttpUtils(@Named(Constants.PROPERTY_CONNECTION_TIMEOUT) int connectionTimeout,
         @Named(Constants.PROPERTY_SO_TIMEOUT) int soTimeout,
         @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT) int globalMaxConnections,
         @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST) int globalMaxConnectionsPerHost) {
      this.soTimeout = soTimeout;
      this.connectionTimeout = connectionTimeout;
      this.globalMaxConnections = globalMaxConnections;
      this.globalMaxConnectionsPerHost = globalMaxConnectionsPerHost;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_HOST
    */
   public String getProxyHost() {
      return proxyHost;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PORT
    */
   public Integer getProxyPort() {
      return proxyPort;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_USER
    */
   public String getProxyUser() {
      return proxyUser;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PASSWORD
    */
   public String getProxyPassword() {
      return proxyPassword;
   }

   public int getSocketOpenTimeout() {
      return soTimeout;
   }

   public int getConnectionTimeout() {
      return connectionTimeout;
   }

   public boolean relaxHostname() {
      return relaxHostname;
   }

   public boolean trustAllCerts() {
      return trustAllCerts;
   }

   public boolean useSystemProxies() {
      return systemProxies;
   }

   public int getMaxConnections() {
      return globalMaxConnections;
   }

   public int getMaxConnectionsPerHost() {
      return globalMaxConnectionsPerHost;
   }

   /**
    * keys to the map are only used for socket information, not path. In this case, you should
    * remove any path or query details from the URI.
    */
   public static URI createBaseEndpointFor(URI endpoint) {
      if (endpoint.getPort() == -1) {
         return URI.create(String.format("%s://%s", endpoint.getScheme(), endpoint.getHost()));
      } else {
         return URI.create(String.format("%s://%s:%d", endpoint.getScheme(), endpoint.getHost(), endpoint.getPort()));
      }
   }

   public static byte[] toByteArrayOrNull(PayloadEnclosing response) {
      if (response.getPayload() != null) {
         InputStream input = response.getPayload().getInput();
         try {
            return toByteArray(input);
         } catch (IOException e) {
            propagate(e);
         } finally {
            closeQuietly(input);
         }
      }
      return null;
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    * 
    * @throws IOException
    */
   public static byte[] closeClientButKeepContentStream(PayloadEnclosing response) {
      byte[] returnVal = toByteArrayOrNull(response);
      if (returnVal != null && !response.getPayload().isRepeatable()) {
         Payload newPayload = Payloads.newByteArrayPayload(returnVal);
         MutableContentMetadata fromMd = response.getPayload().getContentMetadata();
         MutableContentMetadata toMd = newPayload.getContentMetadata();
         copy(fromMd, toMd);
         response.setPayload(newPayload);
      }
      return returnVal;
   }

   public static void copy(ContentMetadata fromMd, MutableContentMetadata toMd) {
      toMd.setContentLength(fromMd.getContentLength());
      toMd.setContentMD5(fromMd.getContentMD5());
      toMd.setContentType(fromMd.getContentType());
      toMd.setContentDisposition(fromMd.getContentDisposition());
      toMd.setContentEncoding(fromMd.getContentEncoding());
      toMd.setContentLanguage(fromMd.getContentLanguage());
      toMd.setExpires(fromMd.getExpires());
   }

   public static URI parseEndPoint(String hostHeader) {
      URI redirectURI = URI.create(hostHeader);
      String scheme = redirectURI.getScheme();

      checkState(redirectURI.getScheme().startsWith("http"),
            String.format("header %s didn't parse an http scheme: [%s]", hostHeader, scheme));
      int port = redirectURI.getPort() > 0 ? redirectURI.getPort() : redirectURI.getScheme().equals("https") ? 443 : 80;
      String host = redirectURI.getHost();
      checkState(host.indexOf('/') == -1,
            String.format("header %s didn't parse an http host correctly: [%s]", hostHeader, host));
      URI endPoint = URI.create(String.format("%s://%s:%d", scheme, host, port));
      return endPoint;
   }

   public static URI replaceHostInEndPoint(URI endPoint, String host) {
      return URI.create(endPoint.toString().replace(endPoint.getHost(), host));
   }

   /**
    * Used to extract the URI and authentication data from a String. Note that the java URI class
    * breaks, if there are special characters like '/' present. Otherwise, we wouldn't need this
    * class, and we could simply use URI.create("uri").getUserData(); Also, URI breaks if there are
    * curly braces.
    * 
    */
   public static URI createUri(String uriPath) {
      List<String> onQuery = newArrayList(Splitter.on('?').split(uriPath));
      if (onQuery.size() == 2) {
         onQuery.add(Strings2.urlEncode(onQuery.remove(1), '=', '&'));
         uriPath = Joiner.on('?').join(onQuery);
      }
      if (uriPath.indexOf('@') != 1) {
         List<String> parts = newArrayList(Splitter.on('@').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         if (parts.size() > 1) {
            parts = newArrayList(Strings2.urlEncode(Joiner.on('@').join(parts), '/', ':'));
         }
         parts.add(Strings2.urlEncode(path, '/', ':'));
         uriPath = Joiner.on('@').join(parts);
      } else {
         List<String> parts = newArrayList(Splitter.on('/').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         parts.add(Strings2.urlEncode(path, ':'));
         uriPath = Joiner.on('/').join(parts);
      }

      if (PATTERN_THAT_BREAKS_URI.matcher(uriPath).matches()) {
         // Compile and use regular expression
         Matcher matcher = URI_PATTERN.matcher(uriPath);
         if (matcher.find()) {
            String scheme = matcher.group(1);
            String rest = matcher.group(4);
            String identity = matcher.group(2);
            String key = matcher.group(3);
            return URI.create(String.format("%s://%s:%s@%s", scheme, Strings2.urlEncode(identity),
                  Strings2.urlEncode(key), rest));
         } else {
            throw new IllegalArgumentException("bad syntax");
         }
      } else {
         return URI.create(uriPath);
      }
   }

   public void logRequest(Logger logger, HttpRequest request, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, request.getRequestLine().toString());
         logMessage(logger, request, prefix);
      }
   }

   private void logMessage(Logger logger, HttpMessage message, String prefix) {
      for (Entry<String, String> header : message.getHeaders().entries()) {
         if (header.getKey() != null)
            logger.debug("%s %s: %s", prefix, header.getKey(), header.getValue());
      }
      if (message.getPayload() != null) {
         if (message.getPayload().getContentMetadata().getContentType() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_TYPE, message.getPayload().getContentMetadata().getContentType());
         if (message.getPayload().getContentMetadata().getContentLength() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_LENGTH, message.getPayload().getContentMetadata()
                  .getContentLength());
         byte[] md5 = message.getPayload().getContentMetadata().getContentMD5();
         if (md5 != null)
            logger.debug("%s %s: %s", prefix, "Content-MD5", CryptoStreams.base64(md5));
         if (message.getPayload().getContentMetadata().getContentDisposition() != null)
            logger.debug("%s %s: %s", prefix, "Content-Disposition", message.getPayload().getContentMetadata()
                  .getContentDisposition());
         if (message.getPayload().getContentMetadata().getContentEncoding() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_ENCODING, message.getPayload().getContentMetadata()
                  .getContentEncoding());
         if (message.getPayload().getContentMetadata().getContentLanguage() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_LANGUAGE, message.getPayload().getContentMetadata()
                  .getContentLanguage());
         if (message.getPayload().getContentMetadata().getExpires() != null)
            logger.debug("%s %s: %s", prefix, EXPIRES, message.getPayload().getContentMetadata()
                  .getExpires());
      }
   }

   public void logResponse(Logger logger, HttpResponse response, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, response.getStatusLine().toString());
         logMessage(logger, response, prefix);
      }
   }

   public static String sortAndConcatHeadersIntoString(Multimap<String, String> headers) {
      StringBuilder buffer = new StringBuilder();
      SortedSetMultimap<String, String> sortedMap = TreeMultimap.create();
      sortedMap.putAll(headers);
      for (Entry<String, String> header : sortedMap.entries()) {
         if (header.getKey() != null)
            buffer.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
      }
      return buffer.toString();
   }

   public void checkRequestHasRequiredProperties(HttpRequest message) {
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull(CONTENT_TYPE) == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentType(value) as opposed to adding a content type header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull(CONTENT_LENGTH) == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentLength(value) as opposed to adding a content length header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getPayload().getContentMetadata().getContentLength() != null
                  || "chunked".equalsIgnoreCase(message.getFirstHeaderOrNull("Transfer-Encoding")),
            "either chunked encoding must be set on the http request or contentlength set on the payload: " + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull("Content-MD5") == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentMD5(value) as opposed to adding a content md5 header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull("Content-Disposition") == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentDisposition(value) as opposed to adding a content disposition header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull(CONTENT_ENCODING) == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentEncoding(value) as opposed to adding a content encoding header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull(CONTENT_LANGUAGE) == null,
            "configuration error please use request.getPayload().getContentMetadata().setContentLanguage(value) as opposed to adding a content language header: "
                  + message);
      checkArgument(
            message.getPayload() == null || message.getFirstHeaderOrNull(EXPIRES) == null,
            "configuration error please use request.getPayload().getContentMetadata().setExpires(value) as opposed to adding an expires header: "
                  + message);
   }

   public static void releasePayload(HttpMessage from) {
      if (from.getPayload() != null)
         from.getPayload().release();
   }

   public static String nullToEmpty(byte[] md5) {
      return md5 != null ? CryptoStreams.base64(md5) : "";
   }

   public static String nullToEmpty(Collection<String> collection) {
      return (collection == null || collection.isEmpty()) ? "" : collection.iterator().next();
   }

   public static Long attemptToParseSizeAndRangeFromHeaders(HttpMessage from) throws HttpException {
      String contentRange = from.getFirstHeaderOrNull("Content-Range");
      if (contentRange == null && from.getPayload() != null) {
         return from.getPayload().getContentMetadata().getContentLength();
      } else if (contentRange != null) {
         return Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1));
      }
      return null;
   }

   public static void checkRequestHasContentLengthOrChunkedEncoding(HttpMessage request, String message) {
      boolean chunked = "chunked".equals(request.getFirstHeaderOrNull("Transfer-Encoding"));
      checkArgument(request.getPayload() == null || chunked
            || request.getPayload().getContentMetadata().getContentLength() != null, message);
   }

   public static void wirePayloadIfEnabled(Wire wire, HttpMessage request) {
      if (request.getPayload() != null && wire.enabled()) {
         wire.output(request);
         checkRequestHasContentLengthOrChunkedEncoding(request,
               "After wiring, the request has neither chunked encoding nor content length: " + request);
      }
   }

   public static <T> T returnValueOnCodeOrNull(Exception from, T value, Predicate<Integer> codePredicate) {
      Iterable<HttpResponseException> throwables = filter(getCausalChain(from), HttpResponseException.class);
      if (size(throwables) >= 1 && get(throwables, 0).getResponse() != null
            && codePredicate.apply(get(throwables, 0).getResponse().getStatusCode())) {
         return value;
      }
      return null;
   }
}

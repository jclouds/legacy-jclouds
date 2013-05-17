/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_LANGUAGE;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.EXPIRES;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map.Entry;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;

import org.jclouds.Constants;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.logging.internal.Wire;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.Invokable;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class HttpUtils {

   @Inject(optional = true)
   @Named(Constants.PROPERTY_RELAX_HOSTNAME)
   private boolean relaxHostname = false;

   private final int globalMaxConnections;
   private final int globalMaxConnectionsPerHost;
   private final int connectionTimeout;
   private final int soTimeout;
   
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

   public int getMaxConnections() {
      return globalMaxConnections;
   }

   public int getMaxConnectionsPerHost() {
      return globalMaxConnectionsPerHost;
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

   public static Optional<String> tryFindHttpMethod(Invokable<?, ?> method) {
      Builder<String> methodsBuilder = ImmutableSet.builder();
      for (Annotation annotation : method.getAnnotations()) {
         HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
         if (http != null)
            methodsBuilder.add(http.value());
      }
      Collection<String> methods = methodsBuilder.build();
      switch (methods.size()) {
      case 0:
         return Optional.absent();
      case 1:
         return Optional.of(get(methods, 0));
      default:
         throw new IllegalStateException("You must specify at most one HttpMethod annotation on: " + method);
      }
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
            logger.debug("%s %s: %s", prefix, CONTENT_MD5, base64().encode(md5));
         if (message.getPayload().getContentMetadata().getContentDisposition() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_DISPOSITION, message.getPayload().getContentMetadata()
                  .getContentDisposition());
         if (message.getPayload().getContentMetadata().getContentEncoding() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_ENCODING, message.getPayload().getContentMetadata()
                  .getContentEncoding());
         if (message.getPayload().getContentMetadata().getContentLanguage() != null)
            logger.debug("%s %s: %s", prefix, CONTENT_LANGUAGE, message.getPayload().getContentMetadata()
                  .getContentLanguage());
         if (message.getPayload().getContentMetadata().getExpires() != null)
            logger.debug("%s %s: %s", prefix, EXPIRES, message.getPayload().getContentMetadata().getExpires());
      }
   }

   public void logResponse(Logger logger, HttpResponse response, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, response.getStatusLine().toString());
         logMessage(logger, response, prefix);
      }
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
      return md5 != null ? base64().encode(md5) : "";
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

   public static <T> T returnValueOnCodeOrNull(Throwable from, T value, Predicate<Integer> codePredicate) {
      Iterable<HttpResponseException> throwables = filter(getCausalChain(from), HttpResponseException.class);
      if (size(throwables) >= 1 && get(throwables, 0).getResponse() != null
            && codePredicate.apply(get(throwables, 0).getResponse().getStatusCode())) {
         return value;
      }
      return null;
   }

   public static Multimap<String, String> filterOutContentHeaders(Multimap<String, String> headers) {
      // http message usually comes in as a null key header, let's filter it out.
      return ImmutableMultimap.copyOf(filterKeys(headers, and(notNull(), not(in(ContentMetadata.HTTP_HEADERS)))));
   }

   public static boolean contains404(Throwable t) {
      return returnValueOnCodeOrNull(t, true, equalTo(404)) != null;
   }
}

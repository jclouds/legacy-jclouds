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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import com.google.common.collect.Multimap;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Nullable;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {

   private List<HttpRequestFilter> requestFilters = Lists.newArrayList();

   private String method;
   private URI endpoint;
   private Payload payload;

   /**
    * 
    * @param endPoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   public HttpRequest(String method, URI endPoint) {
      this.setMethod(checkNotNull(method, "method"));
      this.setEndpoint(checkNotNull(endPoint, "endPoint"));
      checkArgument(endPoint.getHost() != null, String.format("endPoint.getHost() is null for %s",
               endPoint));
   }

   /**
    * 
    * @param endPoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   public HttpRequest(String method, URI endPoint, Multimap<String, String> headers) {
      this(method, endPoint);
      getHeaders().putAll(checkNotNull(headers, "headers"));
   }

   /**
    * 
    * @param endPoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   protected HttpRequest(String method, URI endPoint, Multimap<String, String> headers,
            @Nullable Payload payload) {
      this(method, endPoint);
      getHeaders().putAll(checkNotNull(headers, "headers"));
      setPayload(payload);
   }

   public String getRequestLine() {
      return String.format("%s %s HTTP/1.1", getMethod(), getEndpoint().toASCIIString());
   }

   /**
    * We cannot return an enum, as per specification custom methods are allowed. Enums are not
    * extensible.
    * 
    * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
    */
   public String getMethod() {
      return method;
   }

   public Payload getPayload() {
      return payload;
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(Payload data) {
      this.payload = checkNotNull(data, "data");
      setLength();
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(InputStream data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(byte[] data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(String data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   public void setPayload(File data) {
      setPayload(Payloads.newPayload(checkNotNull(data, "data")));
   }

   private void setLength() {
      Long size = getPayload().calculateSize();
      if (size != null && this.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH) == null) {
         getHeaders().put(HttpHeaders.CONTENT_LENGTH, size.toString());
      }
   }

   public URI getEndpoint() {
      return endpoint;
   }

   public void addFilter(HttpRequestFilter filter) {
      requestFilters.add(filter);
   }

   public List<HttpRequestFilter> getFilters() {
      return requestFilters;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public void setEndpoint(URI endpoint) {
      this.endpoint = endpoint;
   }

}

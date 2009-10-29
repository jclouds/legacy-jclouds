/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.command.Request;

import com.google.common.collect.Multimap;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Nullable;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage implements Request<URI> {

   private List<HttpRequestFilter> requestFilters = Lists.newArrayList();

   private String method;
   private URI endpoint;
   private Object entity;

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
      setHeaders(checkNotNull(headers, "headers"));
   }

   /**
    * 
    * @param endPoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   protected HttpRequest(String method, URI endPoint, Multimap<String, String> headers,
            @Nullable Object entity) {
      this(method, endPoint);
      setHeaders(checkNotNull(headers, "headers"));
      setEntity(entity);
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

   public Object getEntity() {
      return entity;
   }

   public void setEntity(Object content) {
      this.entity = content;
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

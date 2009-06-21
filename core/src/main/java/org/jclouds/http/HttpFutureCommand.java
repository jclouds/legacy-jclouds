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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.command.FutureCommand;
import org.jclouds.logging.Logger;

/**
 * HttpFutureCommand associates a request with a {@link ResponseCallable response parser} which
 * extracts the result object specified as generic type <code>T</code> from the HttpResponse.
 * 
 * @author Adrian Cole
 */
public class HttpFutureCommand<T> extends FutureCommand<HttpRequest, HttpResponse, T> {

   public HttpFutureCommand(URI endPoint, HttpMethod method, String uri,
            ResponseCallable<T> responseCallable) {
      super(new HttpRequest(checkNotNull(endPoint, "endPoint"), checkNotNull(method, "method"),
               checkNotNull(uri, "uri")), responseCallable);
   }

   protected void addHostHeader(String host) {
      getRequest().getHeaders().put(HttpHeaders.HOST, host);
   }

   @Override
   public String toString() {
      return this.getClass().getName() + "{" + "request=" + this.getRequest() + ","
               + "responseFuture=" + this.getResponseFuture() + '}';
   }

   public abstract static class ResponseCallable<T> implements
            FutureCommand.ResponseCallable<HttpResponse, T> {
      @Resource
      protected Logger logger = Logger.NULL;

      public void checkCode() {
         int code = getResponse().getStatusCode();
         if (code >= 300) {
            IOUtils.closeQuietly(getResponse().getContent());
            throw new IllegalStateException("incorrect code for this operation: " + getResponse());
         }
      }

      private HttpResponse response;

      public HttpResponse getResponse() {
         return response;
      }

      public void setResponse(HttpResponse response) {
         this.response = response;
      }
   }

}

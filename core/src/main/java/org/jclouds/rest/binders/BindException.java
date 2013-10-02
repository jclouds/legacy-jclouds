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
package org.jclouds.rest.binders;

import org.jclouds.http.HttpRequest;

/**
 * Exception thrown during the binding process.
 * 
 * @author Ignasi Barrera
 */
public class BindException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   private HttpRequest request;

   public BindException(final HttpRequest request) {
      super();
      this.request = request;
   }

   public BindException(final HttpRequest request, final String message) {
      super(message);
      this.request = request;
   }

   public BindException(final HttpRequest request, final Throwable cause) {
      super(cause.getMessage(), cause);
      this.request = request;
   }

   public BindException(final HttpRequest request, final String message, final Throwable cause) {
      super(message, cause);
      this.request = request;
   }

   @Override
   public String getMessage() {
      String msg = "Could not bind object to request" + request + ": ";
      return msg + super.getMessage();
   }

   public HttpRequest getRequest() {
      return request;
   }

}

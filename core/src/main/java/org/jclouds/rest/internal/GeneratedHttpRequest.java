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
package org.jclouds.rest.internal;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;

/**
 * Represents a request generated from annotations
 * 
 * @author Adrian Cole
 */
public class GeneratedHttpRequest<T> extends HttpRequest {
   private final Class<T> declaring;
   private final Method javaMethod;
   private final Object[] args;
   private final RestAnnotationProcessor<T> processor;

   GeneratedHttpRequest(String method, URI endPoint, RestAnnotationProcessor<T> processor,
            Class<T> declaring, Method javaMethod, Object... args) {
      super(method, endPoint);
      this.processor = processor;
      this.declaring = declaring;
      this.javaMethod = javaMethod;
      this.args = args;
   }

   public Class<T> getDeclaring() {
      return declaring;
   }

   public Method getJavaMethod() {
      return javaMethod;
   }

   public Object[] getArgs() {
      return args;
   }

   public RestAnnotationProcessor<T> getProcessor() {
      return processor;
   }

   public void replaceQueryParam(String name, Object... values) {
      UriBuilder builder = UriBuilder.fromUri(getEndpoint());
      builder.replaceQueryParam(name, values);
      URI newEndpoint = processor.replaceQuery(getEndpoint(), builder.build().getQuery());
      setEndpoint(newEndpoint);
   }

   public void replacePath(String path) {
      UriBuilder builder = UriBuilder.fromUri(getEndpoint());
      builder.replacePath(path);
      setEndpoint(builder.build());
   }
}

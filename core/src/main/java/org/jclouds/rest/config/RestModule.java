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
package org.jclouds.rest.config;

import javax.inject.Inject;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.internal.AsyncRestClientProxy;
import org.jclouds.rest.internal.RuntimeDelegateImpl;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
public class RestModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new ParserModule());
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
      bind(AsyncRestClientProxy.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements AsyncRestClientProxy.Factory {
      @Inject
      private TransformingHttpCommandExecutorService executorService;

      @SuppressWarnings("unchecked")
      public TransformingHttpCommand<?> create(HttpRequest request,
               Function<HttpResponse, ?> transformer, Function<Exception, ?> exceptionTransformer) {
         return new TransformingHttpCommandImpl(executorService, request, transformer,
                  exceptionTransformer);
      }

   }
}
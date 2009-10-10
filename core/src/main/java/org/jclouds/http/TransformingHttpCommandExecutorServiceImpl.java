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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jclouds.concurrent.FutureFunctionCallable;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import javax.inject.Inject;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>. TODO
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandExecutorServiceImpl implements
         TransformingHttpCommandExecutorService {
   private final HttpCommandExecutorService client;
   private final ExecutorService executorService;
   private final LoggerFactory logFactory;

   @Inject
   public TransformingHttpCommandExecutorServiceImpl(HttpCommandExecutorService client,
            ExecutorService executorService, LoggerFactory logFactory) {
      this.client = client;
      this.executorService = executorService;
      this.logFactory = logFactory;
   }

   /**
    * {@inheritDoc}
    */
   public <T> Future<T> submit(HttpCommand command, Function<HttpResponse, T> responseTransformer,
            Function<Exception, T> exceptionTransformer) {
      Future<HttpResponse> responseFuture = client.submit(command);
      Callable<T> valueCallable = new FutureFunctionCallable<HttpResponse, T>(responseFuture,
               responseTransformer, logFactory.getLogger(responseTransformer.getClass().getName()));
      return executorService.submit(valueCallable);
   }

}

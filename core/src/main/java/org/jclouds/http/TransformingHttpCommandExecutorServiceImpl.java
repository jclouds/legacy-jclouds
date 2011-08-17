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

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.concurrent.Futures;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>. TODO
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandExecutorServiceImpl implements TransformingHttpCommandExecutorService {
   private final HttpCommandExecutorService client;
   private final ExecutorService userThreads;

   @Inject
   public TransformingHttpCommandExecutorServiceImpl(HttpCommandExecutorService client,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads) {
      this.client = client;
      this.userThreads = userThreads;
   }

   /**
    * {@inheritDoc}
    */
   public <T, R extends HttpRequest> ListenableFuture<T> submit(HttpCommand command,
         Function<HttpResponse, T> responseTransformer) {
      return Futures.compose(client.submit(command), responseTransformer, userThreads);
   }

}

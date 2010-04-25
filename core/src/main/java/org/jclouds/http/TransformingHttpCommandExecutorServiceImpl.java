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

import static com.google.common.util.concurrent.Futures.compose;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>. TODO
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandExecutorServiceImpl implements
         TransformingHttpCommandExecutorService {
   private final HttpCommandExecutorService client;

   @Inject
   public TransformingHttpCommandExecutorServiceImpl(HttpCommandExecutorService client) {
      this.client = client;
   }

   /**
    * {@inheritDoc}
    */
   public <T> ListenableFuture<T> submit(HttpCommand command,
            Function<HttpResponse, T> responseTransformer) {
      return compose(client.submit(command), responseTransformer, sameThreadExecutor());
   }

}

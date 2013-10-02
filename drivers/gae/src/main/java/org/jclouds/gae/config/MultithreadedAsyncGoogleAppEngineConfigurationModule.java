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
package org.jclouds.gae.config;

import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.gae.AsyncGaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Configures {@link AsyncGaeHttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@Beta
@ConfiguresHttpCommandExecutorService
@ConfiguresExecutorService
public class MultithreadedAsyncGoogleAppEngineConfigurationModule extends GoogleAppEngineConfigurationModule {
   public MultithreadedAsyncGoogleAppEngineConfigurationModule() {
      super(new CurrentRequestExecutorServiceModule());
   }

   /**
    * Used when you are creating multiple contexts in the same app.
    * 
    * @param currentRequestThreadFactory
    * @see CurrentRequestExecutorServiceModule#currentRequestThreadFactory
    */
   public MultithreadedAsyncGoogleAppEngineConfigurationModule(ListeningExecutorService currentRequestThreadFactory) {
      super(new CurrentRequestExecutorServiceModule(currentRequestThreadFactory));
   }

   /**
    * Used when you are creating multiple contexts in the same app.
    * 
    * @param memoizedCurrentRequestExecutorService
    * @see CurrentRequestExecutorServiceModule#memoizedCurrentRequestExecutorService
    */
   public MultithreadedAsyncGoogleAppEngineConfigurationModule(
         Supplier<ListeningExecutorService> memoizedCurrentRequestExecutorService) {
      super(memoizedCurrentRequestExecutorService);
   }

   protected void bindHttpCommandExecutorService() {
      bind(HttpCommandExecutorService.class).to(AsyncGaeHttpCommandExecutorService.class);
   }

}

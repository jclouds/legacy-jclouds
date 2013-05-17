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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.concurrent.DynamicExecutors.newScalingThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.WithSubmissionTrace;

import com.google.appengine.api.ThreadManager;
import com.google.apphosting.api.ApiProxy;
import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@Beta
@ConfiguresExecutorService
public class CurrentRequestExecutorServiceModule extends AbstractModule {

   private final Supplier<ListeningExecutorService> memoizedCurrentRequestExecutorService;

   public CurrentRequestExecutorServiceModule() {
      this(memoizedCurrentRequestExecutorService());
   }

   /**
    * Used when you are creating multiple contexts in the same app.
    * 
    * @param memoizedCurrentRequestExecutorService
    * @see #memoizedCurrentRequestExecutorService
    */
   public CurrentRequestExecutorServiceModule(Supplier<ListeningExecutorService> memoizedCurrentRequestExecutorService) {
      this.memoizedCurrentRequestExecutorService = memoizedCurrentRequestExecutorService;
   }

   /**
    * Used when you are creating multiple contexts in the same app.
    * 
    * @param currentRequestExecutorService
    * @see #currentRequestExecutorService
    */
   public CurrentRequestExecutorServiceModule(ListeningExecutorService currentRequestExecutorService) {
      this.memoizedCurrentRequestExecutorService = Suppliers.ofInstance(currentRequestExecutorService);
   }

   @Override
   protected void configure() {
   }

   public static Supplier<ListeningExecutorService> memoizedCurrentRequestExecutorService() {
      return Suppliers.memoize(new Supplier<ListeningExecutorService>() {
         // important that these are lazy bound vs in configure, as GAE may not
         // quite be initialized, yet!
         @Override
         public ListeningExecutorService get() {
            return currentRequestExecutorService();
         }

      });

   }

   public static ListeningExecutorService currentRequestExecutorService() {
      ThreadFactory factory = checkNotNull(ThreadManager.currentRequestThreadFactory(),
            "ThreadManager.currentRequestThreadFactory()");
      // GAE requests cannot exceed 10 threads per request
      int maxThreads = 10;
      long keepAlive = ApiProxy.getCurrentEnvironment().getRemainingMillis();
      ExecutorService pool = newScalingThreadPool(0, maxThreads, keepAlive, factory);
      return WithSubmissionTrace.wrap(MoreExecutors.listeningDecorator(pool));
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_USER_THREADS)
   protected ListeningExecutorService userExecutor() {
      return memoizedCurrentRequestExecutorService.get();
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_IO_WORKER_THREADS)
   protected ListeningExecutorService ioExecutor() {
      return memoizedCurrentRequestExecutorService.get();
   }
}

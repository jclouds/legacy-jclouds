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
package org.jclouds.concurrent.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;

import com.google.common.util.concurrent.NamingThreadFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures {@link ExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Adrian Cole
 */
@ConfiguresExecutorService
public class ExecutorServiceModule extends AbstractModule {
   private final ExecutorService userThreads;
   private final ExecutorService ioThreads;

   public ExecutorServiceModule(
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads) {
      this.userThreads = userThreads;
      this.ioThreads = ioThreads;
   }

   public ExecutorServiceModule() {
      this(null, null);
   }

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_USER_THREADS)
   ExecutorService provideExecutorService(@Named(Constants.PROPERTY_USER_THREADS) int userThreads) {
      return this.userThreads != null ? this.userThreads : userThreads == 0 ? Executors
               .newCachedThreadPool(new NamingThreadFactory("user thread %d"))
               : newNamedThreadPool("user thread %d", userThreads);
   }

   public static ExecutorService newNamedThreadPool(String name, int maxCount) {
      return Executors.newFixedThreadPool(maxCount, new NamingThreadFactory(name));
   }

   @Provides
   @Singleton
   @Named(Constants.PROPERTY_IO_WORKER_THREADS)
   ExecutorService provideIOExecutor(@Named(Constants.PROPERTY_IO_WORKER_THREADS) int ioThreads) {
      return this.ioThreads != null ? this.ioThreads : ioThreads == 0 ? Executors
               .newCachedThreadPool(new NamingThreadFactory("i/o thread %d")) : newNamedThreadPool(
               "i/o thread %d", ioThreads);
   }
}
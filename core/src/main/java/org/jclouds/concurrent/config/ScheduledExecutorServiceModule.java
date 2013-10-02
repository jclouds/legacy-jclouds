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
package org.jclouds.concurrent.config;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.defaultThreadFactory;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.concurrent.config.ExecutorServiceModule.shutdownOnClose;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Provides an {@link ScheduledExecutorService} to run periodical tasks such as virtual machine monitoring, etc.
 * <p>
 * This module is not registered by default in the context because some providers do not allow to spawn threads.
 * 
 * @author Ignasi Barrera
 * 
 * @see ExecutorServiceModule
 * 
 */
public class ScheduledExecutorServiceModule extends AbstractModule {
   @Provides
   @Singleton
   @Named(PROPERTY_SCHEDULER_THREADS)
   ListeningScheduledExecutorService provideListeningScheduledExecutorService(
         @Named(PROPERTY_SCHEDULER_THREADS) int count, Closer closer) {
      return shutdownOnClose(WithSubmissionTrace.wrap(newScheduledThreadPoolNamed("scheduler thread %d", count)),
            closer);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_SCHEDULER_THREADS)
   ScheduledExecutorService provideScheduledExecutor(
         @Named(PROPERTY_SCHEDULER_THREADS) ListeningScheduledExecutorService in) {
      return in;
   }

   private static ListeningScheduledExecutorService newScheduledThreadPoolNamed(String name, int maxCount) {
      ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(name).setThreadFactory(defaultThreadFactory())
            .build();
      return listeningDecorator(maxCount == 0 ? newSingleThreadScheduledExecutor(factory) : newScheduledThreadPool(
            maxCount, factory));
   }

   @Override
   protected void configure() { // NO_UCD
   }
}

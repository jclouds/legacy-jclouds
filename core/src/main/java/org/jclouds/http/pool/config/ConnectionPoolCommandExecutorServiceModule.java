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
package org.jclouds.http.pool.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

import org.jclouds.http.pool.PoolConstants;
import org.jclouds.lifecycle.config.LifeCycleModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public abstract class ConnectionPoolCommandExecutorServiceModule<C> extends AbstractModule {

   @Inject(optional = true)
   @Named(PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS)
   protected int maxConnections = 12;

   @Inject(optional = true)
   @Named(PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS)
   protected int maxWorkerThreads = 12;

   protected void configure() {
      install(new LifeCycleModule());
      bind(AtomicInteger.class).toInstance(new AtomicInteger());// max errors
      binder().requestInjection(this);
   }

   @Provides
   // @Singleton per uri...
   public abstract BlockingQueue<C> provideAvailablePool() throws Exception;

   /**
    * controls production and destruction of real connections.
    * <p/>
    * aquire before a new connection is created release after an error has occurred
    * 
    * @param max
    * @throws Exception
    */
   @Provides
   // @Singleton per uri...
   public Semaphore provideTotalConnectionSemaphore() throws Exception {
      return new Semaphore(maxConnections, true);
   }
}

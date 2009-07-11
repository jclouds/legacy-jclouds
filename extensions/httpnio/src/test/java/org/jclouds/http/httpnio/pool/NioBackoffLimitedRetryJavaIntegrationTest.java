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
package org.jclouds.http.httpnio.pool;

import java.util.Properties;

import org.jclouds.http.BackoffLimitedRetryJavaIntegrationTest;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.httpnio.config.NioTransformingHttpCommandExecutorServiceModule;
import org.jclouds.http.pool.PoolConstants;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests the retry behavior of the default {@link RetryHandler} implementation
 * {@link BackoffLimitedRetryHandler} to ensure that retries up to the default limit succeed.
 * 
 * TODO: Should either explicitly set retry limit or get it from Guice, rather than assuming it's 5.
 * 
 * @author James Murty
 */
@Test(sequential = true)
public class NioBackoffLimitedRetryJavaIntegrationTest extends
         BackoffLimitedRetryJavaIntegrationTest {

   protected void addConnectionProperties(Properties properties) {
      properties.setProperty(PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE, "75");
      properties.setProperty(PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES, "2");
      properties.setProperty(PoolConstants.PROPERTY_POOL_REQUEST_INVOKER_THREADS, "1");
      properties.setProperty(PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS, "2");
      properties.setProperty(PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS, "12");
   }

   protected Module createClientModule() {
      return new NioTransformingHttpCommandExecutorServiceModule();
   }
}
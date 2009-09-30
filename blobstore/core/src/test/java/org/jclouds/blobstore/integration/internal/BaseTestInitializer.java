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
package org.jclouds.blobstore.integration.internal;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.testng.ITestContext;

import com.google.inject.Module;

public abstract class BaseTestInitializer<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> {

   public BlobStoreContext<S, C, M, B> init(Module configurationModule, ITestContext testContext)
            throws Exception {
      String endpoint = System.getProperty("jclouds.test.endpoint");
      String app = System.getProperty("jclouds.test.app");
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      if (endpoint != null)
         testContext.setAttribute("jclouds.test.endpoint", endpoint);
      if (app != null)
         testContext.setAttribute("jclouds.test.app", app);
      if (account != null)
         testContext.setAttribute("jclouds.test.user", account);
      if (key != null)
         testContext.setAttribute("jclouds.test.key", key);
      if (account != null) {
         return createLiveContext(configurationModule, endpoint, app, account, key);
      } else {
         return createStubContext();
      }
   }

   protected abstract BlobStoreContext<S, C, M, B> createStubContext();

   protected abstract BlobStoreContext<S, C, M, B> createLiveContext(Module configurationModule,
            String url, String app, String account, String key);
}
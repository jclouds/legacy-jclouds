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
package org.jclouds.azure.storage.queue.config;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.azure.storage.AzureQueue;
import org.jclouds.azure.storage.queue.AzureQueueConnection;
import org.jclouds.azure.storage.queue.AzureQueueContext;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.cloud.internal.CloudContextImpl;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

@RequiresHttp
public class AzureQueueContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(AzureQueueContext.class).to(AzureQueueContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class AzureQueueContextImpl extends CloudContextImpl<AzureQueueConnection>
            implements AzureQueueContext {
      @Inject
      public AzureQueueContextImpl(Closer closer, AzureQueueConnection defaultApi,
               @AzureQueue URI endPoint,
               @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account) {
         super(closer, defaultApi, endPoint, account);
      }

   }

}
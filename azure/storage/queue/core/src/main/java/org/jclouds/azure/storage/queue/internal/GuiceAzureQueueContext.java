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
package org.jclouds.azure.storage.queue.internal;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.azure.storage.queue.AzureQueueConnection;
import org.jclouds.azure.storage.queue.AzureQueueContext;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * Uses a Guice Injector to configure the objects served by AzureQueueContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceAzureQueueContext implements AzureQueueContext {

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;
   private final URI endPoint;
   private final String account;

   @Inject
   private GuiceAzureQueueContext(Injector injector, Closer closer,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account, URI endPoint) {
      this.injector = injector;
      this.closer = closer;
      this.endPoint = endPoint;
      this.account = account;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   public String getAccount() {
      return account;
   }

   public AzureQueueConnection getApi() {
      return injector.getInstance(AzureQueueConnection.class);
   }

   public URI getEndPoint() {
      return endPoint;
   }

}

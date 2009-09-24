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
package org.jclouds.azure.storage.queue.xml.config;

import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.queue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret Azure Queue Service responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class AzureQueueParserModule extends AzureStorageParserModule {
   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<BoundedList<QueueMetadata>>> accountNameEnumerationResultsHandler = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<BoundedList<QueueMetadata>>>() {
   };

   @Override
   protected void bindParserImplementationsToReturnTypes() {
      super.bindParserImplementationsToReturnTypes();
      bind(new TypeLiteral<ParseSax.HandlerWithResult<BoundedList<QueueMetadata>>>() {
      }).to(AccountNameEnumerationResultsHandler.class);
   }

   @Override
   protected void bindCallablesThatReturnParseResults() {
      super.bindCallablesThatReturnParseResults();
      bind(accountNameEnumerationResultsHandler).toProvider(
               FactoryProvider.newFactory(accountNameEnumerationResultsHandler,
                        new TypeLiteral<ParseSax<BoundedList<QueueMetadata>>>() {
                        }));
   }

}
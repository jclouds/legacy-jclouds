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
package org.jclouds.azure.storage.blob.xml.config;

import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.blob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret Azure Blob Service responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class AzureBlobParserModule extends AzureStorageParserModule {
   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<BoundedList<ContainerMetadata>>> accountNameEnumerationResultsHandler = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<BoundedList<ContainerMetadata>>>() {
   };

   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<ListBlobsResponse>> containerNameEnumerationResultsHandler = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<ListBlobsResponse>>() {
   };

   @Override
   protected void bindParserImplementationsToReturnTypes() {
      super.bindParserImplementationsToReturnTypes();
      bind(new TypeLiteral<ParseSax.HandlerWithResult<BoundedList<ContainerMetadata>>>() {
      }).to(AccountNameEnumerationResultsHandler.class);
      bind(new TypeLiteral<ParseSax.HandlerWithResult<ListBlobsResponse>>() {
      }).to(ContainerNameEnumerationResultsHandler.class);
   }

   @Override
   protected void bindCallablesThatReturnParseResults() {
      super.bindCallablesThatReturnParseResults();
      bind(accountNameEnumerationResultsHandler).toProvider(
               FactoryProvider.newFactory(accountNameEnumerationResultsHandler,
                        new TypeLiteral<ParseSax<BoundedList<ContainerMetadata>>>() {
                        }));
      bind(containerNameEnumerationResultsHandler).toProvider(
               FactoryProvider.newFactory(containerNameEnumerationResultsHandler,
                        new TypeLiteral<ParseSax<ListBlobsResponse>>() {
                        }));
   }

}
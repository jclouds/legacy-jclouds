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
package org.jclouds.azure.storage.xml.config;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.azure.storage.xml.ErrorHandler;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret AzureStorage responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class AzureStorageParserModule extends AbstractModule {
   protected final TypeLiteral<AzureStorageParserFactory.GenericParseFactory<AzureStorageError>> errorTypeLiteral = new TypeLiteral<AzureStorageParserFactory.GenericParseFactory<AzureStorageError>>() {
   };

   @Override
   protected void configure() {
      bindErrorHandler();
      bindCallablesThatReturnParseResults();
      bindParserImplementationsToReturnTypes();
   }

   protected void bindParserImplementationsToReturnTypes() {
   }

   protected void bindCallablesThatReturnParseResults() {
      bind(errorTypeLiteral).toProvider(
               FactoryProvider.newFactory(errorTypeLiteral,
                        new TypeLiteral<ParseSax<AzureStorageError>>() {
                        }));
   }

   protected void bindErrorHandler() {
      bind(new TypeLiteral<ParseSax.HandlerWithResult<AzureStorageError>>() {
      }).to(ErrorHandler.class);
   }

}
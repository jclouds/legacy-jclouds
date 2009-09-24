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
package org.jclouds.azure.storage.xml;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.http.functions.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates Parsers needed to interpret Azure Storage Service messages. This class uses guice
 * assisted inject, which mandates the creation of many single-method interfaces. These interfaces
 * are not intended for public api.
 * 
 * @author Adrian Cole
 */
public class AzureStorageParserFactory {

   @VisibleForTesting
   public static interface GenericParseFactory<T> {
      ParseSax<T> create(ParseSax.HandlerWithResult<T> handler);
   }

   @Inject
   private GenericParseFactory<AzureStorageError> parseErrorFactory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   /**
    * @return a parser used to handle error conditions.
    */
   public ParseSax<AzureStorageError> createErrorParser() {
      return parseErrorFactory.create(errorHandlerProvider.get());
   }

}
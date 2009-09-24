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
package org.jclouds.azure.storage.blob.xml;

import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.xml.AzureStorageParserFactory;
import org.jclouds.http.functions.ParseSax;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Creates Parsers needed to interpret Azure Blob Service messages. This class uses guice assisted
 * inject, which mandates the creation of many single-method interfaces. These interfaces are not
 * intended for public api.
 * 
 * @author Adrian Cole
 */
public class AzureBlobParserFactory extends AzureStorageParserFactory {

   @Inject
   private GenericParseFactory<BoundedList<ContainerMetadata>> parseContainerMetadataListFactory;

   @Inject
   Provider<AccountNameEnumerationResultsHandler> containerMetaListHandlerProvider;

   public ParseSax<BoundedList<ContainerMetadata>> createContainerMetadataListParser() {
      return parseContainerMetadataListFactory.create(containerMetaListHandlerProvider.get());
   }

   @Inject
   private GenericParseFactory<ListBlobsResponse> parseListBlobsResponseFactory;

   @Inject
   Provider<ContainerNameEnumerationResultsHandler> listBlobsResponseHandlerProvider;

   public ParseSax<ListBlobsResponse> createListBlobsResponseParser() {
      return parseListBlobsResponseFactory.create(listBlobsResponseHandlerProvider.get());
   }
}
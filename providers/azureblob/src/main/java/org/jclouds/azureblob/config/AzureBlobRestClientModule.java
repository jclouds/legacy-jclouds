/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob.config;

import org.jclouds.azure.storage.config.AzureStorageRestClientModule;
import org.jclouds.azureblob.AzureBlobAsyncClient;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.azureblob.handlers.ParseAzureBlobErrorFromXmlContent;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;

/**
 * Configures the Azure Blob Service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AzureBlobRestClientModule extends AzureStorageRestClientModule<AzureBlobClient, AzureBlobAsyncClient> {

   @Override
   protected void configure() {
      install(new AzureBlobModule());
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAzureBlobErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAzureBlobErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAzureBlobErrorFromXmlContent.class);
   }

}

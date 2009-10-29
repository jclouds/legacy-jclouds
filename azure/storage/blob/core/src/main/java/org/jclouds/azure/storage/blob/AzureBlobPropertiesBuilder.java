/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.azure.storage.blob;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.HttpPropertiesBuilder;

/**
 * Builds properties used in AzureBlob Connections
 * 
 * @author Adrian Cole
 */
public class AzureBlobPropertiesBuilder extends HttpPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT,
               "https://{account}.blob.core.windows.net");
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(PROPERTY_AZURESTORAGE_SESSIONINTERVAL, 60 + "");
      return properties;
   }

   public AzureBlobPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public AzureBlobPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public AzureBlobPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
      String endpoint = properties.getProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT);
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, endpoint.replaceAll(
               "\\{account\\}", id));
      return this;
   }

   public AzureBlobPropertiesBuilder withEndpoint(URI endpoint) {
      String account = checkNotNull(properties
               .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT),
               "azureStorageAccount");
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, checkNotNull(endpoint,
               "endpoint").toString());
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, endpoint.toString()
               .replaceAll("\\{account\\}", account));
      return this;
   }

   public AzureBlobPropertiesBuilder withTimeStampExpiration(long seconds) {
      properties.setProperty(PROPERTY_AZURESTORAGE_SESSIONINTERVAL, seconds + "");
      return this;
   }

}

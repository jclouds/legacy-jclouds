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
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_RETRY;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_SESSIONINTERVAL;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_TIMEOUT;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.Properties;

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
      properties
               .setProperty(PROPERTY_AZUREBLOB_ENDPOINT, "https://{account}.blob.core.windows.net");
      properties.setProperty(PROPERTY_AZUREBLOB_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
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
      properties
               .setProperty(PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id, "azureStorageAccount"));
      properties.setProperty(PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret, "azureStorageKey"));
      String endpoint = properties.getProperty(PROPERTY_AZUREBLOB_ENDPOINT);
      properties.setProperty(PROPERTY_AZUREBLOB_ENDPOINT, endpoint.replaceAll("\\{account\\}", id));
      return this;
   }

   public AzureBlobPropertiesBuilder withEndpoint(URI endpoint) {
      String account = checkNotNull(properties.getProperty(PROPERTY_AZURESTORAGE_ACCOUNT),
               "azureStorageAccount");
      properties.setProperty(PROPERTY_AZUREBLOB_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      properties.setProperty(PROPERTY_AZUREBLOB_ENDPOINT, endpoint.toString().replaceAll(
               "\\{account\\}", account));
      return this;
   }

   public AzureBlobPropertiesBuilder withTimeStampExpiration(long seconds) {
      properties.setProperty(PROPERTY_AZUREBLOB_SESSIONINTERVAL, seconds + "");
      return this;
   }

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public AzureBlobPropertiesBuilder withRequestTimeout(long milliseconds) {
      properties.setProperty(PROPERTY_AZUREBLOB_TIMEOUT, Long.toString(milliseconds));
      return this;
   }

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public AzureBlobPropertiesBuilder withMaxRetries(int retries) {
      properties.setProperty(PROPERTY_AZUREBLOB_RETRY, Integer.toString(retries));
      return this;
   }

   protected AzureBlobPropertiesBuilder withMetaPrefix(String prefix) {
      properties.setProperty(PROPERTY_AZUREBLOB_METADATA_PREFIX, prefix);
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, prefix);
      return this;
   }

}

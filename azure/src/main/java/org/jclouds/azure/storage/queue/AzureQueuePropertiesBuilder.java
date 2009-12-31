/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.queue;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azure.storage.queue.reference.AzureQueueConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.http.HttpPropertiesBuilder;

/**
 * Builds properties used in AzureQueue Connections
 * 
 * @author Adrian Cole
 */
public class AzureQueuePropertiesBuilder extends HttpPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT,
               "https://{account}.queue.core.windows.net");
      properties.setProperty(PROPERTY_AZURESTORAGE_SESSIONINTERVAL, 60 + "");
      return properties;
   }

   public AzureQueuePropertiesBuilder(Properties properties) {
      super(properties);
   }

   public AzureQueuePropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public AzureQueuePropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
      String endpoint = properties.getProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT);
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, endpoint.replaceAll(
               "\\{account\\}", id));
      return this;
   }

   public AzureQueuePropertiesBuilder withEndpoint(URI endpoint) {
      String account = checkNotNull(properties
               .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT),
               "azureStorageAccount");
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, checkNotNull(
               endpoint, "endpoint").toString());
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, endpoint.toString()
               .replaceAll("\\{account\\}", account));
      return this;
   }

   public AzureQueuePropertiesBuilder withTimeStampExpiration(long seconds) {
      properties.setProperty(PROPERTY_AZURESTORAGE_SESSIONINTERVAL, seconds + "");
      return this;
   }

}

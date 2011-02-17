/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.azureblob;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Properties;

import org.jclouds.azure.storage.AzureStoragePropertiesBuilder;

/**
 * Builds properties used in AzureBlob Connections
 * 
 * @author Adrian Cole
 */
public class AzureBlobPropertiesBuilder extends AzureStoragePropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(PROPERTY_API_VERSION, "2009-09-19");
      properties.setProperty(PROPERTY_ENDPOINT, "https://{identity}.blob.core.windows.net");
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-TX,US-IL,IE-D,SG,NL-NH,HK");
      return properties;
   }

   public AzureBlobPropertiesBuilder(Properties properties) {
      super(properties);
   }

}

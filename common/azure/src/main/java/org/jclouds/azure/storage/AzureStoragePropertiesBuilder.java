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

package org.jclouds.azure.storage;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.util.Properties;
import java.util.regex.Pattern;

import org.jclouds.PropertiesBuilder;
import org.jclouds.util.Strings2;

/**
 * Builds properties used in Azure Connections
 * 
 * @author Adrian Cole
 */
public class AzureStoragePropertiesBuilder extends PropertiesBuilder {

   public AzureStoragePropertiesBuilder() {
      this(new Properties());
   }

   public AzureStoragePropertiesBuilder(Properties properties) {
      super(properties);
   }

   public static final Pattern IDENTITY_PATTERN = Pattern.compile("\\{identity\\}");

   @Override
   public Properties build() {
      String endpoint = properties.getProperty(PROPERTY_ENDPOINT);
      String identity = properties.getProperty(PROPERTY_IDENTITY);

      properties.setProperty(PROPERTY_ENDPOINT, Strings2.replaceAll(endpoint, IDENTITY_PATTERN,
               identity));
      return super.build();
   }

}

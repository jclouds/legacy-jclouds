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
package org.jclouds.atmosonline.saas;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;

/**
 * Builds properties used in AtmosStorage Connections
 * 
 * @author Adrian Cole
 */
public class AtmosStoragePropertiesBuilder extends PropertiesBuilder {
   
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT,
               "https://accesspoint.atmosonline.com");
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL, "60");
      return properties;
   }

   public AtmosStoragePropertiesBuilder(Properties properties) {
      super(properties);
   }

   public AtmosStoragePropertiesBuilder(String uid, String key) {
      super();
      withCredentials(uid, key);
   }

   public AtmosStoragePropertiesBuilder withCredentials(String uid, String key) {
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_UID, checkNotNull(uid, "uid"));
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY, checkNotNull(key, "key"));
      return this;
   }

   public AtmosStoragePropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT, checkNotNull(
               endpoint, "endpoint").toString());
      return this;
   }

   public AtmosStoragePropertiesBuilder withTimeStampExpiration(long seconds) {
      properties.setProperty(AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL, seconds + "");
      return this;
   }

}

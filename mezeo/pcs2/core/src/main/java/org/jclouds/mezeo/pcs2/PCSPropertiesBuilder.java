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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;

import org.jclouds.http.HttpPropertiesBuilder;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;

/**
 * Builds properties used in PCS Clients
 * 
 * @author Adrian Cole
 */
public class PCSPropertiesBuilder extends HttpPropertiesBuilder {

   public PCSPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public PCSPropertiesBuilder(URI endpoint, String id, String secret) {
      super();
      withCredentials(id, secret);
      withEndpoint(endpoint);
   }

   public PCSPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PCSConstants.PROPERTY_PCS2_USER, checkNotNull(id, "user"));
      properties.setProperty(PCSConstants.PROPERTY_PCS2_PASSWORD, checkNotNull(secret, "key"));
      return this;
   }

   public PCSPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return this;
   }
}

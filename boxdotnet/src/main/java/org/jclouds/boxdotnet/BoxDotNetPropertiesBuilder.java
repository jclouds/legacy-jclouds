/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.boxdotnet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.boxdotnet.reference.BoxDotNetConstants.PROPERTY_BOXDOTNET_ENDPOINT;
import static org.jclouds.boxdotnet.reference.BoxDotNetConstants.PROPERTY_BOXDOTNET_PASSWORD;
import static org.jclouds.boxdotnet.reference.BoxDotNetConstants.PROPERTY_BOXDOTNET_USER;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in BoxDotNet Clients
 * 
 * @author Adrian Cole
 */
public class BoxDotNetPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_BOXDOTNET_ENDPOINT, "https://www.box.net/api/1.0/rest");
      return properties;
   }

   public BoxDotNetPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public BoxDotNetPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public BoxDotNetPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_BOXDOTNET_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_BOXDOTNET_PASSWORD, checkNotNull(secret, "password"));
      return this;
   }

   public BoxDotNetPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_BOXDOTNET_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return this;
   }
}

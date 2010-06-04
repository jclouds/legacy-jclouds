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
package org.jclouds.chef;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.chef.reference.ChefConstants.PROPERTY_CHEF_ENDPOINT;
import static org.jclouds.chef.reference.ChefConstants.PROPERTY_CHEF_RSA_KEY;
import static org.jclouds.chef.reference.ChefConstants.PROPERTY_CHEF_TIMESTAMP_INTERVAL;
import static org.jclouds.chef.reference.ChefConstants.PROPERTY_CHEF_IDENTITY;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in Chef Clients
 * 
 * @author Adrian Cole
 */
public class ChefPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_CHEF_ENDPOINT, "https://api.opscode.com");
      properties.setProperty(PROPERTY_CHEF_TIMESTAMP_INTERVAL, "1");
      return properties;
   }

   public ChefPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public ChefPropertiesBuilder(String identity, String rsaKey) {
      super();
      withCredentials(identity, rsaKey);
   }

   public ChefPropertiesBuilder withCredentials(String identity, String rsaKey) {
      properties.setProperty(PROPERTY_CHEF_IDENTITY, checkNotNull(identity, "identity"));
      properties.setProperty(PROPERTY_CHEF_RSA_KEY, checkNotNull(rsaKey, "password"));
      return this;
   }

   public ChefPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_CHEF_ENDPOINT, checkNotNull(endpoint, "endpoint").toString());
      return this;
   }
}

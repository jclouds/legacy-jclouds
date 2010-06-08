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
package org.jclouds.ibmdev;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_ENDPOINT;
import static org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_LOCATION;
import static org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_PASSWORD;
import static org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_USER;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in IBMDeveloperCloud Clients
 * 
 * @author Adrian Cole
 */
public class IBMDeveloperCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_IBMDEVELOPERCLOUD_ENDPOINT,
               "https://www-180.ibm.com/cloud/enterprise/beta/api/rest/20090403");
      properties.setProperty(PROPERTY_IBMDEVELOPERCLOUD_LOCATION, "1");
      return properties;
   }

   public IBMDeveloperCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public IBMDeveloperCloudPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public IBMDeveloperCloudPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_IBMDEVELOPERCLOUD_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_IBMDEVELOPERCLOUD_PASSWORD, checkNotNull(secret, "password"));
      return this;
   }

   public IBMDeveloperCloudPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_IBMDEVELOPERCLOUD_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return this;
   }
}

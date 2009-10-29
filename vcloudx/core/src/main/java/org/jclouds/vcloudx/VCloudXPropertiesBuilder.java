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
package org.jclouds.vcloudx;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_KEY;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_USER;

import java.net.URI;
import java.util.Properties;

import org.jclouds.http.HttpPropertiesBuilder;

/**
 * Builds properties used in VCloudX Clients
 * 
 * @author Adrian Cole
 */
public class VCloudXPropertiesBuilder extends HttpPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUDX_SESSIONINTERVAL, 9 * 60 + "");
      return properties;
   }

   public VCloudXPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public VCloudXPropertiesBuilder(URI endpoint, String id, String secret) {
      super();
      withCredentials(id, secret);
      withEndpoint(endpoint);
   }

   public VCloudXPropertiesBuilder withTokenExpiration(long seconds) {
      properties.setProperty(PROPERTY_VCLOUDX_SESSIONINTERVAL, seconds + "");
      return this;
   }

   public VCloudXPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_VCLOUDX_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_VCLOUDX_KEY, checkNotNull(secret, "key"));
      return this;
   }

   public VCloudXPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_VCLOUDX_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return this;
   }
}

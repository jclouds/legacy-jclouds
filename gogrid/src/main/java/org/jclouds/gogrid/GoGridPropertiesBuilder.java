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
package org.jclouds.gogrid;

import org.jclouds.PropertiesBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.gogrid.reference.GoGridConstants.*;

import java.net.URI;
import java.util.Properties;

/**
 * Builds properties used in GoGrid Clients
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 *
 */
public class GoGridPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_GOGRID_ENDPOINT, "https://api.gogrid.com/api");
      properties.setProperty(PROPERTY_GOGRID_SESSIONINTERVAL, 60 + "");

      return properties;
   }

   public GoGridPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public GoGridPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public GoGridPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_GOGRID_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_GOGRID_PASSWORD, checkNotNull(secret, "password"));
      return this;
   }

   public GoGridPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_GOGRID_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return this;
   }
}

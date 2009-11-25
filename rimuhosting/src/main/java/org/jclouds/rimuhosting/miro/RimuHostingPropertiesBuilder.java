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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import org.jclouds.http.HttpPropertiesBuilder;
import static org.jclouds.rimuhosting.miro.reference.RimuHostingConstants.*;

import java.net.URI;
import java.util.Properties;

/**
 * Builds properties used in RimuHosting Clients
 *
 * @author Adrian Cole
 */
public class RimuHostingPropertiesBuilder extends HttpPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_RIMUHOSTING_ENDPOINT, "https://rimuhosting.com/r");
      return properties;
   }

   public RimuHostingPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public RimuHostingPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public RimuHostingPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_RIMUHOSTING_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_RIMUHOSTING_PASSWORD, checkNotNull(secret, "password"));
      return this;
   }

   public RimuHostingPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_RIMUHOSTING_ENDPOINT, checkNotNull(endpoint, "endpoint")
              .toString());
      return this;
   }
}

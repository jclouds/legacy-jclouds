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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.gogrid.reference.GoGridConstants.PROPERTY_GOGRID_DEFAULT_DC;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

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
      properties.setProperty(PROPERTY_API_VERSION, GoGridAsyncClient.VERSION);
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.gogrid.com/api");
      properties.setProperty(PROPERTY_GOGRID_DEFAULT_DC, "SANFRANCISCO");
      return properties;
   }

   public GoGridPropertiesBuilder(Properties properties) {
      super(properties);
   }

}

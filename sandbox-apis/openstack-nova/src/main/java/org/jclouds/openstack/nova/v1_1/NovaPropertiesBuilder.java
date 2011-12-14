/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.openstack.nova.v1_1;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.util.Properties;
import java.util.regex.Pattern;

import org.jclouds.PropertiesBuilder;
import org.jclouds.util.Strings2;

/**
 * Builds properties used in Nova Clients
 * 
 * @author Adrian Cole
 */
public class NovaPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "http://localhost:8774/{apiversion}/{identity}");
      properties.setProperty(PROPERTY_API_VERSION, "v1.1");
      return properties;
   }

   public NovaPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public static final Pattern IDENTITY_PATTERN = Pattern.compile("\\{identity\\}");
   public static final Pattern API_VERSION_PATTERN = Pattern.compile("\\{apiversion\\}");

   @Override
   public Properties build() {
      // TODO determine if we can more elegantly do this. right now we have to
      // because URI.create() doesn't allow us to build a URi with an illegal
      // character '{'
      String endpoint = properties.getProperty(PROPERTY_ENDPOINT);
      String identity = properties.getProperty(PROPERTY_IDENTITY);
      String apiVersion = properties.getProperty(PROPERTY_API_VERSION);
      String withIdentity = Strings2.replaceAll(endpoint, IDENTITY_PATTERN, identity);
      String withVersion = Strings2.replaceAll(withIdentity, API_VERSION_PATTERN, apiVersion);
      properties.setProperty(PROPERTY_ENDPOINT, withVersion);
      return super.build();
   }
}

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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.http.HttpPropertiesBuilder;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Builds properties used in SDN Clients
 * 
 * @author Adrian Cole
 */
public class SDNPropertiesBuilder extends HttpPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT, "http://services.nirvanix.com");
      return properties;
   }

   public SDNPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public SDNPropertiesBuilder(String appkey, String appname, String username, String password) {
      super();
      withCredentials(appkey, appname, username, password);
   }

   public SDNPropertiesBuilder withCredentials(String appkey, String appname, String username,
            String password) {
      properties.setProperty(SDNConstants.PROPERTY_SDN_APPKEY, checkNotNull(appkey, "appkey"));
      properties.setProperty(SDNConstants.PROPERTY_SDN_APPNAME, checkNotNull(appname, "appname"));
      properties
               .setProperty(SDNConstants.PROPERTY_SDN_USERNAME, checkNotNull(username, "username"));
      properties
               .setProperty(SDNConstants.PROPERTY_SDN_PASSWORD, checkNotNull(password, "password"));
      return this;
   }

   public SDNPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return this;
   }

   @Override
   public SDNPropertiesBuilder withCredentials(String account, String key) {
      List<String> parts = Lists.newArrayList(Splitter.on('/').split(account));
      if (parts.size() != 3) {
         throw new IllegalArgumentException("account syntax is appkey/appname/username");
      }
      return withCredentials(parts.get(0), parts.get(1), parts.get(2), key);
   }
}

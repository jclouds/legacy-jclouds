/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.live;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.jclouds.Constants;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

/**
 * @author Victor Galkin
 */
public class PropertyHelper {

   private static String provider = "nova";

   public static Map<String, String> setupKeyPair(Properties properties) throws IOException {
      return ImmutableMap.of(
            "private", Resources.toString(Resources.getResource(properties.getProperty("test.ssh.keyfile.private")), Charsets.UTF_8),
            "public", Resources.toString(Resources.getResource(properties.getProperty("test.ssh.keyfile.public")), Charsets.UTF_8));
   }

   public static Properties setupProperties(Class<?> clazz) throws IOException {
      Properties properties = new Properties();

      String propertiesPath = System.getProperty("test.properties");
      if (propertiesPath != null) {
         InputStream propertiesStream = clazz.getResourceAsStream(propertiesPath);
         properties.load(propertiesStream);
      }

      properties.putAll(System.getProperties());

      return properties;
   }

   public static Properties setupOverrides(final Properties properties) {
      properties.setProperty(provider + ".identity", properties.getProperty("test." + provider + ".identity"));
      properties.setProperty(provider + ".credential", properties.getProperty("test." + provider + ".credential"));
      properties.setProperty(provider + ".endpoint", properties.getProperty("test." + provider + ".endpoint"));
      properties.setProperty(provider + ".apiversion", properties.getProperty("test." + provider + ".apiversion"));
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      return properties;
   }


}

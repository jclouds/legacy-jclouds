/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Properties;

/**
 * Test configuration.
 * 
 * @author Ignasi Barrera
 */
public class Config {
   /** The main configuration file. */
   private static final String CONFIG_FILE = "api-live.properties";

   /** The configuration properties */
   private Properties config;

   /** The singleton configuration instance. */
   private static Config instance;

   public Config(final String config) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      this.config = new Properties();

      try {
         this.config.load(cl.getResourceAsStream(config));
      } catch (IOException ex) {
         throw new RuntimeException("Could not load test configuration file", ex);
      }
   }

   public Config(final Properties config) {
      this.config = config;
   }

   public static String get(final String property) {
      return get(property, null);
   }

   public static String get(final String property, final String defaultValue) {
      if (instance == null) {
         String configFile = System.getProperty("abiquo.live.config", CONFIG_FILE);
         instance = new Config(configFile);
      }

      return checkNotNull(instance.config.getProperty(property, defaultValue));
   }

}

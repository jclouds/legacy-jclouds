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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.util.Properties;

import org.jclouds.Constants;
import org.testng.annotations.BeforeClass;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseRestClientLiveTest {
   protected String prefix = System.getProperty("user.name");

   protected String provider;
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiVersion;
   protected String buildVersion;


   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   protected Properties setupProperties() {

      if (emptyToNull(provider) == null)
         throw new NullPointerException("provider must not be null or empty:" + provider);
      if (emptyToNull(identity) == null)
         throw new NullPointerException("identity must not be null or empty:" + provider);

      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

      overrides.setProperty(provider + ".identity", identity);

      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiVersion != null)
         overrides.setProperty(provider + ".api-version", apiVersion);
      if (buildVersion != null)
         overrides.setProperty(provider + ".build-version", buildVersion);

      return overrides;
   }

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiVersion = System.getProperty("test." + provider + ".api-version");
      buildVersion = System.getProperty("test." + provider + ".build-version");
   }

}

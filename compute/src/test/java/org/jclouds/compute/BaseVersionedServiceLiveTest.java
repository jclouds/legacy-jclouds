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
package org.jclouds.compute;

import org.jclouds.Constants;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.BeforeClass;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

/**
 * 
 * @author Jason King
 */
public abstract class BaseVersionedServiceLiveTest {

   protected String provider;
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected String imageId;

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }
   
   protected Properties setupProperties() {

      if (emptyToNull(provider) == null) throw new NullPointerException("provider must not be null or empty:"+provider);
      if (emptyToNull(identity) == null) throw new NullPointerException("identity must not be null or empty:"+provider);

      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

      overrides.setProperty(provider + ".identity", identity);

      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      if (imageId != null)
         overrides.setProperty(provider + ".image-id", imageId);

      return overrides;
   }

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
      imageId = System.getProperty("test." + provider + ".image-id");
   }


}

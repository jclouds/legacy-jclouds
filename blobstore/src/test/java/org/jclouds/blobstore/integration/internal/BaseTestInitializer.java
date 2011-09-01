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
package org.jclouds.blobstore.integration.internal;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.testng.ITestContext;

import com.google.inject.Module;

public abstract class BaseTestInitializer {

   protected String provider = "transient";

   public BlobStoreContext init(Module configurationModule, ITestContext testContext) throws Exception {
      String endpoint = System.getProperty("test." + provider + ".endpoint");
      String app = System.getProperty("test.app");
      String identity = System.getProperty("test." + provider + ".identity");
      String credential = System.getProperty("test." + provider + ".credential");
      String apiversion = System.getProperty("test." + provider + ".apiversion");
      if (endpoint != null)
         testContext.setAttribute("test." + provider + ".endpoint", endpoint);
      if (app != null)
         testContext.setAttribute("test.app", app);
      if (identity != null)
         testContext.setAttribute("test." + provider + ".identity", identity);
      if (credential != null)
         testContext.setAttribute("test." + provider + ".credential", credential);
      if (credential != null)
         testContext.setAttribute("test." + provider + ".apiversion", apiversion);
      if (identity != null) {
         return createLiveContext(configurationModule, endpoint, apiversion, app, identity, credential);
      } else {
         return createStubContext();
      }
   }

   protected Properties setupProperties(String endpoint, String apiversion, String identity, String credential) {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      if (identity != null)
         overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   protected BlobStoreContext createStubContext() throws IOException {
      return new BlobStoreContextFactory().createContext("transient", "foo", "bar");
   }

   protected abstract BlobStoreContext createLiveContext(Module configurationModule, String url, String apiversion,
            String app, String identity, String key) throws IOException;
}
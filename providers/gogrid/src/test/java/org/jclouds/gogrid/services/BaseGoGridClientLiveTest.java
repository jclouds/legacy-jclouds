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
package org.jclouds.gogrid.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code GoGridClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseGoGridClientLiveTest {

   protected RestContext<GoGridClient, GoGridAsyncClient> restContext;
   protected String provider = "gogrid";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected ComputeServiceContext context;
   protected String prefix = System.getProperty("user.name");

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet.<Module> of(
               new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
      restContext = context.getProviderSpecificContext();
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}

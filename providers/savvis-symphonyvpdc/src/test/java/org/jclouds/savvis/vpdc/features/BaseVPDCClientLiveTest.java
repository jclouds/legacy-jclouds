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

package org.jclouds.savvis.vpdc.features;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.savvis.vpdc.VPDCAsyncClient;
import org.jclouds.savvis.vpdc.VPDCClient;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.jclouds.savvis.vpdc.reference.VPDCConstants;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VPDCClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseVPDCClientLiveTest {

   protected RestContext<VPDCClient, VPDCAsyncClient> restContext;
   protected String provider = "savvis-symphonyvpdc";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected ComputeServiceContext context;
   protected String email;
   protected RetryablePredicate<String> taskTester;
   protected String prefix = System.getProperty("user.name");

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      email = checkNotNull(System.getProperty("test." + VPDCConstants.PROPERTY_VPDC_VDC_EMAIL), "test."
               + VPDCConstants.PROPERTY_VPDC_VDC_EMAIL);
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(VPDCConstants.PROPERTY_VPDC_VDC_EMAIL, email);
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
               new Log4JLoggingModule(), new JschSshClientModule()), overrides);
      restContext = context.getProviderSpecificContext();
      taskTester = new RetryablePredicate<String>(new TaskSuccess(restContext.getApi()), 1200, 10, TimeUnit.SECONDS);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}

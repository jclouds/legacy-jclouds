/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.terremark.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "terremark.TerremarkVCloudExpressTemplateBuilderLiveTest")
public class TerremarkVCloudExpressTemplateBuilderLiveTest {
   protected String provider = "trmk-vcloudexpress";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
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
      return overrides;
   }

   @Test
   public void testTemplateBuilderCanUseImageId() {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), setupProperties());

         Template defaultTemplate = newContext.getComputeService().templateBuilder().build();

         Template template = newContext.getComputeService().templateBuilder().imageId(
                  defaultTemplate.getImage().getId()).build();
         assertEquals(template, defaultTemplate);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), setupProperties());

         Template defaultTemplate = newContext.getComputeService().templateBuilder().build();
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "9.10");
         assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
         assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

}
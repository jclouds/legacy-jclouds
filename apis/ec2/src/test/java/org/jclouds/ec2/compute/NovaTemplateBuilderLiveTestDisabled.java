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

package org.jclouds.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, groups = "live")
public class NovaTemplateBuilderLiveTestDisabled {
   protected String provider = "nova";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider + ".endpoint");
      apiversion = checkNotNull(System.getProperty("test." + provider + ".apiversion"), "test." + provider
               + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(provider + ".endpoint", endpoint);
      overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), setupProperties());

         Template defaultTemplate = newContext.getComputeService().templateBuilder().build();
         assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "9.10");
         assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
         assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(defaultTemplate.getLocation().getId(), "nova");
         assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

   @Test
   public void testTemplateBuilderWithNoOwnersParsesImageOnDemand() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         Properties overrides = setupProperties();
         // set owners to nothing
         overrides.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "");

         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), overrides);

         assertEquals(newContext.getComputeService().listImages().size(), 0);

         Template template = newContext.getComputeService().templateBuilder().imageId("nova/ami-6CD61336").build();
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), null);
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "nova");
         assertEquals(getCores(template.getHardware()), 1.0d);

         // ensure we cache the new image for next time
         assertEquals(newContext.getComputeService().listImages().size(), 1);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

}

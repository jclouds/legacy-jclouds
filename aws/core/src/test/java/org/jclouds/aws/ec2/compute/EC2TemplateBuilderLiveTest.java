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

package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.reference.EC2Constants;
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
@Test(groups = "live", testName = "ec2.EC2TemplateBuilderLiveTest")
public class EC2TemplateBuilderLiveTest {
   protected String provider = "ec2";
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
   public void testTemplateBuilderCanUseImageIdAndhardwareId() {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), setupProperties());

         Template template = newContext.getComputeService().templateBuilder().imageId("us-east-1/ami-ccb35ea5")
                  .hardwareId(InstanceType.M2_2XLARGE).build();

         System.out.println(template.getHardware());
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "5.4");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
         assertEquals(template.getImage().getVersion(), "4.4.10");
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "us-east-1");
         assertEquals(getCores(template.getHardware()), 4.0d);
         assertEquals(template.getHardware().getId(), InstanceType.M2_2XLARGE);
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
         assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "0.9.8-beta");
         assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
         assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
         assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
         assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

   @Test
   public void testTemplateBuilderMicro() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
                  .<Module> of(new Log4JLoggingModule()), setupProperties());

         Template microTemplate = newContext.getComputeService().templateBuilder().hardwareId(InstanceType.T1_MICRO)
                  .build();
         System.out.println(microTemplate.getHardware());

         assert (microTemplate.getImage().getProviderId().startsWith("ami-")) : microTemplate;
         assertEquals(microTemplate.getImage().getOperatingSystem().getVersion(), "9.10");
         assertEquals(microTemplate.getImage().getOperatingSystem().is64Bit(), false);
         assertEquals(microTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
         assertEquals(microTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
         assertEquals(microTemplate.getLocation().getId(), "us-east-1");
         assertEquals(getCores(microTemplate.getHardware()), 1.0d);
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

         Template template = newContext.getComputeService().templateBuilder().imageId("us-east-1/ami-ccb35ea5").build();
         System.out.println(template.getHardware());
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "5.4");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
         assertEquals(template.getImage().getVersion(), "4.4.10");
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "us-east-1");
         assertEquals(getCores(template.getHardware()), 2.0d);
         assertEquals(template.getHardware().getId(), "m1.large"); // because it
         // is 64bit

         // ensure we cache the new image for next time
         assertEquals(newContext.getComputeService().listImages().size(), 1);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

}
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ec2.EC2TemplateBuilderLiveTest")
public class EC2TemplateBuilderLiveTest {
   private String password;
   private String user;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
   }

   @Test
   public void testTemplateBuilderCanUseImageId() {
   }

   @Test
   public void testTemplateBuilder() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         newContext = new ComputeServiceContextFactory().createContext("ec2", user, password,
                  ImmutableSet.of(new Log4JLoggingModule()));

         Template defaultTemplate = newContext.getComputeService().templateBuilder().build();
         assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
         assertEquals(defaultTemplate.getImage().getName(), "9.10");
         assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_32);
         assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
         assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
         assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
         newContext.getComputeService().templateBuilder().imageId(
                  Iterables.get(newContext.getComputeService().listImages(), 0).getProviderId()).build();
         newContext.getComputeService().templateBuilder().osFamily(OsFamily.UBUNTU).smallest()
                  .architecture(Architecture.X86_32).imageId("ami-7e28ca17").build();
         newContext.getComputeService().templateBuilder().osFamily(OsFamily.UBUNTU).smallest()
                  .architecture(Architecture.X86_32).imageId("ami-bb709dd2").build();
      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

   @Test
   public void testTemplateBuilderWithNoOwnersParsesImageOnDemand() throws IOException {
      ComputeServiceContext newContext = null;
      try {
         Properties overrides = new Properties();
         // set owners to nothing
         overrides.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "");

         newContext = new ComputeServiceContextFactory().createContext("ec2", user, password,
                  ImmutableSet.of(new Log4JLoggingModule()), overrides);

         assertEquals(newContext.getComputeService().listImages().size(), 0);

         Template template = newContext.getComputeService().templateBuilder().imageId(
                  "ami-ccb35ea5").build();
         System.out.println(template.getImage());
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getName(), "5.4");
         assertEquals(template.getImage().getArchitecture(), Architecture.X86_64);
         assertEquals(template.getImage().getOsFamily(), OsFamily.CENTOS);
         assertEquals(template.getImage().getVersion(), "4.4.10");
         assertEquals(template.getLocation().getId(), "us-east-1");
         assertEquals(template.getSize().getCores(), 4.0d); // because it is 64bit
         
         //ensure we cache the new image for next time
         assertEquals(newContext.getComputeService().listImages().size(), 1);

      } finally {
         if (newContext != null)
            newContext.close();
      }
   }

}

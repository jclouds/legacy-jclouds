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

package org.jclouds.chef.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefContextFactory;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.ChefComputeServiceLiveTest")
public class ChefComputeServiceLiveTest {

   private ComputeServiceContext computeContext;
   private ChefContext chefContext;

   @BeforeGroups(groups = { "live" })
   public void setupCompute() {
      String computeProvider = checkNotNull(System.getProperty("jclouds.compute.provider"), "jclouds.compute.provider");
      String computeEndpoint = System.getProperty("jclouds.compute.endpoint");
      Properties props = new Properties();
      if (computeEndpoint != null && !computeEndpoint.trim().equals(""))
         props.setProperty(computeProvider + ".endpoint", computeEndpoint);
      String computeIdentity = checkNotNull(System.getProperty("jclouds.compute.identity"), "jclouds.compute.identity");
      String computeCredential = checkNotNull(System.getProperty("jclouds.compute.credential"),
               "jclouds.compute.credential");
      computeContext = new ComputeServiceContextFactory().createContext(computeProvider, computeIdentity,
               computeCredential, ImmutableSet.of(new Log4JLoggingModule()), props);
   }

   @BeforeGroups(groups = { "live" })
   public void setupChef() throws IOException {
      String chefEndpoint = checkNotNull(System.getProperty("jclouds.chef.endpoint"), "jclouds.chef.endpoint");
      String chefIdentity = checkNotNull(System.getProperty("jclouds.chef.identity"), "jclouds.chef.identity");
      String chefCredentialFile = System.getProperty("jclouds.chef.credential.pem");
      if (chefCredentialFile == null || chefCredentialFile.equals(""))
         chefCredentialFile = System.getProperty("user.home") + "/.chef/" + chefIdentity + ".pem";
      Properties props = new Properties();
      props.setProperty("chef.endpoint", chefEndpoint);
      chefContext = new ChefContextFactory().createContext(chefIdentity, Files.toString(new File(chefCredentialFile),
               Charsets.UTF_8), ImmutableSet.<Module> of(new Log4JLoggingModule()), props);
   }

   @Test
   public void test() {
      computeContext.getComputeService().listNodes();
      chefContext.getChefService().listNodesDetails();
   }

   @AfterGroups(groups = { "live" })
   public void teardownCompute() {
      if (computeContext != null)
         computeContext.close();
   }

   @AfterGroups(groups = { "live" })
   public void teardownChef() {
      if (chefContext != null)
         chefContext.close();
   }
}

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

package org.jclouds.vi.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vsphere.ViExperimentLiveTest")
public class ViExperimentLiveTest {
   protected String provider = "vsphere";
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

   @Test
   public void testAndExperiment() {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(
               endpoint, identity, credential));
         
//         Set<? extends Location> locations = context.getComputeService().listAssignableLocations();
//         
//         Set<? extends ComputeMetadata> nodes = context.getComputeService().listNodes();

         // TODO
//         Set<? extends Hardware> hardwares = context.getComputeService().listHardwareProfiles();
//
//         Set<? extends Image> images = context.getComputeService().listImages();

         NodeMetadata node = context.getComputeService().getNodeMetadata("provaVM");
         System.out.println(node);

         /*
          * We will probably make a default template out of properties at some point You can control
          * the default template via overriding a method in standalonecomputeservicexontextmodule
          */
         
         /*          Template defaultTemplate = context.getComputeService().templateBuilder()
         .hardwareId("d106ae67-5a1b-8f91-b311-83c93bcb0a1f").imageId("1") //.locationId("")
         .build();
         Set<? extends NodeMetadata> nodeMetadataSet = context.getComputeService().runNodesWithTag("MyServer", 1);
         for (NodeMetadata nodeMetadata : nodeMetadataSet) {
            
//              context.getComputeService().suspendNode(nodeMetadata.getId());
//              context.getComputeService().resumeNode(nodeMetadata.getId());
             
            context.getComputeService().destroyNode(nodeMetadata.getId());
         }
      } catch (Exception e) {
         e.printStackTrace();
         */
      } finally {
         if (context != null)
            context.close();
      }
   }

}
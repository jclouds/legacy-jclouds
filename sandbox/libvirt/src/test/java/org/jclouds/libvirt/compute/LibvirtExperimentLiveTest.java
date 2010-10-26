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

package org.jclouds.libvirt.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.StandaloneComputeServiceContextSpec;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.domain.LibvirtComputeServiceContextModule;
import org.libvirt.Domain;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "libvirt.LibvirtExperimentLiveTest")
public class LibvirtExperimentLiveTest {
   protected String provider = "libvirt";
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
         context = new ComputeServiceContextFactory()
               .createContext(new StandaloneComputeServiceContextSpec<Domain, Domain, Image, Datacenter>("libvirt",
                     endpoint, apiversion, identity, credential, new LibvirtComputeServiceContextModule(), ImmutableSet
                           .<Module> of()));
         
         System.out.println("images " + context.getComputeService().listImages());
         System.out.println("hardware profiles " + context.getComputeService().listHardwareProfiles()); 
         
         Template defaultTemplate = context.getComputeService().templateBuilder()
         //.hardwareId("").locationId("").imageId("")
         .build();
         	
    /*
     * We will probably make a default template out of properties at some point
     * You can control the default template via overriding a method in standalonecomputeservicexontextmodule
     */
         
         
//         context.getComputeService().templateOptions().;
         context.getComputeService().runNodesWithTag("test", 1);
        System.out.println(context.getComputeService().listNodes()); 
        
      } catch (RunNodesException e) {
		e.printStackTrace();
	} finally {
         if (context != null)
            context.close();
      }
   }

}
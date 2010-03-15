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

package org.jclouds.vcloud.bluelock.compute;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.http.HttpResponseException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.VCloudComputeServiceLiveTest;
import org.jclouds.vcloud.domain.NamedResource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.BlueLockVCloudComputeServiceLiveTest")
public class BlueLockVCloudComputeServiceLiveTest extends VCloudComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "bluelock";
   }

   @Override
   public void testListImages() throws Exception {
      super.testListImages();
      Map<String, ? extends Image> images = client.getImages();
      assertEquals(images.size(), 5);
      // TODO verify parsing works
   }

//   // https://forums.bluelock.com/showthread.php?p=353#post353
//   @Override
//   @Test(enabled = false)
//   public void testCreate() throws Exception {
//      super.testCreate();
//   }
//
//   @Override
//   @Test(enabled = false)
//   public void testGet() throws Exception {
//      super.testGet();
//   }
//
//   @Override
//   @Test(enabled = false)
//   public void testReboot() throws Exception {
//      super.testReboot();
//   }

   @Test
   public void testExample() throws Exception {

      // get a synchronous object to use for manipulating vcloud objects in BlueLock
      VCloudClient bluelockClient = VCloudClient.class.cast(context.getProviderSpecificContext()
               .getApi());

      // look at only vApp templates in my default vDC
      Map<String, NamedResource> vAppTemplatesByName = Maps.filterValues(bluelockClient
               .getDefaultVDC().getResourceEntities(), new Predicate<NamedResource>() {

         @Override
         public boolean apply(NamedResource input) {
            return input.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML);
         }

      });

      // get details on a specific template I know by name
      bluelockClient.getVAppTemplate(vAppTemplatesByName
               .get("Ubuntu904Serverx64 1CPUx1GBx20GB a01").getId());
   }

   @Test
   public void testErrorWhereAllNetworksReturn403() throws Exception {
      VCloudClient bluelockClient = VCloudClient.class.cast(context.getProviderSpecificContext()
               .getApi());
      for (String i : new String[] { "1", "2", "3", "4" }) {
         try {
            bluelockClient.getNetwork(i);
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 403);
         }
      }

   }
}

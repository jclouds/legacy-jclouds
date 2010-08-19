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
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.compute.VCloudExpressComputeServiceLiveTest;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "terremark.TerremarkVCloudComputeServiceLiveTest")
public class TerremarkVCloudExpressComputeServiceLiveTest extends VCloudExpressComputeServiceLiveTest {

   @Override
   public void setServiceDefaults() {
      provider = "trmk-vcloudexpress";
      tag = "trmkvcx";
   }

   @Override
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("trmk-vcloudexpress.identity"), "trmk-vcloudexpress.identity");
      credential = checkNotNull(System.getProperty("trmk-vcloudexpress.credential"), "trmk-vcloudexpress.credential");
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getDescription(), "Miami Environment 1");
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient> tmContext = new ComputeServiceContextFactory()
               .createContext(provider, identity, credential).getProviderSpecificContext();
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      Template template = super.buildTemplate(templateBuilder);
      Image image = template.getImage();
      assert image.getDefaultCredentials().identity != null : image;
      assert image.getDefaultCredentials().credential != null : image;
      return template;
   }

   // currently, the wrong CIM OSType data is coming back.
   @Override
   protected void checkOsMatchesTemplate(NodeMetadata node) {
      if (node.getOperatingSystem() != null)
         assertEquals(node.getOperatingSystem().getFamily(), OsFamily.UNKNOWN);
   }

   @Override
   public void testListImages() throws Exception {
      for (Image image : client.listImages()) {
         assert image.getProviderId() != null : image;
         // image.getLocationId() can be null, if it is a location-free image
         assertEquals(image.getType(), ComputeType.IMAGE);
         assert image.getDefaultCredentials().identity != null : image;
         if (image.getOperatingSystem().getFamily() != OsFamily.WINDOWS)
            assert image.getDefaultCredentials().credential != null : image;
      }
   }

}
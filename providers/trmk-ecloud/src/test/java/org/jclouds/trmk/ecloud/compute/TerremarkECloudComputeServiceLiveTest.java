/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.ecloud.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.trmk.ecloud.TerremarkECloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

/**
 * This test is disabled, as it doesn't work while there are too few public ip
 * addresses.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true)
public class TerremarkECloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC,
            ".* - " + System.getProperty("test.trmk-ecloud.datacenter", "MIA"));
      return props;
   }

   public TerremarkECloudComputeServiceLiveTest() {
      provider = "trmk-ecloud";
   }

   @Override
   public void setServiceDefaults() {
      group = "te";
   }

   // terremark does not support metadata
   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(ImmutableMap.<String, String> of()) : String.format(
            "node userMetadata did not match %s %s", userMetadata, node);
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      Template template = super.buildTemplate(templateBuilder);
      Image image = template.getImage();
      assert image.getDefaultCredentials() != null && image.getDefaultCredentials().identity != null : image;
      assert image.getDefaultCredentials() != null && image.getDefaultCredentials().credential != null : image;
      return template;
   }

   @Override
   public void testListImages() throws Exception {
      for (Image image : client.listImages()) {
         assert image.getProviderId() != null : image;
         // image.getLocationId() can be null, if it is a location-free image
         assertEquals(image.getType(), ComputeType.IMAGE);
         if (image.getOperatingSystem().getFamily() != OsFamily.WINDOWS
               && image.getOperatingSystem().getFamily() != OsFamily.SOLARIS) {
            assert image.getDefaultCredentials() != null && image.getDefaultCredentials().identity != null : image;
            assert image.getDefaultCredentials().credential != null : image;
         }
      }
   }

   @Override
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getProviderId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
         NodeMetadata allData = client.getNodeMetadata(node.getId());
         TerremarkECloudClient api = view.utils().injector().getInstance(TerremarkECloudClient.class);
         VApp vApp = api.findVAppInOrgVDCNamed(allData.getLocation().getParent().getDescription(),
               allData.getLocation().getDescription(), allData.getName());
         assertEquals(vApp.getName(), allData.getName());
      }
   }

   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}

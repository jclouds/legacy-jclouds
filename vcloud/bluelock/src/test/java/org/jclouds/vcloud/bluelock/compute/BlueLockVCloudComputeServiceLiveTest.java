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

import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.BlueLockVCloudComputeServiceLiveTest")
public class BlueLockVCloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "bluelock";
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.osFamily(UBUNTU).smallest().build();
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Override
   public void testListImages() throws Exception {
      super.testListImages();
      Map<String, ? extends Image> images = client.getImages();
      assertEquals(images.size(), 5);
      // TODO verify parsing works
   }

   // https://forums.bluelock.com/showthread.php?p=353#post353
   @Override
   @Test(enabled = false)
   public void testCreate() throws Exception {
      super.testCreate();
   }

   @Override
   @Test(enabled = false)
   public void testGet() throws Exception {
      super.testGet();
   }

   @Override
   @Test(enabled = false)
   public void testReboot() throws Exception {
      super.testReboot();
   }
}

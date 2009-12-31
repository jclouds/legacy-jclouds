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
package org.jclouds.vcloud.options;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.disk;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.inNetwork;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.memory;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code InstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.InstantiateVAppTemplateOptionsTest")
public class InstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testInNetwork() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testInNetworkStatic() {
      InstantiateVAppTemplateOptions options = inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testCpuCount() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.processorCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      InstantiateVAppTemplateOptions options = processorCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testRam() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }

   @Test
   public void testRamStatic() {
      InstantiateVAppTemplateOptions options = memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testRamStaticWrong() {
      memory(511);
   }

   @Test
   public void testDisk() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.disk(512);
      assertEquals(options.getDiskSizeKilobytes(), "512");
   }

   @Test
   public void testDiskStatic() {
      InstantiateVAppTemplateOptions options = disk(512);
      assertEquals(options.getDiskSizeKilobytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDiskStaticWrong() {
      disk(0);
   }

}

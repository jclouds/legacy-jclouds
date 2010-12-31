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

package org.jclouds.vcloud.terremark.options;

import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.addNetworkConfig;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.disk;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.inGroup;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.inRow;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.memory;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.withPassword;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkInstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TerremarkInstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new SaxParserModule());

   @Test
   public void testInGroup() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.inGroup("group1");
      assertEquals(options.getProperties().get("group"), "group1");
   }

   @Test
   public void testInGroupStatic() {
      TerremarkInstantiateVAppTemplateOptions options = inGroup("group1");
      assertEquals(options.getProperties().get("group"), "group1");
   }

   @Test
   public void testInRow() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.inRow("row1");
      assertEquals(options.getProperties().get("row"), "row1");
   }

   @Test
   public void testInRowStatic() {
      TerremarkInstantiateVAppTemplateOptions options = inRow("row1");
      assertEquals(options.getProperties().get("row"), "row1");
   }

   @Test
   public void testWithPassword() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.withPassword("password1");
      assertEquals(options.getProperties().get("password"), "password1");
   }

   @Test
   public void testWithPasswordStatic() {
      TerremarkInstantiateVAppTemplateOptions options = withPassword("password1");
      assertEquals(options.getProperties().get("password"), "password1");
   }

   @Test
   public void testAddNetworkConfig() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.addNetworkConfig(new NetworkConfig("default", URI.create("http://localhost"), FenceMode.BRIDGED));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.BRIDGED);
   }

   @Test
   public void testAddNetworkConfigStatic() {
      TerremarkInstantiateVAppTemplateOptions options = addNetworkConfig(new NetworkConfig("default", URI
               .create("http://localhost"), FenceMode.BRIDGED));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.BRIDGED);
   }

   @Test
   public void testCpuCount() {
      assertEquals(processorCount(3).getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      TerremarkInstantiateVAppTemplateOptions options = processorCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testMegabytes() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }

   @Test
   public void testMegabytesStatic() {
      TerremarkInstantiateVAppTemplateOptions options = memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDiskSizeKilobytes() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.disk(512);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDiskSizeKilobytesStatic() {
      disk(512);
   }

}

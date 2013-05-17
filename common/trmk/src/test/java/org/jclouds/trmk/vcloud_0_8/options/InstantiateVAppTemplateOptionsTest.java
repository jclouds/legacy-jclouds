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
package org.jclouds.trmk.vcloud_0_8.options;

import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.customizeOnInstantiate;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.inGroup;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.inRow;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.memory;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.processorCount;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.withPassword;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.NetworkConfig;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code InstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new SaxParserModule());

   @Test
   public void testCustomizeOnInstantiate() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.customizeOnInstantiate(true);
      assertEquals(options.shouldCustomizeOnInstantiate(), Boolean.TRUE);
   }

   @Test
   public void testCustomizeOnInstantiateStatic() {
      InstantiateVAppTemplateOptions options = customizeOnInstantiate(true);
      assertEquals(options.shouldCustomizeOnInstantiate(), Boolean.TRUE);
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

   @Test
   public void testInGroup() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inGroup("group1");
      assertEquals(options.getProperties().get("group"), "group1");
   }

   @Test
   public void testInGroupStatic() {
      InstantiateVAppTemplateOptions options = inGroup("group1");
      assertEquals(options.getProperties().get("group"), "group1");
   }

   @Test
   public void testInRow() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inRow("row1");
      assertEquals(options.getProperties().get("row"), "row1");
   }

   @Test
   public void testInRowStatic() {
      InstantiateVAppTemplateOptions options = inRow("row1");
      assertEquals(options.getProperties().get("row"), "row1");
   }

   @Test
   public void testWithPassword() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.withPassword("password1");
      assertEquals(options.getProperties().get("password"), "password1");
   }

   @Test
   public void testWithPasswordStatic() {
      InstantiateVAppTemplateOptions options = withPassword("password1");
      assertEquals(options.getProperties().get("password"), "password1");
   }

   @Test
   public void testAddNetworkConfig() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.addNetworkConfig(new NetworkConfig("default", URI.create("http://localhost"), FenceMode.ALLOW_IN_OUT));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.ALLOW_IN_OUT);
   }

   @Test
   public void testAddNetworkConfigStatic() {
      InstantiateVAppTemplateOptions options = addNetworkConfig(new NetworkConfig("default",
            URI.create("http://localhost"), FenceMode.ALLOW_IN_OUT));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.ALLOW_IN_OUT);
   }

   @Test
   public void testCpuCount() {
      assertEquals(processorCount(3).getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      InstantiateVAppTemplateOptions options = processorCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testMegabytes() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }

   @Test
   public void testMegabytesStatic() {
      InstantiateVAppTemplateOptions options = memory(512);
      assertEquals(options.getMemorySizeMegabytes(), "512");
   }
}

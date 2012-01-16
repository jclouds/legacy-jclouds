/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.options;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.description;
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
 * Tests behavior of {@code InstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new SaxParserModule());

   @Test
   public void testAddNetworkConfig() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.addNetworkConfig(new NetworkConfig("default", URI.create("http://localhost"), FenceMode.BRIDGED));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.BRIDGED);
   }

   @Test
   public void testAddNetworkConfigStatic() {
      InstantiateVAppTemplateOptions options = addNetworkConfig(new NetworkConfig("default",
            URI.create("http://localhost"), FenceMode.BRIDGED));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getNetworkName(), "default");
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getParentNetwork(), URI.create("http://localhost"));
      assertEquals(Iterables.get(options.getNetworkConfig(), 0).getFenceMode(), FenceMode.BRIDGED);
   }

   @Test
   public void testDescription() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.description("foo");
      assertEquals(options.getDescription(), "foo");
   }

   @Test
   public void testDescriptionStatic() {
      InstantiateVAppTemplateOptions options = description("foo");
      assertEquals(options.getDescription(), "foo");
   }
}

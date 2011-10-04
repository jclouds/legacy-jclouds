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
package org.jclouds.softlayer.compute.functions;

import static com.google.inject.name.Names.bindProperties;
import static org.jclouds.softlayer.compute.functions.ProductItemsToHardware.hardwareId;
import static org.testng.AssertJUnit.assertEquals;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.softlayer.SoftLayerPropertiesBuilder;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests {@code ProductItemsToHardware}
 * 
 * @author Jason King
 */
@Test(groups = "unit")
public class ProductItemsToHardwareTest {

   @Test
   public void testHardwareId() {
      ProductItem item1 = ProductItem.builder().price(ProductItemPrice.builder().id(123).build()).build();
      ProductItem item2 = ProductItem.builder().price(ProductItemPrice.builder().id(456).build()).build();
      ProductItem item3 = ProductItem.builder().price(ProductItemPrice.builder().id(789).build()).build();

      String id = hardwareId().apply(ImmutableList.of(item1, item2, item3));
      assertEquals("123,456,789", id);
   }

   @Test
   public void testHardware() {
      ProductItem cpuItem = ProductItem.builder().id(1).description("2 x 2.0 GHz Cores").units("PRIVATE_CORE")
               .capacity(2F).price(ProductItemPrice.builder().id(123).build()).build();

      ProductItem ramItem = ProductItem.builder().id(2).description("2GB ram").capacity(2F).category(
               ProductItemCategory.builder().categoryCode("ram").build()).price(
               ProductItemPrice.builder().id(456).build()).build();

      ProductItem volumeItem = ProductItem.builder().id(3).description("100 GB (SAN)").capacity(100F).price(
               ProductItemPrice.builder().id(789).build()).category(
               ProductItemCategory.builder().categoryCode("guest_disk0").build()).build();

      Hardware hardware = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindProperties(binder(), new SoftLayerPropertiesBuilder(new Properties()).build());
         }

      }).getInstance(ProductItemsToHardware.class).apply(ImmutableSet.of(cpuItem, ramItem, volumeItem));

      assertEquals("123,456,789", hardware.getId());

      List<? extends Processor> processors = hardware.getProcessors();
      assertEquals(1, processors.size());
      assertEquals(2.0, processors.get(0).getCores());

      assertEquals(2, hardware.getRam());

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(1, volumes.size());
      assertEquals(100F, volumes.get(0).getSize());
   }
}

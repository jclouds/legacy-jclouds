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
package org.jclouds.softlayer.compute.functions;

import static com.google.inject.name.Names.bindProperties;
import static org.jclouds.softlayer.compute.functions.ProductItemsToHardware.hardwareId;
import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.softlayer.SoftLayerProviderMetadata;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemCategory;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.testng.annotations.BeforeMethod;
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

   private ProductItemsToHardware toHardware;
   private ProductItem cpuItem;
   private ProductItem ramItem;
   private ProductItem volumeItem;

   @BeforeMethod
   public void setup() {

      toHardware = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindProperties(binder(), new SoftLayerProviderMetadata().getDefaultProperties());
         }
      }).getInstance(ProductItemsToHardware.class);


      cpuItem = ProductItem.builder()
            .id(1)
            .description("2 x 2.0 GHz Cores")
            .capacity(2F)
            .categories(ProductItemCategory.builder().categoryCode("guest_core").build())
            .prices(ProductItemPrice.builder().id(123).build())
            .build();

      ramItem = ProductItem.builder().id(2).description("2GB ram").capacity(2F).categories(
            ProductItemCategory.builder().categoryCode("ram").build()).prices(
            ProductItemPrice.builder().id(456).build()).build();

      volumeItem = ProductItem.builder().id(3).description("100 GB (SAN)").capacity(100F).prices(
            ProductItemPrice.builder().id(789).build()).categories(
            ProductItemCategory.builder().categoryCode("guest_disk0").build()).build();


   }

   @Test
   public void testHardwareId() {
      ProductItem item1 = ProductItem.builder().prices(ProductItemPrice.builder().id(123).build()).build();
      ProductItem item2 = ProductItem.builder().prices(ProductItemPrice.builder().id(456).build()).build();
      ProductItem item3 = ProductItem.builder().prices(ProductItemPrice.builder().id(789).build()).build();

      String id = hardwareId().apply(ImmutableList.of(item1, item2, item3));
      assertEquals("123,456,789", id);
   }

   @Test
   public void testHardware() {

      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem));

      assertEquals("123,456,789", hardware.getId());

      List<? extends Processor> processors = hardware.getProcessors();
      assertEquals(1, processors.size());
      assertEquals(2.0, processors.get(0).getCores());

      assertEquals(2, hardware.getRam());

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(1, volumes.size());
      Volume volume = volumes.get(0);
      assertEquals(100F, volume.getSize());
      assertEquals(Volume.Type.SAN, volume.getType());
      assertEquals(true, volume.isBootDevice());

   }

   @Test
   public void testHardwareWithPrivateCore() {

     cpuItem = cpuItem.toBuilder()
           .description("Private 2 x 2.0 GHz Cores")
           .build();

     Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem));

     assertEquals("123,456,789", hardware.getId());

     List<? extends Processor> processors = hardware.getProcessors();
     assertEquals(1, processors.size());
     assertEquals(2.0, processors.get(0).getCores());

     assertEquals(2, hardware.getRam());

     List<? extends Volume> volumes = hardware.getVolumes();
     assertEquals(1, volumes.size());
     assertEquals(100F, volumes.get(0).getSize());
   }

   @Test
   public void testHardwareWithTwoDisks() {
      ProductItem localVolumeItem = ProductItem.builder().id(4).description("25 GB").capacity(25F).prices(
            ProductItemPrice.builder().id(987).build()).categories(
            ProductItemCategory.builder().categoryCode("guest_disk1").build()).build();

      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem,localVolumeItem));

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(2, volumes.size());
      Volume volume = volumes.get(0);
      assertEquals(100F, volume.getSize());
      assertEquals(Volume.Type.SAN, volume.getType());
      assertEquals(true, volume.isBootDevice());

      Volume volume1 = volumes.get(1);
      assertEquals(25F, volume1.getSize());
      assertEquals(Volume.Type.LOCAL, volume1.getType());
      assertEquals(false, volume1.isBootDevice());
   }
}

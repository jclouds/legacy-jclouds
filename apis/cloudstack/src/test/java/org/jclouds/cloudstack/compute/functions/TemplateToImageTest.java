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
package org.jclouds.cloudstack.compute.functions;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.parse.ListTemplatesResponseTest;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests {@code TemplateToImage}
 * 
 */
@Test(groups = "unit")
public class TemplateToImageTest {
   static Supplier<Set<? extends Location>> locationSupplier = Suppliers
         .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(ZoneToLocationTest.one,
               ZoneToLocationTest.two));

   static TemplateToImage function = new TemplateToImage(locationSupplier, TemplateToOperatingSystemTest.function);

   // location free image
   static Image one = new ImageBuilder().id("2").providerId("2").name("CentOS 5.3(64-bit) no GUI (XenServer)")
         .operatingSystem(TemplateToOperatingSystemTest.one).description("CentOS 5.3(64-bit) no GUI (XenServer)")
         .status(Status.AVAILABLE).build();
   // location free image
   static Image two = new ImageBuilder().id("4").providerId("4").name("CentOS 5.5(64-bit) no GUI (KVM)")
         .operatingSystem(TemplateToOperatingSystemTest.two).description("CentOS 5.5(64-bit) no GUI (KVM)")
         .status(Status.AVAILABLE).build();
   static Image three = new ImageBuilder().id("203").providerId("203").name("Windows 7 KVM")
         .operatingSystem(TemplateToOperatingSystemTest.three).description("Windows 7 KVM")
         .location(ZoneToLocationTest.two).status(Status.AVAILABLE).build();
   // location free image
   static Image four = new ImageBuilder().id("7").providerId("7").name("CentOS 5.3(64-bit) no GUI (vSphere)")
         .operatingSystem(TemplateToOperatingSystemTest.four).description("CentOS 5.3(64-bit) no GUI (vSphere)")
         .status(Status.AVAILABLE).build();
   static Image five = new ImageBuilder().id("241").providerId("241").name("kvmdev4")
         .operatingSystem(TemplateToOperatingSystemTest.five).description("v5.6.28_Dev4")
         .location(ZoneToLocationTest.two).status(Status.AVAILABLE).build();

   @Test
   public void test() {

      Set<Image> expected = ImmutableSet.of(one, two, three, four, five);

      Set<Template> offerings = new ListTemplatesResponseTest().expected();

      Iterable<Image> profiles = Iterables.transform(offerings, function);

      assertEquals(profiles.toString(), expected.toString());
   }

}

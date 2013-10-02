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

import java.util.Map;
import java.util.Set;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.parse.ListOSCategoriesResponseTest;
import org.jclouds.cloudstack.parse.ListOSTypesResponseTest;
import org.jclouds.cloudstack.parse.ListTemplatesResponseTest;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Guice;

/**
 * Tests {@code TemplateToOperatingSystem}
 * 
 */
@Test(groups = "unit")
public class TemplateToOperatingSystemTest {
   static Map<String, OSType> ostypes = Maps.<String, OSType> uniqueIndex(new ListOSTypesResponseTest().expected(),
         new Function<OSType, String>() {

            @Override
            public String apply(OSType arg0) {
               return arg0.getId();
            }
         });

   static TemplateToOperatingSystem function = new TemplateToOperatingSystem(Suppliers.ofInstance(ostypes),
         Suppliers.ofInstance(new ListOSCategoriesResponseTest().expected()), new BaseComputeServiceContextModule() {
         }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
               .getInstance(Json.class)));

   static OperatingSystem one = OperatingSystem.builder().name("CentOS").family(OsFamily.CENTOS).is64Bit(false)
         .version("5.3").description("CentOS 5.3 (32-bit)").build();
   static OperatingSystem two = OperatingSystem.builder().name("CentOS").family(OsFamily.CENTOS).is64Bit(true)
         .version("5.5").description("CentOS 5.5 (64-bit)").build();
   static OperatingSystem three = OperatingSystem.builder().name("Windows").family(OsFamily.WINDOWS).is64Bit(false)
         .version("7").description("Windows 7 (32-bit)").build();
   static OperatingSystem four = OperatingSystem.builder().name("CentOS").family(OsFamily.CENTOS).is64Bit(true)
         .version("5.3").description("CentOS 5.3 (64-bit)").build();
   static OperatingSystem five = OperatingSystem.builder().name("CentOS").family(OsFamily.CENTOS).is64Bit(true)
         .version("5.4").description("CentOS 5.4 (64-bit)").build();

   @Test
   public void test() {

      Set<OperatingSystem> expected = ImmutableSet.of(one, two, three, four, five);

      Set<Template> offerings = new ListTemplatesResponseTest().expected();

      Iterable<OperatingSystem> profiles = Iterables.transform(offerings, function);

      assertEquals(profiles.toString(), expected.toString());
   }

}

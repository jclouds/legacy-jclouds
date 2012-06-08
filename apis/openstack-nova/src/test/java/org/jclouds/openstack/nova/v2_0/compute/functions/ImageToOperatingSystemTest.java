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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageToOperatingSystem;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests for the function for transforming a nova specific Image into a generic
 * OperatingSystem object.
 * 
 * @author Matt Stephenson
 */
public class ImageToOperatingSystemTest {

   @Test(dataProvider = "getOsFamilyValues")
   public void testOsFamilyValues(OsFamily family) {

      Image imageToConvert = Image.builder().id("id-" + family.name()).name(family.name()).build();

      ImageToOperatingSystem converter = new ImageToOperatingSystem(
            new HashMap<OsFamily, Map<String, String>>());

      OperatingSystem convertedOs = converter.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), family);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), null);
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }

   @DataProvider
   public Object[][] getOsFamilyValues() {
      return Iterables.toArray(
            Iterables.transform(Arrays.asList(OsFamily.values()), new Function<OsFamily, Object[]>() {
               @Override
               public Object[] apply(@Nullable OsFamily osFamily) {
                  return new Object[] { osFamily };
               }
            }), Object[].class);
   }

   @Test
   public void testWindowsServer2008R2x64() {
      String name = "Windows Server 2008 R2 x64";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();

      Map<OsFamily, Map<String, String>> osFamilyMap = new HashMap<OsFamily, Map<String, String>>();
      osFamilyMap.put(OsFamily.WINDOWS, ImmutableMap.of("Server 2008 R2", "Server-2008-R2"));

      ImageToOperatingSystem converter = new ImageToOperatingSystem(osFamilyMap);

      OperatingSystem convertedOs = converter.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.WINDOWS);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), "Server-2008-R2");
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }

   @Test
   public void testWindows98x86() {
      String name = "Windows 98 x86";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();

      Map<OsFamily, Map<String, String>> osFamilyMap = new HashMap<OsFamily, Map<String, String>>();
      osFamilyMap.put(OsFamily.WINDOWS, ImmutableMap.of("98", "98"));

      ImageToOperatingSystem converter = new ImageToOperatingSystem(osFamilyMap);

      OperatingSystem convertedOs = converter.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.WINDOWS);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), "98");
      assertEquals(convertedOs.getArch(), null);
      assertFalse(convertedOs.is64Bit());
   }

   @Test
   public void testRHEL() {
      String name = "Red Hat EL";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();

      ImageToOperatingSystem converter = new ImageToOperatingSystem(
            new HashMap<OsFamily, Map<String, String>>());

      OperatingSystem convertedOs = converter.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.RHEL);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), null);
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }

   @Test
   public void testOEL() {
      String name = "Oracle EL";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();

      ImageToOperatingSystem converter = new ImageToOperatingSystem(
            new HashMap<OsFamily, Map<String, String>>());

      OperatingSystem convertedOs = converter.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.OEL);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), null);
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }
   

   ImageToOperatingSystem converterForUbuntu = new ImageToOperatingSystem(ImmutableMap.<OsFamily, Map<String, String>> of(
            OsFamily.UBUNTU, ImmutableMap.of("lucid", "10.04", "maverick", "10.10", "natty", "11.04", "oneiric",
                     "11.10")));
   @Test
   public void testTryStackOneric() {
      
      String name = "oneiric-server-cloudimg-amd64";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();
      
      OperatingSystem convertedOs = converterForUbuntu.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.UBUNTU);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), "11.10");
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }
   
   @Test
   public void testTryStackNatty() {
      
      String name = "natty-server-cloudimg-amd64";

      Image imageToConvert = Image.builder().id("id-" + name).name(name).build();
      
      OperatingSystem convertedOs = converterForUbuntu.apply(imageToConvert);

      assertEquals(convertedOs.getName(), imageToConvert.getName());
      assertEquals(convertedOs.getFamily(), OsFamily.UBUNTU);
      assertEquals(convertedOs.getDescription(), imageToConvert.getName());
      assertEquals(convertedOs.getVersion(), "11.04");
      assertEquals(convertedOs.getArch(), null);
      assertTrue(convertedOs.is64Bit());
   }
}

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

import static org.jclouds.softlayer.compute.functions.ProductItemToImage.imageId;
import static org.jclouds.softlayer.compute.functions.ProductItemToImage.osBits;
import static org.jclouds.softlayer.compute.functions.ProductItemToImage.osFamily;
import static org.jclouds.softlayer.compute.functions.ProductItemToImage.osVersion;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests {@code ProductItemToImage}
 *
 * @author Jason King
 */
@Test(groups = "unit", testName = "ProductItemToImageTest")
public class ProductItemToImageTest {
   // Operating Systems available MAR 2012
   private static final List<String> operatingSystems = Arrays.asList(
         "CentOS 5 - LAMP Install (32 bit)",
         "CentOS 5 - LAMP Install (64 bit)",
         "CentOS 5 - Minimal Install (32 bit)",
         "CentOS 5 - Minimal Install (64 bit)",
         "CentOS 6.0 - LAMP Install (32 bit)",
         "CentOS 6.0 - LAMP Install (64 bit)",
         "CentOS 6.0 - Minimal Install (32 bit)",
         "CentOS 6.0 - Minimal Install (64 bit)",
         "Debian GNU/Linux 5.0 Lenny/Stable - LAMP Install (32 bit)",
         "Debian GNU/Linux 5.0 Lenny/Stable - LAMP Install (64 bit)",
         "Debian GNU/Linux 5.0 Lenny/Stable - Minimal Install (32 bit)",
         "Debian GNU/Linux 5.0 Lenny/Stable - Minimal Install (64 bit)",
         "Fedora Release 13 (32bit) - LAMP Install",
         "Fedora Release 13 (32bit) - Minimal Install",
         "Fedora Release 13 (64bit) - LAMP Install",
         "Fedora Release 13 (64bit) - Minimal Install",
         "Fedora Release 15 (32bit) - LAMP Install",
         "Fedora Release 15 (32bit) - Minimal Install",
         "Fedora Release 15 (64bit) - LAMP Install",
         "Fedora Release 15 (64bit) - Minimal Install",
         "Red Hat Enterprise Linux 5 - LAMP Install (32 bit)",
         "Red Hat Enterprise Linux 5 - LAMP Install (64 bit)",
         "Red Hat Enterprise Linux 5 - Minimal Install (64 bit)",
         "Red Hat Enterprise Linux 6 - LAMP Install (32 bit)",
         "Red Hat Enterprise Linux 6 - LAMP Install (64 bit)",
         "Red Hat Enterprise Linux 6 - Minimal Install (32 bit)",
         "Red Hat Enterprise Linux 6 - Minimal Install (64 bit)",
         "Ubuntu Linux 10.04 LTS Lucid Lynx - LAMP Install (32 bit)",
         "Ubuntu Linux 10.04 LTS Lucid Lynx - LAMP Install (64 bit)",
         "Ubuntu Linux 10.04 LTS Lucid Lynx - Minimal Install (32 bit)",
         "Ubuntu Linux 10.04 LTS Lucid Lynx - Minimal Install (64 bit)",
         "Ubuntu Linux 8 LTS Hardy Heron - LAMP Install (32 bit)",
         "Ubuntu Linux 8 LTS Hardy Heron - LAMP Install (64 bit)",
         "Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (32 bit)",
         "Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (64 bit)",
         "CloudLinux 6 (32 bit)", 
         "CloudLinux 6 (64 bit)",          
         "Windows Server 2003 Datacenter SP2 with R2 (32 bit)",
         "Windows Server 2003 Datacenter SP2 with R2 (64 bit)",
         "Windows Server 2003 Enterprise SP2 with R2 (64 bit)",
         "Windows Server 2003 Standard SP2 with R2 (64 bit)",
         "Windows Server 2008 Datacenter Edition SP2 (32bit)",
         "Windows Server 2008 Datacenter Edition SP2 (64bit)",
         "Windows Server 2008 Enterprise Edition SP2 (32bit)",
         "Windows Server 2008 Enterprise Edition SP2 (64bit)",
         "Windows Server 2008 R2 Datacenter Edition (64bit)",
         "Windows Server 2008 R2 Enterprise Edition (64bit)",
         "Windows Server 2008 R2 Standard Edition (64bit)",
         "Windows Server 2008 Standard Edition SP2 (32bit)",
         "Windows Server 2008 Standard Edition SP2 (64bit)");

   @Test
   public void testConversion() {
      for( String description : operatingSystems )
      {
         ProductItem item = ProductItem.builder()
                                       .description(description)
                                       .prices(ProductItemPrice.builder().id(1234).build())
                                       .build();
         Image i = new ProductItemToImage().apply(item);
         OperatingSystem os = i.getOperatingSystem();
         assertNotNull(os);
         assertNotNull(os.getFamily());
         assertFalse(os.getFamily().equals(OsFamily.UNRECOGNIZED));
         assertNotNull(os.getVersion());
      }
   }

   @Test
   public void testUbuntu() {
         ProductItem item = ProductItem.builder()
                                       .description("Ubuntu Linux 10.04 LTS Lucid Lynx - Minimal Install (64 bit)")
                                       .prices(ProductItemPrice.builder().id(1234).build())
                                       .build();
         Image i = new ProductItemToImage().apply(item);
         OperatingSystem os = i.getOperatingSystem();
         assertNotNull(os);
         assertEquals(OsFamily.UBUNTU, os.getFamily());
         assertEquals("10.04",os.getVersion());
         assertTrue(os.is64Bit());
   }

   @Test
   public void testUbuntuNoBitCount() {
      ProductItem item = ProductItem.builder()
            .description("Ubuntu Linux 10.04 LTS Lucid Lynx - Minimal Install")
            .prices(ProductItemPrice.builder().id(1234).build())
            .build();
      Image i = new ProductItemToImage().apply(item);
      OperatingSystem os = i.getOperatingSystem();
      assertNotNull(os);
      assertEquals(OsFamily.UBUNTU, os.getFamily());
      assertEquals("10.04",os.getVersion());
      assertFalse(os.is64Bit());
   }


   @Test
   public void testCompletelyUnknown() {
      ProductItem item = ProductItem.builder()
            .description("This fails to match anything!!!")
            .prices(ProductItemPrice.builder().id(1234).build())
            .build();
      Image i = new ProductItemToImage().apply(item);
      OperatingSystem os = i.getOperatingSystem();
      assertNotNull(os);
      assertEquals(OsFamily.UNRECOGNIZED, os.getFamily());
      assertNull(os.getVersion());
      assertFalse(os.is64Bit());
   }
   
   @Test
   public void test64BitUnknown() {
      ProductItem item = ProductItem.builder()
            .description("This only has the bit-count (64 bit)")
            .prices(ProductItemPrice.builder().id(1234).build())
            .build();
      Image i = new ProductItemToImage().apply(item);
      OperatingSystem os = i.getOperatingSystem();
      assertNotNull(os);
      assertEquals(OsFamily.UNRECOGNIZED, os.getFamily());
      assertNull(os.getVersion());
      assertTrue(os.is64Bit());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNull() {
      new ProductItemToImage().apply(null);
   }
   
   @Test(expectedExceptions = NullPointerException.class)
   public void testNoDescription() {
      ProductItem item = ProductItem.builder()
            .prices(ProductItemPrice.builder().id(1234).build())
            .build();
      new ProductItemToImage().apply(item);
   }
   
   @Test
   public void testId() {
      ProductItemPrice price = ProductItemPrice.builder().id(1234).build();
      ProductItem item = ProductItem.builder().prices(price).build();
      assertEquals("1234",imageId().apply(item));
   }

   @Test
   public void testIdManyPrices() {
      ProductItemPrice price1 = ProductItemPrice.builder().id(1234).build();
      ProductItemPrice price2 = ProductItemPrice.builder().id(5678).build();
      ProductItem item = ProductItem.builder().prices(ImmutableSet.of(price1,price2)).build();
      assertEquals("1234",imageId().apply(item));
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testIdMissingPrices() {
      ProductItem item = ProductItem.builder().build();
      imageId().apply(item);
   }
   
   @Test(expectedExceptions = NullPointerException.class)
   public void testIdNull() {
      imageId().apply(null);
   }

   @Test
   public void testOsFamily() {
      assertEquals(OsFamily.UBUNTU,osFamily().apply("Ubuntu Linux os"));
   }

   @Test
   public void testOsFamilyUnrecognized() {
      assertEquals(OsFamily.UNRECOGNIZED,osFamily().apply("not a known operating system"));
   }
   
   @Test
   public void testOsFamilyNull() {
      assertEquals(OsFamily.UNRECOGNIZED,osFamily().apply(null));
   }

   @Test
   public void testOsBitsWithSpace() {
      assertEquals(osBits().apply("a (32 bit) os"),Integer.valueOf(32));
   }

   @Test
   public void testOsBitsNoSpace() {
      assertEquals(osBits().apply("a (64bit) os"),Integer.valueOf(64));
   }

   @Test
   public void testOsBitsMissing() {
      assertNull(osBits().apply("an os"));
   }

   @Test
   public void testOsBitsNull() {
      assertNull(osBits().apply(null));
   }

   @Test
   public void testOsVersion() {
      assertEquals("2099",osVersion().apply("Windows Server 2099 (256 bit)"));
   }

   @Test
   public void testOsVersionMissing() {
      assertNull(osVersion().apply("asd Server"));
   }

   @Test
   public void testOsVersionNull() {
      assertNull(osVersion().apply(null));
   }
}

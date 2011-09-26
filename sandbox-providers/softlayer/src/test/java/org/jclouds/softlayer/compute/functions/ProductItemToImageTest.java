package org.jclouds.softlayer.compute.functions;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.softlayer.domain.ProductItem;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.jclouds.softlayer.compute.functions.ProductItemToImage.*;
import static org.testng.AssertJUnit.*;

/**
 * Tests {@code ProductItemToImage}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class ProductItemToImageTest {
   // Operating Systems available SEPT 2011
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
         ProductItem item = ProductItem.builder().description(description).build();
         Image i = new ProductItemToImage().apply(item);
         OperatingSystem os = i.getOperatingSystem();
         assertNotNull(os);
         assertNotNull(os.getFamily());
         assertFalse(os.getFamily().equals(OsFamily.UNRECOGNIZED));
         assertNotNull(os.getVersion());
      }
   }

   @Test
   public void testOsFamily() {
      ProductItem item = ProductItem.builder().description("Ubuntu Linux os").build();
      assertEquals(OsFamily.UBUNTU,osFamily().apply(item));
   }

   @Test
   public void testOsFamilyUnrecognized() {
      ProductItem item = ProductItem.builder().description("not a known operating system").build();
      assertEquals(OsFamily.UNRECOGNIZED,osFamily().apply(item));
   }

   @Test
   public void testBitsWithSpace() {
      ProductItem item = ProductItem.builder().description("a (32 bit) os").build();
      assertEquals(osBits().apply(item),new Integer(32));
   }

   @Test
   public void testBitsNoSpace() {
      ProductItem item = ProductItem.builder().description("a (64bit) os").build();
      assertEquals(osBits().apply(item),new Integer(64));
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testBitsMissing() {
      ProductItem item = ProductItem.builder().description("an os").build();
      osBits().apply(item);
   }

   @Test
   public void testOsVersion() {
      ProductItem item = ProductItem.builder().description("Windows Server 2099 (256 bit)").build();
      assertEquals("2099",osVersion().apply(item));
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testOsVersionMissing() {
      ProductItem item = ProductItem.builder().description("asd Server ").build();
      osVersion().apply(item);
   }
}

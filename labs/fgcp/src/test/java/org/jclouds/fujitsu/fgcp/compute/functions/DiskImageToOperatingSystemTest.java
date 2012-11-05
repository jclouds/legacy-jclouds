package org.jclouds.fujitsu.fgcp.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.DiskImage.Builder;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "DiskImageToOperatingSystemTest")
public class DiskImageToOperatingSystemTest {
   // Operating Systems available JAN 2012 (taken from osName)
   private static final List<String> operatingSystems = Arrays.asList(
         // JP
         "CentOS 5.6 32bit (English)",
         "CentOS 5.6 64bit (English)",
         "Red Hat Enterprise Linux 5.5 32bit (Japanese)",
         "Red Hat Enterprise Linux 5.5 64bit (Japanese)",
         "Windows Server 2003 R2 EE 32bit SP2 (日本語版) サポート付",
         "Windows Server 2003 R2 EE 32bit SP2 (日本語版)",
         "Windows Server 2008 R2 EE 64bit (日本語版) サポート付",
         "Windows Server 2008 R2 EE 64bit (日本語版)",
         "Windows Server 2008 R2 SE 64bit (日本語版)  サポート付",
         "Windows Server 2008 R2 SE 64bit (日本語版)",
         "Windows Server 2008 SE 32bit SP2 (日本語版) サポート付",
         "Windows Server 2008 SE 32bit SP2 (日本語版)",
         // AU
         "CentOS 5.4 64bit (English)", "CentOS 5.4 32bit (English)",
         "Windows Server 2008 R2 SE 64bit (English)",
         "Windows Server 2008 R2 EE 64bit (English)");

   @Test
   public void testConversion() {
      for (String description : operatingSystems) {
         Builder builder = DiskImage.builder();
         builder.osName(description);
         builder.osType("hvm");
         builder.creatorName("creator");
         builder.registrant("registrant");
         builder.description("description");
         builder.id("ABCDEFGH");
         DiskImage image = builder.build();

         OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

         assertNotNull(os, description);
         assertNotNull(os.getFamily(), description);
         assertNotEquals(OsFamily.UNRECOGNIZED, os.getFamily(),
               "OsFamily not recognised: " + description);
         assertNotNull(os.getVersion(), "Version not recognised: "
               + description);
         assertEquals(os.getName(), description);
         assertEquals(os.getDescription(), description);
         assertNotNull(os.getArch(), description);
      }
   }

   @Test
   public void testOsFamilyUnrecognized() {
      DiskImage image = DiskImage.builder()
            .osName("not a known operating system").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertEquals(os.getFamily(), OsFamily.UNRECOGNIZED);
   }

   @Test
   public void test64BitsWithSpace() {
      DiskImage image = DiskImage.builder().osName("a (64 bit) os").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertTrue(os.is64Bit());
   }

   @Test
   public void test64BitsNoSpace() {
      DiskImage image = DiskImage.builder().osName("a (64bit) os").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertTrue(os.is64Bit());
   }

   @Test
   public void test32BitsNoSpace() {
      DiskImage image = DiskImage.builder().osName("a (32bit) os").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertFalse(os.is64Bit());
   }

   @Test
   public void testx64NoSpace() {
      DiskImage image = DiskImage.builder().osName("a (x64) os").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertTrue(os.is64Bit());
   }

   @Test
   public void testWindowsVersion() {
      DiskImage image = DiskImage.builder()
            .osName("Windows Server 2008 R2 SE 64 bit").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertEquals(os.getVersion(), "2008 R2 SE");
   }

   @Test
   public void testCentOSVersion() {
      DiskImage image = DiskImage.builder()
            .osName("CentOS 6.2 64bit (English)").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertEquals(os.getVersion(), "6.2");
   }

   @Test
   public void testUnrecognizedOsVersion() {
      DiskImage image = DiskImage.builder()
            .osName("Windows Server 2099 (256 bit)").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);

      assertNotNull(os);
      assertNull(os.getVersion());
   }

   @Test
   public void testOsVersionMissing() {
      DiskImage image = DiskImage.builder().osName("asd Server").build();

      OperatingSystem os = new DiskImageToOperatingSystem().apply(image);
      assertNotNull(os);
      assertNull(os.getVersion(), "os.getVersion(): \'" + os.getVersion()
            + "\'");
   }
}

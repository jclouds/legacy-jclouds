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
package org.jclouds.compute.domain;

import static org.jclouds.compute.domain.TemplateBuilderSpec.parse;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginCredentials;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginUser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.lang.reflect.Field;

import javax.inject.Provider;

import org.jclouds.ContextBuilder;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.gson.Gson;

/**
 * 
 * <p/>
 * inspired by guava {@code CacheBuilderSpecTest}
 * 
 * @author Adrian Cole
 */
@Test(testName = "TemplateBuilderSpecTest")
public class TemplateBuilderSpecTest {
   Provider<TemplateBuilder> templateBuilders = ContextBuilder.newBuilder("stub").buildInjector()
         .getProvider(TemplateBuilder.class);

   public void testParse_empty() {
      TemplateBuilderSpec spec = parse("");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get(), templateBuilders.get().from(spec));
   }

   public void testParse_hardwareId() {
      TemplateBuilderSpec spec = parse("hardwareId=m1.small");
      assertEquals(spec.hardwareId, "m1.small");
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().hardwareId("m1.small"),
            templateBuilders.get().from(spec));
   }

   public void testParse_hardwareIdRepeated() {
      try {
         parse("hardwareId=m1.small, hardwareId=t1.micro");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_hardwareIdNotCompatibleWithHardwareValues() {
      try {
         parse("hardwareId=m1.small,minCores=1");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("hardwareId=m1.small,minRam=512");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("hardwareId=m1.small,hypervisorMatches=OpenVZ");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }
   
   public void testParse_minCores() {
      TemplateBuilderSpec spec = parse("minCores=32");
      assertNull(spec.hardwareId);
      assertEquals(32, spec.minCores.intValue());
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().minCores(32), templateBuilders.get().from(spec));
   }

   public void testParse_minCoresRepeated() {
      try {
         parse("minCores=10, minCores=20");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }
   
   public void testParse_minRam() {
      TemplateBuilderSpec spec = parse("minRam=10");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertEquals(spec.minRam.intValue(), 10);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().minRam(10), templateBuilders.get().from(spec));
   }

   public void testParse_minDisk() {
      TemplateBuilderSpec spec = parse("minDisk=10");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertEquals(spec.minDisk.doubleValue(), 10.0);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().minRam(10), templateBuilders.get().from(spec));
   }

   public void testParse_minDiskIsNotEqual() {
      TemplateBuilderSpec spec1 = parse("minDisk=10");
      TemplateBuilderSpec spec2 = parse("minDisk=20");
      assertTemplateBuilderEquivalence(templateBuilders.get().minDisk(10), templateBuilders.get().from(spec1));
      assertTemplateBuilderEquivalence(templateBuilders.get().minDisk(20), templateBuilders.get().from(spec2));
      assertNotEquals(spec1.minDisk, spec2.minDisk);
   }

   public void testParse_minRamRepeated() {
      try {
         parse("minRam=10, minRam=20");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_hypervisorMatches() {
      TemplateBuilderSpec spec = parse("hypervisorMatches=OpenVZ");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertEquals(spec.hypervisorMatches, "OpenVZ");
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().hypervisorMatches("OpenVZ"),
            templateBuilders.get().from(spec));
   }

   public void testParse_hypervisorMatchesRepeated() {
      try {
         parse("hypervisorMatches=VSphere, hypervisorMatches=OpenVZ");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }
   
   public void testParse_imageId() {
      TemplateBuilderSpec spec = parse("imageId=us-east-1/ami-fffffff");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertEquals(spec.imageId, "us-east-1/ami-fffffff");
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().imageId("us-east-1/ami-fffffff"),
            templateBuilders.get().from(spec));
   }
   
   public void testParse_imageIdRepeated() {
      try {
         parse("imageId=us-east-1/ami-fffffff, imageId=ami-eeeeeee");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_imageIdNotCompatibleWithImageValues() {
      try {
         parse("imageId=us-east-1/ami-fffffff,imageNameMatches=foo");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("imageId=us-east-1/ami-fffffff,osFamily=UBUNTU");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("imageId=us-east-1/ami-fffffff,osVersionMatches=10.04");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("imageId=us-east-1/ami-fffffff,os64Bit=true");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("imageId=us-east-1/ami-fffffff,osArchMatches=x86");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("imageId=us-east-1/ami-fffffff,osDescriptionMatches=^((?!MGC).)*$");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }


   public void testParse_imageNameMatches() {
      TemplateBuilderSpec spec = parse("imageNameMatches=.*w/ None.*");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertEquals(spec.imageNameMatches, ".*w/ None.*");
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().imageNameMatches(".*w/ None.*"),
            templateBuilders.get().from(spec));
   }

   public void testParse_imageNameMatchesRepeated() {
      try {
         parse("imageNameMatches=hello, imageNameMatches=.*w/ None.*");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_osFamily() {
      TemplateBuilderSpec spec = parse("osFamily=UBUNTU");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertEquals(spec.osFamily, OsFamily.UBUNTU);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().osFamily(OsFamily.UBUNTU),
            templateBuilders.get().from(spec));
   }

   public void testParse_osFamilyRepeated() {
      try {
         parse("osFamily=UBUNTU, osFamily=LINUX");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_osVersionMatches() {
      TemplateBuilderSpec spec = parse("osVersionMatches=.*[Aa]utomated SSH Access.*");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertEquals(spec.osVersionMatches, ".*[Aa]utomated SSH Access.*");
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().osVersionMatches(".*[Aa]utomated SSH Access.*"),
            templateBuilders.get().from(spec));
   }

   public void testParse_osVersionMatchesRepeated() {
      try {
         parse("osVersionMatches=11.04, osVersionMatches=.*[Aa]utomated SSH Access.*");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_os64Bit() {
      TemplateBuilderSpec spec = parse("os64Bit=true");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osVersionMatches);
      assertEquals(spec.os64Bit.booleanValue(), true);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().os64Bit(true),
            templateBuilders.get().from(spec));
   }

   public void testParse_os64BitRepeated() {
      try {
         parse("os64Bit=false, os64Bit=true");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_osArchMatches() {
      TemplateBuilderSpec spec = parse("osArchMatches=x86");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertEquals(spec.osArchMatches, "x86");
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().osArchMatches("x86"),
            templateBuilders.get().from(spec));
   }

   public void testParse_osArchMatchesRepeated() {
      try {
         parse("osArchMatches=x86, osArchMatches=foo");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }
   
   public void testParse_osDescriptionMatches() {
      TemplateBuilderSpec spec = parse("osDescriptionMatches=^((?!MGC).)*$");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertEquals(spec.osDescriptionMatches, "^((?!MGC).)*$");
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(templateBuilders.get().osDescriptionMatches("^((?!MGC).)*$"),
            templateBuilders.get().from(spec));
   }

   public void testParse_osDescriptionMatchesRepeated() {
      try {
         parse("osDescriptionMatches=^((?!MGC).)*$, osDescriptionMatches=.*[Aa]utomated SSH Access.*");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_loginUser() {
      TemplateBuilderSpec spec = parse("loginUser=ubuntu");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertEquals(spec.loginUser, "ubuntu");
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(
            templateBuilders.get().options(overrideLoginUser("ubuntu")), templateBuilders
                  .get().from(spec));
   }

   public void testParse_loginUserRepeated() {
      try {
         parse("loginUser=aws-user,loginUser=ubuntu");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_loginUserWithPassword() {
      TemplateBuilderSpec spec = parse("loginUser=root:toor");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertEquals(spec.loginUser, "root:toor");
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(
            templateBuilders.get().options(
                  overrideLoginCredentials(LoginCredentials.builder().user("root").password("toor").build())),
            templateBuilders.get().from(spec));
   }
   
   public void testParse_authenticateSudoWithoutLoginUser() {
      try {
         parse("authenticateSudo=true");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_authenticateSudo() {
      TemplateBuilderSpec spec = parse("loginUser=root:toor,authenticateSudo=true");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertEquals(spec.loginUser, "root:toor");
      assertEquals(spec.authenticateSudo.booleanValue(), true);
      assertNull(spec.locationId);
      assertTemplateBuilderEquivalence(
            templateBuilders.get().options(
                  overrideLoginCredentials(LoginCredentials.builder().user("root").password("toor")
                        .authenticateSudo(true).build())), templateBuilders.get().from(spec));
   }

   public void testParse_authenticateSudoRepeated() {
      try {
         parse("loginUser=root:toor,authenticateSudo=true,authenticateSudo=false");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParse_locationId() {
      TemplateBuilderSpec spec = parse("locationId=stub");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertNull(spec.osFamily);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertEquals(spec.locationId, "stub");
      assertTemplateBuilderEquivalence(templateBuilders.get().locationId("stub"),
            templateBuilders.get().from(spec));
   }
   
   public void testParse_locationIdRepeated() {
      try {
         parse("locationId=stub, locationId=stub");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }
   
   public void testParse_multipleKeys() {
      TemplateBuilderSpec spec = parse("osFamily=UBUNTU,osVersionMatches=1[012].[01][04],imageNameMatches=.*w/ None.*");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertNull(spec.minRam);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertEquals(spec.imageNameMatches, ".*w/ None.*");
      assertEquals(spec.osFamily, OsFamily.UBUNTU);
      assertEquals(spec.osVersionMatches, "1[012].[01][04]");
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      TemplateBuilder expected = templateBuilders.get().osVersionMatches("1[012].[01][04]").imageNameMatches(".*w/ None.*").osFamily(OsFamily.UBUNTU);
      assertTemplateBuilderEquivalence(expected, templateBuilders.get().from(spec));
   }

   public void testParse_whitespaceAllowed() {
      TemplateBuilderSpec spec = parse(" minRam=10,\nosFamily=UBUNTU");
      assertNull(spec.hardwareId);
      assertNull(spec.minCores);
      assertEquals(spec.minRam.intValue(), 10);
      assertNull(spec.minDisk);
      assertNull(spec.hypervisorMatches);
      assertNull(spec.imageId);
      assertNull(spec.imageNameMatches);
      assertEquals(spec.osFamily, OsFamily.UBUNTU);
      assertNull(spec.osVersionMatches);
      assertNull(spec.os64Bit);
      assertNull(spec.osArchMatches);
      assertNull(spec.osDescriptionMatches);
      assertNull(spec.loginUser);
      assertNull(spec.authenticateSudo);
      assertNull(spec.locationId);
      TemplateBuilder expected = templateBuilders.get().minRam(10).osFamily(OsFamily.UBUNTU);
      assertTemplateBuilderEquivalence(expected, templateBuilders.get().from(spec));
   }

   public void testNiceJson() {
      TemplateBuilderSpec spec = parse("osFamily=UBUNTU,osVersionMatches=1[012].[01][04],imageNameMatches=.*w/ None.*");
      assertEquals(new Gson().toJson(spec), "{\"imageNameMatches\":\".*w/ None.*\",\"osFamily\":\"UBUNTU\",\"osVersionMatches\":\"1[012].[01][04]\"}");
      assertEquals(new Gson().fromJson(new Gson().toJson(spec), TemplateBuilderSpec.class), spec);
   }
   
   public void testParse_unknownKey() {
      try {
         parse("foo=17");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testTemplateBuilderFrom_string() {
      TemplateBuilder fromString = templateBuilders.get().from("minRam=10,osFamily=UBUNTU");
      TemplateBuilder expected = templateBuilders.get().minCores(30).minRam(10).osFamily(OsFamily.UBUNTU);
      assertTemplateBuilderEquivalence(expected, fromString);
   }

   private void assertTemplateBuilderEquivalence(TemplateBuilder a, TemplateBuilder b) {
      // Labs hack: dig into the TemplateBuilder instances, verifying all fields
      // are equal.
      for (Field f : TemplateBuilder.class.getFields()) {
         f.setAccessible(true);
         try {
            assertEquals(f.get(a), f.get(b), "Field " + f.getName() + " not equal");
         } catch (IllegalArgumentException e) {
            throw new AssertionError(e.getMessage());
         } catch (IllegalAccessException e) {
            throw new AssertionError(e.getMessage());
         }
      }
   }
}

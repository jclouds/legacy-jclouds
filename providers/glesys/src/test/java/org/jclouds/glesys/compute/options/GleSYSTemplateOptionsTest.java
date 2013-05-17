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
package org.jclouds.glesys.compute.options;

import static org.jclouds.glesys.compute.options.GleSYSTemplateOptions.Builder.ip;
import static org.jclouds.glesys.compute.options.GleSYSTemplateOptions.Builder.rootPassword;
import static org.jclouds.glesys.compute.options.GleSYSTemplateOptions.Builder.transferGB;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of {@code GleSYSTemplateOptions} and {@code
 * GleSYSTemplateOptions.Builder.*}.
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GleSYSTemplateOptionsTest")
public class GleSYSTemplateOptionsTest {
   @Test
   public void testAs() {
      TemplateOptions options = new GleSYSTemplateOptions();
      assertEquals(options.as(GleSYSTemplateOptions.class), options);
   }

   @Test
   public void testDefaultip() {
      TemplateOptions options = new GleSYSTemplateOptions();
      assertEquals(options.as(GleSYSTemplateOptions.class).getIp(), "any");
   }

   @Test
   public void testip() {
      TemplateOptions options = new GleSYSTemplateOptions().ip("1.1.1.1");
      assertEquals(options.as(GleSYSTemplateOptions.class).getIp(), "1.1.1.1");
   }

   @Test
   public void testipStatic() {
      TemplateOptions options = ip("1.1.1.1");
      assertEquals(options.as(GleSYSTemplateOptions.class).getIp(), "1.1.1.1");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIpThrowsNPE() {
      new GleSYSTemplateOptions().ip(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidIpThrowsIllegalArgument() {
      new GleSYSTemplateOptions().ip("1.1.1");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testipIsInvalidThrowsIllegalArgument() {
      new GleSYSTemplateOptions().ip("foo");
   }

   @Test
   public void testDefaultRootPassword() {
      TemplateOptions options = new GleSYSTemplateOptions();
      assertEquals(options.as(GleSYSTemplateOptions.class).getRootPassword(), null);
   }

   @Test
   public void testRootPassword() {
      TemplateOptions options = new GleSYSTemplateOptions().rootPassword("secret");
      assertEquals(options.as(GleSYSTemplateOptions.class).getRootPassword(), "secret");
   }   

   @Test
   public void testRootPasswordStatic() {
      TemplateOptions options = rootPassword("secret");
      assertEquals(options.as(GleSYSTemplateOptions.class).getRootPassword(), "secret");
   }   

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullRootPasswordThrowsNPE() {
      new GleSYSTemplateOptions().rootPassword(null);
   }   

   @Test
   public void testDefaultTranferGB() {
      TemplateOptions options = new GleSYSTemplateOptions();
      assertEquals(options.as(GleSYSTemplateOptions.class).getTransferGB(), 50);
   }

   @Test
   public void testTransferGB() {
      TemplateOptions options = new GleSYSTemplateOptions().transferGB(75);
      assertEquals(options.as(GleSYSTemplateOptions.class).getTransferGB(), 75);
   }   

   @Test
   public void testTransferGBStatic() {
      TemplateOptions options = transferGB(75);
      assertEquals(options.as(GleSYSTemplateOptions.class).getTransferGB(), 75);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNegativeTransferGBThrowsException() {
      new GleSYSTemplateOptions().transferGB(-1);
   }
   
   @Test
   public void testClone() {
      GleSYSTemplateOptions clone = transferGB(75).rootPassword("root").ip("1.1.1.1").clone();
      assertEquals(clone.getTransferGB(), 75);
      assertEquals(clone.getRootPassword(), "root");
      assertEquals(clone.getIp(), "1.1.1.1");
   }
}

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
package org.jclouds.glesys.compute.options;

import static org.jclouds.glesys.compute.options.GleSYSTemplateOptions.Builder.ip;
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

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testipIsInvalidThrowsIllegalArgument() {
      new GleSYSTemplateOptions().ip("foo");
   }
}

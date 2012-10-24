/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link AbiquoTemplateOptions} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AbiquoTemplateOptionsTest")
public class AbiquoTemplateOptionsTest {
   public void testAs() {
      TemplateOptions options = new AbiquoTemplateOptions();
      assertEquals(options.as(AbiquoTemplateOptions.class), options);
   }

   public void testOverrideCores() {
      TemplateOptions options = new AbiquoTemplateOptions().overrideCores(5);
      assertEquals(options.as(AbiquoTemplateOptions.class).getOverrideCores(), Integer.valueOf(5));
   }

   public void testOverrideRam() {
      TemplateOptions options = new AbiquoTemplateOptions().overrideRam(2048);
      assertEquals(options.as(AbiquoTemplateOptions.class).getOverrideRam(), Integer.valueOf(2048));
   }

   public void testVncPassword() {
      TemplateOptions options = new AbiquoTemplateOptions().vncPassword("foo");
      assertEquals(options.as(AbiquoTemplateOptions.class).getVncPassword(), "foo");
   }

}

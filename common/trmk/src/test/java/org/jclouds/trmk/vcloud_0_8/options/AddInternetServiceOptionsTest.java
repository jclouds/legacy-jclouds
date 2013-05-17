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
package org.jclouds.trmk.vcloud_0_8.options;

import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.disabled;
import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CreateInternetServiceOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AddInternetServiceOptionsTest {

   Injector injector = Guice.createInjector(new SaxParserModule());

   @Test
   public void testWithDescription() {
      AddInternetServiceOptions options = new AddInternetServiceOptions();
      options.withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testWithDescriptionStatic() {
      AddInternetServiceOptions options = withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testDisabled() {
      AddInternetServiceOptions options = new AddInternetServiceOptions();
      options.disabled();
      assertEquals(options.enabled, "false");
   }

   @Test
   public void testDisabledStatic() {
      AddInternetServiceOptions options = disabled();
      assertEquals(options.enabled, "false");
   }

}

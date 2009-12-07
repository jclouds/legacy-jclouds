/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.options;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.cpuCount;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.inNetwork;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.megabytes;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code InstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.InstantiateVAppTemplateOptionsTest")
public class InstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testInNetwork() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testInNetworkStatic() {
      InstantiateVAppTemplateOptions options = inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testCpuCount() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      InstantiateVAppTemplateOptions options = cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testMegabytes() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test
   public void testMegabytesStatic() {
      InstantiateVAppTemplateOptions options = megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMegabytesStaticWrong() {
      megabytes(511);
   }
}

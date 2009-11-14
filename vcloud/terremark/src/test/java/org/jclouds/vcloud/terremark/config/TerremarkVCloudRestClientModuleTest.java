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
package org.jclouds.vcloud.terremark.config;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.config.VCloudDiscoveryRestClientModule;
import org.jclouds.vcloud.config.VCloudRestClientModuleTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVCloudRestClientModuleTest")
public class TerremarkVCloudRestClientModuleTest extends VCloudRestClientModuleTest {

   @Override
   protected Injector createInjector() {
      return Guice.createInjector(new TerremarkVCloudRestClientModule(),
               new VCloudDiscoveryRestClientModule(), new ParserModule(), new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_USER)).to("user");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_KEY)).to("secret");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_SESSIONINTERVAL))
                              .to("2");
                  }
               });
   }

   @Test
   void postStrings() throws IOException {
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Jsr330.named("InstantiateVAppTemplateParams"))), Utils
               .toStringAndClose(getClass().getResourceAsStream(
                        "/terremark/InstantiateVAppTemplateParams.xml")));
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Jsr330.named("CreateInternetService"))), Utils
               .toStringAndClose(getClass().getResourceAsStream(
                        "/terremark/CreateInternetService.xml")));
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Jsr330.named("CreateNodeService"))),
               Utils.toStringAndClose(getClass().getResourceAsStream(
                        "/terremark/CreateNodeService.xml")));
   }

}
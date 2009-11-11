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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rest.domain.NamedLink;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code VDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VDCHandlerTest")
public class VDCHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/vdc.xml");

      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vdc+xml");
      assertEquals(result.getResourceEntities(), ImmutableMap.<String, NamedLink> of());
      assertEquals(
               result.getAvailableNetworks(),
               ImmutableMap
                        .of(
                                 "10.114.34.128/26",
                                 new NamedLinkImpl(
                                          "10.114.34.128/26",
                                          "application/vnd.vmware.vcloud.network+xml",
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
   }
}

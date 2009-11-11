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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rest.domain.internal.LinkImpl;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TerremarkVAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVAppHandlerTest")
public class TerremarkVAppHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/terremark/launched_vapp.xml");

      VApp result = (VApp) factory.create(injector.getInstance(TerremarkVAppHandler.class)).parse(
               is);
      assertEquals(result.getName(), "adriantest");
      assertEquals(result.getStatus(), 0);

      assertEquals(result.getSize(), 4);

      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13775"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vApp+xml");
      assertEquals(result.getVDC(), new LinkImpl("application/vnd.vmware.vcloud.vdc+xml", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

   }
}

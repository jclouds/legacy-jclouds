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
package org.jclouds.vcloudx.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloudx.domain.OrgLinks;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code OrgLinksHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloudx.OrgLinksHandlerTest")
public class OrgLinksHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/org.xml");

      OrgLinks result = (OrgLinks) factory.create(injector.getInstance(OrgLinksHandler.class))
               .parse(is);
      assertEquals(result.getName(), "adrian@jclouds.org");
      assertEquals(result.getOrg(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"));
      assertEquals(result.getCatalog(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog"));
      assertEquals(result.getVDCs(), Sets.newHashSet(URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));
      assertEquals(result.getTaskLists(), Sets.newHashSet(URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/tasksList/32")));
   }
}

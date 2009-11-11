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

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Organization;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code OrgHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.OrgHandlerTest")
public class OrgHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/org.xml");

      Organization result = (Organization) factory.create(injector.getInstance(OrgHandler.class))
               .parse(is);
      assertEquals(result.getName(), "adrian@jclouds.org");
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"));
      assertEquals(result.getType(), ORG_XML);
      assertEquals(result.getCatalog(), new NamedLinkImpl("Miami Environment 1 Catalog", CATALOG_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog")));
      assertEquals(result.getVDCs(), ImmutableMap.of("Miami Environment 1", new NamedLinkImpl(
               "Miami Environment 1", VCloudMediaType.VDC_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"))));
      assertEquals(
               result.getTasksLists(),
               ImmutableMap
                        .of(
                                 "Miami Environment 1 Tasks List",
                                 new NamedLinkImpl(
                                          "Miami Environment 1 Tasks List",
                                          TASKSLIST_XML,
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/tasksList/32"))));
   }
}

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
package org.jclouds.vcloudx;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloudx.domain.OrgLinks;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudXClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloudx.VCloudXClientLiveTest")
public class VCloudXClientLiveTest {

   private VCloudXClient connection;
   private String account;

   @Test
   public void testOrganization() throws Exception {
      OrgLinks response = connection.getOrganization();
      assertNotNull(response);
      assertEquals(response.getName(), account);
      assertNotNull(response.getOrg());
      assertNotNull(response.getCatalog());
      assertEquals(response.getTaskLists().size(), 1);
      assertEquals(response.getVDCs().size(), 1);
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint");
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = new BaseVCloudXContextBuilder(new VCloudXPropertiesBuilder(URI.create(endpoint),
               account, key).build()).withModules(new Log4JLoggingModule()).buildContext().getApi();
   }

}

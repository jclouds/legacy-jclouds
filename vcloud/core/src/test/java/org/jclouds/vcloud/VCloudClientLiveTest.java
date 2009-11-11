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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudClientLiveTest")
public class VCloudClientLiveTest {

   protected VCloudClient connection;
   protected String account;

   @Test
   public void testOrganization() throws Exception {
      Catalog response = connection.getCatalog().get(10, TimeUnit.SECONDS);
      assertNotNull(response);
      assertNotNull(response.getName());
      assertNotNull(response.getLocation());
      assertEquals(response.getType(), "application/vnd.vmware.vcloud.catalog+xml");
      assert response.size() > 0;
   }

   @Test
   public void testDefaultVDC() throws Exception {
      VDC response = connection.getDefaultVDC().get(10, TimeUnit.SECONDS);
      assertNotNull(response);
      assertNotNull(response.getName());
      assertNotNull(response.getLocation());
      assertEquals(response.getType(), "application/vnd.vmware.vcloud.vdc+xml");
      assertNotNull(response.getResourceEntities());
      assertNotNull(response.getAvailableNetworks());
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.getDefaultTasksList().get(10,
               TimeUnit.SECONDS);
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
   }

   @Test
   public void testGetTask() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.getDefaultTasksList().get(10,
               TimeUnit.SECONDS);
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      for (Task t : response.getTasks()) {
         assertEquals(connection.getTask(t.getLocation()).get(10, TimeUnit.SECONDS).getLocation(),
                  t.getLocation());
      }
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint");
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = new VCloudContextBuilder(new VCloudPropertiesBuilder(URI.create(endpoint),
               account, key).build()).withModules(new Log4JLoggingModule()).buildContext().getApi();
   }

}

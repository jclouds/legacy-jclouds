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
package org.jclouds.vcloud.terremark;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class TerremarkVCloudClientLiveTest extends VCloudClientLiveTest {
   TerremarkVCloudClient tmClient;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testDefaultVDC() throws Exception {
      super.testDefaultVDC();
      TerremarkVDC response = (TerremarkVDC) tmClient.getDefaultVDC().get(10, TimeUnit.SECONDS);
      assertNotNull(response);
      assertNotNull(response.getCatalog());
      assertNotNull(response.getInternetServices());
      assertNotNull(response.getPublicIps());
   }

   @Test(enabled = false)
   // disabled until stop functionality is added
   public void testInstantiate() throws InterruptedException, ExecutionException, TimeoutException {
      URI template = tmClient.getCatalog().get(10, TimeUnit.SECONDS).get(
               "Ubuntu Server 9.04 (32-bit)").getLocation();
      
      URI network = tmClient.getDefaultVDC().get(10, TimeUnit.SECONDS).getAvailableNetworks()
               .values().iterator().next().getLocation();
      
      String response = tmClient.instantiateVAppTemplate("adriantest", template, 1, 512, network);
      
      
      System.out.println(response);
   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = tmClient = new TerremarkVCloudContextBuilder(
               new TerremarkVCloudPropertiesBuilder(account, key).build()).withModules(
               new Log4JLoggingModule()).buildContext().getApi();
   }

}

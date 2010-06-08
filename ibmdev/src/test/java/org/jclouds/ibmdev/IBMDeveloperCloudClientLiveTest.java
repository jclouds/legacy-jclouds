/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Set;

import org.jclouds.ibmdev.domain.Address;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.domain.Key;
import org.jclouds.ibmdev.domain.Location;
import org.jclouds.ibmdev.domain.Volume;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code IBMDeveloperCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ibmdevelopercloud.IBMDeveloperCloudClientLiveTest")
public class IBMDeveloperCloudClientLiveTest {

   private IBMDeveloperCloudClient connection;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = (IBMDeveloperCloudClient) IBMDeveloperCloudContextFactory.createContext(user,
               password, new Log4JLoggingModule()).getProviderSpecificContext().getApi();
   }

   @Test
   public void testListImages() throws Exception {
      Set<? extends Image> response = connection.listImages();
      assertNotNull(response);
   }

   @Test
   public void testGetImage() throws Exception {
      Set<? extends Image> response = connection.listImages();
      assertNotNull(response);
      if (response.size() > 0) {
         Image image = Iterables.get(response, 0);
         assertEquals(connection.getImage(image.getId()).getId(), image.getId());
      }
   }

   @Test
   public void testListInstances() throws Exception {
      Set<? extends Instance> response = connection.listInstances();
      assertNotNull(response);
   }

   @Test
   public void testListInstancesFromRequestReturnsNull() throws Exception {
      Set<? extends Instance> response = connection.listInstancesFromRequest(Long.MAX_VALUE);
      assertNull(response);
   }

   @Test
   public void testGetInstance() throws Exception {
      Set<? extends Instance> response = connection.listInstances();
      assertNotNull(response);
      if (response.size() > 0) {
         Instance instance = Iterables.get(response, 0);
         assertEquals(connection.getInstance(instance.getId()).getId(), instance.getId());
      }
   }

   @Test
   public void testListKeys() throws Exception {
      Set<? extends Key> response = connection.listKeys();
      assertNotNull(response);
   }

   @Test
   public void testGetKey() throws Exception {
      Set<? extends Key> response = connection.listKeys();
      assertNotNull(response);
      if (response.size() > 0) {
         Key key = Iterables.get(response, 0);
         assertEquals(connection.getKey(key.getName()).getName(), key.getName());
      }
   }

   @Test
   public void testListVolumes() throws Exception {
      Set<? extends Volume> response = connection.listVolumes();
      assertNotNull(response);
   }

   @Test
   public void testGetVolume() throws Exception {
      Set<? extends Volume> response = connection.listVolumes();
      assertNotNull(response);
      if (response.size() > 0) {
         Volume image = Iterables.get(response, 0);
         assertEquals(connection.getVolume(image.getId()).getId(), image.getId());
      }
   }

   @Test
   public void testListLocations() throws Exception {
      Set<? extends Location> response = connection.listLocations();
      assertNotNull(response);
   }

   @Test
   public void testGetLocation() throws Exception {
      Set<? extends Location> response = connection.listLocations();
      assertNotNull(response);
      if (response.size() > 0) {
         Location image = Iterables.get(response, 0);
         assertEquals(connection.getLocation(image.getId()).getId(), image.getId());
      }
   }

   @Test
   public void testListAddresss() throws Exception {
      Set<? extends Address> response = connection.listAddresses();
      assertNotNull(response);
   }

}

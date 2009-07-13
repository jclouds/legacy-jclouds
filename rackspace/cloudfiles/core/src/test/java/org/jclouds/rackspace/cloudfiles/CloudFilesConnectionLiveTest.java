/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_KEY;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesAuthenticationLiveTest")
public class CloudFilesConnectionLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_CLOUDFILES_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_CLOUDFILES_KEY);

   private String bucketPrefix = System.getProperty("user.name") + ".cfint";

   @Test
   public void testListOwnedContainers() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();
      List<ContainerMetadata> response = connection.listOwnedContainers();
      assertNotNull(response);
   }

   @Test
   public void testPutContainers() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();
      assertTrue(connection.putContainer(bucketPrefix + ".hello"));
      List<ContainerMetadata> response = connection.listOwnedContainers();
      assertNotNull(response);
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), bucketPrefix + ".hello");
   }

}

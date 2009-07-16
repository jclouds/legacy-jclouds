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

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesAuthenticationLiveTest")
public class CloudFilesConnectionLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_RACKSPACE_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_RACKSPACE_KEY);

   private String bucketPrefix = System.getProperty("user.name") + ".cfint";

   @Test
   public void testListOwnedContainers() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();
      List<ContainerMetadata> response = connection.listOwnedContainers();
      assertNotNull(response);
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      String[] containerNames = new String[] { bucketPrefix + ".testListOwnedContainers1",
               bucketPrefix + ".testListOwnedContainers2" };
      assertTrue(connection.putContainer(containerNames[0]));
      assertTrue(connection.putContainer(containerNames[1]));
      response = connection.listOwnedContainers();
      assertEquals(response.size(), initialContainerCount + 2);

      assertTrue(connection.deleteContainerIfEmpty(containerNames[0]));
      assertTrue(connection.deleteContainerIfEmpty(containerNames[1]));
      response = connection.listOwnedContainers();
      assertEquals(response.size(), initialContainerCount);
   }

   @Test
   public void testHeadAccountMetadata() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();
      AccountMetadata metadata = connection.getAccountMetadata();
      assertNotNull(metadata);
      long initialContainerCount = metadata.getContainerCount();

      String containerName = bucketPrefix + ".testHeadAccountMetadata";
      assertTrue(connection.putContainer(containerName));

      metadata = connection.getAccountMetadata();
      assertNotNull(metadata);
      assertEquals(metadata.getContainerCount(), initialContainerCount + 1);

      assertTrue(connection.deleteContainerIfEmpty(containerName));
   }

   @Test
   public void testDeleteContainer() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();

      assertTrue(connection.deleteContainerIfEmpty("does-not-exist"));

      String containerName = bucketPrefix + ".testDeleteContainer";
      assertTrue(connection.putContainer(containerName));
      assertTrue(connection.deleteContainerIfEmpty(containerName));
   }

   @Test
   public void testPutContainers() throws Exception {
      CloudFilesConnection connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser,
               sysRackspaceKey).withJsonDebug().buildContext().getConnection();
      String containerName1 = bucketPrefix + ".hello";
      assertTrue(connection.putContainer(containerName1));
      // List only the container just created, using a marker with the container name less 1 char
      List<ContainerMetadata> response = connection
               .listOwnedContainers(ListContainerOptions.Builder.afterMarker(
                        containerName1.substring(0, containerName1.length() - 1)).maxResults(1));
      assertNotNull(response);
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), bucketPrefix + ".hello");

      // TODO: Contrary to the API documentation, a container can be created with '?' in the name.
      String containerName2 = bucketPrefix + "?should-be-illegal-question-char";
      connection.putContainer(containerName2);
      // List only the container just created, using a marker with the container name less 1 char
      response = connection.listOwnedContainers(ListContainerOptions.Builder.afterMarker(
               containerName2.substring(0, containerName2.length() - 1)).maxResults(1));
      assertEquals(response.size(), 1);

      // TODO: Should throw a specific exception, not UndeclaredThrowableException
      try {
         connection.putContainer(bucketPrefix + "/illegal-slash-char");
         fail("Should not be able to create container with illegal '/' character");
      } catch (Exception e) {
      }

      assertTrue(connection.deleteContainerIfEmpty(containerName1));
      assertTrue(connection.deleteContainerIfEmpty(containerName2));
   }

}

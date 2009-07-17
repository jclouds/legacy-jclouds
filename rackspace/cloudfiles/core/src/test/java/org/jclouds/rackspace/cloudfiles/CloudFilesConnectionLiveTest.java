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

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;
import org.testng.annotations.BeforeGroups;
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
   CloudFilesConnection connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      connection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser, sysRackspaceKey)
               .withModule(new Log4JLoggingModule()).withJsonDebug().buildContext().getConnection();
   }

   @Test
   public void testListOwnedContainers() throws Exception {
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
      assertTrue(connection.deleteContainerIfEmpty("does-not-exist"));

      String containerName = bucketPrefix + ".testDeleteContainer";
      assertTrue(connection.putContainer(containerName));
      assertTrue(connection.deleteContainerIfEmpty(containerName));
   }

   @Test
   public void testPutContainers() throws Exception {
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

   @Test
   public void testPutAndDeleteObjects() throws Exception {
      String containerName = bucketPrefix + ".testPutAndDeleteObjects";
      String data = "Here is my data";

      assertTrue(connection.putContainer(containerName));

      // Test with string data, ETag hash, and a piece of metadata
      CFObject object = new CFObject("object", data);
      object.setContentLength(data.length());
      object.generateETag();
      object.getMetadata().setContentType("text/plain");
      // TODO: Metadata values aren't being stored by CF, but the names are. Odd...
      object.getMetadata().getUserMetadata().put(
               CloudFilesHeaders.USER_METADATA_PREFIX + "metadata", "metadata-value");
      byte[] md5 = connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils
               .toHexString(object.getMetadata().getETag()));
      // TODO: Get and confirm data

      // Test with invalid ETag (as if object's data was corrupted in transit)
      String correctEtag = HttpUtils.toHexString(object.getMetadata().getETag());
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getMetadata().setETag(HttpUtils.fromHexString(incorrectEtag));
      try {
         connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      // Test chunked/streamed upload with data of "unknown" length
      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
      object = new CFObject("chunked-object", bais);
      md5 = connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), correctEtag);
      // TODO: Get and confirm data

      assertTrue(connection.deleteObject(containerName, "object"));
      assertTrue(connection.deleteObject(containerName, "chunked-object"));
      assertTrue(connection.deleteContainerIfEmpty(containerName));
   }

}

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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesAuthenticationLiveTest")
public class CloudFilesBlobStoreLiveTest {

   private String bucketPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;
   CloudFilesBlobStore connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      connection = CloudFilesContextBuilder.newBuilder(account, key).withModule(
               new Log4JLoggingModule()).withJsonDebug().buildContext().getApi();
   }

   @Test
   public void testListOwnedContainers() throws Exception {
      List<ContainerMetadata> response = connection.listContainers();
      assertNotNull(response);
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      // Create test containers
      String[] containerJsr330 = new String[] { bucketPrefix + ".testListOwnedContainers1",
               bucketPrefix + ".testListOwnedContainers2" };
      assertTrue(connection.createContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.createContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS));

      // Test default listing
      response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail

      // Test listing with options
      response = connection.listContainers(ListContainerOptions.Builder.afterMarker(
               containerJsr330[0].substring(0, containerJsr330[0].length() - 1)).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), containerJsr330[0]);

      response = connection.listContainers(ListContainerOptions.Builder.afterMarker(
               containerJsr330[0]).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), containerJsr330[1]);

      // Cleanup and test containers have been removed
      assertTrue(connection.deleteContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS));
      response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail
   }

   @Test
   public void testHeadAccountMetadata() throws Exception {
      AccountMetadata metadata = connection.getAccountMetadata();
      assertNotNull(metadata);
      long initialContainerCount = metadata.getContainerCount();

      String containerName = bucketPrefix + ".testHeadAccountMetadata";
      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));

      metadata = connection.getAccountMetadata();
      assertNotNull(metadata);
      assertTrue(metadata.getContainerCount() >= initialContainerCount);

      assertTrue(connection.deleteContainer(containerName).get(10, TimeUnit.SECONDS));
   }

   @Test
   public void testDeleteContainer() throws Exception {
      assertTrue(connection.deleteContainer("does-not-exist").get(10, TimeUnit.SECONDS));

      String containerName = bucketPrefix + ".testDeleteContainer";
      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainer(containerName).get(10, TimeUnit.SECONDS));
   }

   @Test
   public void testPutContainers() throws Exception {
      String containerName1 = bucketPrefix + ".hello";
      assertTrue(connection.createContainer(containerName1).get(10, TimeUnit.SECONDS));
      // List only the container just created, using a marker with the container name less 1 char
      List<ContainerMetadata> response = connection
               .listContainers(ListContainerOptions.Builder.afterMarker(
                        containerName1.substring(0, containerName1.length() - 1)).maxResults(1));
      assertNotNull(response);
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), bucketPrefix + ".hello");

      String containerName2 = bucketPrefix + "?should-be-illegal-question-char";
      try {
         connection.createContainer(containerName2).get(10, TimeUnit.MILLISECONDS);
         fail("Should not be able to create container with illegal '?' character");
      } catch (Exception e) {
      }
      // List only the container just created, using a marker with the container name less 1 char
      response = connection.listContainers(ListContainerOptions.Builder.afterMarker(
               containerName2.substring(0, containerName2.length() - 1)).maxResults(1));
      assertEquals(response.size(), 1);

      // TODO: Should throw a specific exception, not UndeclaredThrowableException
      try {
         connection.createContainer(bucketPrefix + "/illegal-slash-char").get(10,
                  TimeUnit.MILLISECONDS);
         fail("Should not be able to create container with illegal '/' character");
      } catch (Exception e) {
      }

      assertTrue(connection.deleteContainer(containerName1).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainer(containerName2).get(10, TimeUnit.SECONDS));
   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = bucketPrefix + ".testObjectOperations";
      String data = "Here is my data";

      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));

      // Test PUT with string data, ETag hash, and a piece of metadata
      Blob<BlobMetadata> object = new Blob<BlobMetadata>("object");
      object.setData(data);
      object.setContentLength(data.length());
      object.generateMD5();
      object.getMetadata().setContentType("text/plain");
      object.getMetadata().getUserMetadata().put("Metadata", "metadata-value");
      byte[] md5 = object.getMetadata().getContentMD5();
      byte[] newEtag = connection.putBlob(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));

      // Test HEAD of missing object
      try {
         connection.blobMetadata(containerName, "non-existent-object");
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Test HEAD of object
      BlobMetadata metadata = connection.blobMetadata(containerName, object.getKey());
      // TODO assertEquals(metadata.getKey(), object.getKey());
      assertEquals(metadata.getSize(), data.length());
      assertEquals(metadata.getContentType(), "text/plain");
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getUserMetadata().entries().size(), 1);
      assertEquals(Iterables.getLast(metadata.getUserMetadata().get("metadata")), "metadata-value");

      // // Test POST to update object's metadata
      Multimap<String, String> userMetadata = HashMultimap.create();
      userMetadata.put("New-Metadata-1", "value-1");
      userMetadata.put("New-Metadata-2", "value-2");
      assertTrue(connection.setObjectMetadata(containerName, object.getKey(), userMetadata));

      // Test GET of missing object
      try {
         connection.getBlob(containerName, "non-existent-object").get(10, TimeUnit.SECONDS);
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }
      // Test GET of object (including updated metadata)
      Blob<BlobMetadata> getBlob = connection.getBlob(containerName, object.getKey()).get(120,
               TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
      // TODO assertEquals(getBlob.getKey(), object.getKey());
      assertEquals(getBlob.getContentLength(), data.length());
      assertEquals(getBlob.getMetadata().getContentType(), "text/plain");
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));
      assertEquals(newEtag, getBlob.getMetadata().getETag());
      assertEquals(getBlob.getMetadata().getUserMetadata().entries().size(), 2);
      assertEquals(
               Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("new-metadata-1")),
               "value-1");
      assertEquals(
               Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("new-metadata-2")),
               "value-2");

      // Test PUT with invalid ETag (as if object's data was corrupted in transit)
      String correctEtag = HttpUtils.toHexString(newEtag);
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getMetadata().setETag(HttpUtils.fromHexString(incorrectEtag));
      try {
         connection.putBlob(containerName, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      // Test PUT chunked/streamed upload with data of "unknown" length
      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
      object = new Blob<BlobMetadata>("chunked-object");
      object.setData(bais);
      newEtag = connection.putBlob(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));

      // Test GET with options
      // Non-matching ETag
      try {
         connection.getBlob(containerName, object.getKey(),
                  GetOptions.Builder.ifETagDoesntMatch(newEtag)).get(120, TimeUnit.SECONDS);
      } catch (Exception e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 304);
      }

      // Matching ETag
      getBlob = connection.getBlob(containerName, object.getKey(),
               GetOptions.Builder.ifETagMatches(newEtag)).get(120, TimeUnit.SECONDS);
      assertEquals(getBlob.getMetadata().getETag(), newEtag);
      getBlob = connection.getBlob(containerName, object.getKey(), GetOptions.Builder.startAt(8))
               .get(120, TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data.substring(8));

      assertTrue(connection.removeBlob(containerName, "object").get(10, TimeUnit.SECONDS));
      assertTrue(connection.removeBlob(containerName, "chunked-object").get(10, TimeUnit.SECONDS));

      assertTrue(connection.deleteContainer(containerName).get(10, TimeUnit.SECONDS));
   }

}

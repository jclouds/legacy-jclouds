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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSConnectionLiveTest")
public class PCSBlobStoreLiveTest {

   private PCSBlobStore connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      URI endpoint = URI.create(checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint"));

      connection = PCSContextFactory.createContext(endpoint, user, password,
               new Log4JLoggingModule()).getApi();

   }

   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   @Test
   public void testListContainers() throws Exception {
      List<ContainerMetadata> response = connection.listContainers();
      assertNotNull(response);
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      // Create test containers
      String[] containerJsr330 = new String[] { containerPrefix + ".testListOwnedContainers1",
               containerPrefix + ".testListOwnedContainers2" };
      assertTrue(connection.createContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.createContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.containerExists(containerJsr330[1]));

      // Test default listing
      response = connection.listContainers();
      // Map<String, String> nameToId = Maps.newHashMap();
      //
      // for (ContainerMetadata data : response) {
      // String path = data.getUrl().getPath();
      // int indexAfterContainersSlash = path.indexOf("containers/") + "containers/".length();
      // String id = path.substring(indexAfterContainersSlash);
      // nameToId.put(data.getName(), id);
      // }
      // assert nameToId.size() >= 2 : nameToId;
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail

      // Cleanup and test containers have been removed
      // assertTrue(connection.deleteContainer(nameToId.get(containerJsr330[0])).get(10,
      // TimeUnit.SECONDS));
      // assertTrue(connection.deleteContainer(nameToId.get(containerJsr330[1])).get(10,
      // TimeUnit.SECONDS));
      connection.listBlobs(containerJsr330[0]).get(10, TimeUnit.SECONDS);
      connection.listBlobs(containerJsr330[1]).get(10, TimeUnit.SECONDS);
      assertTrue(connection.deleteContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS));
      assertTrue(!connection.containerExists(containerJsr330[0]));

      response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail
   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = containerPrefix + ".testObjectOperations";
      String data = "Here is my data";

      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));

      // Test PUT with string data, ETag hash, and a piece of metadata
      PCSFile object = new PCSFile("object");
      object.setData(data);
      object.setContentLength(data.length());
      object.generateMD5();
      object.getMetadata().setContentType("text/plain");
      object.getMetadata().getUserMetadata().put("Metadata", "metadata-value");
      byte[] md5 = object.getMetadata().getContentMD5();
      // etag support by end of the year
      assertNotNull(connection.putBlob(containerName, object).get(10, TimeUnit.SECONDS));
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));

      // Test HEAD of missing object
      try {
         connection.blobMetadata(containerName, "non-existent-object");
         assert false;
      } catch (KeyNotFoundException e) {
      }

      // Test HEAD of object
      FileMetadata metadata = connection.blobMetadata(containerName, object.getKey());
      // TODO assertEquals(metadata.getKey(), object.getKey());
      assertEquals(metadata.getSize(), data.length());
      assertEquals(metadata.getContentType(), "text/plain");
      // assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
      // .getContentMD5()));
      // assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getUserMetadata().entries().size(), 1);
      assertEquals(Iterables.getLast(metadata.getUserMetadata().get("metadata")), "metadata-value");

      // // Test POST to update object's metadata
      // Multimap<String, String> userMetadata = HashMultimap.create();
      // userMetadata.put("New-Metadata-1", "value-1");
      // userMetadata.put("New-Metadata-2", "value-2");
      // assertTrue(connection.setObjectMetadata(containerName, object.getKey(), userMetadata));

      // Test GET of missing object
      try {
         connection.getBlob(containerName, "non-existent-object").get(10, TimeUnit.SECONDS);
         assert false;
      } catch (KeyNotFoundException e) {
      }
      // Test GET of object (including updated metadata)
      PCSFile getBlob = connection.getBlob(containerName, object.getKey()).get(120,
               TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
      assertEquals(getBlob.getKey(), object.getKey());
      assertEquals(getBlob.getContentLength(), data.length());
      assertEquals(getBlob.getMetadata().getContentType(), "text/plain");
      // assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
      // .getContentMD5()));
      // assertEquals(newEtag, getBlob.getMetadata().getETag());
      // assertEquals(getBlob.getMetadata().getUserMetadata().entries().size(), 2);
      // assertEquals(
      // Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("new-metadata-1")),
      // "value-1");
      // assertEquals(
      // Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("new-metadata-2")),
      // "value-2");

      // Test PUT with invalid ETag (as if object's data was corrupted in transit)
      // String correctEtag = HttpUtils.toHexString(newEtag);
      // String incorrectEtag = "0" + correctEtag.substring(1);
      // object.getMetadata().setETag(HttpUtils.fromHexString(incorrectEtag));
      try {
         connection.putBlob(containerName, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      assertTrue(connection.removeBlob(containerName, "object").get(10, TimeUnit.SECONDS));
      assertTrue(connection.removeBlob(containerName, "chunked-object").get(10, TimeUnit.SECONDS));

      assertTrue(connection.deleteContainer(containerName).get(10, TimeUnit.SECONDS));
   }

}

/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.openstack.swift;

import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.underPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.GetOptions;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class SwiftClientLiveTest extends BaseBlobStoreIntegrationTest {

   public CommonSwiftClient getApi() {
      return (CommonSwiftClient) context.getProviderSpecificContext().getApi();
   }

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert getApi().deleteContainerIfEmpty("dbienf");
   }

   @Test
   public void testListOwnedContainers() throws Exception {
      String containerPrefix = getContainerName();
      try {
         Set<ContainerMetadata> response = getApi().listContainers();
         assertNotNull(response);
         long initialContainerCount = response.size();
         assertTrue(initialContainerCount >= 0);

         // Create test containers
         String[] containerNames = new String[] { containerPrefix + ".testListOwnedContainers1",
                  containerPrefix + ".testListOwnedContainers2" };
         assertTrue(getApi().createContainer(containerNames[0]));
         assertTrue(getApi().createContainer(containerNames[1]));

         // Test default listing
         response = getApi().listContainers();
         // assertEquals(response.size(), initialContainerCount + 2);// if the
         // containers already
         // exist, this will fail

         // Test listing with options
         response = getApi().listContainers(
                  ListContainerOptions.Builder.afterMarker(
                           containerNames[0].substring(0, containerNames[0].length() - 1)).maxResults(1));
         assertEquals(response.size(), 1);
         assertEquals(Iterables.get(response, 0).getName(), containerNames[0]);

         response = getApi().listContainers(ListContainerOptions.Builder.afterMarker(containerNames[0]).maxResults(1));
         assertEquals(response.size(), 1);
         assertEquals(Iterables.get(response, 0).getName(), containerNames[1]);

         // Cleanup and test containers have been removed
         assertTrue(getApi().deleteContainerIfEmpty(containerNames[0]));
         assertTrue(getApi().deleteContainerIfEmpty(containerNames[1]));
         response = getApi().listContainers();
         // assertEquals(response.size(), initialContainerCount + 2);// if the
         // containers already
         // exist, this will fail
      } finally {
         returnContainer(containerPrefix);
      }
   }

   @Test
   public void testHeadAccountMetadata() throws Exception {
      String containerPrefix = getContainerName();
      String containerName = containerPrefix + ".testHeadAccountMetadata";
      try {
         AccountMetadata metadata = getApi().getAccountStatistics();
         assertNotNull(metadata);
         long initialContainerCount = metadata.getContainerCount();

         assertTrue(getApi().createContainer(containerName));

         metadata = getApi().getAccountStatistics();
         assertNotNull(metadata);
         assertTrue(metadata.getContainerCount() >= initialContainerCount);

         assertTrue(getApi().deleteContainerIfEmpty(containerName));
      } finally {
         returnContainer(containerPrefix);
      }
   }

   @Test
   public void testPutContainers() throws Exception {
      String containerName = getContainerName();
      try {
         String containerName1 = containerName + ".hello";
         assertTrue(getApi().createContainer(containerName1));
         // List only the container just created, using a marker with the
         // container name less 1 char
         Set<ContainerMetadata> response = getApi().listContainers(
                  ListContainerOptions.Builder.afterMarker(containerName1.substring(0, containerName1.length() - 1))
                           .maxResults(1));
         assertNotNull(response);
         assertEquals(response.size(), 1);
         assertEquals(Iterables.get(response, 0).getName(), containerName + ".hello");

         String containerName2 = containerName + "?should-be-illegal-question-char";
         assert getApi().createContainer(containerName2);

         assert getApi().createContainer(containerName + "/illegal-slash-char");

         assertTrue(getApi().deleteContainerIfEmpty(containerName1));
         assertTrue(getApi().deleteContainerIfEmpty(containerName2));
      } finally {
         returnContainer(containerName);
      }
   }

   public void testListContainerPath() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String containerName = getContainerName();
      try {

         String data = "foo";

         getApi().putObject(containerName, newSwiftObject(data, "foo"));
         getApi().putObject(containerName, newSwiftObject(data, "path/bar"));

         PageSet<ObjectInfo> container = getApi().listObjects(containerName, underPath(""));
         assert container.getNextMarker() == null;
         assertEquals(container.size(), 1);
         assertEquals(Iterables.get(container, 0).getName(), "foo");
         container = getApi().listObjects(containerName, underPath("path"));
         assert container.getNextMarker() == null;
         assertEquals(container.size(), 1);
         assertEquals(Iterables.get(container, 0).getName(), "path/bar");
      } finally {
         returnContainer(containerName);
      }

   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = getContainerName();
      try {
         // Test PUT with string data, ETag hash, and a piece of metadata
         String data = "Here is my data";
         String key = "object";
         SwiftObject object = newSwiftObject(data, key);
         byte[] md5 = object.getPayload().getContentMetadata().getContentMD5();
         String newEtag = getApi().putObject(containerName, object);
         assert newEtag != null;
         assertEquals(CryptoStreams.hex(md5), CryptoStreams.hex(object.getPayload().getContentMetadata()
                  .getContentMD5()));

         // Test HEAD of missing object
         assert getApi().getObjectInfo(containerName, "non-existent-object") == null;

         // Test HEAD of object
         MutableObjectInfoWithMetadata metadata = getApi().getObjectInfo(containerName, object.getInfo().getName());
         assertEquals(metadata.getName(), object.getInfo().getName());

         assertEquals(metadata.getBytes(), new Long(data.length()));
         assertEquals(metadata.getContentType(), "text/plain; charset=UTF-8");

         assertEquals(CryptoStreams.hex(md5), CryptoStreams.hex(metadata.getHash()));
         assertEquals(metadata.getHash(), CryptoStreams.hex(newEtag));
         assertEquals(metadata.getMetadata().entrySet().size(), 1);
         assertEquals(metadata.getMetadata().get("metadata"), "metadata-value");

         // // Test POST to update object's metadata
         Map<String, String> userMetadata = Maps.newHashMap();
         userMetadata.put("New-Metadata-1", "value-1");
         userMetadata.put("New-Metadata-2", "value-2");
         assertTrue(getApi().setObjectInfo(containerName, object.getInfo().getName(), userMetadata));

         // Test GET of missing object
         assert getApi().getObject(containerName, "non-existent-object") == null;
         // Test GET of object (including updated metadata)
         SwiftObject getBlob = getApi().getObject(containerName, object.getInfo().getName());
         assertEquals(Strings2.toStringAndClose(getBlob.getPayload().getInput()), data);
         // TODO assertEquals(getBlob.getName(),
         // object.getMetadata().getName());
         assertEquals(getBlob.getInfo().getBytes(), new Long(data.length()));
         assertEquals(getBlob.getInfo().getContentType(), "text/plain; charset=UTF-8");
         assertEquals(CryptoStreams.hex(md5), CryptoStreams.hex(getBlob.getInfo().getHash()));
         assertEquals(CryptoStreams.hex(newEtag), getBlob.getInfo().getHash());
         assertEquals(getBlob.getInfo().getMetadata().entrySet().size(), 2);
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-1"), "value-1");
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-2"), "value-2");

         // Test PUT with invalid ETag (as if object's data was corrupted in
         // transit)
         String correctEtag = newEtag;
         String incorrectEtag = "0" + correctEtag.substring(1);
         object.getInfo().setHash(CryptoStreams.hex(incorrectEtag));
         try {
            getApi().putObject(containerName, object);
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 422);
         }

         // Test PUT chunked/streamed upload with data of "unknown" length
         ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
         SwiftObject blob = getApi().newSwiftObject();
         blob.getInfo().setName("chunked-object");
         blob.setPayload(bais);
         newEtag = getApi().putObject(containerName, blob);
         assertEquals(CryptoStreams.hex(md5), CryptoStreams.hex(getBlob.getInfo().getHash()));

         // Test GET with options
         // Non-matching ETag
         try {
            getApi()
                     .getObject(containerName, object.getInfo().getName(),
                              GetOptions.Builder.ifETagDoesntMatch(newEtag));
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 304);
         }

         // Matching ETag
         getBlob = getApi().getObject(containerName, object.getInfo().getName(),
                  GetOptions.Builder.ifETagMatches(newEtag));
         assertEquals(getBlob.getInfo().getHash(), CryptoStreams.hex(newEtag));
         getBlob = getApi().getObject(containerName, object.getInfo().getName(), GetOptions.Builder.startAt(8));
         assertEquals(Strings2.toStringAndClose(getBlob.getPayload().getInput()), data.substring(8));

      } finally {
         returnContainer(containerName);
      }
   }

   private SwiftObject newSwiftObject(String data, String key) throws IOException {
      SwiftObject object = getApi().newSwiftObject();
      object.getInfo().setName(key);
      object.setPayload(data);
      Payloads.calculateMD5(object);
      object.getInfo().setContentType("text/plain");
      object.getInfo().getMetadata().put("Metadata", "metadata-value");
      return object;
   }

}

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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.underPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesClientLiveTest")
public class CloudFilesClientLiveTest extends BaseBlobStoreIntegrationTest<CloudFilesClient> {

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert context.getApi().deleteContainerIfEmpty("dbienf").get(10, TimeUnit.SECONDS);
   }

   @Test
   public void testCDNOperations() throws Exception {
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour

      // Create two new containers for testing
      final String containerNameWithCDN = getContainerName();
      final String containerNameWithoutCDN = getContainerName();
      try {
         try {
            context.getApi().disableCDN(containerNameWithCDN);
            context.getApi().disableCDN(containerNameWithoutCDN);
         } catch (Exception e) {

         }
         ContainerCDNMetadata cdnMetadata = null;

         // Enable CDN with PUT for one container
         final URI cdnUri = context.getApi().enableCDN(containerNameWithCDN);
         assertTrue(cdnUri != null);

         // Confirm CDN is enabled via HEAD request and has default TTL
         cdnMetadata = context.getApi().getCDNMetadata(containerNameWithCDN);

         // Ticket #2213 this should be true, but it is false
         // assertTrue(!cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getCDNUri(), cdnUri);
         final long initialTTL = cdnMetadata.getTTL();

         try {
            cdnMetadata = context.getApi().getCDNMetadata(containerNameWithoutCDN);
            assert cdnMetadata == null || !cdnMetadata.isCDNEnabled() : containerNameWithoutCDN
                     + " should not have metadata";
         } catch (ContainerNotFoundException e) {
         }

         try {
            cdnMetadata = context.getApi().getCDNMetadata("DoesNotExist");
            assert false : "should not exist";
         } catch (ContainerNotFoundException e) {
         }

         // List CDN metadata for containers, and ensure all CDN info is available for enabled
         // container
         SortedSet<ContainerCDNMetadata> cdnMetadataList = context.getApi().listCDNContainers();
         assertTrue(cdnMetadataList.size() >= 1);

         assertTrue(cdnMetadataList.contains(new ContainerCDNMetadata(containerNameWithCDN, true,
                  initialTTL, cdnUri)));

         // Test listing with options
         cdnMetadataList = context.getApi().listCDNContainers(
                  ListCdnContainerOptions.Builder.enabledOnly());
         assertTrue(Iterables.all(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
            public boolean apply(ContainerCDNMetadata cdnMetadata) {
               return cdnMetadata.isCDNEnabled();
            }
         }));

         cdnMetadataList = context.getApi().listCDNContainers(
                  ListCdnContainerOptions.Builder.afterMarker(
                           containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1))
                           .maxResults(1));
         assertEquals(cdnMetadataList.size(), 1);

         // Enable CDN with PUT for the same container, this time with a custom TTL
         long ttl = 4000;
         context.getApi().enableCDN(containerNameWithCDN, ttl);

         cdnMetadata = context.getApi().getCDNMetadata(containerNameWithCDN);

         // Ticket #2213 this should be true, but it is false
         // assertTrue(!cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), ttl);

         // Check POST by updating TTL settings
         ttl = minimumTTL;
         context.getApi().updateCDN(containerNameWithCDN, minimumTTL);

         cdnMetadata = context.getApi().getCDNMetadata(containerNameWithCDN);
         // Ticket #2213 this should be true, but it is false
         // assertTrue(!cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), minimumTTL);

         // Confirm that minimum allowed value for TTL is 3600, lower values are ignored.
         context.getApi().updateCDN(containerNameWithCDN, 3599L);
         cdnMetadata = context.getApi().getCDNMetadata(containerNameWithCDN);
         assertEquals(cdnMetadata.getTTL(), minimumTTL); // Note that TTL is 3600 here, not 3599

         // Disable CDN with POST
         assertTrue(context.getApi().disableCDN(containerNameWithCDN));

         cdnMetadata = context.getApi().getCDNMetadata(containerNameWithCDN);
         assertEquals(cdnMetadata.isCDNEnabled(), false);
      } finally {
         recycleContainer(containerNameWithCDN);
         recycleContainer(containerNameWithoutCDN);
      }
   }

   @Test
   public void testListOwnedContainers() throws Exception {
      String containerPrefix = getContainerName();
      try {
         SortedSet<ContainerMetadata> response = context.getApi().listContainers().get(10,
                  TimeUnit.SECONDS);
         assertNotNull(response);
         long initialContainerCount = response.size();
         assertTrue(initialContainerCount >= 0);

         // Create test containers
         String[] containerJsr330 = new String[] { containerPrefix + ".testListOwnedContainers1",
                  containerPrefix + ".testListOwnedContainers2" };
         assertTrue(context.getApi().createContainer(containerJsr330[0]).get(10, TimeUnit.SECONDS));
         assertTrue(context.getApi().createContainer(containerJsr330[1]).get(10, TimeUnit.SECONDS));

         // Test default listing
         response = context.getApi().listContainers().get(10, TimeUnit.SECONDS);
         // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
         // exist, this will fail

         // Test listing with options
         response = context.getApi().listContainers(
                  ListContainerOptions.Builder.afterMarker(
                           containerJsr330[0].substring(0, containerJsr330[0].length() - 1))
                           .maxResults(1)).get(10, TimeUnit.SECONDS);
         assertEquals(response.size(), 1);
         assertEquals(response.first().getName(), containerJsr330[0]);

         response = context.getApi().listContainers(
                  ListContainerOptions.Builder.afterMarker(containerJsr330[0]).maxResults(1)).get(
                  10, TimeUnit.SECONDS);
         assertEquals(response.size(), 1);
         assertEquals(response.first().getName(), containerJsr330[1]);

         // Cleanup and test containers have been removed
         assertTrue(context.getApi().deleteContainerIfEmpty(containerJsr330[0]).get(10,
                  TimeUnit.SECONDS));
         assertTrue(context.getApi().deleteContainerIfEmpty(containerJsr330[1]).get(10,
                  TimeUnit.SECONDS));
         response = context.getApi().listContainers().get(10, TimeUnit.SECONDS);
         // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
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
         AccountMetadata metadata = context.getApi().getAccountStatistics();
         assertNotNull(metadata);
         long initialContainerCount = metadata.getContainerCount();

         assertTrue(context.getApi().createContainer(containerName).get(10, TimeUnit.SECONDS));

         metadata = context.getApi().getAccountStatistics();
         assertNotNull(metadata);
         assertTrue(metadata.getContainerCount() >= initialContainerCount);

         assertTrue(context.getApi().deleteContainerIfEmpty(containerName)
                  .get(10, TimeUnit.SECONDS));
      } finally {
         returnContainer(containerPrefix);
      }
   }

   @Test
   public void testPutContainers() throws Exception {
      String containerName = getContainerName();
      try {
         String containerName1 = containerName + ".hello";
         assertTrue(context.getApi().createContainer(containerName1).get(10, TimeUnit.SECONDS));
         // List only the container just created, using a marker with the container name less 1 char
         SortedSet<ContainerMetadata> response = context.getApi().listContainers(
                  ListContainerOptions.Builder.afterMarker(
                           containerName1.substring(0, containerName1.length() - 1)).maxResults(1))
                  .get(10, TimeUnit.SECONDS);
         assertNotNull(response);
         assertEquals(response.size(), 1);
         assertEquals(response.first().getName(), containerName + ".hello");

         String containerName2 = containerName + "?should-be-illegal-question-char";
         try {
            context.getApi().createContainer(containerName2).get(10, TimeUnit.MILLISECONDS);
            fail("Should not be able to create container with illegal '?' character");
         } catch (Exception e) {
         }

         // TODO: Should throw a specific exception, not UndeclaredThrowableException
         try {
            context.getApi().createContainer(containerName + "/illegal-slash-char").get(10,
                     TimeUnit.MILLISECONDS);
            fail("Should not be able to create container with illegal '/' character");
         } catch (Exception e) {
         }
         assertTrue(context.getApi().deleteContainerIfEmpty(containerName1).get(10,
                  TimeUnit.SECONDS));
         assertTrue(context.getApi().deleteContainerIfEmpty(containerName2).get(10,
                  TimeUnit.SECONDS));
      } finally {
         returnContainer(containerName);
      }
   }

   public void testListContainerPath() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {

         String data = "foo";

         context.getApi().putObject(containerName, newCFObject(data, "foo")).get(10,
                  TimeUnit.SECONDS);
         context.getApi().putObject(containerName, newCFObject(data, "path/bar")).get(10,
                  TimeUnit.SECONDS);

         ListResponse<ObjectInfo> container = context.getApi().listObjects(containerName,
                  underPath("")).get(10, TimeUnit.SECONDS);
         assert !container.isTruncated();
         assertEquals(container.size(), 1);
         assertEquals(container.first().getName(), "foo");
         container = context.getApi().listObjects(containerName, underPath("path")).get(10,
                  TimeUnit.SECONDS);
         assert !container.isTruncated();
         assertEquals(container.size(), 1);
         assertEquals(container.first().getName(), "path/bar");
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
         CFObject object = newCFObject(data, key);
         byte[] md5 = object.getInfo().getHash();
         String newEtag = context.getApi().putObject(containerName, object).get(10,
                  TimeUnit.SECONDS);
         assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getInfo().getHash()));

         // Test HEAD of missing object
         try {
            context.getApi().getObjectInfo(containerName, "non-existent-object");
            assert false;
         } catch (KeyNotFoundException e) {
         }

         // Test HEAD of object
         MutableObjectInfoWithMetadata metadata = context.getApi().getObjectInfo(containerName,
                  object.getInfo().getName());
         // TODO assertEquals(metadata.getName(), object.getMetadata().getName());
         assertEquals(metadata.getBytes(), new Long(data.length()));
         assertEquals(metadata.getContentType(), "text/plain");
         assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getInfo().getHash()));
         assertEquals(metadata.getHash(), HttpUtils.fromHexString(newEtag));
         assertEquals(metadata.getMetadata().entrySet().size(), 1);
         assertEquals(metadata.getMetadata().get("metadata"), "metadata-value");

         // // Test POST to update object's metadata
         Map<String, String> userMetadata = Maps.newHashMap();
         userMetadata.put("New-Metadata-1", "value-1");
         userMetadata.put("New-Metadata-2", "value-2");
         assertTrue(context.getApi().setObjectInfo(containerName, object.getInfo().getName(),
                  userMetadata));

         // Test GET of missing object
         try {
            context.getApi().getObject(containerName, "non-existent-object").get(10,
                     TimeUnit.SECONDS);
            assert false;
         } catch (KeyNotFoundException e) {
         }
         // Test GET of object (including updated metadata)
         CFObject getBlob = context.getApi().getObject(containerName, object.getInfo().getName())
                  .get(120, TimeUnit.SECONDS);
         assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
         // TODO assertEquals(getBlob.getName(), object.getMetadata().getName());
         assertEquals(getBlob.getContentLength(), new Long(data.length()));
         assertEquals(getBlob.getInfo().getContentType(), "text/plain");
         assertEquals(HttpUtils.toHexString(md5), HttpUtils
                  .toHexString(getBlob.getInfo().getHash()));
         assertEquals(HttpUtils.fromHexString(newEtag), getBlob.getInfo().getHash());
         assertEquals(getBlob.getInfo().getMetadata().entrySet().size(), 2);
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-1"), "value-1");
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-2"), "value-2");

         // Test PUT with invalid ETag (as if object's data was corrupted in transit)
         String correctEtag = newEtag;
         String incorrectEtag = "0" + correctEtag.substring(1);
         object.getInfo().setHash(HttpUtils.fromHexString(incorrectEtag));
         try {
            context.getApi().putObject(containerName, object).get(10, TimeUnit.SECONDS);
         } catch (Throwable e) {
            assertEquals(e.getCause().getClass(), HttpResponseException.class);
            assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
         }

         // Test PUT chunked/streamed upload with data of "unknown" length
         ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
         CFObject blob = context.getApi().newCFObject();
         blob.getInfo().setName("chunked-object");
         blob.setData(bais);
         newEtag = context.getApi().putObject(containerName, blob).get(10, TimeUnit.SECONDS);
         assertEquals(HttpUtils.toHexString(md5), HttpUtils
                  .toHexString(getBlob.getInfo().getHash()));

         // Test GET with options
         // Non-matching ETag
         try {
            context.getApi().getObject(containerName, object.getInfo().getName(),
                     GetOptions.Builder.ifETagDoesntMatch(newEtag)).get(120, TimeUnit.SECONDS);
         } catch (Exception e) {
            assertEquals(e.getCause().getClass(), HttpResponseException.class);
            assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 304);
         }

         // Matching ETag
         getBlob = context.getApi().getObject(containerName, object.getInfo().getName(),
                  GetOptions.Builder.ifETagMatches(newEtag)).get(120, TimeUnit.SECONDS);
         assertEquals(getBlob.getInfo().getHash(), HttpUtils.fromHexString(newEtag));
         getBlob = context.getApi().getObject(containerName, object.getInfo().getName(),
                  GetOptions.Builder.startAt(8)).get(120, TimeUnit.SECONDS);
         assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data.substring(8));

      } finally {
         returnContainer(containerName);
      }
   }

   private CFObject newCFObject(String data, String key) throws IOException {
      CFObject object = context.getApi().newCFObject();
      object.getInfo().setName(key);
      object.setData(data);
      object.generateMD5();
      object.getInfo().setContentType("text/plain");
      object.getInfo().getMetadata().put("Metadata", "metadata-value");
      return object;
   }

}

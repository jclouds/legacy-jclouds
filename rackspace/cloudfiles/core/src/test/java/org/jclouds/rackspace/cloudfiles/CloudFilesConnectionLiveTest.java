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
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesConnectionLiveTest")
public class CloudFilesConnectionLiveTest {

   private String bucketPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;
   CloudFilesConnection connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      connection = CloudFilesContextFactory.createContext(account, key, new Log4JLoggingModule())
               .getApi();
   }

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert connection.deleteContainerIfEmpty("dbienf").get(10, TimeUnit.SECONDS);
   }

   @Test
   public void testCDNOperations() throws Exception {
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour

      // Create two new containers for testing
      final String containerNameWithCDN = bucketPrefix + ".testCDNOperationsContainerWithCDN";
      final String containerNameWithoutCDN = bucketPrefix + ".testCDNOperationsContainerWithoutCDN";
      assertTrue(connection.createContainer(containerNameWithCDN).get(10, TimeUnit.SECONDS));
      assertTrue(connection.createContainer(containerNameWithoutCDN).get(10, TimeUnit.SECONDS));

      ContainerCDNMetadata cdnMetadata = null;

      // Enable CDN with PUT for one container
      final String cdnUri = connection.enableCDN(containerNameWithCDN);
      assertTrue(cdnUri != null);
      assertTrue(cdnUri.startsWith("http://"));

      // Confirm CDN is enabled via HEAD request and has default TTL
      cdnMetadata = connection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getCdnUri(), cdnUri);
      final long initialTTL = cdnMetadata.getTtl();

      try {
         cdnMetadata = connection.getCDNMetadata(containerNameWithoutCDN);
         assert false : "should not exist";
      } catch (ContainerNotFoundException e) {
      }

      try {
         cdnMetadata = connection.getCDNMetadata("DoesNotExist");
         assert false : "should not exist";
      } catch (ContainerNotFoundException e) {
      }

      // List CDN metadata for containers, and ensure all CDN info is available for enabled
      // container
      SortedSet<ContainerCDNMetadata> cdnMetadataList = connection.listCDNContainers();
      assertTrue(cdnMetadataList.size() >= 1);
      assertTrue(Iterables.any(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
         public boolean apply(ContainerCDNMetadata cdnMetadata) {
            return (cdnMetadata.getName().equals(containerNameWithCDN)
                     && cdnMetadata.isCdnEnabled() && cdnMetadata.getTtl() == initialTTL && cdnMetadata
                     .getCdnUri().equals(cdnUri));
         }
      }));

      // Test listing with options
      cdnMetadataList = connection.listCDNContainers(ListCdnContainerOptions.Builder.enabledOnly());
      assertTrue(Iterables.all(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
         public boolean apply(ContainerCDNMetadata cdnMetadata) {
            return cdnMetadata.isCdnEnabled();
         }
      }));

      cdnMetadataList = connection.listCDNContainers(ListCdnContainerOptions.Builder.afterMarker(
               containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1)).maxResults(1));
      assertEquals(cdnMetadataList.size(), 1);
      assertEquals(cdnMetadataList.first().getName(), containerNameWithCDN);

      // Enable CDN with PUT for the same container, this time with a custom TTL
      long ttl = 4000;
      connection.enableCDN(containerNameWithCDN, ttl);

      cdnMetadata = connection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getTtl(), ttl);

      // Check POST by updating TTL settings
      ttl = minimumTTL;
      connection.updateCDN(containerNameWithCDN, minimumTTL);

      cdnMetadata = connection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getTtl(), minimumTTL);

      // Confirm that minimum allowed value for TTL is 3600, lower values are ignored.
      connection.updateCDN(containerNameWithCDN, 3599L);
      cdnMetadata = connection.getCDNMetadata(containerNameWithCDN);
      assertEquals(cdnMetadata.getTtl(), minimumTTL); // Note that TTL is 3600 here, not 3599

      // Disable CDN with POST
      assertTrue(connection.disableCDN(containerNameWithCDN));

      cdnMetadata = connection.getCDNMetadata(containerNameWithCDN);
      assertEquals(cdnMetadata.isCdnEnabled(), false);

      // Delete test containers
      assertTrue(connection.deleteContainerIfEmpty(containerNameWithCDN).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainerIfEmpty(containerNameWithoutCDN).get(10,
               TimeUnit.SECONDS));
   }

   @Test
   public void testListOwnedContainers() throws Exception {
      SortedSet<ContainerMetadata> response = connection.listContainers();
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
      assertEquals(response.first().getName(), containerJsr330[0]);

      response = connection.listContainers(ListContainerOptions.Builder.afterMarker(
               containerJsr330[0]).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.first().getName(), containerJsr330[1]);

      // Cleanup and test containers have been removed
      assertTrue(connection.deleteContainerIfEmpty(containerJsr330[0]).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainerIfEmpty(containerJsr330[1]).get(10, TimeUnit.SECONDS));
      response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail
   }

   @Test
   public void testHeadAccountMetadata() throws Exception {
      AccountMetadata metadata = connection.getAccountStatistics();
      assertNotNull(metadata);
      long initialContainerCount = metadata.getContainerCount();

      String containerName = bucketPrefix + ".testHeadAccountMetadata";
      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));

      metadata = connection.getAccountStatistics();
      assertNotNull(metadata);
      assertTrue(metadata.getContainerCount() >= initialContainerCount);

      assertTrue(connection.deleteContainerIfEmpty(containerName).get(10, TimeUnit.SECONDS));
   }

   @Test
   public void testDeleteContainer() throws Exception {
      assertTrue(connection.deleteContainerIfEmpty("does-not-exist").get(10, TimeUnit.SECONDS));

      String containerName = bucketPrefix + ".testDeleteContainer";
      assertTrue(connection.createContainer(containerName).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainerIfEmpty(containerName).get(10, TimeUnit.SECONDS));
   }

   @Test
   public void testPutContainers() throws Exception {
      String containerName1 = bucketPrefix + ".hello";
      assertTrue(connection.createContainer(containerName1).get(10, TimeUnit.SECONDS));
      // List only the container just created, using a marker with the container name less 1 char
      SortedSet<ContainerMetadata> response = connection
               .listContainers(ListContainerOptions.Builder.afterMarker(
                        containerName1.substring(0, containerName1.length() - 1)).maxResults(1));
      assertNotNull(response);
      assertEquals(response.size(), 1);
      assertEquals(response.first().getName(), bucketPrefix + ".hello");

      String containerName2 = bucketPrefix + "?should-be-illegal-question-char";
      try {
         connection.createContainer(containerName2).get(10, TimeUnit.MILLISECONDS);
         fail("Should not be able to create container with illegal '?' character");
      } catch (Exception e) {
      }

      // TODO: Should throw a specific exception, not UndeclaredThrowableException
      try {
         connection.createContainer(bucketPrefix + "/illegal-slash-char").get(10,
                  TimeUnit.MILLISECONDS);
         fail("Should not be able to create container with illegal '/' character");
      } catch (Exception e) {
      }

      assertTrue(connection.deleteContainerIfEmpty(containerName1).get(10, TimeUnit.SECONDS));
      assertTrue(connection.deleteContainerIfEmpty(containerName2).get(10, TimeUnit.SECONDS));
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
      String newEtag = connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));

      // Test HEAD of missing object
      try {
         connection.getObjectMetadata(containerName, "non-existent-object");
         assert false;
      } catch (KeyNotFoundException e) {
      }

      // Test HEAD of object
      BlobMetadata metadata = connection.getObjectMetadata(containerName, object.getKey());
      // TODO assertEquals(metadata.getKey(), object.getKey());
      assertEquals(metadata.getSize(), data.length());
      assertEquals(metadata.getContentType(), "text/plain");
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getUserMetadata().entrySet().size(), 1);
      assertEquals(metadata.getUserMetadata().get("metadata"), "metadata-value");

      // // Test POST to update object's metadata
      Multimap<String, String> userMetadata = HashMultimap.create();
      userMetadata.put("New-Metadata-1", "value-1");
      userMetadata.put("New-Metadata-2", "value-2");
      assertTrue(connection.setObjectMetadata(containerName, object.getKey(), userMetadata));

      // Test GET of missing object
      try {
         connection.getObject(containerName, "non-existent-object").get(10, TimeUnit.SECONDS);
         assert false;
      } catch (KeyNotFoundException e) {
      }
      // Test GET of object (including updated metadata)
      Blob<BlobMetadata> getBlob = connection.getObject(containerName, object.getKey()).get(120,
               TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
      // TODO assertEquals(getBlob.getKey(), object.getKey());
      assertEquals(getBlob.getContentLength(), data.length());
      assertEquals(getBlob.getMetadata().getContentType(), "text/plain");
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));
      assertEquals(newEtag, getBlob.getMetadata().getETag());
      assertEquals(getBlob.getMetadata().getUserMetadata().entrySet().size(), 2);
      assertEquals(getBlob.getMetadata().getUserMetadata().get("new-metadata-1"), "value-1");
      assertEquals(getBlob.getMetadata().getUserMetadata().get("new-metadata-2"), "value-2");

      // Test PUT with invalid ETag (as if object's data was corrupted in transit)
      String correctEtag = newEtag;
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getMetadata().setETag(incorrectEtag);
      try {
         connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      // Test PUT chunked/streamed upload with data of "unknown" length
      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
      object = new Blob<BlobMetadata>("chunked-object");
      object.setData(bais);
      newEtag = connection.putObject(containerName, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));

      // Test GET with options
      // Non-matching ETag
      try {
         connection.getObject(containerName, object.getKey(),
                  GetOptions.Builder.ifETagDoesntMatch(newEtag)).get(120, TimeUnit.SECONDS);
      } catch (Exception e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 304);
      }

      // Matching ETag
      getBlob = connection.getObject(containerName, object.getKey(),
               GetOptions.Builder.ifETagMatches(newEtag)).get(120, TimeUnit.SECONDS);
      assertEquals(getBlob.getMetadata().getETag(), newEtag);
      getBlob = connection.getObject(containerName, object.getKey(), GetOptions.Builder.startAt(8))
               .get(120, TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data.substring(8));

      assertTrue(connection.removeObject(containerName, "object").get(10, TimeUnit.SECONDS));
      assertTrue(connection.removeObject(containerName, "chunked-object").get(10, TimeUnit.SECONDS));

      assertTrue(connection.deleteContainerIfEmpty(containerName).get(10, TimeUnit.SECONDS));
   }

}

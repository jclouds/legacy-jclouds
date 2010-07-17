/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.underPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.util.Utils;
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
public class CloudFilesClientLiveTest extends BaseBlobStoreIntegrationTest {
   private static final EncryptionService encryptionService = new JCEEncryptionService();

   public CloudFilesClient getApi() {
      return (CloudFilesClient) context.getProviderSpecificContext().getApi();
   }

   /**
    * this method overrides containerName to ensure it isn't found
    */
   @Test(groups = { "integration", "live" })
   public void deleteContainerIfEmptyNotFound() throws Exception {
      assert getApi().deleteContainerIfEmpty("dbienf");
   }

   @Test
   public void testCDNOperations() throws Exception {
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour

      // Create two new containers for testing
      final String containerNameWithCDN = getContainerName();
      final String containerNameWithoutCDN = getContainerName();
      try {
         try {
            getApi().disableCDN(containerNameWithCDN);
            getApi().disableCDN(containerNameWithoutCDN);
         } catch (Exception e) {

         }
         ContainerCDNMetadata cdnMetadata = null;

         // Enable CDN with PUT for one container
         final URI cdnUri = getApi().enableCDN(containerNameWithCDN);
         assertTrue(cdnUri != null);

         // Confirm CDN is enabled via HEAD request and has default TTL
         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getCDNUri(), cdnUri);
         final long initialTTL = cdnMetadata.getTTL();

         try {
            cdnMetadata = getApi().getCDNMetadata(containerNameWithoutCDN);
            assert cdnMetadata == null || !cdnMetadata.isCDNEnabled() : containerNameWithoutCDN
                  + " should not have metadata";
         } catch (ContainerNotFoundException e) {
         } catch (HttpResponseException e) {
         }

         try {
            cdnMetadata = getApi().getCDNMetadata("DoesNotExist");
            assert false : "should not exist";
         } catch (ContainerNotFoundException e) {
         } catch (HttpResponseException e) {
         }
         // List CDN metadata for containers, and ensure all CDN info is
         // available for enabled
         // container
         Set<ContainerCDNMetadata> cdnMetadataList = getApi().listCDNContainers();
         assertTrue(cdnMetadataList.size() >= 1);

         assertTrue(cdnMetadataList.contains(new ContainerCDNMetadata(containerNameWithCDN, true, initialTTL, cdnUri)));

         // Test listing with options
         cdnMetadataList = getApi().listCDNContainers(ListCdnContainerOptions.Builder.enabledOnly());
         assertTrue(Iterables.all(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
            public boolean apply(ContainerCDNMetadata cdnMetadata) {
               return cdnMetadata.isCDNEnabled();
            }
         }));

         cdnMetadataList = getApi().listCDNContainers(
               ListCdnContainerOptions.Builder.afterMarker(
                     containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1)).maxResults(1));
         assertEquals(cdnMetadataList.size(), 1);

         // Enable CDN with PUT for the same container, this time with a custom
         // TTL
         long ttl = 4000;
         getApi().enableCDN(containerNameWithCDN, ttl);

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), ttl);

         // Check POST by updating TTL settings
         ttl = minimumTTL;
         getApi().updateCDN(containerNameWithCDN, minimumTTL);

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), minimumTTL);

         // Confirm that minimum allowed value for TTL is 3600, lower values are
         // ignored.
         getApi().updateCDN(containerNameWithCDN, 3599L);
         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         assertEquals(cdnMetadata.getTTL(), 3599L);

         // Disable CDN with POST
         assertTrue(getApi().disableCDN(containerNameWithCDN));

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
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
               ListContainerOptions.Builder.afterMarker(containerNames[0].substring(0, containerNames[0].length() - 1))
                     .maxResults(1));
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

         // TODO: Should throw a specific exception, not
         // UndeclaredThrowableException
         try {
            getApi().createContainer(containerName + "/illegal-slash-char");
            fail("Should not be able to create container with illegal '/' character");
         } catch (Exception e) {
         }
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

         getApi().putObject(containerName, newCFObject(data, "foo"));
         getApi().putObject(containerName, newCFObject(data, "path/bar"));

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
         CFObject object = newCFObject(data, key);
         byte[] md5 = object.getPayload().getContentMD5();
         String newEtag = getApi().putObject(containerName, object);
         assert newEtag != null;
         assertEquals(encryptionService.hex(md5), encryptionService.hex(object.getPayload().getContentMD5()));

         // Test HEAD of missing object
         assert getApi().getObjectInfo(containerName, "non-existent-object") == null;

         // Test HEAD of object
         MutableObjectInfoWithMetadata metadata = getApi().getObjectInfo(containerName, object.getInfo().getName());
         // TODO assertEquals(metadata.getName(),
         // object.getMetadata().getName());

         // rackspace recently doesn't return a content-length or type on head
         assertEquals(metadata.getBytes(), null);
         assertEquals(metadata.getContentType(), null);
         // assertEquals(metadata.getBytes(), new Long(data.length()));
         // assertEquals(metadata.getContentType(), "text/plain");
         
         assertEquals(encryptionService.hex(md5), encryptionService.hex(metadata.getHash()));
         assertEquals(metadata.getHash(), encryptionService.fromHex(newEtag));
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
         CFObject getBlob = getApi().getObject(containerName, object.getInfo().getName());
         assertEquals(Utils.toStringAndClose(getBlob.getPayload().getInput()), data);
         // TODO assertEquals(getBlob.getName(),
         // object.getMetadata().getName());
         assertEquals(getBlob.getInfo().getBytes(), new Long(data.length()));
         assertEquals(getBlob.getInfo().getContentType(), "text/plain");
         assertEquals(encryptionService.hex(md5), encryptionService.hex(getBlob.getInfo().getHash()));
         assertEquals(encryptionService.fromHex(newEtag), getBlob.getInfo().getHash());
         assertEquals(getBlob.getInfo().getMetadata().entrySet().size(), 2);
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-1"), "value-1");
         assertEquals(getBlob.getInfo().getMetadata().get("new-metadata-2"), "value-2");

         // Test PUT with invalid ETag (as if object's data was corrupted in
         // transit)
         String correctEtag = newEtag;
         String incorrectEtag = "0" + correctEtag.substring(1);
         object.getInfo().setHash(encryptionService.fromHex(incorrectEtag));
         try {
            getApi().putObject(containerName, object);
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 422);
         }

         // Test PUT chunked/streamed upload with data of "unknown" length
         ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
         CFObject blob = getApi().newCFObject();
         blob.getInfo().setName("chunked-object");
         blob.setPayload(bais);
         newEtag = getApi().putObject(containerName, blob);
         assertEquals(encryptionService.hex(md5), encryptionService.hex(getBlob.getInfo().getHash()));

         // Test GET with options
         // Non-matching ETag
         try {
            getApi()
                  .getObject(containerName, object.getInfo().getName(), GetOptions.Builder.ifETagDoesntMatch(newEtag));
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 304);
         }

         // Matching ETag
         getBlob = getApi().getObject(containerName, object.getInfo().getName(),
               GetOptions.Builder.ifETagMatches(newEtag));
         assertEquals(getBlob.getInfo().getHash(), encryptionService.fromHex(newEtag));
         getBlob = getApi().getObject(containerName, object.getInfo().getName(), GetOptions.Builder.startAt(8));
         assertEquals(Utils.toStringAndClose(getBlob.getPayload().getInput()), data.substring(8));

      } finally {
         returnContainer(containerName);
      }
   }

   private CFObject newCFObject(String data, String key) throws IOException {
      CFObject object = getApi().newCFObject();
      object.getInfo().setName(key);
      object.setPayload(data);
      context.utils().encryption().generateMD5BufferingIfNotRepeatable(object);
      object.getInfo().setContentType("text/plain");
      object.getInfo().getMetadata().put("Metadata", "metadata-value");
      return object;
   }

}

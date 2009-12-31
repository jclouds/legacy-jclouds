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
package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.util.SortedSet;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code AzureBlobClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "azureblob.AzureBlobClientLiveTest")
public class AzureBlobClientLiveTest {

   protected AzureBlobClient connection;

   private String containerPrefix = System.getProperty("user.name") + "-azureblob";
   private EncryptionService encryptionService = new JCEEncryptionService();

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      connection = AzureBlobContextFactory.createContext(account, key, new Log4JLoggingModule())
               .getApi();
   }

   @Test
   public void testListContainers() throws Exception {

      SortedSet<ListableContainerProperties> response = connection.listContainers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   String privateContainer;
   String publicContainer;
   String account;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateContainer() throws Exception {
      boolean created = false;
      while (!created) {
         privateContainer = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createContainer(privateContainer, CreateContainerOptions.Builder
                     .withMetadata(ImmutableMultimap.of("foo", "bar")));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      SortedSet<ListableContainerProperties> response = connection.listContainers();
      assert null != response;
      long containerCount = response.size();
      assertTrue(containerCount >= 1);
      ListBlobsResponse list = connection.listBlobs(privateContainer);
      assertEquals(list.getUrl(), URI.create(String.format("https://%s.blob.core.windows.net/%s",
               account, privateContainer)));
      // TODO ... check to see the container actually exists
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicContainer() throws Exception {
      boolean created = false;
      while (!created) {
         publicContainer = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createContainer(publicContainer, CreateContainerOptions.Builder
                     .withPublicAcl());
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }

      URL url = new URL(String.format("http://%s.blob.core.windows.net/%s", account,
               publicContainer));
      Utils.toStringAndClose(url.openStream());
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicRootContainer() throws Exception {
      try {
         connection.deleteRootContainer();
      } catch (AzureStorageResponseException e) {
         sleepIfWaitingForDeleteToFinish(e);
      }
      boolean created = false;
      while (!created) {
         try {
            created = connection.createRootContainer();
         } catch (AzureStorageResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 409) {// TODO look for specific message
               Thread.sleep(5000);
               continue;
            } else {
               throw htpe;
            }
         }
      }
      ListBlobsResponse list = connection.listBlobs();
      assertEquals(list.getUrl(), URI.create(String.format(
               "https://%s.blob.core.windows.net/%%24root", account)));
   }

   private void sleepIfWaitingForDeleteToFinish(AzureStorageResponseException e)
            throws InterruptedException {
      if (e.getResponse().getStatusCode() == 409) {
         Thread.sleep(5000);
      }

   }

   @Test
   public void testListContainersWithOptions() throws Exception {

      BoundedSortedSet<ListableContainerProperties> response = connection
               .listContainers(ListOptions.Builder.prefix(privateContainer).maxResults(1));
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
      assertEquals(privateContainer, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreatePublicRootContainer" })
   public void testDeleteRootContainer() throws Exception {
      assert connection.deleteRootContainer();
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer",
            "testCreatePublicContainer" })
   public void testListOwnedContainers() throws Exception {

      // Test default listing
      SortedSet<ListableContainerProperties> response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail

      // Test listing with options
      response = connection.listContainers(ListOptions.Builder.prefix(
               privateContainer.substring(0, privateContainer.length() - 1)).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.first().getName(), privateContainer);

      response = connection.listContainers(ListOptions.Builder.prefix(publicContainer)
               .maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.first().getName(), publicContainer);

   }

   @Test
   public void testDeleteOneContainer() throws Exception {
      connection.deleteContainer("does-not-exist");
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testListOwnedContainers",
            "testObjectOperations" })
   public void testDeleteContainer() throws Exception {
      connection.deleteContainer(privateContainer);
      connection.deleteContainer(publicContainer);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer",
            "testCreatePublicContainer" })
   public void testObjectOperations() throws Exception {
      String data = "Here is my data";

      // Test PUT with string data, ETag hash, and a piece of metadata
      AzureBlob object = connection.newBlob();
      object.getProperties().setName("object");
      object.setPayload(data);
      object.setContentLength(data.length());
      object.generateMD5();
      object.getProperties().setContentType("text/plain");
      object.getProperties().getMetadata().put("Metadata", "metadata-value");
      byte[] md5 = object.getProperties().getContentMD5();
      String newEtag = connection.putBlob(privateContainer, object);
      assertEquals(encryptionService.toHexString(md5), encryptionService.toHexString(object
               .getProperties().getContentMD5()));

      // Test HEAD of missing object
      try {
         connection.getBlobProperties(privateContainer, "non-existent-object");
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Test HEAD of object
      BlobProperties metadata = connection.getBlobProperties(privateContainer, object
               .getProperties().getName());
      // TODO assertEquals(metadata.getName(), object.getProperties().getName());
      // we can't check this while hacking around lack of content-md5, as GET of the first byte will
      // show incorrect length 1, the returned size, as opposed to the real length. This is an ok
      // tradeoff, as a container list will contain the correct size of the objects in an
      // inexpensive fashion
      // http://code.google.com/p/jclouds/issues/detail?id=92
      // assertEquals(metadata.getSize(), data.length());
      assertEquals(metadata.getContentType(), "text/plain");
      // Azure doesn't return the Content-MD5 on head request...
      assertEquals(encryptionService.toHexString(md5), encryptionService.toHexString(object
               .getProperties().getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getMetadata().entrySet().size(), 1);
      assertEquals(metadata.getMetadata().get("metadata"), "metadata-value");

      // // Test POST to update object's metadata
      // Multimap<String, String> userMetadata = LinkedHashMultimap.create();
      // userMetadata.put("New-Metadata-1", "value-1");
      // userMetadata.put("New-Metadata-2", "value-2");
      // assertTrue(connection.setBlobProperties(privateContainer, object.getProperties().getName(),
      // userMetadata));

      // Test GET of missing object
      try {
         connection.getBlob(privateContainer, "non-existent-object");
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }
      // Test GET of object (including updated metadata)
      AzureBlob getBlob = connection.getBlob(privateContainer, object.getProperties().getName());
      assertEquals(Utils.toStringAndClose(getBlob.getContent()), data);
      // TODO assertEquals(getBlob.getName(), object.getProperties().getName());
      assertEquals(getBlob.getContentLength(), new Long(data.length()));
      assertEquals(getBlob.getProperties().getContentType(), "text/plain");
      assertEquals(encryptionService.toHexString(md5), encryptionService.toHexString(getBlob
               .getProperties().getContentMD5()));
      assertEquals(newEtag, getBlob.getProperties().getETag());
      // wait until we can update metadata
      // assertEquals(getBlob.getProperties().getMetadata().entries().size(), 2);
      // assertEquals(
      // Iterables.getLast(getBlob.getProperties().getMetadata().get("New-Metadata-1")),
      // "value-1");
      // assertEquals(
      // Iterables.getLast(getBlob.getProperties().getMetadata().get("New-Metadata-2")),
      // "value-2");
      assertEquals(metadata.getMetadata().entrySet().size(), 1);
      assertEquals(metadata.getMetadata().get("metadata"), "metadata-value");

      // Test PUT with invalid ETag (as if object's data was corrupted in transit)
      String correctEtag = newEtag;
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getProperties().setETag(incorrectEtag);
      try {
         connection.putBlob(privateContainer, object);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
      object = connection.newBlob();
      object.getProperties().setName("chunked-object");
      object.setPayload(bais);
      object.setContentLength(new Long(data.getBytes().length));
      newEtag = connection.putBlob(privateContainer, object);
      assertEquals(encryptionService.toHexString(md5), encryptionService.toHexString(getBlob
               .getProperties().getContentMD5()));

      // Test GET with options
      // Non-matching ETag
      try {
         connection.getBlob(privateContainer, object.getProperties().getName(), GetOptions.Builder
                  .ifETagDoesntMatch(newEtag));
      } catch (Exception e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 304);
      }

      // Matching ETag
      getBlob = connection.getBlob(privateContainer, object.getProperties().getName(),
               GetOptions.Builder.ifETagMatches(newEtag));
      assertEquals(getBlob.getProperties().getETag(), newEtag);

      // Range
      // doesn't work per
      // http://social.msdn.microsoft.com/Forums/en-US/windowsazure/thread/479fa63f-51df-4b66-96b5-33ae362747b6
      // getBlob = connection
      // .getBlob(privateContainer, object.getProperties().getName(),
      // GetOptions.Builder.startAt(8)).get(120,
      // TimeUnit.SECONDS);
      // assertEquals(Utils.toStringAndClose((InputStream) getBlob.getData()), data.substring(8));

      connection.deleteBlob(privateContainer, "object");
      connection.deleteBlob(privateContainer, "chunked-object");
   }
}

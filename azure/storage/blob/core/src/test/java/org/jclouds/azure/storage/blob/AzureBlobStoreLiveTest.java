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
package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code AzureBlobConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "cloudservers.AzureBlobStoreLiveTest")
public class AzureBlobStoreLiveTest {

   protected AzureBlobStore connection;

   private String containerPrefix = System.getProperty("user.name") + "-azureblob";

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      connection = AzureBlobContextFactory.createAzureBlobContext(account, key,
               new Log4JLoggingModule()).getApi();
   }

   @Test
   public void testListContainers() throws Exception {

      List<ContainerMetadata> response = connection.listContainers();
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
            created = connection.createContainer(
                     privateContainer,
                     CreateContainerOptions.Builder
                              .withMetadata(ImmutableMultimap.of("foo", "bar"))).get(10,
                     TimeUnit.SECONDS);
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      List<ContainerMetadata> response = connection.listContainers();
      assert null != response;
      long containerCount = response.size();
      assertTrue(containerCount >= 1);
      ListBlobsResponse list = connection.listBlobs(privateContainer).get(10, TimeUnit.SECONDS);
      assertEquals(list.getContainerUrl(), URI.create(String.format(
               "https://%s.blob.core.windows.net/%s", account, privateContainer)));
      // TODO ... check to see the container actually exists
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicContainer() throws Exception {
      boolean created = false;
      while (!created) {
         publicContainer = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createContainer(publicContainer,
                     CreateContainerOptions.Builder.withPublicAcl()).get(10, TimeUnit.SECONDS);
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
         connection.deleteRootContainer().get(10, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         sleepIfWaitingForDeleteToFinish(e);
      }
      boolean created = false;
      while (!created) {
         try {
            created = connection.createRootContainer().get(10, TimeUnit.SECONDS);
         } catch (ExecutionException e) {
            AzureStorageResponseException htpe = (AzureStorageResponseException) e.getCause()
                     .getCause();
            if (htpe.getResponse().getStatusCode() == 409) {// TODO look for specific message
               Thread.sleep(5000);
               continue;
            }
            throw e;
         }
      }
      ListBlobsResponse list = connection.listBlobs().get(10, TimeUnit.SECONDS);
      assertEquals(list.getContainerUrl(), URI.create(String.format(
               "https://%s.blob.core.windows.net/%%24root", account)));
   }

   private void sleepIfWaitingForDeleteToFinish(ExecutionException e) throws InterruptedException {
      if (e.getCause() instanceof AzureStorageResponseException) {
         if (((AzureStorageResponseException) e.getCause()).getResponse().getStatusCode() == 409) {
            Thread.sleep(5000);
         }
      }
   }

   @Test
   public void testListContainersWithOptions() throws Exception {

      BoundedList<ContainerMetadata> response = connection.listContainers(ListOptions.Builder
               .prefix(privateContainer).maxResults(1));
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
      assertEquals(privateContainer, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreatePublicRootContainer" })
   public void testDeleteRootContainer() throws Exception {
      assert connection.deleteRootContainer().get(10, TimeUnit.SECONDS);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer",
            "testCreatePublicContainer" })
   public void testListOwnedContainers() throws Exception {

      // Test default listing
      List<ContainerMetadata> response = connection.listContainers();
      // assertEquals(response.size(), initialContainerCount + 2);// if the containers already
      // exist, this will fail

      // Test listing with options
      response = connection.listContainers(ListOptions.Builder.prefix(
               privateContainer.substring(0, privateContainer.length() - 1)).maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), privateContainer);

      response = connection.listContainers(ListOptions.Builder.prefix(publicContainer)
               .maxResults(1));
      assertEquals(response.size(), 1);
      assertEquals(response.get(0).getName(), publicContainer);

   }

   @Test
   public void testDeleteOneContainer() throws Exception {
      assertTrue(connection.deleteContainer("does-not-exist").get(10, TimeUnit.SECONDS));
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testListOwnedContainers",
            "testObjectOperations" })
   public void testDeleteContainer() throws Exception {
      assert connection.deleteContainer(privateContainer).get(10, TimeUnit.SECONDS);
      assert connection.deleteContainer(publicContainer).get(10, TimeUnit.SECONDS);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer",
            "testCreatePublicContainer" })
   public void testObjectOperations() throws Exception {
      String data = "Here is my data";

      // Test PUT with string data, ETag hash, and a piece of metadata
      Blob object = new Blob("object");
      object.setData(data);
      object.setContentLength(data.length());
      object.generateMD5();
      object.getMetadata().setContentType("text/plain");
      object.getMetadata().getUserMetadata().put("Metadata", "metadata-value");
      byte[] md5 = object.getMetadata().getContentMD5();
      byte[] newEtag = connection.putBlob(privateContainer, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));

      // Test HEAD of missing object
      try {
         connection.blobMetadata(privateContainer, "non-existent-object");
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Test HEAD of object
      BlobMetadata metadata = connection.blobMetadata(privateContainer, object.getKey());
      // TODO assertEquals(metadata.getKey(), object.getKey());
      // we can't check this while hacking around lack of content-md5, as GET of the first byte will
      // show incorrect length 1, the returned size, as opposed to the real length. This is an ok
      // tradeoff, as a container list will contain the correct size of the objects in an
      // inexpensive fashion
      // http://code.google.com/p/jclouds/issues/detail?id=92
      // assertEquals(metadata.getSize(), data.length());
      assertEquals(metadata.getContentType(), "text/plain");
      // Azure doesn't return the Content-MD5 on head request...
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(object.getMetadata()
               .getContentMD5()));
      assertEquals(metadata.getETag(), newEtag);
      assertEquals(metadata.getUserMetadata().entries().size(), 1);
      assertEquals(Iterables.getLast(metadata.getUserMetadata().get("metadata")), "metadata-value");

      // // Test POST to update object's metadata
      // Multimap<String, String> userMetadata = HashMultimap.create();
      // userMetadata.put("New-Metadata-1", "value-1");
      // userMetadata.put("New-Metadata-2", "value-2");
      // assertTrue(connection.setObjectMetadata(privateContainer, object.getKey(), userMetadata));

      // Test GET of missing object
      try {
         connection.getBlob(privateContainer, "non-existent-object").get(10, TimeUnit.SECONDS);
         assert false;
      } catch (Exception e) {
         e.printStackTrace();
      }
      // Test GET of object (including updated metadata)
      Blob getBlob = connection.getBlob(privateContainer, object.getKey()).get(120,
               TimeUnit.SECONDS);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data);
      // TODO assertEquals(getBlob.getKey(), object.getKey());
      assertEquals(getBlob.getContentLength(), data.length());
      assertEquals(getBlob.getMetadata().getContentType(), "text/plain");
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));
      assertEquals(newEtag, getBlob.getMetadata().getETag());
      // wait until we can update metadata
      // assertEquals(getBlob.getMetadata().getUserMetadata().entries().size(), 2);
      // assertEquals(
      // Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("New-Metadata-1")),
      // "value-1");
      // assertEquals(
      // Iterables.getLast(getBlob.getMetadata().getUserMetadata().get("New-Metadata-2")),
      // "value-2");
      assertEquals(metadata.getUserMetadata().entries().size(), 1);
      assertEquals(Iterables.getLast(metadata.getUserMetadata().get("metadata")), "metadata-value");

      // Test PUT with invalid ETag (as if object's data was corrupted in transit)
      String correctEtag = HttpUtils.toHexString(newEtag);
      String incorrectEtag = "0" + correctEtag.substring(1);
      object.getMetadata().setETag(HttpUtils.fromHexString(incorrectEtag));
      try {
         connection.putBlob(privateContainer, object).get(10, TimeUnit.SECONDS);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      // Test PUT chunked/streamed upload with data of "unknown" length
      ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
      object = new Blob("chunked-object");
      object.setData(bais);
      newEtag = connection.putBlob(privateContainer, object).get(10, TimeUnit.SECONDS);
      assertEquals(HttpUtils.toHexString(md5), HttpUtils.toHexString(getBlob.getMetadata()
               .getContentMD5()));

      // Test GET with options
      // Non-matching ETag
      try {
         connection.getBlob(privateContainer, object.getKey(),
                  GetOptions.Builder.ifETagDoesntMatch(newEtag)).get(120, TimeUnit.SECONDS);
      } catch (Exception e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 304);
      }

      // Matching ETag
      getBlob = connection.getBlob(privateContainer, object.getKey(),
               GetOptions.Builder.ifETagMatches(newEtag)).get(120, TimeUnit.SECONDS);
      assertEquals(getBlob.getMetadata().getETag(), newEtag);

      // Range
      // doesn't work per
      // http://social.msdn.microsoft.com/Forums/en-US/windowsazure/thread/479fa63f-51df-4b66-96b5-33ae362747b6
      // getBlob = connection
      // .getBlob(privateContainer, object.getKey(), GetOptions.Builder.startAt(8)).get(120,
      // TimeUnit.SECONDS);
      // assertEquals(IOUtils.toString((InputStream) getBlob.getData()), data.substring(8));

      assertTrue(connection.removeBlob(privateContainer, "object").get(10, TimeUnit.SECONDS));
      assertTrue(connection.removeBlob(privateContainer, "chunked-object")
               .get(10, TimeUnit.SECONDS));
   }

}

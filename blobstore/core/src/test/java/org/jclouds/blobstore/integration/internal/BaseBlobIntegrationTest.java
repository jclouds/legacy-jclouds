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
package org.jclouds.blobstore.integration.internal;

import static org.jclouds.http.options.GetOptions.Builder.ifETagDoesntMatch;
import static org.jclouds.http.options.GetOptions.Builder.ifETagMatches;
import static org.jclouds.http.options.GetOptions.Builder.ifModifiedSince;
import static org.jclouds.http.options.GetOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.http.options.GetOptions.Builder.range;
import static org.jclouds.http.options.GetOptions.Builder.startAt;
import static org.jclouds.http.options.GetOptions.Builder.tail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
public class BaseBlobIntegrationTest<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends BaseBlobStoreIntegrationTest<S, C, M, B> {

   @Test(groups = { "integration", "live" })
   public void testGetIfModifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {
         String key = "apples";

         DateTime before = new DateTime().minusSeconds(1);
         // first create the object
         addObjectAndValidateContent(containerName, key);
         // now, modify it
         addObjectAndValidateContent(containerName, key);
         DateTime after = new DateTime().plusSeconds(1);

         context.getApi().getBlob(containerName, key, ifModifiedSince(before)).get(10,
                  TimeUnit.SECONDS);
         validateContent(containerName, key);

         try {
            context.getApi().getBlob(containerName, key, ifModifiedSince(after)).get(10,
                     TimeUnit.SECONDS);
            validateContent(containerName, key);
         } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               assertEquals(ex.getResponse().getStatusCode(), 304);
            } else if (e.getCause() instanceof RuntimeException) {
               // TODO enhance stub connection so that it throws the correct error
            } else {
               throw e;
            }
         }
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testGetIfUnmodifiedSince() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         DateTime before = new DateTime().minusSeconds(1);
         addObjectAndValidateContent(containerName, key);
         DateTime after = new DateTime().plusSeconds(1);

         context.getApi().getBlob(containerName, key, ifUnmodifiedSince(after)).get(10,
                  TimeUnit.SECONDS);
         validateContent(containerName, key);

         try {
            context.getApi().getBlob(containerName, key, ifUnmodifiedSince(before)).get(10,
                     TimeUnit.SECONDS);
            validateContent(containerName, key);
         } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               assertEquals(ex.getResponse().getStatusCode(), 412);
            } else if (e.getCause() instanceof RuntimeException) {
               // TODO enhance stub connection so that it throws the correct error
            } else {
               throw e;
            }
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfMatch() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);

         context.getApi().getBlob(containerName, key, ifETagMatches(goodETag)).get(10,
                  TimeUnit.SECONDS);
         validateContent(containerName, key);

         try {
            context.getApi().getBlob(containerName, key, ifETagMatches(badETag)).get(10,
                     TimeUnit.SECONDS);
            validateContent(containerName, key);
         } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               assertEquals(ex.getResponse().getStatusCode(), 412);
            } else if (e.getCause() instanceof RuntimeException) {
               // TODO enhance stub connection so that it throws the correct error
            } else {
               throw e;
            }
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfNoneMatch() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);

         context.getApi().getBlob(containerName, key, ifETagDoesntMatch(badETag)).get(10,
                  TimeUnit.SECONDS);
         validateContent(containerName, key);

         try {
            context.getApi().getBlob(containerName, key, ifETagDoesntMatch(goodETag)).get(10,
                     TimeUnit.SECONDS);
            validateContent(containerName, key);
         } catch (ExecutionException e) {
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               assertEquals(ex.getResponse().getStatusCode(), 304);
            } else {
               throw e;
            }
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetRange() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         B object1 = context.getApi().getBlob(containerName, key, range(0, 5)).get(10,
                  TimeUnit.SECONDS);
         assertEquals(BlobStoreUtils.getContentAsStringAndClose(object1), TEST_STRING.substring(0,
                  6));

         B object2 = context.getApi().getBlob(containerName, key, range(6, TEST_STRING.length()))
                  .get(10, TimeUnit.SECONDS);
         assertEquals(BlobStoreUtils.getContentAsStringAndClose(object2), TEST_STRING.substring(6,
                  TEST_STRING.length()));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetTwoRanges() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         B object = context.getApi().getBlob(containerName, key,
                  range(0, 5).range(6, TEST_STRING.length())).get(10, TimeUnit.SECONDS);

         assertEquals(BlobStoreUtils.getContentAsStringAndClose(object), TEST_STRING);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetTail() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         B object = context.getApi().getBlob(containerName, key, tail(5)).get(10, TimeUnit.SECONDS);
         assertEquals(BlobStoreUtils.getContentAsStringAndClose(object), TEST_STRING
                  .substring(TEST_STRING.length() - 5));
         assertEquals(object.getContentLength(), 5);
         assertEquals(object.getMetadata().getSize(), TEST_STRING.length());
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetStartAt() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String containerName = getContainerName();
      try {
         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         B object = context.getApi().getBlob(containerName, key, startAt(5)).get(10,
                  TimeUnit.SECONDS);
         assertEquals(BlobStoreUtils.getContentAsStringAndClose(object), TEST_STRING.substring(5,
                  TEST_STRING.length()));
         assertEquals(object.getContentLength(), TEST_STRING.length() - 5);
         assertEquals(object.getMetadata().getSize(), TEST_STRING.length());
      } finally {
         returnContainer(containerName);
      }
   }

   private void addObjectAndValidateContent(String sourcecontainerName, String sourceKey)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      addBlobToContainer(sourcecontainerName, sourceKey);
      validateContent(sourcecontainerName, sourceKey);
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNotFound() throws Exception {
      String containerName = getContainerName();
      String key = "test";
      try {
         assert context.getApi().removeBlob(containerName, key).get(10, TimeUnit.SECONDS);
      } finally {
         returnContainer(containerName);
      }
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "sp ace" }, { "qu?stion" }, { "unic¿de" }, { "path/" } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "delete")
   public void deleteObject(String key) throws Exception {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, key);
         assert context.getApi().removeBlob(containerName, key).get(10, TimeUnit.SECONDS);
         assertContainerEmptyDeleting(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   private void assertContainerEmptyDeleting(String containerName, String key)
            throws InterruptedException, ExecutionException, TimeoutException {
      List<M> listing = context.getApi().listBlobs(containerName).get(10, TimeUnit.SECONDS);
      assertEquals(listing.size(), 0, String.format(
               "deleting %s, we still have %s left in container %s, using encoding %s", key,
               listing.size(), containerName, LOCAL_ENCODING));
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNoContainer() throws Exception {
      try {
         context.getApi().removeBlob("donb", "test").get(10, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assert (e.getCause() instanceof HttpResponseException || e.getCause() instanceof ContainerNotFoundException);
         if (e.getCause() instanceof HttpResponseException)
            assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 404);
      }
   }

   @DataProvider(name = "putTests")
   public Object[][] createData1() throws IOException {

      String realObject = IOUtils.toString(new FileInputStream("pom.xml"));

      return new Object[][] { { "file", "text/xml", new File("pom.xml"), realObject },
               { "string", "text/xml", realObject, realObject },
               { "bytes", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "putTests")
   public void testPutObject(String key, String type, Object content, Object realObject)
            throws Exception {
      B object = context.newBlob(key);
      object.getMetadata().setContentType(type);
      object.setData(content);
      if (content instanceof InputStream) {
         object.generateMD5();
      }
      String containerName = getContainerName();
      try {
         assertNotNull(context.getApi().putBlob(containerName, object).get(10, TimeUnit.SECONDS));
         object = context.getApi().getBlob(containerName, object.getKey())
                  .get(10, TimeUnit.SECONDS);
         String returnedString = BlobStoreUtils.getContentAsStringAndClose(object);
         assertEquals(returnedString, realObject);
         assertEquals(context.getApi().listBlobs(containerName).get(10, TimeUnit.SECONDS).size(), 1);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMetadata() throws Exception {
      String key = "hello";

      B object = context.newBlob(key);
      object.setData(TEST_STRING);
      object.getMetadata().setContentType("text/plain");
      object.getMetadata().setSize(TEST_STRING.length());
      // NOTE all metadata in jclouds comes out as lowercase, in an effort to normalize the
      // providers.
      object.getMetadata().getUserMetadata().put("Adrian", "powderpuff");
      object.getMetadata().setContentMD5(HttpUtils.md5(TEST_STRING.getBytes()));
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, object);
         B newObject = validateContent(containerName, key);

         M metadata = newObject.getMetadata();

         validateMetadata(metadata);
         validateMetadata(context.getApi().blobMetadata(containerName, key));
      } finally {
         returnContainer(containerName);
      }
   }

   protected void validateMetadata(M metadata) {
      assertEquals(metadata.getContentType(), "text/plain");
      assertEquals(metadata.getSize(), TEST_STRING.length());
      assertEquals(metadata.getUserMetadata().get("adrian"), Collections
               .singletonList("powderpuff"));
      assertEquals(metadata.getContentMD5(), HttpUtils.md5(TEST_STRING.getBytes()));
   }

}
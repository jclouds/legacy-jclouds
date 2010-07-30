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
package org.jclouds.blobstore.integration.internal;

import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagDoesntMatch;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagMatches;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifModifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.jclouds.blobstore.util.BlobStoreUtils.getContentAsStringOrNullAndClose;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import static org.jclouds.concurrent.ConcurrentUtils.*;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * @author Adrian Cole
 */
public class BaseBlobIntegrationTest extends BaseBlobStoreIntegrationTest {
   private byte[] oneHundredOneConstitutions;
   private byte[] oneHundredOneConstitutionsMD5;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      Payload result = context.utils().encryption().generatePayloadWithMD5For(getTestDataSupplier().getInput());
      oneHundredOneConstitutions = (byte[]) result.getRawContent();
      oneHundredOneConstitutionsMD5 = result.getContentMD5();
   }

   @SuppressWarnings("unchecked")
   public static InputSupplier<InputStream> getTestDataSupplier() throws IOException {
      byte[] oneConstitution = ByteStreams.toByteArray(new GZIPInputStream(BaseJettyTest.class
               .getResourceAsStream("/const.txt.gz")));
      InputSupplier<ByteArrayInputStream> constitutionSupplier = ByteStreams.newInputStreamSupplier(oneConstitution);

      InputSupplier<InputStream> temp = ByteStreams.join(constitutionSupplier);

      for (int i = 0; i < 100; i++) {
         temp = ByteStreams.join(temp, constitutionSupplier);
      }
      return temp;
   }

   @Test(groups = { "integration", "live" })
   public void testBigFileGets() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         String key = "constitution.txt";

         uploadConstitution(containerName, key);
         Map<Integer, Future<?>> responses = Maps.newHashMap();
         for (int i = 0; i < 10; i++) {

            responses.put(i, compose(context.getAsyncBlobStore().getBlob(containerName, key),
                     new Function<Blob, Void>() {

                        @Override
                        public Void apply(Blob from) {
                           assertEquals(context.utils().encryption().md5(from.getPayload().getInput()),
                                    oneHundredOneConstitutionsMD5);
                           return null;
                        }

                     }, this.exec));
         }
         Map<Integer, Exception> exceptions = awaitCompletion(responses, exec, 30000l, Logger.CONSOLE,
                  "get constitution");
         assert exceptions.size() == 0 : exceptions;

      } finally {
         returnContainer(containerName);
      }

   }

   private void uploadConstitution(String containerName, String key) throws IOException {
      Blob sourceObject = context.getBlobStore().newBlob(key);
      sourceObject.getMetadata().setContentType("text/plain");
      sourceObject.getMetadata().setContentMD5(oneHundredOneConstitutionsMD5);
      sourceObject.setPayload(oneHundredOneConstitutions);
      context.getBlobStore().putBlob(containerName, sourceObject);
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfModifiedSince() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String key = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         // first create the blob
         addObjectAndValidateContent(containerName, key);
         // now, modify it
         addObjectAndValidateContent(containerName, key);
         Date after = new Date(System.currentTimeMillis() + 1000);

         context.getBlobStore().getBlob(containerName, key, ifModifiedSince(before));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifModifiedSince(after));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testGetIfUnmodifiedSince() throws InterruptedException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         addObjectAndValidateContent(containerName, key);
         Date after = new Date(System.currentTimeMillis() + 1000);

         context.getBlobStore().getBlob(containerName, key, ifUnmodifiedSince(after));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifUnmodifiedSince(before));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfMatch() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         String goodETag = addObjectAndValidateContent(containerName, key);

         context.getBlobStore().getBlob(containerName, key, ifETagMatches(goodETag));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifETagMatches("powerfrisbee"));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfNoneMatch() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         String goodETag = addObjectAndValidateContent(containerName, key);

         context.getBlobStore().getBlob(containerName, key, ifETagDoesntMatch("powerfrisbee"));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifETagDoesntMatch(goodETag));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetRange() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         Blob blob1 = context.getBlobStore().getBlob(containerName, key, range(0, 5));
         assertEquals(getContentAsStringOrNullAndClose(blob1), TEST_STRING.substring(0, 6));

         Blob blob2 = context.getBlobStore().getBlob(containerName, key, range(6, TEST_STRING.length()));
         assertEquals(getContentAsStringOrNullAndClose(blob2), TEST_STRING.substring(6, TEST_STRING.length()));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetTwoRanges() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         Blob blob = context.getBlobStore().getBlob(containerName, key, range(0, 5).range(6, TEST_STRING.length()));

         assertEquals(getContentAsStringOrNullAndClose(blob), TEST_STRING);
      } finally {
         returnContainer(containerName);
      }
   }

   // @Test(groups = { "integration", "live" })
   // public void testGetTail() throws InterruptedException, ExecutionException,
   // TimeoutException,
   // IOException {
   // String containerName = getContainerName();
   // try {
   //
   // String key = "apples";
   //
   // addObjectAndValidateContent(containerName, key);
   // Blob blob = context.getBlobStore().getBlob(containerName, key,
   // tail(5)).get(30,
   // TimeUnit.SECONDS);
   // assertEquals(BlobStoreUtils.getContentAsStringAndClose(blob), TEST_STRING
   // .substring(TEST_STRING.length() - 5));
   // assertEquals(blob.getContentLength(), 5);
   // assertEquals(blob.getMetadata().getSize(), TEST_STRING.length());
   // } finally {
   // returnContainer(containerName);
   // }
   // }

   // @Test(groups = { "integration", "live" })
   // public void testGetStartAt() throws InterruptedException,
   // ExecutionException,
   // TimeoutException,
   // IOException {
   // String containerName = getContainerName();
   // try {
   // String key = "apples";
   //
   // addObjectAndValidateContent(containerName, key);
   // Blob blob = context.getBlobStore().getBlob(containerName, key,
   // startAt(5)).get(30,
   // TimeUnit.SECONDS);
   // assertEquals(BlobStoreUtils.getContentAsStringAndClose(blob),
   // TEST_STRING.substring(5,
   // TEST_STRING.length()));
   // assertEquals(blob.getContentLength(), TEST_STRING.length() - 5);
   // assertEquals(blob.getMetadata().getSize(), TEST_STRING.length());
   // } finally {
   // returnContainer(containerName);
   // }
   // }

   private String addObjectAndValidateContent(String sourcecontainerName, String sourceKey) throws InterruptedException {
      String eTag = addBlobToContainer(sourcecontainerName, sourceKey);
      validateContent(sourcecontainerName, sourceKey);
      return eTag;
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNotFound() throws InterruptedException {
      String containerName = getContainerName();
      String key = "test";
      try {
         context.getBlobStore().removeBlob(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void blobNotFound() throws InterruptedException {
      String containerName = getContainerName();
      String key = "test";
      try {
         assert !context.getBlobStore().blobExists(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" }, { "path/foo" }, { "colon:" },
               { "asteri*k" }, { "quote\"" }, { "{great<r}" }, { "lesst>en" }, { "p|pe" } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "delete")
   public void deleteObject(String key) throws InterruptedException {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, key);
         context.getBlobStore().removeBlob(containerName, key);
         assertContainerEmptyDeleting(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   private void assertContainerEmptyDeleting(String containerName, String key) {
      Iterable<? extends StorageMetadata> listing = Iterables.filter(context.getBlobStore().list(containerName),
               new Predicate<StorageMetadata>() {

                  @Override
                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.BLOB;
                  }

               });
      assertEquals(Iterables.size(listing), 0, String.format(
               "deleting %s, we still have %s blobs left in container %s, using encoding %s", key, Iterables
                        .size(listing), containerName, LOCAL_ENCODING));
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNoContainer() {
      try {
         context.getBlobStore().removeBlob("donb", "test");
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 404);
      } catch (ContainerNotFoundException e) {
      }

   }

   @DataProvider(name = "putTests")
   public Object[][] createData1() throws IOException {

      String realObject = Utils.toStringAndClose(new FileInputStream("pom.xml"));

      return new Object[][] { { "file", "text/xml", new File("pom.xml"), realObject },
               { "string", "text/xml", realObject, realObject },
               { "bytes", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "putTests")
   public void testPutObject(String key, String type, Object content, Object realObject) throws InterruptedException,
            IOException {
      Blob blob = context.getBlobStore().newBlob(key);
      blob.getMetadata().setContentType(type);
      blob.setPayload(Payloads.newPayload(content));
      if (content instanceof InputStream) {
         context.utils().encryption().generateMD5BufferingIfNotRepeatable(blob);
      }
      String containerName = getContainerName();
      try {
         assertNotNull(context.getBlobStore().putBlob(containerName, blob));
         blob = context.getBlobStore().getBlob(containerName, blob.getMetadata().getName());
         String returnedString = getContentAsStringOrNullAndClose(blob);
         assertEquals(returnedString, realObject);
         PageSet<? extends StorageMetadata> set = context.getBlobStore().list(containerName);
         assert set.size() == 1 : set;
      } finally {
         returnContainer(containerName);
      }
   }

   protected volatile static EncryptionService encryptionService;
   static {
      try {
         encryptionService = new JCEEncryptionService();
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMetadata() throws InterruptedException {
      String key = "hello";

      Blob blob = context.getBlobStore().newBlob(key);
      blob.setPayload(TEST_STRING);
      blob.getMetadata().setContentType(MediaType.TEXT_PLAIN);
      blob.getMetadata().setSize(new Long(TEST_STRING.length()));
      // NOTE all metadata in jclouds comes out as lowercase, in an effort to
      // normalize the
      // providers.
      blob.getMetadata().getUserMetadata().put("Adrian", "powderpuff");
      blob.getMetadata().setContentMD5(encryptionService.md5(Utils.toInputStream(TEST_STRING)));
      String containerName = getContainerName();
      try {
         assertNull(context.getBlobStore().blobMetadata(containerName, "powderpuff"));

         addBlobToContainer(containerName, blob);
         Blob newObject = validateContent(containerName, key);

         BlobMetadata metadata = newObject.getMetadata();

         validateMetadata(metadata);
         validateMetadata(context.getBlobStore().blobMetadata(containerName, key));

         // write 2 items with the same key to ensure that provider doesn't
         // accept dupes
         blob.getMetadata().getUserMetadata().put("Adrian", "wonderpuff");
         blob.getMetadata().getUserMetadata().put("Adrian", "powderpuff");

         addBlobToContainer(containerName, blob);
         validateMetadata(context.getBlobStore().blobMetadata(containerName, key));

      } finally {
         returnContainer(containerName);
      }
   }

   protected void validateMetadata(BlobMetadata metadata) {
      assert metadata.getContentType().startsWith("text/plain") : metadata.getContentType();
      assertEquals(metadata.getSize(), new Long(TEST_STRING.length()));
      assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
      assertEquals(metadata.getContentMD5(), encryptionService.md5(Utils.toInputStream(TEST_STRING)));
   }

}
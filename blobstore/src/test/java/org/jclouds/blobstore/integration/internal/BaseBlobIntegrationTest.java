/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagDoesntMatch;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagMatches;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifModifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.jclouds.io.ByteSources.asByteSource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.crypto.Crypto;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.WriteTo;
import org.jclouds.io.payloads.StreamingPayload;
import org.jclouds.logging.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
public class BaseBlobIntegrationTest extends BaseBlobStoreIntegrationTest {
   private InputSupplier<InputStream> oneHundredOneConstitutions;
   private byte[] oneHundredOneConstitutionsMD5;
   private static long oneHundredOneConstitutionsLength;

   @BeforeClass(groups = { "integration", "live" }, dependsOnMethods = "setupContext")
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      oneHundredOneConstitutions = getTestDataSupplier();
      oneHundredOneConstitutionsMD5 = md5Supplier(oneHundredOneConstitutions);
   }

   protected static byte[] md5Supplier(InputSupplier<InputStream> supplier) throws IOException {
      return asByteSource(supplier.getInput()).hash(md5()).asBytes();
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
      oneHundredOneConstitutionsLength = oneConstitution.length * 101l;
      return temp;
   }

   public static long getOneHundredOneConstitutionsLength() throws IOException {
      if (oneHundredOneConstitutionsLength == 0) {
         getTestDataSupplier();
      }
      return oneHundredOneConstitutionsLength;
   }

   /**
    * Attempt to capture the issue detailed in
    * http://groups.google.com/group/jclouds/browse_thread/thread/4a7c8d58530b287f
    */
   @Test(groups = { "integration", "live" })
   public void testPutFileParallel() throws InterruptedException, IOException, TimeoutException {

      File payloadFile = File.createTempFile("testPutFileParallel", "png");
      Files.copy(createTestInput(), payloadFile);
      payloadFile.deleteOnExit();
      
      final Payload testPayload = Payloads.newFilePayload(payloadFile);
      final byte[] md5 = md5Supplier(testPayload);
      testPayload.getContentMetadata().setContentType("image/png");
      
      final AtomicInteger blobCount = new AtomicInteger();
      final String container = getContainerName();
      try {
         Map<Integer, ListenableFuture<?>> responses = Maps.newHashMap();
         for (int i = 0; i < 10; i++) {

            responses.put(i, this.exec.submit(new Callable<Void>() {

               @Override
               public Void call() throws Exception {
                  String name = blobCount.incrementAndGet() + "";
                  Blob blob = view.getBlobStore().blobBuilder(name).payload(testPayload).build();
                  view.getBlobStore().putBlob(container, blob);
                  assertConsistencyAwareBlobExists(container, name);
                  blob = view.getBlobStore().getBlob(container, name);

                  assert Arrays.equals(md5Supplier(blob.getPayload()), md5) : String.format(
                           "md5 didn't match on %s/%s", container, name);

                  view.getBlobStore().removeBlob(container, name);
                  assertConsistencyAwareBlobDoesntExist(container, name);
                  return null;
               }

            }));
         }
         Map<Integer, Exception> exceptions = awaitCompletion(responses, exec, 30000l, Logger.CONSOLE,
                  "putFileParallel");
         assert exceptions.size() == 0 : exceptions;

      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testBigFileGets() throws InterruptedException, IOException, TimeoutException {
      final String expectedContentDisposition = "attachment; filename=constit.txt";
      final String container = getContainerName();
      try {
         final String name = "constitution.txt";

         uploadConstitution(container, name, expectedContentDisposition);
         Map<Integer, ListenableFuture<?>> responses = Maps.newHashMap();
         for (int i = 0; i < 10; i++) {

            responses.put(i, Futures.transform(view.getAsyncBlobStore().getBlob(container, name),
                     new Function<Blob, Void>() {

                        @Override
                        public Void apply(Blob from) {
                           try {
                              validateMetadata(from.getMetadata(), container, name);
                              assertEquals(md5Supplier(from.getPayload()), oneHundredOneConstitutionsMD5);
                              checkContentDisposition(from, expectedContentDisposition);
                           } catch (IOException e) {
                              Throwables.propagate(e);
                           }
                           return null;
                        }

                     }, this.exec));
         }
         Map<Integer, Exception> exceptions = awaitCompletion(responses, exec, 30000l, Logger.CONSOLE,
                  "get constitution");
         assert exceptions.size() == 0 : exceptions;

      } finally {
         returnContainer(container);
      }

   }

   private void uploadConstitution(String container, String name, String contentDisposition) throws IOException {
      view.getBlobStore().putBlob(
               container,
               view.getBlobStore().blobBuilder(name).payload(oneHundredOneConstitutions.getInput()).contentType(
                        "text/plain").contentMD5(oneHundredOneConstitutionsMD5).contentLength(
                        oneHundredOneConstitutionsLength).contentDisposition(contentDisposition).build());
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfModifiedSince() throws InterruptedException {
      String container = getContainerName();
      try {
         String name = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         // first create the blob
         addObjectAndValidateContent(container, name);
         // now, modify it
         addObjectAndValidateContent(container, name);
         Date after = new Date(System.currentTimeMillis() + 1000);

         view.getBlobStore().getBlob(container, name, ifModifiedSince(before));
         validateContent(container, name);

         try {
            view.getBlobStore().getBlob(container, name, ifModifiedSince(after));
            validateContent(container, name);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(container);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testCreateBlobWithExpiry() throws InterruptedException {
      String container = getContainerName();
      BlobStore blobStore = view.getBlobStore();
      try {
         final String blobName = "hello";
         final Date expires = new Date((System.currentTimeMillis() / 1000) * 1000 + 60 * 1000);

         blobStore.putBlob(container, blobStore.blobBuilder(blobName).payload(TEST_STRING).expires(expires).build());

         assertConsistencyAwareBlobExpiryMetadata(container, blobName, expires);

      } finally {
         returnContainer(container);
      }
   }
   
   @Test(groups = { "integration", "live" })
   public void testGetIfUnmodifiedSince() throws InterruptedException {
      String container = getContainerName();
      try {

         String name = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         addObjectAndValidateContent(container, name);
         Date after = new Date(System.currentTimeMillis() + 1000);

         view.getBlobStore().getBlob(container, name, ifUnmodifiedSince(after));
         validateContent(container, name);

         try {
            view.getBlobStore().getBlob(container, name, ifUnmodifiedSince(before));
            validateContent(container, name);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfMatch() throws InterruptedException {
      String container = getContainerName();
      try {

         String name = "apples";

         String goodETag = addObjectAndValidateContent(container, name);

         view.getBlobStore().getBlob(container, name, ifETagMatches(goodETag));
         validateContent(container, name);

         try {
            view.getBlobStore().getBlob(container, name, ifETagMatches("powerfrisbee"));
            validateContent(container, name);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfNoneMatch() throws InterruptedException {
      String container = getContainerName();
      try {

         String name = "apples";

         String goodETag = addObjectAndValidateContent(container, name);

         view.getBlobStore().getBlob(container, name, ifETagDoesntMatch("powerfrisbee"));
         validateContent(container, name);

         try {
            view.getBlobStore().getBlob(container, name, ifETagDoesntMatch(goodETag));
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetRange() throws InterruptedException, IOException {
      String container = getContainerName();
      try {

         String name = "apples";

         addObjectAndValidateContent(container, name);
         Blob blob1 = view.getBlobStore().getBlob(container, name, range(0, 5));
         validateMetadata(blob1.getMetadata(), container, name);
         assertEquals(getContentAsStringOrNullAndClose(blob1), TEST_STRING.substring(0, 6));

         Blob blob2 = view.getBlobStore().getBlob(container, name, range(6, TEST_STRING.length()));
         validateMetadata(blob2.getMetadata(), container, name);
         assertEquals(getContentAsStringOrNullAndClose(blob2), TEST_STRING.substring(6, TEST_STRING.length()));
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetTwoRanges() throws InterruptedException, IOException {
      String container = getContainerName();
      try {

         String name = "apples";

         addObjectAndValidateContent(container, name);
         Blob blob = view.getBlobStore().getBlob(container, name, range(0, 5).range(6, TEST_STRING.length()));
         validateMetadata(blob.getMetadata(), container, name);
         assertEquals(getContentAsStringOrNullAndClose(blob), TEST_STRING);
      } finally {
         returnContainer(container);
      }
   }

   private String addObjectAndValidateContent(String sourcecontainer, String sourceKey) throws InterruptedException {
      String eTag = addBlobToContainer(sourcecontainer, sourceKey);
      validateContent(sourcecontainer, sourceKey);
      return eTag;
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNotFound() throws InterruptedException {
      String container = getContainerName();
      String name = "test";
      try {
         view.getBlobStore().removeBlob(container, name);
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void blobNotFound() throws InterruptedException {
      String container = getContainerName();
      String name = "test";
      try {
         assert !view.getBlobStore().blobExists(container, name);
      } finally {
         returnContainer(container);
      }
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" }, { "path/foo" }, { "colon:" },
               { "asteri*k" }, { "quote\"" }, { "{great<r}" }, { "lesst>en" }, { "p|pe" } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "delete")
   public void deleteObject(String name) throws InterruptedException {
      String container = getContainerName();
      try {
         addBlobToContainer(container, name, name, MediaType.TEXT_PLAIN);
         view.getBlobStore().removeBlob(container, name);
         assertContainerEmptyDeleting(container, name);
      } finally {
         returnContainer(container);
      }
   }

   private void assertContainerEmptyDeleting(String container, String name) {
      Iterable<? extends StorageMetadata> listing = Iterables.filter(view.getBlobStore().list(container),
               new Predicate<StorageMetadata>() {

                  @Override
                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.BLOB;
                  }

               });
      assertEquals(Iterables.size(listing), 0, String.format(
               "deleting %s, we still have %s blobs left in container %s, using encoding %s", name, Iterables
                        .size(listing), container, LOCAL_ENCODING));
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNoContainer() {
      try {
         view.getBlobStore().removeBlob("donb", "test");
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 404);
      } catch (ContainerNotFoundException e) {
      }

   }

   @DataProvider(name = "putTests")
   public Object[][] createData1() throws IOException {
      File file = new File("pom.xml");
      String realObject = Files.toString(file, Charsets.UTF_8);

      return new Object[][] { { "file", "text/xml", file, realObject },
               { "string", "text/xml", realObject, realObject },
               { "bytes", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "putTests")
   public void testPutObject(String name, String type, Object content, Object realObject) throws InterruptedException,
            IOException {
      PayloadBlobBuilder blobBuilder = view.getBlobStore().blobBuilder(name).payload(Payloads.newPayload(content))
               .contentType(type);
      addContentMetadata(blobBuilder);
      if (content instanceof InputStream) {
         blobBuilder.calculateMD5();
      }
      Blob blob = blobBuilder.build();
      String container = getContainerName();
      try {
         assertNotNull(view.getBlobStore().putBlob(container, blob));
         blob = view.getBlobStore().getBlob(container, blob.getMetadata().getName());
         validateMetadata(blob.getMetadata(), container, name);
         checkContentMetadata(blob);

         String returnedString = getContentAsStringOrNullAndClose(blob);
         assertEquals(returnedString, realObject);
         PageSet<? extends StorageMetadata> set = view.getBlobStore().list(container);
         assert set.size() == 1 : set;
      } finally {
         returnContainer(container);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testPutObjectStream() throws InterruptedException, IOException, ExecutionException {
      PayloadBlobBuilder blobBuilder = view.getBlobStore().blobBuilder("streaming").payload(
               new StreamingPayload(new WriteTo() {
                  @Override
                  public void writeTo(OutputStream outstream) throws IOException {
                     outstream.write("foo".getBytes());
                  }
               }));
      addContentMetadata(blobBuilder);

      Blob blob = blobBuilder.build();

      String container = getContainerName();
      try {

         assertNotNull(view.getBlobStore().putBlob(container, blob));

         blob = view.getBlobStore().getBlob(container, blob.getMetadata().getName());
         String returnedString = getContentAsStringOrNullAndClose(blob);
         assertEquals(returnedString, "foo");
         validateMetadata(blob.getMetadata(), container, blob.getMetadata().getName());
         checkContentMetadata(blob);
         PageSet<? extends StorageMetadata> set = view.getBlobStore().list(container);
         assert set.size() == 1 : set;
      } finally {
         returnContainer(container);
      }
   }

   private void checkContentMetadata(Blob blob) {
      checkContentType(blob, "text/csv");
      checkContentDisposition(blob, "attachment; filename=photo.jpg");
      checkContentEncoding(blob, "gzip");
      checkContentLanguage(blob, "en");
   }

   private void addContentMetadata(PayloadBlobBuilder blobBuilder) {
      blobBuilder.contentType("text/csv");
      blobBuilder.contentDisposition("attachment; filename=photo.jpg");
      blobBuilder.contentEncoding("gzip");
      blobBuilder.contentLanguage("en");
   }

   protected void checkContentType(Blob blob, String contentType) {
      assert blob.getPayload().getContentMetadata().getContentType().startsWith(contentType) : blob.getPayload()
               .getContentMetadata().getContentType();
      assert blob.getMetadata().getContentMetadata().getContentType().startsWith(contentType) : blob.getMetadata()
               .getContentMetadata().getContentType();
   }

   protected void checkContentDisposition(Blob blob, String contentDisposition) {
      assert blob.getPayload().getContentMetadata().getContentDisposition().startsWith(contentDisposition) : blob
               .getPayload().getContentMetadata().getContentDisposition();
      assert blob.getMetadata().getContentMetadata().getContentDisposition().startsWith(contentDisposition) : blob
               .getMetadata().getContentMetadata().getContentDisposition();

   }

   protected void checkContentEncoding(Blob blob, String contentEncoding) {
      assert (blob.getPayload().getContentMetadata().getContentEncoding().indexOf(contentEncoding) != -1) : blob
               .getPayload().getContentMetadata().getContentEncoding();
      assert (blob.getMetadata().getContentMetadata().getContentEncoding().indexOf(contentEncoding) != -1) : blob
               .getMetadata().getContentMetadata().getContentEncoding();
   }

   protected void checkContentLanguage(Blob blob, String contentLanguage) {
      assert blob.getPayload().getContentMetadata().getContentLanguage().startsWith(contentLanguage) : blob
               .getPayload().getContentMetadata().getContentLanguage();
      assert blob.getMetadata().getContentMetadata().getContentLanguage().startsWith(contentLanguage) : blob
               .getMetadata().getContentMetadata().getContentLanguage();
   }

   protected volatile static Crypto crypto;
   static {
      try {
         crypto = new JCECrypto();
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (CertificateException e) {
         Throwables.propagate(e);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMetadata() throws InterruptedException, IOException {
      String name = "hello";
      // NOTE all metadata in jclouds comes out as lowercase, in an effort to
      // normalize the
      // providers.
      Blob blob = view.getBlobStore().blobBuilder(name).userMetadata(ImmutableMap.of("Adrian", "powderpuff"))
               .payload(TEST_STRING).contentType(MediaType.TEXT_PLAIN).calculateMD5().build();
      String container = getContainerName();
      try {
         assertNull(view.getBlobStore().blobMetadata(container, "powderpuff"));

         addBlobToContainer(container, blob);
         Blob newObject = validateContent(container, name);

         BlobMetadata metadata = newObject.getMetadata();

         validateMetadata(metadata);
         validateMetadata(metadata, container, name);
         validateMetadata(view.getBlobStore().blobMetadata(container, name));

         // write 2 items with the same name to ensure that provider doesn't
         // accept dupes
         blob.getMetadata().getUserMetadata().put("Adrian", "wonderpuff");
         blob.getMetadata().getUserMetadata().put("Adrian", "powderpuff");

         addBlobToContainer(container, blob);
         validateMetadata(view.getBlobStore().blobMetadata(container, name));

      } finally {
         returnContainer(container);
      }
   }

   protected void validateMetadata(BlobMetadata metadata) throws IOException {
      assert metadata.getContentMetadata().getContentType().startsWith("text/plain") : metadata.getContentMetadata()
               .getContentType();
      assertEquals(metadata.getContentMetadata().getContentLength(), Long.valueOf(TEST_STRING.length()));
      assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
      checkMD5(metadata);
   }

   protected void checkMD5(BlobMetadata metadata) throws IOException {
      assertEquals(metadata.getContentMetadata().getContentMD5(), md5().hashString(TEST_STRING, UTF_8).asBytes());
   }

   private File createTestInput() throws IOException {
      File file = File.createTempFile("testimg", "png");
      file.deleteOnExit();
      Random random = new Random();
      byte[] buffer = new byte[random.nextInt(2 * 1024 * 1024)];
      random.nextBytes(buffer);
      Files.copy(ByteStreams.newInputStreamSupplier(buffer), file);
      return file;
   }
}

/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3;

import static com.google.common.io.ByteStreams.join;
import static com.google.common.io.ByteStreams.newInputStreamSupplier;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.jclouds.crypto.CryptoStreams.md5;
import static org.jclouds.io.Payloads.newByteArrayPayload;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.apachehc.config.ApacheHCHttpCommandExecutorServiceModule;
import org.jclouds.io.Payload;
import org.jclouds.s3.S3ClientLiveTest;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.s3.domain.S3Object;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.inject.Module;

/**
 * Tests behavior of {@code S3Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "AWSS3ClientLiveTest")
public class AWSS3ClientLiveTest extends S3ClientLiveTest {
   private InputSupplier<InputStream> oneHundredOneConstitutions;
   private byte[] oneHundredOneConstitutionsMD5;
   private static long oneHundredOneConstitutionsLength;

   @Override
   public AWSS3Client getApi() {
      return (AWSS3Client) context.getProviderSpecificContext().getApi();
   }

   @Override
   protected Module createHttpModule() {
      // in order to be able to debug the wire protocol I use ApacheHC...
      return new ApacheHCHttpCommandExecutorServiceModule();
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      oneHundredOneConstitutions = getTestDataSupplier();
      oneHundredOneConstitutionsMD5 = md5(oneHundredOneConstitutions);
   }

   @SuppressWarnings("unchecked")
   public static InputSupplier<InputStream> getTestDataSupplier() throws IOException {
      byte[] oneConstitution = toByteArray(new GZIPInputStream(BaseJettyTest.class.getResourceAsStream("/const.txt.gz")));
      InputSupplier<ByteArrayInputStream> constitutionSupplier = newInputStreamSupplier(oneConstitution);

      InputSupplier<InputStream> temp = join(constitutionSupplier);
      // we have to go beyond 5MB per part
      for (oneHundredOneConstitutionsLength = oneConstitution.length; oneHundredOneConstitutionsLength < 5 * 1024 * 1024; oneHundredOneConstitutionsLength += oneConstitution.length) {
         temp = join(temp, constitutionSupplier);
      }
      return temp;
   }

   public void testMultipartSynchronously() throws InterruptedException, IOException {
      String containerName = getContainerName();
      S3Object object = null;
      try {
         String key = "constitution.txt";
         String uploadId = getApi().initiateMultipartUpload(containerName,
                  ObjectMetadataBuilder.create().key(key).contentMD5(oneHundredOneConstitutionsMD5).build());
         byte[] buffer = toByteArray(oneHundredOneConstitutions.getInput());
         assertEquals(oneHundredOneConstitutionsLength, (long) buffer.length);

         Payload part1 = newByteArrayPayload(buffer);
         part1.getContentMetadata().setContentLength((long) buffer.length);
         part1.getContentMetadata().setContentMD5(oneHundredOneConstitutionsMD5);

         String eTagOf1 = null;
         try {
            eTagOf1 = getApi().uploadPart(containerName, key, 1, uploadId, part1);
         } catch (KeyNotFoundException e) {
            // note that because of eventual consistency, the upload id may not be present yet
            // we may wish to add this condition to the retry handler

            // we may also choose to implement ListParts and wait for the uploadId to become
            // available there.
            eTagOf1 = getApi().uploadPart(containerName, key, 1, uploadId, part1);
         }

         String eTag = getApi().completeMultipartUpload(containerName, key, uploadId, ImmutableMap.of(1, eTagOf1));

         assert !eTagOf1.equals(eTag);

         object = getApi().getObject(containerName, key);
         assertEquals(toByteArray(object.getPayload()), buffer);

         // noticing amazon does not return content-md5 header or a parsable ETag after a multi-part
         // upload is complete:
         // https://forums.aws.amazon.com/thread.jspa?threadID=61344
         assertEquals(object.getPayload().getContentMetadata().getContentMD5(), null);
         assertEquals(getApi().headObject(containerName, key).getContentMetadata().getContentMD5(), null);

      } finally {
         if (object != null)
            object.getPayload().close();
         returnContainer(containerName);
      }
   }
   
   public void testMultipartChunkedFileStream() throws IOException, InterruptedException {
      
      FileOutputStream fous = new FileOutputStream(new File("target/const.txt"));
      ByteStreams.copy(oneHundredOneConstitutions.getInput(), fous);
      fous.flush();
      fous.close();
      String containerName = getContainerName();
      
      try {
         BlobStore blobStore = context.getBlobStore();
         blobStore.createContainerInLocation(null, containerName);
         Blob blob = blobStore.blobBuilder("const.txt")
            .payload(new File("target/const.txt")).build();
         blobStore.putBlob(containerName, blob, PutOptions.Builder.multipart());
      } finally {
         returnContainer(containerName);
      }
   }
}

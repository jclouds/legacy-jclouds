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

package org.jclouds.aws.s3;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.apachehc.config.ApacheHCHttpCommandExecutorServiceModule;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.s3.S3ClientLiveTest;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
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

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      oneHundredOneConstitutions = getTestDataSupplier();
      oneHundredOneConstitutionsMD5 = CryptoStreams.md5(oneHundredOneConstitutions);
   }

   @SuppressWarnings("unchecked")
   public static InputSupplier<InputStream> getTestDataSupplier() throws IOException {
      byte[] oneConstitution = ByteStreams.toByteArray(new GZIPInputStream(BaseJettyTest.class
               .getResourceAsStream("/const.txt.gz")));
      InputSupplier<ByteArrayInputStream> constitutionSupplier = ByteStreams.newInputStreamSupplier(oneConstitution);

      InputSupplier<InputStream> temp = ByteStreams.join(constitutionSupplier);
      // we have to go beyond 5MB per part
      for (oneHundredOneConstitutionsLength = oneConstitution.length; oneHundredOneConstitutionsLength < 5 * 1024 * 1024; oneHundredOneConstitutionsLength += oneConstitution.length) {
         temp = ByteStreams.join(temp, constitutionSupplier);
      }
      return temp;
   }

   public void testMultipartSynchronously() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         String key = "constitution.txt";
         String uploadId = getApi().initiateMultipartUpload(containerName,
                  ObjectMetadataBuilder.create().key(key).build());

         byte[] buffer = ByteStreams.toByteArray(oneHundredOneConstitutions.getInput());
         Payload part1 = Payloads.newByteArrayPayload(buffer);
         part1.getContentMetadata().setContentLength((long) buffer.length);
         part1.getContentMetadata().setContentMD5(oneHundredOneConstitutionsMD5);

         // failure here looks very similar to http://java.net/jira/browse/GLASSFISH-15773
         String eTagOf1 = getApi().uploadPart(containerName, key, 1, uploadId, part1);

         String eTag = getApi().completeMultipartUpload(containerName, key, uploadId, ImmutableMap.of(1, eTagOf1));

         assertEquals(eTagOf1, eTag);

      } finally {
         returnContainer(containerName);
      }
   }

}

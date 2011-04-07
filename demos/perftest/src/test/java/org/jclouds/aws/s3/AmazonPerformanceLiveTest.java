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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Runs operations that amazon s3 sample code is capable of performing.
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, groups = { "live" })
public class AmazonPerformanceLiveTest extends BasePerformanceLiveTest {
   private AmazonS3 s3;

   @BeforeClass(groups = { "integration", "live" })
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      exec = Executors.newCachedThreadPool();
      s3 = new AmazonS3Client(new BasicAWSCredentials(System.getProperty("test.aws-s3.identity"),
            System.getProperty("test.aws-s3.credential")));
   }

   @Override
   @Test(enabled = false)
   public void testPutStringSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putByteArray(final String bucket, final String key, final byte[] data, final String contentType) {

      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            ObjectMetadata md = new ObjectMetadata();
            md.setContentType(contentType);
            md.setContentLength(data.length);
            return s3.putObject(new PutObjectRequest(bucket, key, new ByteArrayInputStream(data), md)).getETag();
         }
      });
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putFile(final String bucket, final String key, final File data, String contentType) {

      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            return s3.putObject(new PutObjectRequest(bucket, key, data)).getETag();
         }
      });
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putInputStream(final String bucket, final String key, final InputStream data,
         final String contentType) {

      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            ObjectMetadata md = new ObjectMetadata();
            md.setContentType(contentType);
            md.setContentLength(data.available());
            return s3.putObject(new PutObjectRequest(bucket, key, data, md)).getETag();
         }
      });
   }

   @Override
   protected Future<?> putString(String bucket, String key, String data, String contentType) {
      throw new UnsupportedOperationException();
   }

}

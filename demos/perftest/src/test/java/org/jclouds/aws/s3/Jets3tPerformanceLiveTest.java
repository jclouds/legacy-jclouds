/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.repackaged.com.google.common.base.Throwables;

/**
 * Runs operations that jets3t is capable of performing.
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, groups = { "live" })
public class Jets3tPerformanceLiveTest extends BasePerformanceLiveTest {
   private S3Service jetClient;

   @BeforeClass(groups = { "integration", "live" })
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      super.setUpResourcesOnThisThread(testContext);
      exec = Executors.newCachedThreadPool();
      jetClient = new RestS3Service(new AWSCredentials(System.getProperty("test.aws-s3.identity"),
            System.getProperty("test.aws-s3.credential")));
   }

   @Override
   @Test(enabled = false)
   public void testPutStringSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   protected Future<?> putByteArray(String bucket, String key, byte[] data, String contentType) {
      throw new UnsupportedOperationException();

   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putFile(final String bucket, String key, File data, String contentType) {
      final org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(key);
      object.setContentType(contentType);
      object.setDataInputFile(data);
      object.setContentLength(data.length());
      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            return jetClient.putObject(bucket, object);
         }
      });

   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putInputStream(final String bucket, String key, InputStream data, String contentType) {
      final org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(key);
      object.setContentType(contentType);
      object.setDataInputStream(data);
      try {
         object.setContentLength(data.available());
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            return jetClient.putObject(bucket, object);
         }
      });
   }

   @Override
   protected Future<?> putString(String bucket, String key, String data, String contentType) {
      throw new UnsupportedOperationException();
   }

}
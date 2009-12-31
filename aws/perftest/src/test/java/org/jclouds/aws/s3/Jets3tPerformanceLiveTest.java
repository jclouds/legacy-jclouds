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
package org.jclouds.aws.s3;

/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Runs operations that jets3t is capable of performing.
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "perftest.Jets3tPerformanceLiveTest", groups = { "live" })
public class Jets3tPerformanceLiveTest extends BasePerformanceLiveTest {
   private S3Service jetClient;

   @BeforeClass(groups = { "live" }, dependsOnMethods = "setUpResourcesOnThisThread")
   protected void createLiveS3Context(ITestContext testContext) throws S3ServiceException {
      if (testContext.getAttribute("jclouds.test.user") != null) {
         AWSCredentials credentials = new AWSCredentials((String) testContext
                  .getAttribute("jclouds.test.user"), (String) testContext
                  .getAttribute("jclouds.test.key"));
         jetClient = new RestS3Service(credentials);
      } else {
         throw new RuntimeException("not configured properly");
      }
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
   public void testPutBytesParallelEU() throws InterruptedException, ExecutionException,
            TimeoutException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutBytesSerialEU() throws Exception {
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
   protected boolean putByteArray(String bucket, String key, byte[] data, String contentType)
            throws Exception {
      throw new UnsupportedOperationException();

   }

   @Override
   protected boolean putFile(String bucket, String key, File data, String contentType)
            throws Exception {
      org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(key);
      object.setContentType(contentType);
      object.setDataInputFile(data);
      object.setContentLength(data.length());
      return jetClient.putObject(bucket, object) != null;
   }

   @Override
   protected boolean putInputStream(String bucket, String key, InputStream data, String contentType)
            throws Exception {
      org.jets3t.service.model.S3Object object = new org.jets3t.service.model.S3Object(key);
      object.setContentType(contentType);
      object.setDataInputStream(data);
      object.setContentLength(data.available());
      return jetClient.putObject(bucket, object) != null;
   }

   @Override
   protected boolean putString(String bucket, String key, String data, String contentType)
            throws Exception {
      throw new UnsupportedOperationException();
   }

}
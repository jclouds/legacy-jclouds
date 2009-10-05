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
package org.jclouds.aws.s3;

import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.createIn;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Provider;

import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.BucketMetadata.LocationConstraint;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests relative performance of S3 functions.
 * 
 * @author Adrian Cole
 */
public abstract class BasePerformanceLiveTest extends
         BaseBlobStoreIntegrationTest<S3Connection, BucketMetadata, ObjectMetadata, S3Object> {
   protected int timeoutSeconds = 10;
   protected int loopCount = 100;
   protected ExecutorService exec;
   protected CompletionService<Boolean> completer;

   @BeforeClass(groups = { "live" }, dependsOnMethods = "setUpResourcesOnThisThread")
   public void setUpCallables() throws InterruptedException, ExecutionException, TimeoutException {
      exec = Executors.newCachedThreadPool();
      completer = new ExecutorCompletionService<Boolean>(exec);
   }

   @AfterClass(groups = { "live" })
   public void tearDownExecutor() throws Exception {
      exec.shutdownNow();
      exec = null;
   }

   // too slow...
   @Test(enabled = false)
   public void testPutBytesSerialEU() throws Exception {
      String euContainerName = createScratchContainerInEU();
      try {
         doSerial(new PutBytesCallable(euContainerName), loopCount);
      } finally {
         destroyContainer(euContainerName);
      }
   }

   /**
    * using scratch containerName as we are changing location
    * 
    * @throws TimeoutException
    * @throws ExecutionException
    * @throws InterruptedException
    */
   protected String createScratchContainerInEU() throws InterruptedException, ExecutionException,
            TimeoutException {
      String containerName = getScratchContainerName();
      context.getApi().putBucketIfNotExists(containerName, createIn(LocationConstraint.EU)).get(30,
               TimeUnit.SECONDS);
      return containerName;
   }

   // too slow...
   @Test(enabled = false)
   public void testPutBytesParallelEU() throws InterruptedException, ExecutionException,
            TimeoutException {
      String euContainerName = createScratchContainerInEU();
      try {
         doParallel(new PutBytesCallable(euContainerName), loopCount);
      } finally {
         destroyContainer(euContainerName);
      }
   }

   @Test
   public void testPutBytesSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutBytesCallable(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutBytesParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutBytesCallable(bucketName), loopCount);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutFileSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutFileCallable(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutFileParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutFileCallable(bucketName), loopCount);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutInputStreamSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutInputStreamCallable(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutInputStreamCallable(bucketName), loopCount);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutStringSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutStringCallable(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutStringParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutStringCallable(bucketName), loopCount);
      } finally {
         returnContainer(bucketName);
      }
   }

   private void doSerial(Provider<Callable<Boolean>> provider, int loopCount) throws Exception,
            ExecutionException {
      for (int i = 0; i < loopCount; i++)
         assert provider.get().call();
   }

   private void doParallel(Provider<Callable<Boolean>> provider, int loopCount)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < loopCount; i++)
         completer.submit(provider.get());
      for (int i = 0; i < loopCount; i++)
         assert completer.take().get(timeoutSeconds, TimeUnit.SECONDS);
   }

   class PutBytesCallable implements Provider<Callable<Boolean>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected byte[] test = new byte[1024 * 2];
      private final String bucketName;

      public PutBytesCallable(String bucketName) {
         this.bucketName = bucketName;
      }

      public Callable<Boolean> get() {
         return new Callable<Boolean>() {
            public Boolean call() throws Exception {
               String bucketName2 = bucketName;
               return putByteArray(bucketName2, key.getAndIncrement() + "", test,
                        "application/octetstring");
            }
         };

      }
   }

   class PutFileCallable implements Provider<Callable<Boolean>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected File file = new File("pom.xml");
      private final String bucketName;

      public PutFileCallable(String bucketName) {
         this.bucketName = bucketName;
      }

      public Callable<Boolean> get() {
         return new Callable<Boolean>() {
            public Boolean call() throws Exception {
               return putFile(bucketName, key.getAndIncrement() + "", file, "text/xml");
            }
         };

      }
   }

   class PutInputStreamCallable extends PutBytesCallable {
      final AtomicInteger key = new AtomicInteger(0);
      private final String bucketName;

      public PutInputStreamCallable(String bucketName) {
         super(bucketName);
         this.bucketName = bucketName;
      }

      @Override
      public Callable<Boolean> get() {
         return new Callable<Boolean>() {
            public Boolean call() throws Exception {
               return putInputStream(bucketName, key.getAndIncrement() + "",
                        new ByteArrayInputStream(test), "application/octetstring");
            }
         };

      }
   }

   class PutStringCallable implements Provider<Callable<Boolean>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected String testString = "hello world!";
      private final String bucketName;

      public PutStringCallable(String bucketName) {
         this.bucketName = bucketName;
      }

      public Callable<Boolean> get() {
         return new Callable<Boolean>() {
            public Boolean call() throws Exception {
               return putString(bucketName, key.getAndIncrement() + "", testString, "text/plain");
            }
         };

      }
   }

   protected abstract boolean putByteArray(String bucket, String key, byte[] data,
            String contentType) throws Exception;

   protected abstract boolean putFile(String bucket, String key, File data, String contentType)
            throws Exception;

   protected abstract boolean putInputStream(String bucket, String key, InputStream data,
            String contentType) throws Exception;

   protected abstract boolean putString(String bucket, String key, String data, String contentType)
            throws Exception;

}

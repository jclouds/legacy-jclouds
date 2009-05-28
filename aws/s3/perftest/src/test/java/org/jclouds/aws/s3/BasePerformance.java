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

import org.jclouds.aws.s3.S3IntegrationTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Provider;

/**
 * Tests relative performance of S3 functions.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" })
public abstract class BasePerformance extends S3IntegrationTest {
   @Override
   protected boolean debugEnabled() {
      return false;
   }

   protected static int LOOP_COUNT = 100;

   protected ExecutorService exec;

   protected CompletionService<Boolean> completer;

   @BeforeTest
   protected void setUpCallables() {
      exec = Executors.newCachedThreadPool();
      completer = new ExecutorCompletionService<Boolean>(exec);
   }

   @AfterTest
   protected void tearDownExecutor() throws Exception {
      exec.shutdownNow();
      exec = null;
   }

   @Test(enabled = true)
   public void testPutBytesSerial() throws Exception {
      doSerial(new PutBytesCallable(this.bucketName), LOOP_COUNT / 10);
   }

   @Test(enabled = true)
   public void testPutBytesParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      doParallel(new PutBytesCallable(this.bucketName), LOOP_COUNT);
   }

   @Test(enabled = true)
   public void testPutFileSerial() throws Exception {
      doSerial(new PutFileCallable(this.bucketName), LOOP_COUNT / 10);
   }

   @Test(enabled = true)
   public void testPutFileParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      doParallel(new PutFileCallable(this.bucketName), LOOP_COUNT);
   }

   @Test(enabled = true)
   public void testPutInputStreamSerial() throws Exception {
      doSerial(new PutInputStreamCallable(this.bucketName), LOOP_COUNT / 10);
   }

   @Test(enabled = true)
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      doParallel(new PutInputStreamCallable(this.bucketName), LOOP_COUNT);
   }

   @Test(enabled = true)
   public void testPutStringSerial() throws Exception {
      doSerial(new PutStringCallable(this.bucketName), LOOP_COUNT / 10);
   }

   @Test(enabled = true)
   public void testPutStringParallel() throws InterruptedException, ExecutionException,
            TimeoutException {
      doParallel(new PutStringCallable(this.bucketName), LOOP_COUNT);
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
         assert completer.take().get(10, TimeUnit.SECONDS);
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

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

import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Provider;

import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests relative performance of S3 functions.
 * 
 * @author Adrian Cole
 */
public abstract class BasePerformanceLiveTest extends BaseBlobStoreIntegrationTest {
   static {
      containerCount = 1;
   }
   protected int timeoutSeconds = 15;
   protected int loopCount = Integer.parseInt(System.getProperty("test.s3.loopcount", "1000"));
   protected ExecutorService exec;
   protected Logger logger = Logger.NULL;;

   @Test(enabled = false)
   public void testPutBytesSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutBytesFuture(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutBytesParallel() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutBytesFuture(bucketName), loopCount, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(enabled = false)
   public void testPutFileSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutFileFuture(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutFileParallel() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutFileFuture(bucketName), loopCount, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(enabled = false)
   public void testPutInputStreamSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutInputStreamFuture(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutInputStreamFuture(bucketName), loopCount, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test(enabled = false)
   public void testPutStringSerial() throws Exception {
      String bucketName = getContainerName();
      try {
         doSerial(new PutStringFuture(bucketName), loopCount / 10);
      } finally {
         returnContainer(bucketName);
      }
   }

   @Test
   public void testPutStringParallel() throws InterruptedException, ExecutionException, TimeoutException {
      String bucketName = getContainerName();
      try {
         doParallel(new PutStringFuture(bucketName), loopCount, bucketName);
      } finally {
         returnContainer(bucketName);
      }
   }

   private void doSerial(Provider<ListenableFuture<?>> provider, int loopCount) throws Exception, ExecutionException {
      for (int i = 0; i < loopCount; i++)
         assert provider.get().get() != null;
   }

   private void doParallel(Provider<ListenableFuture<?>> provider, int loopCount, String containerName)
         throws InterruptedException, ExecutionException, TimeoutException {
      Map<Integer, ListenableFuture<?>> responses = Maps.newHashMap();
      for (int i = 0; i < loopCount; i++)
         responses.put(i, provider.get());

      Map<Integer, Exception> exceptions = awaitCompletion(responses, exec, null, Logger.NULL, String.format(
            "putting into containerName: %s", containerName));

      assert exceptions.size() == 0 : exceptions;

   }

   class PutBytesFuture implements Provider<ListenableFuture<?>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected byte[] test = new byte[1024 * 2];
      private final String bucketName;

      public PutBytesFuture(String bucketName) {
         this.bucketName = bucketName;
      }

      public ListenableFuture<?> get() {
         return Futures.makeListenable(putByteArray(bucketName, key.getAndIncrement() + "", test,
               "application/octetstring"));
      }
   }

   class PutFileFuture implements Provider<ListenableFuture<?>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected File file = new File("pom.xml");
      private final String bucketName;

      public PutFileFuture(String bucketName) {
         this.bucketName = bucketName;
      }

      public ListenableFuture<?> get() {
         return Futures.makeListenable(putFile(bucketName, key.getAndIncrement() + "", file, "text/xml"));
      }
   }

   class PutInputStreamFuture extends PutBytesFuture {
      final AtomicInteger key = new AtomicInteger(0);
      private final String bucketName;

      public PutInputStreamFuture(String bucketName) {
         super(bucketName);
         this.bucketName = bucketName;
      }

      @Override
      public ListenableFuture<?> get() {

         return Futures.makeListenable(putInputStream(bucketName, key.getAndIncrement() + "", new ByteArrayInputStream(
               test), "application/octetstring"));

      }
   }

   class PutStringFuture implements Provider<ListenableFuture<?>> {
      final AtomicInteger key = new AtomicInteger(0);
      protected String testString = "hello world!";
      private final String bucketName;

      public PutStringFuture(String bucketName) {
         this.bucketName = bucketName;
      }

      public ListenableFuture<?> get() {
         return Futures.makeListenable(putString(bucketName, key.getAndIncrement() + "", testString, "text/plain"));
      }
   }

   protected abstract Future<?> putByteArray(String bucket, String key, byte[] data, String contentType);

   protected abstract Future<?> putFile(String bucket, String key, File data, String contentType);

   protected abstract Future<?> putInputStream(String bucket, String key, InputStream data, String contentType);

   protected abstract Future<?> putString(String bucket, String key, String data, String contentType);

}

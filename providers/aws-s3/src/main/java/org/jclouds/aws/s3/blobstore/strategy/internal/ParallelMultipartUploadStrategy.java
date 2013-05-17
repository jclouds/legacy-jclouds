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
package org.jclouds.aws.s3.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.strategy.AsyncMultipartUploadStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.util.Throwables2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

public class ParallelMultipartUploadStrategy implements AsyncMultipartUploadStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   @VisibleForTesting
   static final int DEFAULT_PARALLEL_DEGREE = 4;
   @VisibleForTesting
   static final int DEFAULT_MIN_RETRIES = 5;
   @VisibleForTesting
   static final int DEFAULT_MAX_PERCENT_RETRIES = 10;
   
   private final ListeningExecutorService ioExecutor;
  
   @Inject(optional = true)
   @Named("jclouds.mpu.parallel.degree")
   @VisibleForTesting
   int parallelDegree = DEFAULT_PARALLEL_DEGREE;

   @Inject(optional = true)
   @Named("jclouds.mpu.parallel.retries.min")
   @VisibleForTesting
   int minRetries = DEFAULT_MIN_RETRIES;

   @Inject(optional = true)
   @Named("jclouds.mpu.parallel.retries.maxpercent")
   @VisibleForTesting
   int maxPercentRetries = DEFAULT_MAX_PERCENT_RETRIES;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;
   
   protected final AWSS3AsyncBlobStore ablobstore;
   protected final PayloadSlicer slicer;

   @Inject
   public ParallelMultipartUploadStrategy(AWSS3AsyncBlobStore ablobstore, PayloadSlicer slicer,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor) {
      this.ablobstore = checkNotNull(ablobstore, "ablobstore");
      this.slicer = checkNotNull(slicer, "slicer");
      this.ioExecutor = checkNotNull(ioExecutor, "ioExecutor");
   }
   
   protected void prepareUploadPart(final String container, final String key, 
         final String uploadId, final Integer part, final Payload payload, 
         final long offset, final long size, final SortedMap<Integer, String> etags, 
         final BlockingQueue<Integer> activeParts, 
         final Map<Integer, ListenableFuture<String>> futureParts, 
         final AtomicInteger errors, final int maxRetries, final Map<Integer, Exception> errorMap, 
         final Queue<Part> toRetry, final CountDownLatch latch) {
      if (errors.get() > maxRetries) {
         activeParts.remove(part); // remove part from the bounded-queue without blocking
         latch.countDown();
         return;
      }
      final AWSS3AsyncClient client = ablobstore.getContext().unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getAsyncApi();
      Payload chunkedPart = slicer.slice(payload, offset, size);
      logger.debug(String.format("async uploading part %s of %s to container %s with uploadId %s", part, key, container, uploadId));
      final long start = System.currentTimeMillis();
      final ListenableFuture<String> futureETag = client.uploadPart(container, key, part, uploadId, chunkedPart);
      futureETag.addListener(new Runnable() {
         @Override
         public void run() {
            try {
               etags.put(part, futureETag.get());
               logger.debug(String.format("async uploaded part %s of %s to container %s in %sms with uploadId %s", 
                     part, key, container, System.currentTimeMillis() - start, uploadId));
            } catch (CancellationException e) {
               errorMap.put(part, e);
               String message = String.format("%s while uploading part %s - [%s,%s] to container %s with uploadId: %s running since %dms", 
                     e.getMessage(), part, offset, size, container, uploadId, System.currentTimeMillis() - start);
               logger.debug(message);
            } catch (Exception e) {
               errorMap.put(part, e);
               String message = String.format("%s while uploading part %s - [%s,%s] to container %s with uploadId: %s running since %dms", 
                     e.getMessage(), part, offset, size, container, uploadId, System.currentTimeMillis() - start);
               logger.error(message, e);
               if (errors.incrementAndGet() <= maxRetries)
                  toRetry.add(new Part(part, offset, size));
            } finally {
               activeParts.remove(part); // remove part from the bounded-queue without blocking
               futureParts.remove(part);
               latch.countDown();
            }
         }
      }, ioExecutor);
      futureParts.put(part, futureETag);
   }   
   
   @Override
   public ListenableFuture<String> execute(final String container, final Blob blob, final PutOptions options) {
      return ioExecutor.submit(new Callable<String>() {
               @Override
               public String call() throws Exception {
                  String key = blob.getMetadata().getName();
                  Payload payload = blob.getPayload();
                  MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
                  algorithm.calculateChunkSize(payload.getContentMetadata()
                        .getContentLength());
                  int parts = algorithm.getParts();
                  long chunkSize = algorithm.getChunkSize();
                  long remaining = algorithm.getRemaining();
                  if (parts > 0) {
                     AWSS3Client client = ablobstore
                           .getContext().unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getApi();
                     String uploadId = null;
                     final Map<Integer, ListenableFuture<String>> futureParts = 
                        new ConcurrentHashMap<Integer, ListenableFuture<String>>();
                     final Map<Integer, Exception> errorMap = Maps.newHashMap();
                     AtomicInteger errors = new AtomicInteger(0);
                     int maxRetries = Math.max(minRetries, parts * maxPercentRetries / 100);
                     int effectiveParts = remaining > 0 ? parts + 1 : parts;
                     try {
                        uploadId = client.initiateMultipartUpload(container,
                                 ObjectMetadataBuilder.create().key(key).build()); // TODO md5
                        logger.debug(String.format("initiated multipart upload of %s to container %s" + 
                              " with uploadId %s consisting from %s part (possible max. retries: %d)", 
                              key, container, uploadId, effectiveParts, maxRetries));
                        // we need a bounded-blocking queue to control the amount of parallel jobs 
                        ArrayBlockingQueue<Integer> activeParts = new ArrayBlockingQueue<Integer>(parallelDegree);
                        Queue<Part> toRetry = new ConcurrentLinkedQueue<Part>();
                        SortedMap<Integer, String> etags = new ConcurrentSkipListMap<Integer, String>();
                        CountDownLatch latch = new CountDownLatch(effectiveParts);
                        int part;
                        while ((part = algorithm.getNextPart()) <= parts) {
                           Integer partKey = Integer.valueOf(part);
                           activeParts.put(partKey);
                           prepareUploadPart(container, key, uploadId, partKey, payload, 
                                 algorithm.getNextChunkOffset(), chunkSize, etags, 
                                 activeParts, futureParts, errors, maxRetries, errorMap, toRetry, latch);
                        }
                        if (remaining > 0) {
                           Integer partKey = Integer.valueOf(part);
                           activeParts.put(partKey);
                           prepareUploadPart(container, key, uploadId, partKey, payload, 
                                 algorithm.getNextChunkOffset(), remaining, etags, 
                                 activeParts, futureParts, errors, maxRetries, errorMap, toRetry, latch);
                        }
                        latch.await();
                        // handling retries
                        while (errors.get() <= maxRetries && toRetry.size() > 0) {
                           int atOnce = Math.min(Math.min(toRetry.size(), errors.get()), parallelDegree);
                           CountDownLatch retryLatch = new CountDownLatch(atOnce);
                           for (int i = 0; i < atOnce; i++) {
                              Part failedPart = toRetry.poll();
                              Integer partKey = Integer.valueOf(failedPart.getPart());
                              activeParts.put(partKey);
                              prepareUploadPart(container, key, uploadId, partKey, payload, 
                                    failedPart.getOffset(), failedPart.getSize(), etags, 
                                    activeParts, futureParts, errors, maxRetries, errorMap, toRetry, retryLatch);
                           }
                           retryLatch.await();
                        }
                        if (errors.get() > maxRetries) {
                           throw new BlobRuntimeException(String.format(
                                 "Too many failed parts: %s while multipart upload of %s to container %s with uploadId %s", 
                                 errors.get(), key, container, uploadId));
                        }
                        String eTag = client.completeMultipartUpload(container, key, uploadId, etags);
                        logger.debug(String.format("multipart upload of %s to container %s with uploadId %s" +
                            " successfully finished with %s retries", key, container, uploadId, errors.get()));
                        return eTag;
                     } catch (Exception ex) {
                        RuntimeException rtex = Throwables2.getFirstThrowableOfType(ex, RuntimeException.class);
                        if (rtex == null) {
                           rtex = new RuntimeException(ex);
                        }
                        for (Map.Entry<Integer, ListenableFuture<String>> entry : futureParts.entrySet()) {
                           entry.getValue().cancel(false);
                        }
                        if (uploadId != null) {
                           client.abortMultipartUpload(container, key, uploadId);
                        }
                        throw rtex;
                     }
                  } else {
                     // Issue 936: don't just call putBlob, as that will see options=multiPart and 
                     // recursively call this execute method again; instead mark as not multipart
                     // because it can all fit in one go.
                     PutOptions nonMultipartOptions = PutOptions.Builder.multipart(false);
                     ListenableFuture<String> futureETag = ablobstore.putBlob(container, blob, nonMultipartOptions);
                     return maxTime != null ? 
                           futureETag.get(maxTime,TimeUnit.SECONDS) : futureETag.get();
                  }
               }
            });
   }
   
   static class Part {
      private int part;
      private long offset;
      private long size;
      
      Part(int part, long offset, long size) {
         this.part = part;
         this.offset = offset;
         this.size = size;
      }

      public int getPart() {
         return part;
      }

      public void setPart(int part) {
         this.part = part;
      }

      public long getOffset() {
         return offset;
      }

      public void setOffset(long offset) {
         this.offset = offset;
      }

      public long getSize() {
         return size;
      }

      public void setSize(long size) {
         this.size = size;
      }     
   }
}

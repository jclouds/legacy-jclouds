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

package org.jclouds.aws.s3.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;


import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.util.Throwables2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Provides a sequential multipart upload strategy.
 * 
 * The file partitioning algorithm:
 * 
 * The default partition size we choose is 32mb. A multiple of this default partition size is used.
 * The number of parts first grows to a chosen magnitude (for example 100 parts), then it grows the
 * partition size instead of number of partitions. When we reached the maximum part size, then again
 * it starts to grow the number of partitions.
 * 
 * @author Tibor Kiss
 */
public class SequentialMultipartUploadStrategy implements MultipartUploadStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   static final long DEFAULT_PART_SIZE = 33554432; // 32MB
   
   @VisibleForTesting
   static final int DEFAULT_MAGNITUDE_BASE = 100;
   
   @Inject(optional = true)
   @Named("jclouds.mpu.parts.size")
   @VisibleForTesting
   long defaultPartSize = DEFAULT_PART_SIZE;
   
   @Inject(optional = true)
   @Named("jclouds.mpu.parts.magnitude")
   @VisibleForTesting
   int magnitudeBase = DEFAULT_MAGNITUDE_BASE;

   private final AWSS3BlobStore ablobstore;
   private final PayloadSlicer slicer;

   // calculated only once, but not from the constructor
   private volatile long parts; // required number of parts with chunkSize
   private volatile long chunkSize;
   private volatile long remaining; // number of bytes remained for the last part

   // sequentially updated values
   private volatile int part;
   private volatile long chunkOffset;
   private volatile long copied;

   @Inject
   public SequentialMultipartUploadStrategy(AWSS3BlobStore ablobstore, PayloadSlicer slicer) {
      this.ablobstore = checkNotNull(ablobstore, "ablobstore");
      this.slicer = checkNotNull(slicer, "slicer");
   }

   @VisibleForTesting
   protected long calculateChunkSize(long length) {
      long unitPartSize = defaultPartSize; // first try with default part size
      long parts = length / unitPartSize;
      long partSize = unitPartSize;
      int magnitude = (int) (parts / magnitudeBase);
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > MAX_PART_SIZE) {
            partSize = MAX_PART_SIZE;
            unitPartSize = MAX_PART_SIZE;
         }
         parts = length / partSize;
         if (parts * partSize < length) {
            partSize = (magnitude + 1) * unitPartSize;
            if (partSize > MAX_PART_SIZE) {
               partSize = MAX_PART_SIZE;
               unitPartSize = MAX_PART_SIZE;
            }
            parts = length / partSize;
         }
      }
      if (parts > MAX_NUMBER_OF_PARTS) { // if splits in too many parts or
                                         // cannot be split
         unitPartSize = MIN_PART_SIZE; // take the minimum part size
         parts = length / unitPartSize;
      }
      if (parts > MAX_NUMBER_OF_PARTS) { // if still splits in too many parts
         parts = MAX_NUMBER_OF_PARTS - 1; // limit them. do not care about not
                                          // covering
      }
      long remainder = length % unitPartSize;
      if (remainder == 0 && parts > 0) {
         parts -= 1;
      }
      this.chunkSize = partSize;
      this.parts = parts;
      this.remaining = length - partSize * parts;
      logger.debug(" %d bytes partitioned in %d parts of part size: %d, remaining: %d%s", length, parts, chunkSize,
            remaining, (remaining > MAX_PART_SIZE ? " overflow!" : ""));
      return this.chunkSize;
   }

   public long getCopied() {
      return copied;
   }

   public void setCopied(long copied) {
      this.copied = copied;
   }

   @VisibleForTesting
   protected long getParts() {
      return parts;
   }

   protected int getNextPart() {
      return ++part;
   }

   protected void addCopied(long copied) {
      this.copied += copied;
   }

   protected long getNextChunkOffset() {
      long next = chunkOffset;
      chunkOffset += getChunkSize();
      return next;
   }

   @VisibleForTesting
   protected long getChunkSize() {
      return chunkSize;
   }

   @VisibleForTesting
   protected long getRemaining() {
      return remaining;
   }

   private String prepareUploadPart(AWSS3Client client, String container, String key, String uploadId, int part,
         Payload chunkedPart) {
      String eTag = null;
      try {
         eTag = client.uploadPart(container, key, part, uploadId, chunkedPart);         
      } catch (KeyNotFoundException e) {
         // note that because of eventual consistency, the upload id may not be present yet
         // we may wish to add this condition to the retry handler

         // we may also choose to implement ListParts and wait for the uploadId to become
         // available there.
         eTag = client.uploadPart(container, key, part, uploadId, chunkedPart);
      }
      return eTag;
   }

   @Override
   public String execute(String container, Blob blob) {
      String key = blob.getMetadata().getName();
      calculateChunkSize(blob.getPayload().getContentMetadata().getContentLength());
      long parts = getParts();
      if (parts > 0) {
         AWSS3Client client = (AWSS3Client) ablobstore.getContext()
               .getProviderSpecificContext().getApi();
         String uploadId = client.initiateMultipartUpload(container,
               ObjectMetadataBuilder.create().key(key).build()); // TODO md5
         try {
            SortedMap<Integer, String> etags = Maps.newTreeMap();
            int part;
            while ((part = getNextPart()) <= getParts()) {
               String eTag = prepareUploadPart(client, container, key,
                     uploadId, part, slicer.slice(blob.getPayload(),
                           getNextChunkOffset(), chunkSize));
               etags.put(new Integer(part), eTag);
            }
            long remaining = getRemaining();
            if (remaining > 0) {
               String eTag = prepareUploadPart(client, container, key,
                     uploadId, part, slicer.slice(blob.getPayload(),
                           getNextChunkOffset(), remaining));
               etags.put(new Integer(part), eTag);
            }
            return client.completeMultipartUpload(container, key, uploadId, etags);
         } catch (Exception ex) {
            RuntimeException rtex = Throwables2.getFirstThrowableOfType(ex, RuntimeException.class);
            if (rtex == null) {
               rtex = new RuntimeException(ex);
            }
            client.abortMultipartUpload(container, key, uploadId);
            throw rtex;
         }
      } else {
         return ablobstore.putBlob(container, blob);
      }
   }
}

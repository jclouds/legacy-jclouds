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
/*
 *   MultipartUploadSlicingAlgorithm.java
 *
 * 
 *   Created by: tibor
 *
 *   History
 *
 */

package org.jclouds.openstack.swift.blobstore.strategy.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.swift.blobstore.strategy.MultipartUpload;

import javax.annotation.Resource;
import javax.inject.Named;

public class MultipartUploadSlicingAlgorithm {

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

   // calculated only once, but not from the constructor
   private volatile int parts; // required number of parts with chunkSize
   private volatile long chunkSize;
   private volatile long remaining; // number of bytes remained for the last part

   // sequentially updated values
   private volatile int part;
   private volatile long chunkOffset;
   private volatile long copied;

   @VisibleForTesting
   protected long calculateChunkSize(long length) {
      long unitPartSize = defaultPartSize; // first try with default part size
      int parts = (int)(length / unitPartSize);
      long partSize = unitPartSize;
      int magnitude = parts / magnitudeBase;
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > MultipartUpload.MAX_PART_SIZE) {
            partSize = MultipartUpload.MAX_PART_SIZE;
            unitPartSize = MultipartUpload.MAX_PART_SIZE;
         }
         parts = (int)(length / partSize);
         if (parts * partSize < length) {
            partSize = (magnitude + 1) * unitPartSize;
            if (partSize > MultipartUpload.MAX_PART_SIZE) {
               partSize = MultipartUpload.MAX_PART_SIZE;
               unitPartSize = MultipartUpload.MAX_PART_SIZE;
            }
            parts = (int)(length / partSize);
         }
      }
      if (parts > MultipartUpload.MAX_NUMBER_OF_PARTS) { // if splits in too many parts or
                                         // cannot be split
         unitPartSize = MultipartUpload.MIN_PART_SIZE; // take the minimum part size
         parts = (int)(length / unitPartSize);
      }
      if (parts > MultipartUpload.MAX_NUMBER_OF_PARTS) { // if still splits in too many parts
         parts = MultipartUpload.MAX_NUMBER_OF_PARTS - 1; // limit them. do not care about not
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
            remaining, remaining > MultipartUpload.MAX_PART_SIZE ? " overflow!" : "");
      return this.chunkSize;
   }

   public long getCopied() {
      return copied;
   }

   public void setCopied(long copied) {
      this.copied = copied;
   }

   @VisibleForTesting
   protected int getParts() {
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

}

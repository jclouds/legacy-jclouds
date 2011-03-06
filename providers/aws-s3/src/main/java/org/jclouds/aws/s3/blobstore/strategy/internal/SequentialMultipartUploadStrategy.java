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

import static org.jclouds.io.Payloads.newChunkedFilePayload;

import java.io.File;

import java.util.Map;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.s3.domain.ObjectMetadataBuilder;

import com.google.common.collect.Maps;

/**
 * Provides a sequential multipart upload strategy.
 * 
 * The file partitioning algorithm:
 *
 * The default partition size we choose is 32mb. 
 * A multiple of this default partition size is used. 
 * The number of parts first grows to a chosen magnitude (for example 100 parts), 
 * then it grows the partition size instead of number of partitions.
 * When we reached the maximum part size, then again it starts to grow the number of partitions.
 *
 * @author Tibor Kiss
 */
public class SequentialMultipartUploadStrategy implements
      MultipartUploadStrategy {

   private final long DEFAULT_PART_SIZE = 33554432; // 32mb 
   private final int MAGNITUDE_BASE = 100;
      
   private final AWSS3BlobStore ablobstore;

   // calculated only once, but not from the constructor
   private volatile long parts; // required number of parts with chunkSize
   private volatile long chunkSize;
   private volatile long remaining; // number of bytes remained for the last part
   
   // sequentially updated values
   private volatile int part;
   private volatile long chunkOffset;
   private volatile long copied;
   
   public SequentialMultipartUploadStrategy(AWSS3BlobStore ablobstore) {
      this.ablobstore = ablobstore;
   }

   protected long calculateChunkSize(long length) {
      long unitPartSize = DEFAULT_PART_SIZE; // first try with default part size
      long parts = length / unitPartSize;
      long partSize = unitPartSize;
      int magnitude = (int) (parts / MAGNITUDE_BASE);
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > MAX_PART_SIZE) {
            partSize = MAX_PART_SIZE;
            unitPartSize = MAX_PART_SIZE;
         }
         parts = length / partSize;
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
      System.out.println(" " + length + " bytes partitioned in " + parts
            + " parts of part size: " + chunkSize + ", remaining: "
            + remaining + (remaining > MAX_PART_SIZE ? " overflow!" : ""));
      return this.chunkSize;
   }
   
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
   
   protected long getChunkSize() {
      return chunkSize;
   }
   
   protected long getRemaining() {
      return remaining;
   }
   
   private String prepareUploadPart(AWSS3Client client, String container, String key, String uploadId,
         int part, File file, long chunkOffset, long chunkSize) {
      Payload chunkedPart = newChunkedFilePayload(file, part, chunkOffset, chunkSize);
      chunkedPart.getContentMetadata().setContentLength(chunkSize);
      //chukedPayload.getContentMetadata().setContentMD5(???);
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
      Payload payload = blob.getPayload();
      if (payload instanceof FilePayload) {
         String key = blob.getMetadata().getName();
         File file = FilePayload.class.cast(payload).getRawContent();
         calculateChunkSize(file.length());
         long parts = getParts();
         if (parts > 0) {
            AWSS3Client client = (AWSS3Client) ablobstore.getContext().getProviderSpecificContext().getApi();
            String uploadId = client.initiateMultipartUpload(container, 
                  ObjectMetadataBuilder.create().key(key).build()); // TODO md5            
            Map<Integer, String> etags = Maps.newHashMap();
            int part;
            while ((part = getNextPart()) <= getParts()) {
               String eTag = prepareUploadPart(client, container, key, uploadId, part, file, getNextChunkOffset(), chunkSize);
               etags.put(new Integer(part), eTag);
            }
            long remaining = getRemaining();
            if (remaining > 0) {
               String eTag = prepareUploadPart(client, container, key, uploadId, part, file, getNextChunkOffset(), remaining);
               etags.put(new Integer(part), eTag);
            }            
            return client.completeMultipartUpload(container, key, uploadId, etags);
         } else {
            return ablobstore.putBlob(container, blob);
         }
      } else {
         return ablobstore.putBlob(container, blob);
      }
   }
}

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
package org.jclouds.aws.s3.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.SortedMap;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.util.Throwables2;

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

   protected final AWSS3BlobStore ablobstore;
   protected final PayloadSlicer slicer;

   @Inject
   public SequentialMultipartUploadStrategy(AWSS3BlobStore ablobstore, PayloadSlicer slicer) {
      this.ablobstore = checkNotNull(ablobstore, "ablobstore");
      this.slicer = checkNotNull(slicer, "slicer");
   }
   
   protected void prepareUploadPart(String container, String key, String uploadId, int part,
         Payload payload, long offset, long size, SortedMap<Integer, String> etags) {
      AWSS3Client client = ablobstore.getContext().unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getApi();
      Payload chunkedPart = slicer.slice(payload, offset, size);
      String eTag = null;
      try {
         eTag = client.uploadPart(container, key, part, uploadId, chunkedPart);
         etags.put(Integer.valueOf(part), eTag);
      } catch (KeyNotFoundException e) {
         // note that because of eventual consistency, the upload id may not be present yet
         // we may wish to add this condition to the retry handler

         // we may also choose to implement ListParts and wait for the uploadId to become
         // available there.
         eTag = client.uploadPart(container, key, part, uploadId, chunkedPart);
         etags.put(Integer.valueOf(part), eTag);
      }
   }

   @Override
   public String execute(String container, Blob blob, PutOptions options) {
      String key = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
      algorithm
               .calculateChunkSize(checkNotNull(
                        payload.getContentMetadata().getContentLength(),
                        "contentLength required on all uploads to amazon s3; please invoke payload.getContentMetadata().setContentLength(length) first"));
      int parts = algorithm.getParts();
      long chunkSize = algorithm.getChunkSize();
      if (parts > 0) {
         AWSS3Client client = ablobstore.getContext()
               .unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getApi();
         String uploadId = client.initiateMultipartUpload(container,
               ObjectMetadataBuilder.create().key(key).build()); // TODO md5
         try {
            SortedMap<Integer, String> etags = Maps.newTreeMap();
            int part;
            while ((part = algorithm.getNextPart()) <= parts) {
               prepareUploadPart(container, key, uploadId, part, payload, 
                     algorithm.getNextChunkOffset(), chunkSize, etags);
            }
            long remaining = algorithm.getRemaining();
            if (remaining > 0) {
               prepareUploadPart(container, key, uploadId, part, payload, 
                     algorithm.getNextChunkOffset(), remaining, etags);
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
         // TODO: find a way to disable multipart.  if we pass the original options, it goes into a stack overflow
         return ablobstore.putBlob(container, blob, PutOptions.NONE);
      }
   }
}

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
package org.jclouds.azureblob.blobstore.strategy;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;
import org.jclouds.azureblob.AzureBlobClient;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Named;

import java.security.SecureRandom;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Decomposes a blob into blocks for upload and assembly through PutBlock and PutBlockList
 */
public class AzureBlobBlockUploadStrategy implements MultipartUploadStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   private Logger logger = Logger.NULL;

   private final AzureBlobClient client;
   private final PayloadSlicer slicer;

   @Inject
   public AzureBlobBlockUploadStrategy(AzureBlobClient client, PayloadSlicer slicer) {
      this.client = checkNotNull(client, "client");
      this.slicer = checkNotNull(slicer, "slicer");
   }

   @Override
   public String execute(String container, Blob blob) {
      String blobName = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      Long length = payload.getContentMetadata().getContentLength();
      checkNotNull(length,
            "please invoke payload.getContentMetadata().setContentLength(length) prior to azure block upload");
      checkArgument(length <= (MAX_NUMBER_OF_BLOCKS * MAX_BLOCK_SIZE));
      long offset = 0L;
      List<String> blockIds = Lists.newArrayList();
      int blockCount = 0;
      int totalBlocks = (int) Math.ceil(((double)length) / MAX_BLOCK_SIZE);
      long bytesWritten = 0;
      while (offset < length) {
         blockCount++;
         long chunkSize = MAX_BLOCK_SIZE;
         if (blockCount >= totalBlocks) {
            chunkSize = length % MAX_BLOCK_SIZE;
         }
         bytesWritten += chunkSize;
         Payload block = slicer.slice(payload, offset, chunkSize);
         offset += MultipartUploadStrategy.MAX_BLOCK_SIZE;
         String blockName = blobName + "-" + offset + "-" + new SecureRandom().nextInt();
         byte blockIdBytes[] = Hashing.md5().hashBytes(blockName.getBytes()).asBytes();
         String blockId = BaseEncoding.base64().encode(blockIdBytes);
         blockIds.add(blockId);
         client.putBlock(container, blobName, blockId, block);
      }
      checkState(bytesWritten == length, "Wrote " + bytesWritten + " bytes, but we wanted to write " + length + " bytes");
      return client.putBlockList(container, blobName, blockIds);
   }
}

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

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.s3.blobstore.strategy.MultipartUpload;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code MultipartUploadSlicingAlgorithm} from the perspective of
 * partitioning algorithm
 * 
 * @author Tibor Kiss
 */
@Test(groups = "unit")
public class MpuPartitioningAlgorithmTest {

   /**
    * Below 1 parts the MPU is not used.
    * When we have more than {@code MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE} bytes data,
    * the MPU starts to become active.
    */
   @Test
   public void testLowerLimitFromWhereMultipartBecomeActive() {
      MultipartUploadSlicingAlgorithm strategy = new MultipartUploadSlicingAlgorithm();
      
      // exactly the MIN_PART_SIZE
      long length = MultipartUpload.MIN_PART_SIZE;
      long chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE);
      assertEquals(strategy.getParts(), 0);
      assertEquals(strategy.getRemaining(), length);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);

      // below DEFAULT_PART_SIZE
      length = MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE;
      chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE);
      assertEquals(strategy.getParts(), 0);
      assertEquals(strategy.getRemaining(), length);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);

      // exactly the DEFAULT_PART_SIZE
      length = MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE + 1;
      chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE);
      assertEquals(strategy.getParts(), 1);
      assertEquals(strategy.getRemaining(), 1);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length); 
   }

   /**
    * Phase 1 of the algorithm.
    * ChunkSize does not grow from a {@code MultipartUploadStrategy.DEFAULT_PART_SIZE} 
    * until we reach {@code MultipartUploadSlicingAlgorithm.MAGNITUDE_BASE} number of parts. 
    */
   @Test
   public void testWhenChunkSizeHasToStartGrowing() {
      MultipartUploadSlicingAlgorithm strategy = new MultipartUploadSlicingAlgorithm();
      // upper limit while we still have exactly defaultPartSize chunkSize
      long length = MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE * MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE;
      long chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE);
      assertEquals(strategy.getParts(), MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE - 1);
      assertEquals(strategy.getRemaining(), MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);

      // then chunkSize is increasing
      length += 1;
      chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUploadSlicingAlgorithm.DEFAULT_PART_SIZE * 2);
      assertEquals(strategy.getParts(), MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE / 2);
      assertEquals(strategy.getRemaining(), 1);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);
   }
   
   /**
    * Phase 2 of the algorithm.
    * The number of parts does not grow from {@code MultipartUploadSlicingAlgorithm.MAGNITUDE_BASE} 
    * until we reach the {@code MultipartUploadStrategy.MAX_PART_SIZE}. 
    */
   @Test
   public void testWhenPartsHasToStartGrowingFromMagnitudeBase() {
      MultipartUploadSlicingAlgorithm strategy = new MultipartUploadSlicingAlgorithm();
      // upper limit while we still have exactly MAGNITUDE_BASE parts (together with the remaining)
      long length = MultipartUpload.MAX_PART_SIZE * MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE;
      long chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUpload.MAX_PART_SIZE);
      assertEquals(strategy.getParts(), MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE - 1);
      assertEquals(strategy.getRemaining(), MultipartUpload.MAX_PART_SIZE);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);

      // then the number of parts is increasing
      length += 1;
      chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUpload.MAX_PART_SIZE);
      assertEquals(strategy.getParts(), MultipartUploadSlicingAlgorithm.DEFAULT_MAGNITUDE_BASE);
      assertEquals(strategy.getRemaining(), 1);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);
   }
   
   /**
    * Phase 3 of the algorithm.
    * The number of parts are increasing until {@code MAX_NUMBER_OF_PARTS}
    * while its size does not exceeds the {@code MultipartUploadStrategy.MAX_PART_SIZE}. 
    */
   @Test
   public void testWhenPartsExceedsMaxNumberOfParts() {
      MultipartUploadSlicingAlgorithm strategy = new MultipartUploadSlicingAlgorithm();
      // upper limit while we still have exactly MAX_NUMBER_OF_PARTS parts (together with the remaining)
      long length = MultipartUpload.MAX_PART_SIZE * MultipartUpload.MAX_NUMBER_OF_PARTS;
      long chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUpload.MAX_PART_SIZE);
      assertEquals(strategy.getParts(), MultipartUpload.MAX_NUMBER_OF_PARTS - 1);
      assertEquals(strategy.getRemaining(), MultipartUpload.MAX_PART_SIZE);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);

      // then the number of parts is increasing
      length += 1;
      chunkSize = strategy.calculateChunkSize(length);
      assertEquals(chunkSize, MultipartUpload.MAX_PART_SIZE);
      assertEquals(strategy.getParts(), MultipartUpload.MAX_NUMBER_OF_PARTS);
      assertEquals(strategy.getRemaining(), 1);
      assertEquals(chunkSize * strategy.getParts() + strategy.getRemaining(), length);
   } 
}

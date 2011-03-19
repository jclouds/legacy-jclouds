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

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.io.PayloadSlicer;

/**
 * Print out on the console some graph data regarding the partitioning algorithm.
 * 
 * @author Tibor Kiss
 */
public class MpuGraphData {

   private static void calculate(long length, SequentialMultipartUploadStrategy strategy) {
      System.out.println("" + length + " " + strategy.getParts() + " " 
            + strategy.calculateChunkSize(length) + " " + + strategy.getRemaining());
   }
   
   private static void foreach(long from, long to1, long to2, long to3, SequentialMultipartUploadStrategy strategy) {
      long i = 0L, step = 1L;
      System.out.println("=== {" + from + "," + to1 + "} ===");
      for (; i < to1 - from; step += i, i += step) {
         calculate(i + from, strategy);
      }
      calculate(to1, strategy);
      System.out.println("=== {" + (to1 + 1) + "," + to2 + "} ===");
      for (; i < to2 - to1; step += i / 20, i += step) {
         calculate(i + from, strategy);
      }
      calculate(to2, strategy);
      System.out.println("=== {" + (to2 + 1) + "," + to3 + "} ===");
      for (; i < to3 - to2; step += i / 40, i += step) {
         calculate(i + from, strategy);
      }
      calculate(to3, strategy);
   }

   public static void main(String[] args) {
      AWSS3BlobStore ablobStore = createMock(AWSS3BlobStore.class);      
      PayloadSlicer slicer = createMock(PayloadSlicer.class);
      
      replay(ablobStore);
      replay(slicer);

      SequentialMultipartUploadStrategy strategy = new SequentialMultipartUploadStrategy(ablobStore, slicer);
      foreach(1L, 
            SequentialMultipartUploadStrategy.DEFAULT_PART_SIZE * SequentialMultipartUploadStrategy.magnitudeBase,
            MultipartUploadStrategy.MAX_PART_SIZE * SequentialMultipartUploadStrategy.magnitudeBase,
            MultipartUploadStrategy.MAX_PART_SIZE * MultipartUploadStrategy.MAX_NUMBER_OF_PARTS,
            strategy);

      verify(ablobStore);
      verify(slicer);
   }

}

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

import org.jclouds.aws.s3.blobstore.strategy.MultipartUpload;

/**
 * Print out on the console some graph data regarding the partitioning algorithm.
 *
 * @author Tibor Kiss
 */
public class MpuGraphData {

   private static void calculate(long length, MultipartUploadSlicingAlgorithm algorithm) {
      System.out.println("" + length + " " + algorithm.getParts() + " "
            + algorithm.calculateChunkSize(length) + " " + + algorithm.getRemaining());
   }

   private static void foreach(long from, long to1, long to2, long to3, MultipartUploadSlicingAlgorithm algorithm) {
      long i = 0L, step = 1L;
      System.out.println("=== {" + from + "," + to1 + "} ===");
      for (; i < to1 - from; step += i, i += step) {
         calculate(i + from, algorithm);
      }
      calculate(to1, algorithm);
      System.out.println("=== {" + (to1 + 1) + "," + to2 + "} ===");
      for (; i < to2 - to1; step += i / 20, i += step) {
         calculate(i + from, algorithm);
      }
      calculate(to2, algorithm);
      System.out.println("=== {" + (to2 + 1) + "," + to3 + "} ===");
      for (; i < to3 - to2; step += i / 40, i += step) {
         calculate(i + from, algorithm);
      }
      calculate(to3, algorithm);
   }

   public static void main(String[] args) {
      MultipartUploadSlicingAlgorithm algorithm = new MultipartUploadSlicingAlgorithm();
      foreach(1L,
            algorithm.defaultPartSize * algorithm.magnitudeBase,
            MultipartUpload.MAX_PART_SIZE * algorithm.magnitudeBase,
            MultipartUpload.MAX_PART_SIZE * MultipartUpload.MAX_NUMBER_OF_PARTS,
            algorithm);
   }

}

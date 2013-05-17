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
package org.jclouds.s3;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.ListBucketOptions;

import com.google.common.base.Function;

/**
 * Utilities for using S3.
 *
 * @author Adrian Cole
 */
public class S3 {

   /**
    * List all objects in a bucket, in a way that manages pagination, based on
    * the criteria in the {@link ListBucketOptions} passed in.
    *
    * ex.
    *
    * <pre>
    * continueAfterEachPage = listBucket(s3Client, bucket, options).concat();
    *
    * </pre>
    *
    * @param s3Client
    *           the {@link S3Client} to use for the requests
    * @param bucket
    *           the bucket to list
    * @param options
    *           the {@link ListBucketOptions} describing the listBucket requests
    *
    * @return iterable of objects fitting the criteria
    * @see PagedIterable
    */
   public static PagedIterable<ObjectMetadata> listBucket(final S3Client s3Client, final String bucket,
         final ListBucketOptions options) {
      return PagedIterables.advance(ToIterableWithMarker.INSTANCE.apply(s3Client.listBucket(bucket, options)),
            new Function<Object, IterableWithMarker<ObjectMetadata>>() {

               @Override
               public IterableWithMarker<ObjectMetadata> apply(Object input) {
                  return ToIterableWithMarker.INSTANCE.apply(s3Client.listBucket(bucket,
                        options.clone().afterMarker(input.toString())));
               }

               @Override
               public String toString() {
                  return "listBucket(" + options + ")";
               }
            });
   }

   private enum ToIterableWithMarker implements Function<ListBucketResponse, IterableWithMarker<ObjectMetadata>> {
      INSTANCE;
      @Override
      public IterableWithMarker<ObjectMetadata> apply(ListBucketResponse in) {
         return IterableWithMarkers.from(in, in.getNextMarker());
      }
   }

}

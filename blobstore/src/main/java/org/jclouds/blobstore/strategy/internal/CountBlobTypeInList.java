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
package org.jclouds.blobstore.strategy.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.ListBlobsInContainer;

import com.google.common.collect.Iterables;

/**
 * counts all blobs in the blobstore at the prefix by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class CountBlobTypeInList implements CountListStrategy {
   protected final ListBlobsInContainer getAllBlobMetadata;

   @Inject
   CountBlobTypeInList(ListBlobsInContainer getAllBlobMetadata) {
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public long execute(String container, ListContainerOptions options) {
      return Iterables.size(getAllBlobMetadata.execute(container, options));
   }

   public long execute(String container) {
      return execute(container, null);
   }
}

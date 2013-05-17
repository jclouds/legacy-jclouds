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
package org.jclouds.atmos.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreListOptionsToListOptions implements
         Function<ListContainerOptions, org.jclouds.atmos.options.ListOptions> {
   @Override
   public org.jclouds.atmos.options.ListOptions apply(ListContainerOptions from) {
      checkNotNull(from, "set options to instance NONE instead of passing null");
      org.jclouds.atmos.options.ListOptions httpOptions = new org.jclouds.atmos.options.ListOptions();
      if (from.getMarker() != null) {
         httpOptions.token(from.getMarker());
      }
      if (from.getMaxResults() != null) {
         httpOptions.limit(from.getMaxResults());
      }
      if (from.isDetailed()) {
         httpOptions.includeMeta();
      }
      return httpOptions;
   }
}

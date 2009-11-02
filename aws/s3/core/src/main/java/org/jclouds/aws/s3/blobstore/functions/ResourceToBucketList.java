/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.blobstore.functions;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.internal.TreeSetListBucketResponse;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToBucketList implements
         Function<ListContainerResponse<? extends ResourceMetadata>, ListBucketResponse> {
   private final BlobToObjectMetadata blob2ObjectMd;

   @Inject
   public ResourceToBucketList(BlobToObjectMetadata blob2ObjectMd) {
      this.blob2ObjectMd = blob2ObjectMd;
   }

   public ListBucketResponse apply(ListContainerResponse<? extends ResourceMetadata> list) {

      Iterable<ObjectMetadata> contents = Iterables.transform(Iterables.filter(
               list, new Predicate<ResourceMetadata>() {

                  public boolean apply(ResourceMetadata input) {
                     return input.getType() == ResourceType.BLOB;
                  }

               }), new Function<ResourceMetadata, ObjectMetadata>() {

         public MutableObjectMetadata apply(ResourceMetadata from) {
            return blob2ObjectMd.apply((BlobMetadata) from);
         }

      });

      SortedSet<String> commonPrefixes = Sets.newTreeSet(Iterables.transform(Iterables.filter(list,
               new Predicate<ResourceMetadata>() {

                  public boolean apply(ResourceMetadata input) {
                     return input.getType() == ResourceType.RELATIVE_PATH;
                  }

               }), new Function<ResourceMetadata, String>() {

         public String apply(ResourceMetadata from) {
            return from.getName();
         }

      }));
      return new TreeSetListBucketResponse(null, contents, list.getPath(), list.getMarker(), list
               .getMaxResults(), "/", Iterables.size(contents) == list.getMaxResults(), commonPrefixes);
   }
}
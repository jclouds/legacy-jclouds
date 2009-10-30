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
package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.internal.BoundedTreeSet;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToObjectList implements
         Function<BoundedSortedSet<? extends ResourceMetadata>, BoundedSortedSet<ObjectInfo>> {
   private final ResourceToObjectInfo resource2ObjectMd;

   @Inject
   public ResourceToObjectList(ResourceToObjectInfo resource2ObjectMd) {
      this.resource2ObjectMd = resource2ObjectMd;
   }

   public BoundedSortedSet<ObjectInfo> apply(BoundedSortedSet<? extends ResourceMetadata> list) {

      return new BoundedTreeSet<ObjectInfo>(Iterables.transform(list,
               new Function<ResourceMetadata, ObjectInfo>() {

                  public ObjectInfo apply(ResourceMetadata from) {
                     return resource2ObjectMd.apply(from);
                  }

               }), list.getPath(), list.getMarker(), list.getMaxResults(), list.size() == list
               .getMaxResults());
   }
}
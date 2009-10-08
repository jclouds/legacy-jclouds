/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.collect.ComputationException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ContainerAndFileNameToResourceId implements Function<Object, String> {

   private ConcurrentMap<Key, String> cachedFinder;

   @Inject
   public ContainerAndFileNameToResourceId(ConcurrentMap<Key, String> cachedFinder) {
      this.cachedFinder = cachedFinder;
   }

   public String apply(Object from) {
      checkState(checkNotNull(from, "args") instanceof Object[],
               "this must be applied to a method!");
      Object[] args = (Object[]) from;
      checkArgument(args[0] instanceof String, "arg[0] must be a container name");
      checkArgument(args[1] instanceof String, "arg[1] must be a pcsfile name (key)");
      String container = args[0].toString();
      String key = args[1].toString();

      try {
         return cachedFinder.get(new Key(container, key));
      } catch (ComputationException e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         Utils.<KeyNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw e;
      }
   }

}
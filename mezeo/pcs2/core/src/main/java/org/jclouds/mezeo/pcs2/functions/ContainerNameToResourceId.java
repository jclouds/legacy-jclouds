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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.collect.ComputationException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ContainerNameToResourceId implements Function<Object, String> {

   private final ConcurrentMap<String, String> finder;

   @Inject
   public ContainerNameToResourceId(ConcurrentMap<String, String> finder) {
      this.finder = finder;
   }

   public String apply(Object from) {
      String toFind = checkNotNull(from, "name").toString();
      try {
         return finder.get(toFind);
      } catch (ComputationException e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw e;
      }
   }

}
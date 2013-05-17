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
package org.jclouds.compute.predicates.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * 
 * The point of RefreshAndDoubleCheckOnFailUnlessStateInvalid is to keep an atomic reference to a
 * node, so as to eliminate a redundant {@link ComputeService#getNodeMetadata} call after the
 * predicate passes.
 * 
 * @author Adrian Cole
 */
@Singleton
public class RefreshNodeAndDoubleCheckOnFailUnlessStatusInvalid extends RefreshAndDoubleCheckOnFailUnlessStatusInvalid<NodeMetadata.Status, NodeMetadata> {

   private final GetNodeMetadataStrategy client;

   @Inject
   public RefreshNodeAndDoubleCheckOnFailUnlessStatusInvalid(Status intended, GetNodeMetadataStrategy client) {
      this(intended, ImmutableSet.of(Status.ERROR), client);
   }

   public RefreshNodeAndDoubleCheckOnFailUnlessStatusInvalid(Status intended, Set<Status> invalids,
            GetNodeMetadataStrategy client) {
      super(intended, invalids);
      this.client = checkNotNull(client, "client");
   }

   @Override
   protected NodeMetadata refreshOrNull(NodeMetadata resource) {
      if (resource == null || resource.getId() == null)
         return null;
      return client.getNode(resource.getId());
   }
}

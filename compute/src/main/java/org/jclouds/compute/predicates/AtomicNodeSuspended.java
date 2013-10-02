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
package org.jclouds.compute.predicates;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.predicates.internal.RefreshNodeAndDoubleCheckOnFailUnlessStatusInvalid;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * Tests to see if a node is suspended.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AtomicNodeSuspended extends RefreshNodeAndDoubleCheckOnFailUnlessStatusInvalid {

   @Inject
   public AtomicNodeSuspended(GetNodeMetadataStrategy client) {
      super(Status.SUSPENDED, ImmutableSet.of(Status.ERROR, Status.TERMINATED), client);
   }
}

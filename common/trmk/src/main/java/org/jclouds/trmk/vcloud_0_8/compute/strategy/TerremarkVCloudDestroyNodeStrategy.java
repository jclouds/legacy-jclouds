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
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.trmk.vcloud_0_8.compute.TerremarkVCloudComputeClient;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudDestroyNodeStrategy implements DestroyNodeStrategy {
   protected final TerremarkVCloudComputeClient computeClient;
   protected final GetNodeMetadataStrategy getNode;

   @Inject
   protected TerremarkVCloudDestroyNodeStrategy(TerremarkVCloudComputeClient computeClient, GetNodeMetadataStrategy getNode) {
      this.computeClient = computeClient;
      this.getNode = getNode;

   }

   @Override
   public NodeMetadata destroyNode(String id) {
      computeClient.stop(URI.create(checkNotNull(id, "node.id")));
      return getNode.getNode(id);
   }

}

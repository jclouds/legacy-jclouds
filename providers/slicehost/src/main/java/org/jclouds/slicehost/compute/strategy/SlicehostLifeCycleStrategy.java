/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.slicehost.compute.strategy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.slicehost.SlicehostClient;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostLifeCycleStrategy implements RebootNodeStrategy, SuspendNodeStrategy, ResumeNodeStrategy {
   private final SlicehostClient client;
   private final GetNodeMetadataStrategy getNode;

   @Inject
   protected SlicehostLifeCycleStrategy(SlicehostClient client, GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata rebootNode(String id) {
      int sliceId = Integer.parseInt(id);
      client.hardRebootSlice(sliceId);
      return getNode.getNode(id);
   }

   @Override
   public NodeMetadata suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public NodeMetadata resumeNode(String id) {
      throw new UnsupportedOperationException("resume not supported");
   }

}
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

   private final SlicehostClient client;
   private final Function<Slice, NodeMetadata> sliceToNodeMetadata;

   @Inject
   protected SlicehostGetNodeMetadataStrategy(SlicehostClient client, Function<Slice, NodeMetadata> sliceToNodeMetadata) {
      this.client = client;
      this.sliceToNodeMetadata = sliceToNodeMetadata;
   }

   @Override
   public NodeMetadata execute(String id) {
      int sliceId = Integer.parseInt(id);
      Slice slice = client.getSlice(sliceId);
      return slice == null ? null : sliceToNodeMetadata.apply(slice);
   }
}
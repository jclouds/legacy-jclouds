/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.functions.FindLocationForResourceInVDC;
import org.jclouds.vcloud.compute.functions.GetExtra;
import org.jclouds.vcloud.compute.functions.VCloudGetNodeMetadata;
import org.jclouds.vcloud.domain.VAppStatus;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudGetNodeMetadataStrategy extends VCloudGetNodeMetadata implements
         GetNodeMetadataStrategy {

   @Inject
   protected VCloudGetNodeMetadataStrategy(VCloudClient client, VCloudComputeClient computeClient,
            Map<VAppStatus, NodeState> vAppStatusToNodeState, GetExtra getExtra,
            FindLocationForResourceInVDC findLocationForResourceInVDC,
            Provider<Set<? extends Image>> images) {
      super(client, computeClient, vAppStatusToNodeState, getExtra, findLocationForResourceInVDC,
               images);
   }

   @Override
   public NodeMetadata execute(String id) {
      return getNodeMetadataByIdInVDC(checkNotNull(id, "node.id"));
   }

}
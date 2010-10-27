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

package org.jclouds.rimuhosting.miro.compute.strategy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.rimuhosting.miro.RimuHostingClient;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingRebootNodeStrategy implements RebootNodeStrategy {
   private final RimuHostingClient client;
   private final GetNodeMetadataStrategy getNode;

   @Inject
   protected RimuHostingRebootNodeStrategy(RimuHostingClient client, GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata rebootNode(String id) {
      Long serverId = Long.parseLong(id);
      // if false server wasn't around in the first place
      client.restartServer(serverId).getState();
      return getNode.getNode(id);
   }

}
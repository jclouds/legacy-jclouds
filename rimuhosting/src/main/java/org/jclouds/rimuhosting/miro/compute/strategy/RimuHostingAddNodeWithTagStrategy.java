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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
   private final RimuHostingClient client;
   private final Function<Server, Iterable<String>> getPublicAddresses;
   private final Map<RunningState, NodeState> runningStateToNodeState;

   @Inject
   protected RimuHostingAddNodeWithTagStrategy(RimuHostingClient client,
            Function<Server, Iterable<String>> getPublicAddresses, Map<RunningState, NodeState> runningStateToNodeState) {
      this.client = client;
      this.getPublicAddresses = getPublicAddresses;
      this.runningStateToNodeState = runningStateToNodeState;
   }

   @Override
   public NodeMetadata execute(String tag, String name, Template template) {
      NewServerResponse serverResponse = client.createServer(name, checkNotNull(template.getImage().getProviderId(),
               "imageId"), checkNotNull(template.getSize().getProviderId(), "sizeId"));
      Server server = client.getServer(serverResponse.getServer().getId());
      NodeMetadata node = new NodeMetadataImpl(server.getId().toString(), name, server.getId().toString(), template
               .getLocation(), null, ImmutableMap.<String, String> of(), tag, template.getImage().getId(), template
               .getImage().getOperatingSystem(), runningStateToNodeState.get(server.getState()), getPublicAddresses
               .apply(server), ImmutableList.<String> of(), ImmutableMap.<String, String> of(), new Credentials("root",
               serverResponse.getNewInstanceRequest().getCreateOptions().getPassword()));
      return node;
   }

}
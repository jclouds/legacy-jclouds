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
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingCreateNodeWithGroupEncodedIntoName implements CreateNodeWithGroupEncodedIntoName {
   protected final RimuHostingClient client;
   protected final Map<String, Credentials> credentialStore;
   protected final Function<Server, NodeMetadata> serverToNodeMetadata;

   @Inject
   protected RimuHostingCreateNodeWithGroupEncodedIntoName(RimuHostingClient client, Map<String, Credentials> credentialStore,
            Function<Server, NodeMetadata> serverToNodeMetadata) {
      this.client = client;
      this.credentialStore = credentialStore;
      this.serverToNodeMetadata = serverToNodeMetadata;
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      NewServerResponse serverResponse = client.createServer(name, checkNotNull(template.getImage().getProviderId(),
               "imageId"), checkNotNull(template.getHardware().getProviderId(), "hardwareId"));
      Server from = client.getServer(serverResponse.getServer().getId());
      credentialStore.put("node#" + from.getId(), new Credentials("root", serverResponse.getNewInstanceRequest()
               .getCreateOptions().getPassword()));
      return serverToNodeMetadata.apply(from);
   }

}
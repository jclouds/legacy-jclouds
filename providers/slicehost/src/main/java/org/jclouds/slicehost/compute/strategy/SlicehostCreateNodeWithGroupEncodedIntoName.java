/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.slicehost.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostCreateNodeWithGroupEncodedIntoName implements CreateNodeWithGroupEncodedIntoName {

   protected final SlicehostClient client;
   protected final Map<String, Credentials> credentialStore;
   protected final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;
   protected final Function<Slice, NodeMetadata> sliceToNodeMetadata;

   @Inject
   protected SlicehostCreateNodeWithGroupEncodedIntoName(SlicehostClient client,
            Map<String, Credentials> credentialStore,
            PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate,
            Function<Slice, NodeMetadata> sliceToNodeMetadata) {
      this.client = checkNotNull(client, "client");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.sliceToNodeMetadata = checkNotNull(sliceToNodeMetadata, "sliceToNodeMetadata");
      this.prioritizeCredentialsFromTemplate = checkNotNull(prioritizeCredentialsFromTemplate,
               "prioritizeCredentialsFromTemplate");
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      checkState(group != null, "group (that which groups identical nodes together) must be specified");
      checkState(name != null && name.indexOf(group) != -1, "name should have %s encoded into it", group);
      checkState(template != null, "template must be specified");

      Slice slice = client.createSlice(name, Integer.parseInt(template.getImage().getProviderId()), Integer
               .parseInt(template.getHardware().getProviderId()));

      NodeAndInitialCredentials<Slice> from = new NodeAndInitialCredentials<Slice>(slice, slice.getId() + "",
               LoginCredentials.builder().password(slice.getRootPassword()).build());

      LoginCredentials fromNode = from.getCredentials();
      LoginCredentials creds = prioritizeCredentialsFromTemplate.apply(template, fromNode);
      if (creds != null)
         credentialStore.put("node#" + from.getNodeId(), creds);
      NodeMetadata node = sliceToNodeMetadata.apply(from.getNode());
      return node;
   }
}
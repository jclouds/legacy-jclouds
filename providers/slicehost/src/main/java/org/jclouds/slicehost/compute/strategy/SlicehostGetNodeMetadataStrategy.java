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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

   private final SlicehostClient client;
   private final Map<String, Credentials> credentialStore;
   private final Function<Slice, NodeMetadata> nodeMetadataAdapter;

   @Inject
   protected SlicehostGetNodeMetadataStrategy(SlicehostClient client, Map<String, Credentials> credentialStore,
            Function<Slice, NodeMetadata> nodeMetadataAdapter) {
      this.client = client;
      this.credentialStore = credentialStore;
      this.nodeMetadataAdapter = Functions.compose(addLoginCredentials, checkNotNull(nodeMetadataAdapter,
               "nodeMetadataAdapter"));
   }

   private final Function<NodeMetadata, NodeMetadata> addLoginCredentials = new Function<NodeMetadata, NodeMetadata>() {

      @Override
      public NodeMetadata apply(NodeMetadata arg0) {
         return credentialStore.containsKey("node#" + arg0.getId()) ? NodeMetadataBuilder.fromNodeMetadata(arg0)
                  .credentials(LoginCredentials.fromCredentials(credentialStore.get("node#" + arg0.getId()))).build()
                  : arg0;
      }

      @Override
      public String toString() {
         return "addLoginCredentialsFromCredentialStore()";
      }
   };

   @Override
   public NodeMetadata getNode(String id) {
      int sliceId = Integer.parseInt(id);
      Slice slice = client.getSlice(sliceId);
      return slice == null ? null : nodeMetadataAdapter.apply(slice);
   }
}
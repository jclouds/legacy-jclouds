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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.domain.Credentials;
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
   protected final Function<Slice, NodeMetadata> sliceToNodeMetadata;

   @Inject
   protected SlicehostCreateNodeWithGroupEncodedIntoName(SlicehostClient client, Map<String, Credentials> credentialStore,
         Function<Slice, NodeMetadata> sliceToNodeMetadata) {
      this.client = checkNotNull(client, "client");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.sliceToNodeMetadata = checkNotNull(sliceToNodeMetadata, "sliceToNodeMetadata");
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      Slice from = client.createSlice(name, Integer.parseInt(template.getImage().getProviderId()),
            Integer.parseInt(template.getHardware().getProviderId()));
      credentialStore.put("node#" + from.getId(), new Credentials("root", from.getRootPassword()));
      return sliceToNodeMetadata.apply(from);
   }
}
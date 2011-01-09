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

package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.VCloudExpressVApp;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

   protected final VCloudExpressClient client;
   protected final Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata;

   @Inject
   protected VCloudExpressGetNodeMetadataStrategy(VCloudExpressClient client,
         Function<VCloudExpressVApp, NodeMetadata> vAppToNodeMetadata) {
      this.client = checkNotNull(client, "client");
      this.vAppToNodeMetadata = vAppToNodeMetadata;
   }

   public NodeMetadata getNode(String in) {
      URI id = URI.create(in);
      VCloudExpressVApp from = client.getVApp(id);
      if (from == null)
         return null;
      return vAppToNodeMetadata.apply(from);
   }

}
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

package org.jclouds.vcloud.terremark.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.compute.functions.HardwareForVCloudExpressVApp;
import org.jclouds.vcloud.compute.functions.VCloudExpressVAppToNodeMetadata;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudExpressVAppToNodeMetadata extends VCloudExpressVAppToNodeMetadata {
   private final ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap;

   @Inject
   public TerremarkVCloudExpressVAppToNodeMetadata(VCloudExpressComputeClient computeClient,
            Map<String, Credentials> credentialStore, Map<Status, NodeState> vAppStatusToNodeState,
            HardwareForVCloudExpressVApp hardwareForVCloudExpressVApp,
            FindLocationForResource findLocationForResourceInVDC, @Memoized Supplier<Set<? extends Image>> images,
            ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap) {
      super(computeClient, credentialStore, vAppStatusToNodeState, hardwareForVCloudExpressVApp,
               findLocationForResourceInVDC, images);
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
   }

   @Override
   public NodeMetadata apply(VCloudExpressVApp from) {
      NodeMetadata node = super.apply(from);
      if (node == null)
         return null;
      if (node.getGroup() != null) {
         node = installCredentialsFromCache(node);
      }
      return node;
   }

   NodeMetadata installCredentialsFromCache(NodeMetadata node) {
      OrgAndName orgAndName = getOrgAndNameFromNode(node);
      if (credentialsMap.containsKey(orgAndName)) {
         Credentials creds = credentialsMap.get(orgAndName);
         node = NodeMetadataBuilder.fromNodeMetadata(node).credentials(creds).build();
         credentialStore.put("node#" + node.getId(), creds);
      }
      // this is going to need refactoring.. we really need a credential list in the store per
      // node.
      String adminPasswordKey = "node#" + node.getId() + "#adminPassword";
      if (credentialStore.containsKey(adminPasswordKey)) {
         node = NodeMetadataBuilder.fromNodeMetadata(node).adminPassword(
                  credentialStore.get(adminPasswordKey).credential).build();
      }
      return node;
   }

   OrgAndName getOrgAndNameFromNode(NodeMetadata node) {
      URI orgId = URI.create(node.getLocation().getParent().getId());
      OrgAndName orgAndName = new OrgAndName(orgId, node.getGroup());
      return orgAndName;
   }

}
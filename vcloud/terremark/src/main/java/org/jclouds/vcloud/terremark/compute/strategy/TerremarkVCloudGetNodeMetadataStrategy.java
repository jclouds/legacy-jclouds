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

package org.jclouds.vcloud.terremark.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.installNewCredentials;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.compute.CommonVCloudComputeClient;
import org.jclouds.vcloud.compute.domain.VCloudLocation;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.compute.functions.GetExtra;
import org.jclouds.vcloud.compute.strategy.VCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudGetNodeMetadataStrategy extends VCloudGetNodeMetadataStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap;

   @Inject
   protected TerremarkVCloudGetNodeMetadataStrategy(CommonVCloudClient client, CommonVCloudComputeClient computeClient,
            Map<Status, NodeState> vAppStatusToNodeState, GetExtra getExtra,
            FindLocationForResource findLocationForResourceInVDC, Supplier<Set<? extends Image>> images,
            ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap) {
      super(client, computeClient, vAppStatusToNodeState, getExtra, findLocationForResourceInVDC, images);
      this.credentialsMap = credentialsMap;
   }

   @Override
   public NodeMetadata execute(String id) {
      NodeMetadata node = super.execute(checkNotNull(id, "node.id"));
      if (node == null)
         return null;
      if (node.getTag() != null) {
         node = installCredentialsFromCache(node);
      }
      return node;
   }

   NodeMetadata installCredentialsFromCache(NodeMetadata node) {
      OrgAndName orgAndName = getOrgAndNameFromNode(node);
      if (credentialsMap.containsKey(orgAndName)) {
         Credentials creds = credentialsMap.get(orgAndName);
         node = installNewCredentials(node, creds);
      }
      return node;
   }

   OrgAndName getOrgAndNameFromNode(NodeMetadata node) {
      URI orgId = VCloudLocation.class.cast(node.getLocation().getParent()).getResource().getId();
      OrgAndName orgAndName = new OrgAndName(orgId, node.getTag());
      return orgAndName;
   }

}
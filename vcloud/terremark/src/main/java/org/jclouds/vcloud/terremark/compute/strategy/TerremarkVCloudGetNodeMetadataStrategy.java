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
package org.jclouds.vcloud.terremark.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.installNewCredentials;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.compute.functions.VCloudGetNodeMetadata;
import org.jclouds.vcloud.compute.strategy.VCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.domain.KeyPair;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudGetNodeMetadataStrategy extends VCloudGetNodeMetadataStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ConcurrentMap<OrgAndName, KeyPair> credentialsMap;

   @Inject
   protected TerremarkVCloudGetNodeMetadataStrategy(VCloudGetNodeMetadata getNodeMetadata,
            ConcurrentMap<OrgAndName, KeyPair> credentialsMap) {
      super(getNodeMetadata);
      this.credentialsMap = credentialsMap;
   }

   @Override
   public NodeMetadata execute(String id) {
      NodeMetadata node = getNodeMetadata.execute(checkNotNull(id, "node.id"));
      if (node == null)
         return null;
      if (node.getTag() != null) {
         node = installCredentialsFromCache(node);
      }
      if (node.getCredentials() == null)
         node = installDefaultCredentialsFromImage(node);
      return node;
   }

   NodeMetadata installCredentialsFromCache(NodeMetadata node) {
      OrgAndName orgAndName = getOrgAndNameFromNode(node);
      if (credentialsMap.containsKey(orgAndName)) {
         String identity = getLoginAccountForNode(node);
         if (identity != null) {
            String privateKey = credentialsMap.get(orgAndName).getPrivateKey();
            Credentials creds = new Credentials(identity, privateKey);
            node = installNewCredentials(node, creds);
         }
      }
      return node;
   }

   OrgAndName getOrgAndNameFromNode(NodeMetadata node) {
      String orgId = node.getLocation().getParent().getId();
      OrgAndName orgAndName = new OrgAndName(orgId, node.getTag());
      return orgAndName;
   }

   String getLoginAccountForNode(NodeMetadata node) {
      String identity = null;
      if (node.getCredentials() != null)
         identity = node.getCredentials().identity;
      else if (node.getImage() != null && node.getImage().getDefaultCredentials() != null)
         identity = node.getImage().getDefaultCredentials().identity;
      return identity;
   }

   NodeMetadata installDefaultCredentialsFromImage(NodeMetadata node) {
      if (node.getImage() != null && node.getImage().getDefaultCredentials() != null)
         node = installNewCredentials(node, node.getImage().getDefaultCredentials());
      return node;
   }
}
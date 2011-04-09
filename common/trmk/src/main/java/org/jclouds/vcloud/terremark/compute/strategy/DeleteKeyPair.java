/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.domain.KeyPair;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class DeleteKeyPair {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   final TerremarkVCloudClient terremarkClient;
   final ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap;

   @Inject
   DeleteKeyPair(TerremarkVCloudClient terremarkClient, ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap) {
      this.terremarkClient = terremarkClient;
      this.credentialsMap = credentialsMap;
   }

   public void execute(OrgAndName orgTag) {
      for (KeyPair keyPair : terremarkClient.listKeyPairsInOrg(orgTag.getOrg())) {
         if (keyPair.getName().matches("jclouds#" + orgTag.getName() + "#[0-9a-f]+")) {
            logger.debug(">> deleting keyPair(%s)", keyPair.getName());
            terremarkClient.deleteKeyPair(keyPair.getId());
            // TODO: test this clear happens
            credentialsMap.remove(orgTag);
            logger.debug("<< deleted keyPair(%s)", keyPair.getName());
         }
      }
   }
}
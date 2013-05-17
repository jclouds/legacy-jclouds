/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;

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
   final Map<String, Credentials> credentialStore;

   @Inject
   DeleteKeyPair(TerremarkVCloudClient terremarkClient, Map<String, Credentials> credentialStore) {
      this.terremarkClient = terremarkClient;
      this.credentialStore = credentialStore;
   }

   public void execute(OrgAndName orgTag) {
      for (KeyPair keyPair : terremarkClient.listKeyPairsInOrg(orgTag.getOrg())) {
         if (keyPair.getName().matches("jclouds_" + orgTag.getName().replaceAll("-", "_") + "_[0-9a-f]+")) {
            logger.debug(">> deleting keyPair(%s)", keyPair.getName());
            terremarkClient.deleteKeyPair(keyPair.getId());
            logger.debug("<< deleted keyPair(%s)", keyPair.getName());
            credentialStore.remove("group#" + orgTag.getName());
         }
      }
   }
}

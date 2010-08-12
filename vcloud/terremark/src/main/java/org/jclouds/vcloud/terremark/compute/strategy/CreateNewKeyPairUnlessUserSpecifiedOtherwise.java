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

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.CreateUniqueKeyPair;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.domain.KeyPair;

import com.google.common.annotations.VisibleForTesting;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class CreateNewKeyPairUnlessUserSpecifiedOtherwise {
   final ConcurrentMap<OrgAndName, KeyPair> credentialsMap;
   @VisibleForTesting
   final CreateUniqueKeyPair createUniqueKeyPair;

   @Inject
   CreateNewKeyPairUnlessUserSpecifiedOtherwise(ConcurrentMap<OrgAndName, KeyPair> credentialsMap,
            CreateUniqueKeyPair createUniqueKeyPair) {
      this.credentialsMap = credentialsMap;
      this.createUniqueKeyPair = createUniqueKeyPair;
   }

   @VisibleForTesting
   public void execute(String org, String tag, TerremarkVCloudTemplateOptions options) {
      String sshKeyFingerprint = options.getSshKeyFingerprint();
      boolean shouldAutomaticallyCreateKeyPair = options.shouldAutomaticallyCreateKeyPair();
      if (sshKeyFingerprint == null && shouldAutomaticallyCreateKeyPair) {
         OrgAndName orgAndName = new OrgAndName(org, tag);
         if (credentialsMap.containsKey(orgAndName)) {
            options.sshKeyFingerprint(credentialsMap.get(orgAndName).getFingerPrint());
         } else {
            KeyPair keyPair = createUniqueKeyPair.apply(orgAndName);
            credentialsMap.put(orgAndName, keyPair);
            options.sshKeyFingerprint(keyPair.getFingerPrint());
         }
      }
   }
}
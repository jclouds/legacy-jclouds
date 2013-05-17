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

import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshKeys;
import org.jclouds.trmk.vcloud_0_8.compute.domain.OrgAndName;
import org.jclouds.trmk.vcloud_0_8.compute.functions.CreateUniqueKeyPair;
import org.jclouds.trmk.vcloud_0_8.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;

import com.google.common.annotations.VisibleForTesting;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class CreateNewKeyPairUnlessUserSpecifiedOtherwise {
   final Map<String, Credentials> credentialStore;
   @VisibleForTesting
   final CreateUniqueKeyPair createUniqueKeyPair;

   @Inject
   CreateNewKeyPairUnlessUserSpecifiedOtherwise(Map<String, Credentials> credentialStore,
            CreateUniqueKeyPair createUniqueKeyPair) {
      this.credentialStore = credentialStore;
      this.createUniqueKeyPair = createUniqueKeyPair;
   }

   @VisibleForTesting
   public void execute(URI org, String group, String identity, TerremarkVCloudTemplateOptions options) {
      String sshKeyFingerprint = options.getSshKeyFingerprint();
      boolean shouldAutomaticallyCreateKeyPair = options.shouldAutomaticallyCreateKeyPair();
      if (sshKeyFingerprint == null && shouldAutomaticallyCreateKeyPair) {

         // make sure that we don't request multiple keys simultaneously
         synchronized (credentialStore) {
            // if there is already a keypair for the group specified, use it
            if (credentialStore.containsKey("group#" + group)) {
               LoginCredentials creds = LoginCredentials.fromCredentials(credentialStore.get("group#" + group));
               checkState(creds.getOptionalPrivateKey().isPresent(),
                        "incorrect state: should have private key for: %s", creds);
               options.sshKeyFingerprint(SshKeys.fingerprintPrivateKey(creds.getPrivateKey()));
            } else {
               // otherwise create a new keypair and key it under the group
               KeyPair keyPair = createUniqueKeyPair.apply(new OrgAndName(org, group));
               credentialStore.put("group#" + group, LoginCredentials.builder().user(identity).privateKey(
                        keyPair.getPrivateKey()).build());
               options.sshKeyFingerprint(keyPair.getFingerPrint());
            }
         }

      }

   }
}

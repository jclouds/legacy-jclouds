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
package org.jclouds.aws.ec2.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.crypto.SshKeys.fingerprintPublicKey;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.RegionNameAndPublicKeyMaterial;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ImportOrReturnExistingKeypair implements Function<RegionNameAndPublicKeyMaterial, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final AWSEC2Client ec2Client;

   @Inject
   public ImportOrReturnExistingKeypair(AWSEC2Client ec2Client) {
      this.ec2Client = ec2Client;
   }

   @Override
   public KeyPair apply(RegionNameAndPublicKeyMaterial from) {
      return importOrReturnExistingKeypair(from.getRegion(), from.getName(), from.getPublicKeyMaterial());
   }

   @VisibleForTesting
   KeyPair importOrReturnExistingKeypair(String region, String group, String publicKeyMaterial) {
      checkNotNull(region, "region");
      checkNotNull(group, "group");
      checkNotNull(publicKeyMaterial, "publicKeyMaterial");
      logger.debug(">> importing keyPair region(%s) group(%s)", region, group);
      KeyPair keyPair = null;
      // loop for eventual consistency or race condition.
      // as this command is idempotent, it should be ok
      while (keyPair == null)
         try {
            keyPair = ec2Client.getKeyPairServices().importKeyPairInRegion(region, "jclouds#" + group,
                     publicKeyMaterial);
            keyPair = addFingerprintToKeyPair(publicKeyMaterial, keyPair);
            logger.debug("<< imported keyPair(%s)", keyPair);
         } catch (IllegalStateException e) {
            keyPair = Iterables.getFirst(ec2Client.getKeyPairServices().describeKeyPairsInRegion(region,
                     "jclouds#" + group), null);
            if (keyPair != null) {
               keyPair = addFingerprintToKeyPair(publicKeyMaterial, keyPair);
               logger.debug("<< retrieved existing keyPair(%s)", keyPair);
            }
         }
      return keyPair;
   }

   public KeyPair addFingerprintToKeyPair(String publicKeyMaterial, KeyPair keyPair) {
      // add in the fingerprint as it makes correlating keys in ssh logs possible
      keyPair = keyPair.toBuilder().fingerprint(fingerprintPublicKey(publicKeyMaterial)).build();
      return keyPair;
   }

}

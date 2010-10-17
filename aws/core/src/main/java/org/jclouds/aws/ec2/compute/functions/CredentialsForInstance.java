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

package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CredentialsForInstance implements Function<RunningInstance, Credentials> {
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Map<RegionAndName, Image> imageForInstance;

   @Inject
   CredentialsForInstance(Map<RegionAndName, KeyPair> credentialsMap,
         PopulateDefaultLoginCredentialsForImageStrategy credentialProvider, Map<RegionAndName, Image> imageForInstance) {
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.imageForInstance = imageForInstance;
   }

   @Override
   public Credentials apply(RunningInstance instance) {
      Credentials credentials = null;// default if no keypair exists

      if (instance.getKeyName() != null) {
         credentials = new Credentials(getLoginAccountFor(instance), getPrivateKeyOrNull(instance));
      }
      return credentials;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) {
      return checkNotNull(
            credentialProvider.execute(imageForInstance.get(new RegionAndName(from.getRegion(), from.getImageId()))),
            "login from image: " + from.getImageId()).identity;
   }
}
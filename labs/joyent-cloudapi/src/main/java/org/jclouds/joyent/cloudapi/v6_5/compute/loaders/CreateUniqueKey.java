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
package org.jclouds.joyent.cloudapi.v6_5.compute.loaders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.SecureRandom;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.SshKeys;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudClient;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.logging.Logger;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class CreateUniqueKey extends CacheLoader<DatacenterAndName, KeyAndPrivateKey> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final JoyentCloudClient cloudApiClient;
   protected final GroupNamingConvention.Factory namingConvention;
   protected final Crypto crypto;
   protected final Provider<SecureRandom> secureRandom;

   @Inject
   public CreateUniqueKey(JoyentCloudClient cloudApiClient, GroupNamingConvention.Factory namingConvention, Crypto crypto, Provider<SecureRandom> secureRandom) {
      this.cloudApiClient = checkNotNull(cloudApiClient, "cloudApiClient");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
      this.crypto = checkNotNull(crypto, "crypto");
      this.secureRandom = checkNotNull(secureRandom, "secureRandom");
   }

   @Override
   public KeyAndPrivateKey load(DatacenterAndName datacenterAndName) {
      String datacenterId = checkNotNull(datacenterAndName, "datacenterAndName").getDatacenter();
      String prefix = datacenterAndName.getName();
      
      Map<String, String> keyPair = SshKeys.generate(crypto.rsaKeyPairGenerator(), secureRandom.get());
      String publicKey = keyPair.get("public");
      String privateKey = keyPair.get("private");

      logger.debug(">> creating key datacenter(%s) prefix(%s)", datacenterId, prefix);

      Key key = null;
      while (key == null) {
         String name = namingConvention.createWithoutPrefix().uniqueNameForGroup(prefix);
         try {
            key = cloudApiClient.getKeyClient().create(Key.builder().name(name).key(publicKey).build());
         } catch (IllegalStateException e) {
            logger.trace("error creating keypair named %s, %s", name, e.getMessage());
         }
      }

      logger.debug("<< created key(%s)", key.getName());
      return KeyAndPrivateKey.fromKeyAndPrivateKey(key, privateKey);
   }

}

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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

/**
 * @author Adam Lowe
 */
@Singleton
public class CreateUniqueKeyPair extends CacheLoader<ZoneAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final NovaClient novaClient;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   public CreateUniqueKeyPair(NovaClient novaClient, GroupNamingConvention.Factory namingConvention) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public KeyPair load(ZoneAndName zoneAndName) {
      String zoneId = checkNotNull(zoneAndName, "zoneAndName").getZone();
      String prefix = zoneAndName.getName();

      Optional<KeyPairClient> client = novaClient.getKeyPairExtensionForZone(zoneId);
      checkArgument(client.isPresent(), "Key pairs are required, but the extension is not available in zone %s!",
            zoneId);

      logger.debug(">> creating keyPair zone(%s) prefix(%s)", zoneId, prefix);

      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = client.get().createKeyPair(namingConvention.createWithoutPrefix().uniqueNameForGroup(prefix));
         } catch (IllegalStateException e) {

         }
      }

      logger.debug("<< created keyPair(%s)", keyPair.getName());
      return keyPair;
   }

}

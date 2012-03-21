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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.domain.KeyPair;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adam Lowe
 */
@Singleton
public class CreateUniqueKeyPair implements Function<ZoneAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final NovaClient novaClient;
   protected final Supplier<String> randomSuffix;

   @Inject
   public CreateUniqueKeyPair(NovaClient novaClient, Supplier<String> randomSuffix) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
      this.randomSuffix = randomSuffix;
   }

   @Override
   public KeyPair apply(ZoneAndName zoneAndName) {
      checkNotNull(zoneAndName, "zoneAndName");
      return createNewKeyPairInZone(zoneAndName.getZone(), zoneAndName.getName());
   }

   @VisibleForTesting
   KeyPair createNewKeyPairInZone(String zone, String group) {
      checkNotNull(zone, "zone");
      checkNotNull(group, "group");
      logger.debug(">> creating keyPair zone(%s) group(%s)", zone, group);

      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = novaClient.getKeyPairExtensionForZone(zone).get().createKeyPair(getNextName(group));
         } catch (IllegalStateException e) {

         }
      }
      
      logger.debug("<< created keyPair(%s)", keyPair);
      return keyPair;
   }

   private String getNextName(String group) {
      return String.format("jclouds#%s#%s", group, randomSuffix.get());
   }
}

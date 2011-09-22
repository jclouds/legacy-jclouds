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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateUniqueKeyPair extends CacheLoader<RegionAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;
   protected final Supplier<String> randomSuffix;
   protected final Map<RegionAndName, KeyPair> knownKeys;

   @Inject
   public CreateUniqueKeyPair(Map<RegionAndName, KeyPair> knownKeys, EC2Client ec2Client, Supplier<String> randomSuffix) {
      this.knownKeys = knownKeys;
      this.ec2Client = ec2Client;
      this.randomSuffix = randomSuffix;
   }

   @Override
   public KeyPair load(RegionAndName from) {
      if (knownKeys.containsKey(from)){
         return knownKeys.get(from);
      } else {
         KeyPair keyPair = createNewKeyPairInRegion(from.getRegion(), from.getName());
         knownKeys.put(new RegionAndName(from.getRegion(), keyPair.getKeyName()), keyPair);
         knownKeys.put(from, keyPair);
         return keyPair;
      }
   }

   @VisibleForTesting
   KeyPair createNewKeyPairInRegion(String region, String group) {
      checkNotNull(region, "region");
      checkNotNull(group, "group");
      logger.debug(">> creating keyPair region(%s) group(%s)", region, group);
      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, getNextName(region, group));
            logger.debug("<< created keyPair(%s)", keyPair.getKeyName());
         } catch (IllegalStateException e) {

         }
      }
      return keyPair;
   }

   private String getNextName(String region, String group) {
      return String.format("jclouds#%s#%s#%s", group, region, randomSuffix.get());
   }
}
